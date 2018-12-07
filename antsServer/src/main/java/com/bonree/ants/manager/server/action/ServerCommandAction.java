package com.bonree.ants.manager.server.action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bonree.ants.manager.action.AntsAction;
import com.bonree.ants.manager.cache.HostNodeManager;
import com.bonree.ants.manager.command.CmdProperty;
import com.bonree.ants.manager.node.AgentHostNode;
import com.bonree.ants.manager.node.HostNode;
import com.bonree.ants.manager.proto.CmdsRequestProto;
import com.bonree.ants.manager.proto.CmdsResponseProto;
import com.bonree.ants.manager.proto.RequestResponseProto;
import com.bonree.ants.manager.proto.ReturnCode;
import com.bonree.ants.manager.server.command.CmdManager;
import com.bonree.ants.manager.type.AntsHeaders;
import com.bonree.ants.manager.util.HttpUtil;
import com.bonree.ants.manager.util.JsonUtil;
import com.bonree.ants.manager.util.NodeUtil;
import com.bonree.ants.manager.util.ProtoBufUtil;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;

/*******************************************************************************
 * 版权信息：博睿宏远科技发展有限公司
 * Copyright: Copyright (c) 2007博睿宏远科技发展有限公司,Inc.All Rights Reserved.
 * 
 * @date 2018年11月29日 下午2:20:45
 * @Author: <a href=mailto:weizheng@bonree.com>魏征</a>
 * @Description: 处理从客户端收到的命令，将命令转发到相应的agent进行执行
 *                  目前命令存储到队列中，队列满了，则无法继续请求命令
 *                  目前命令无法取消，目前命令无法过期，后面可添加过期时间，即命令未执行超过一定的时间，
 *                  需要将命令进行过期
 ******************************************************************************/
public class ServerCommandAction implements AntsAction<RequestResponseProto> {

    private final static Logger LOG = LoggerFactory.getLogger(ServerCommandAction.class);
    private final BlockingQueue<CmdContext> cmdQueue;
    private final ExecutorService cmdsPool;
    private boolean running = false;
    private final Object blockObj = new Object();
    private final long timeOut = 60000;

    private final Set<String> execNodes = new HashSet<>();

    private final CmdManager cmdManager;
    private final HostNodeManager<AgentHostNode> nodeManager;

    public ServerCommandAction(CmdManager cmdManager, HostNodeManager<AgentHostNode> nodeManager) {
        this.cmdManager = cmdManager;
        this.nodeManager = nodeManager;
        cmdsPool = Executors.newFixedThreadPool(4, r -> new Thread(r, "cmds-" + Thread.currentThread().getId()));
        cmdQueue = new ArrayBlockingQueue<>(1000);

        cmdsPool.execute(() -> {
            try {
                execCmd(); // 代码执行检测
            } catch (Exception e) {
                LOG.error("dealCmd error:", e);
            }
        });
    }

    @Override
    public void dealData(ChannelHandlerContext ctx, RequestResponseProto msg) throws Exception {
        cmdsPool.execute(() -> {
            try {
                dealCmd(ctx, msg);
            } catch (Exception e) {
                LOG.error("addCmd2Cache error:", e);
            }
        });
    }

    @Override
    public void dealIdleStateEvent(ChannelHandlerContext ctx, Object evt) throws Exception {

    }

