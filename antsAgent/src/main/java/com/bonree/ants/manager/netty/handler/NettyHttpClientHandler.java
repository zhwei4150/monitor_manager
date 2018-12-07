package com.bonree.ants.manager.netty.handler;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bonree.ants.manager.action.ActionGroup;
import com.bonree.ants.manager.action.AntsAction;
import com.bonree.ants.manager.proto.RequestResponseProto;
import com.bonree.ants.manager.type.AntsHeaders;
import com.bonree.ants.manager.util.ProtoBufUtil;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.timeout.IdleStateEvent;

public class NettyHttpClientHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

    private final static Logger LOG = LoggerFactory.getLogger(NettyHttpClientHandler.class);

    private ActionGroup<RequestResponseProto> actions;

    public NettyHttpClientHandler(ActionGroup<RequestResponseProto> actions) {
        this.actions = actions;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) throws Exception {
        String contentType = msg.headers().get(HttpHeaderNames.CONTENT_TYPE);
        LOG.info("content-type:{}", contentType);
        String lengthValue = msg.headers().get(HttpHeaderNames.CONTENT_LENGTH);
        long length = 0;
        if (lengthValue != null) {
            length = Long.parseLong(lengthValue);
        }
        LOG.info("content-length:{}", length);
        if (length > 0) {
            byte[] content = new byte[msg.content().readableBytes()];
            msg.content().readBytes(content);
            if (StringUtils.equalsIgnoreCase(contentType, AntsHeaders.CONTENT_PROTO)) {
                RequestResponseProto reqResp = ProtoBufUtil.deserialize(content, RequestResponseProto.class);
                LOG.info("action-type:{}", reqResp.getActionType());
                actions.getAction(reqResp.getActionType()).dealData(ctx, reqResp);
            } else if (StringUtils.equalsIgnoreCase(contentType, AntsHeaders.CONTENT_JSON)) {
                // 暂时还没有json的协议
            }
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            actions.getAction(AntsAction.BEATS).dealIdleStateEvent(ctx, evt);
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        actions.getAction(AntsAction.BEATS).dealexceptionCaught(ctx, cause);
    }

}
