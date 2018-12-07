package com.bonree.ants.manager.agent.action;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bonree.ants.manager.action.AntsAction;
import com.bonree.ants.manager.agent.job.AgentJobDetail;
import com.bonree.ants.manager.agent.job.JobController;
import com.bonree.ants.manager.cache.HostNodeManager;
import com.bonree.ants.manager.node.HostNode;
import com.bonree.ants.manager.node.ServerHostNode;
import com.bonree.ants.manager.proto.BeatsRequestProto;
import com.bonree.ants.manager.proto.BeatsResponseProto;
import com.bonree.ants.manager.proto.BeatsType;
import com.bonree.ants.manager.proto.RequestResponseProto;
import com.bonree.ants.manager.proto.ReturnCode;
import com.bonree.ants.manager.type.AntsHeaders;
import com.bonree.ants.manager.util.HttpUtil;
import com.bonree.ants.manager.util.NodeUtil;
import com.bonree.ants.manager.util.ProtoBufUtil;
import com.bonree.ants.manager.util.UriUtil;
import com.google.common.collect.Lists;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class AgentBeatsAction implements AntsAction<RequestResponseProto> {
    private static final Logger LOG = LoggerFactory.getLogger(AgentBeatsAction.class);

    private final FileRequestProxy fileRequestProxy;
    private final HostNodeManager<ServerHostNode> hostManager;
    private final JobController jobController;
    private final Map<String, AgentJobDetail> jobsDetail;
    private Map<Integer, HostNode> remoteNodes;

    public AgentBeatsAction(HostNodeManager<ServerHostNode> hostManager, FileRequestProxy fileRequestProxy, JobController jobController) {
        this.jobsDetail = new ConcurrentHashMap<>();
        this.hostManager = hostManager;
        this.fileRequestProxy = fileRequestProxy;
        this.jobController = jobController;
    }

    @Override
    public void dealData(ChannelHandlerContext ctx, RequestResponseProto msg) throws Exception {
        HostNode remoteNode = remoteNodes.get(ctx.hashCode());

        BeatsResponseProto beatsResponse = msg.getBeatsResponse();
        try {
            // 接收到心跳消息，需要对比和服务器的时间戳，并且领取leader的任务
            if (hostManager.getHostNode(remoteNode).getTimeMillis() == 0) {
                // 表示网络通
                hostManager.addHostChannel(remoteNode, ctx.channel());

                hostManager.getHostNode(remoteNode).setTimeMillis(beatsResponse.getServerMillis());
            }
            ServerHostNode serverNode = hostManager.getHostNode(remoteNode);
            long serverMillis = beatsResponse.getServerMillis();
            if (serverNode.getTimeMillis() != serverMillis) {
                LOG.warn("ignore this response, serverNode:{},{} is not same,agent is {},server is {}", remoteNode, "serverMillis", serverNode
                        .getTimeMillis(), serverMillis);
                return;
            }
            HostNode leaderNode = NodeUtil.minimumNode(hostManager.listHostNodes());
            if (NodeUtil.nodeEqual(remoteNode, leaderNode)) {
                // 接收的remoteNode和leaderNode的信息一致，则可以领取该任务
                if (beatsResponse.getJob() != null) {
                    AgentJobDetail detail = new AgentJobDetail(beatsResponse.getJob());
                    jobsDetail.put(beatsResponse.getJob().getName(), detail);
                    LOG.info("recvr job from {}. jobs : {}", remoteNode, detail);
                    requestFilesAndStartJob(ctx, detail);
                }
            }
        } catch (Exception e) {
            LOG.error("cause:", e);
            throw e;
        }
    }

    private void requestFilesAndStartJob(ChannelHandlerContext ctx, AgentJobDetail detail) throws Exception {
        fileRequestProxy.requestFile(detail.getJobProperty().getName(), ctx.channel(), detail
                .getJobProperty(), new FileTransferListener() {

                    @Override
                    public void terminal(String transName) {
                        try {
                            jobController.startJob(detail);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void process(String transName, String fileName, long offset, long fileSize) {

                    }

                    @Override
                    public void error(String transName, String fileName, ReturnCode code) {

                    }
                });
    }

    @Override
    public void dealIdleStateEvent(ChannelHandlerContext ctx, Object evt) throws Exception {
        try {
            HostNode remoteNode = remoteNodes.get(ctx.hashCode());
            if (remoteNode == null) {
                remoteNode = NodeUtil.parseInetAddress(ctx.channel().remoteAddress(), HostNode.class);
                remoteNodes.put(ctx.hashCode(), remoteNode);
            }
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.WRITER_IDLE) {
                // 通过超时构造心跳协议包
                long serverMilli = hostManager.getHostNode(remoteNode).getTimeMillis();
                if (serverMilli == 0) { // 还没有进行注册或者连接出现异常
                    LOG.info("send register msg");
                    BeatsRequestProto beatsRequestProto = new BeatsRequestProto(BeatsType.REGISTRY);
                    RequestResponseProto reqResqProto = new RequestResponseProto(AntsAction.BEATS);
                    reqResqProto.setBeatsRequest(beatsRequestProto);

                    byte[] byteReq = ProtoBufUtil.serialize(reqResqProto);
                    ByteBuf heartMsg = Unpooled.wrappedBuffer(byteReq);
                    FullHttpRequest request = HttpUtil.constructRequest(UriUtil
                            .makeUri(AntsAction.BEATS), AntsHeaders.CONTENT_PROTO, heartMsg);
                    ctx.channel().writeAndFlush(request);

                } else {
                    LOG.info("send heartbeat msg");
                    HostNode minNode = NodeUtil.minimumNode(hostManager.listHostNodes());

                    BeatsRequestProto beatsReq = new BeatsRequestProto(BeatsType.BEATS);
                    beatsReq.setJobNames(Lists.newArrayList(jobsDetail.keySet()));
                    beatsReq.setMinNode(minNode);

                    RequestResponseProto reqResq = new RequestResponseProto(AntsAction.BEATS);
                    reqResq.setActionType(AntsAction.BEATS);
                    reqResq.setBeatsRequest(beatsReq);

                    byte[] byteReq = ProtoBufUtil.serialize(reqResq);
                    ByteBuf heartMsg = Unpooled.wrappedBuffer(byteReq);

                    FullHttpRequest request = HttpUtil.constructRequest(UriUtil
                            .makeUri(AntsAction.BEATS), AntsHeaders.CONTENT_PROTO, heartMsg);

                    ctx.channel().writeAndFlush(request);
                }
            } else if (event.state() == IdleState.READER_IDLE) {
                LOG.warn("no message receive from antsServer!");
            }
        } catch (Exception e) {
            LOG.error("cause:", e);
            throw e;
        }
    }

    @Override
    public void dealexceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        try {
            HostNode remoteNode = remoteNodes.get(ctx.hashCode());
            // 将serverNode设置为不可达
            if (remoteNode != null) {
                hostManager.removeHostChannel(remoteNode);
                hostManager.getHostNode(remoteNode).setTimeMillis(0);
            }
            LOG.error("cause:{}", cause);
        } catch (Exception e) {
            LOG.error("cause:", e);
            throw e;
        }
    }

    @Override
    public void close() throws Exception {

    }

}