    @Override
    public void dealexceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

    }

    @Override
    public void close() throws Exception {
        cmdsPool.shutdown();

    }

    private boolean isRunning() {
        return running;
    }

    private void markRunning() {
        running = true;
    }

    private void markFinish() {
        running = false;
    }

    private void dealCmd(ChannelHandlerContext ctx, RequestResponseProto msg) throws Exception {
        int type = msg.getCmdsRequest().getType();
        if (type == 0) { // 客户端传来的信息
            boolean flag = cmdQueue.offer(new CmdContext(ctx, msg.getCmdsRequest()));
            if (!flag) {// 添加成功，即可返回
                Map<String, Object> result = HttpUtil.commandResult(3001, "commands queue is full!");
                String json = JsonUtil.toJsonString(result);
                ByteBuf content = Unpooled.wrappedBuffer(json.getBytes());
                FullHttpResponse response = HttpUtil.constructResponse(AntsHeaders.CONTENT_JSON, content);
                ctx.channel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            }

        } else if (type == 1) {
            // 接收来自agent的消息，将消息缓存
            HostNode node = NodeUtil.parseInetAddress(ctx.channel().remoteAddress(), HostNode.class);
            execNodes.remove(node.getHost());
            if (execNodes.isEmpty()) {
                blockObj.notify();
            }
        }
    }

    private void execCmd() throws Exception {
        while (true) {
            if (!isRunning()) {
                CmdContext cmdContext = cmdQueue.take();
                if (cmdContext != null) {
                    markRunning();
                    String moduleName = cmdContext.getRequest().getModuleName();
                    String cmdName = cmdContext.getRequest().getCmdName();
                    if (StringUtils.isEmpty(moduleName) || StringUtils.isEmpty(cmdName)) {
                        Map<String, Object> result = HttpUtil.commandResult(3002, "request error!");
                        String json = JsonUtil.toJsonString(result);
                        ByteBuf content = Unpooled.wrappedBuffer(json.getBytes());
                        FullHttpResponse response = HttpUtil.constructResponse(AntsHeaders.CONTENT_JSON, content);
                        cmdContext.getCtx().channel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                        markFinish();
                    } else {
                        if (!cmdManager.checkCmdExist(moduleName, cmdName)) {
                            Map<String, Object> result = HttpUtil.commandResult(3002, "cmd is not exist!");
                            String json = JsonUtil.toJsonString(result);
                            ByteBuf content = Unpooled.wrappedBuffer(json.getBytes());
                            FullHttpResponse response = HttpUtil.constructResponse(AntsHeaders.CONTENT_JSON, content);
                            cmdContext.getCtx().channel().writeAndFlush(response)
                                    .addListener(ChannelFutureListener.CLOSE);
                            markFinish();
                        } else {
                            // 需要执行的
                            List<HostNode> nodes = selectAgentHost(cmdManager.getCmdModule(moduleName).getAgentNodes());

                            CmdProperty property = cmdManager.getSpecifyCmd(moduleName, cmdName);
                            CmdsResponseProto cmdResponse = new CmdsResponseProto(ReturnCode.OK);
                            cmdResponse.setCmdProperty(property);
                            cmdResponse.setJsonData(cmdContext.getRequest().getData());

                            RequestResponseProto reqResp = new RequestResponseProto(AntsAction.COMMANDS);
                            reqResp.setCmdsResponse(cmdResponse);
                            byte[] bytes = ProtoBufUtil.serialize(reqResp);
                            ByteBuf agentContent = Unpooled.wrappedBuffer(bytes);
                            FullHttpResponse agentResponse = HttpUtil
                                    .constructResponse(AntsHeaders.CONTENT_PROTO, agentContent);
                            for (HostNode hostNode : nodes) {
                                Channel channel = nodeManager.getHostChannel(hostNode);
                                execNodes.add(hostNode.getHost());
                                channel.writeAndFlush(agentResponse)
                                        .addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                            }

                            blockObj.wait(timeOut); // 需要等待所有的agent返回结果，才能将结果回传给client

                            String json = "{\"response:\":\"OK\"}";
                            ByteBuf clientContent = Unpooled.wrappedBuffer(json.getBytes());
                            FullHttpResponse clientResponse = HttpUtil
                                    .constructResponse(AntsHeaders.CONTENT_JSON, clientContent);
                            cmdContext.getCtx().channel().writeAndFlush(clientResponse)
                                    .addListener(ChannelFutureListener.CLOSE);
                            markFinish();
                        }
                    }
                }
            }
        }
    }

    private List<HostNode> selectAgentHost(String hostNames) {
        List<HostNode> hostNodes = new ArrayList<>(16);
        if (StringUtils.equalsIgnoreCase(hostNames, "random")) {
            HostNode hostNode = nodeManager.getRandomHostNode();
            if (hostNode != null) {
                hostNodes.add(hostNode);
            }
        } else if (StringUtils.equalsIgnoreCase(hostNames, "all")) {
            hostNodes.addAll(nodeManager.listHostNodes());
        } else {
            String[] nodeArr = hostNames.split(",");
            for (String node : nodeArr) {
                HostNode hostNode = nodeManager.getHostNode(node);
                if (hostNode != null) {
                    hostNodes.add(hostNode);
                }
            }
        }
        return hostNodes;
    }

    private static class CmdContext {
        private final ChannelHandlerContext ctx;
        private final CmdsRequestProto request;

        private CmdContext(ChannelHandlerContext ctx, CmdsRequestProto request) {
            this.ctx = ctx;
            this.request = request;
        }

        public ChannelHandlerContext getCtx() {
            return ctx;
        }

        public CmdsRequestProto getRequest() {
            return request;
        }

    }

}
