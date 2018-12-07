package com.bonree.ants.manager.server.action;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bonree.ants.manager.action.AntsAction;
import com.bonree.ants.manager.cache.HostNodeManager;
import com.bonree.ants.manager.job.JobProperty;
import com.bonree.ants.manager.node.AgentHostNode;
import com.bonree.ants.manager.node.HostNode;
import com.bonree.ants.manager.node.ServerHostNode;
import com.bonree.ants.manager.proto.BeatsRequestProto;
import com.bonree.ants.manager.proto.BeatsResponseProto;
import com.bonree.ants.manager.proto.BeatsType;
import com.bonree.ants.manager.proto.RequestResponseProto;
import com.bonree.ants.manager.proto.ReturnCode;
import com.bonree.ants.manager.server.job.JobManager;
import com.bonree.ants.manager.type.AntsHeaders;
import com.bonree.ants.manager.util.HttpUtil;
import com.bonree.ants.manager.util.NodeUtil;
import com.bonree.ants.manager.util.ProtoBufUtil;
import com.google.common.base.Preconditions;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class ServerBeatsAction implements AntsAction<RequestResponseProto> {

    private final static Logger LOG = LoggerFactory.getLogger(ServerBeatsAction.class);

    private final Map<Integer, AgentHostNode> cacheNodes;
    private final ServerHostNode serverNode;
    private final JobManager jobManager;
    private final HostNodeManager<AgentHostNode> hostManager;
    private final ExecutorService beatsPool;

    public ServerBeatsAction(ServerHostNode serverNode, HostNodeManager<AgentHostNode> hostManager, JobManager jobManager) {
        this.hostManager = Preconditions.checkNotNull(hostManager);
        this.jobManager = Preconditions.checkNotNull(jobManager);
        this.cacheNodes = new ConcurrentHashMap<>();
        this.serverNode = Preconditions.checkNotNull(serverNode);

        beatsPool = Executors.newFixedThreadPool(2, r -> new Thread(r, "beats-" + Thread.currentThread().getId()));
    }

    @Override
    public void dealData(ChannelHandlerContext ctx, RequestResponseProto msg) throws Exception {
        beatsPool.execute(() -> {
            try {
                dealBeats(ctx, msg);
            } catch (Exception e) {
                LOG.error("dealBeats:{} --occur exception:", ctx, e);
            }
        });
    }

    @Override
    public void dealIdleStateEvent(ChannelHandlerContext ctx, Object evt) throws Exception {
        beatsPool.execute(() -> {
            try {
                dealBeatsIdle(ctx, evt);
            } catch (Exception e) {
                LOG.error("dealIdleState error", e);
            }
        });
    }

    @Override
    public void dealexceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        beatsPool.execute(() -> {
            try {
                dealException(ctx, cause);
            } catch (Exception e) {
                LOG.error("dealException error", e);
            }
        });

    }

    public void dealException(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cacheNodes.get(ctx.hashCode()) == null) {
            LOG.info("close client conection:{}", NodeUtil.parseInetAddress(ctx.channel()
                    .remoteAddress(), HostNode.class));
            ctx.channel().close();
            return;
        }
        hostManager.removeHostNode(cacheNodes.get(ctx.hashCode()));
        LOG.info("remove agent--{}:{}", cacheNodes.get(ctx.hashCode()).getHost(), cacheNodes.get(ctx.hashCode())
                .getPort());
        LOG.info("close idle client--{}:{}", cacheNodes.get(ctx.hashCode()).getHost(), cacheNodes.get(ctx.hashCode())
                .getPort());
        cacheNodes.remove(ctx.hashCode());
        ctx.channel().close();
    }

    public void dealBeatsIdle(ChannelHandlerContext ctx, Object evt) throws Exception {
        try {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                LOG.warn("5s not recvr data,close the not alive connection");
                AgentHostNode node = cacheNodes.get(ctx.hashCode());
                if (node != null) {
                    hostManager.removeHostNode(cacheNodes.get(ctx.hashCode()));
                    LOG.info("remove agent--{}:{}", cacheNodes.get(ctx.hashCode()).getHost(), cacheNodes.get(ctx
                            .hashCode()).getPort());
                    LOG.info("close idle client--{}:{}", cacheNodes.get(ctx.hashCode()).getHost(), cacheNodes.get(ctx
                            .hashCode()).getPort());
                    cacheNodes.remove(ctx.hashCode());
                    ctx.channel().close();
                }
            }
        } catch (Exception e) {
            LOG.error("dealBeatsIdle, some thing is error!", e);
            throw e;
        }
    }

    public void dealBeats(ChannelHandlerContext ctx, RequestResponseProto msg) throws Exception {
        AgentHostNode remoteNode = cacheNodes.get(ctx.hashCode());
        if (remoteNode == null) {
            AgentHostNode node = NodeUtil.parseInetAddress(ctx.channel().remoteAddress(), AgentHostNode.class);
            cacheNodes.put(ctx.hashCode(), node);
        }

        try {
            BeatsRequestProto beatsRequest = msg.getBeatsRequest();

            if (beatsRequest.getType() == BeatsType.REGISTRY) { // 接收到注册事件
                // 添加agentNode到nodeManager中
                hostManager.addHostNode(remoteNode);
                // 构造response消息
                BeatsResponseProto beatsResponse = new BeatsResponseProto(ReturnCode.OK);
                beatsResponse.setServerMillis(serverNode.getTimeMillis());

                RequestResponseProto reqResp = new RequestResponseProto(AntsAction.BEATS);
                reqResp.setBeatsResponse(beatsResponse);

                byte[] byteResponse = ProtoBufUtil.serialize(reqResp);
                ByteBuf heartMsg = Unpooled.wrappedBuffer(byteResponse);

                FullHttpResponse response = HttpUtil.constructResponse(AntsHeaders.CONTENT_PROTO, heartMsg);
                ctx.channel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                LOG.info("response register!");

            } else {
                LOG.info("heartbeat msg");

                List<String> jobNames = beatsRequest.getJobNames();

                if (jobNames == null || jobNames.isEmpty()) {
                    LOG.info("heartbeat, agent:{} ,empty job!", remoteNode);
                } else {
                    LOG.info("heartbeat, agent:{} ,jobs : {}", remoteNode, jobNames);
                }

                BeatsResponseProto beatsResp = new BeatsResponseProto(ReturnCode.OK);
                beatsResp.setServerMillis(serverNode.getTimeMillis());

                if (!NodeUtil.nodeEqual(beatsRequest.getMinNode(), serverNode)) {
                    LOG.info("back server , update job!");

                } else {

                    if (!jobManager.checkAndUpdateJob(remoteNode, jobNames)) {
                        LOG.warn("jobs is exception...");
                        // 检查不通过，则需要修正任务
                    }
                    // 尝试分配任务
                    Optional<JobProperty> job = jobManager.dispatchJob(remoteNode);
                    if (job.isPresent()) {
                        LOG.info("job:{}", job);
                        beatsResp.setJob(job.get());
                        hostManager.getHostNode(remoteNode).addJob(job.get());
                    }

                }

                RequestResponseProto reqResp = new RequestResponseProto(AntsAction.BEATS);
                reqResp.setBeatsResponse(beatsResp);

                byte[] byteResp = ProtoBufUtil.serialize(reqResp);
                ByteBuf heartResp = Unpooled.wrappedBuffer(byteResp);

                FullHttpResponse response = HttpUtil.constructResponse(AntsHeaders.CONTENT_PROTO, heartResp);
                ctx.channel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);

            }
        } catch (Exception e) {
            LOG.error("some thing is error!", e);
            throw e;
        }
    }

    @Override
    public void close() throws Exception {
        beatsPool.awaitTermination(500, TimeUnit.MILLISECONDS);
        beatsPool.shutdown();
    }

}
