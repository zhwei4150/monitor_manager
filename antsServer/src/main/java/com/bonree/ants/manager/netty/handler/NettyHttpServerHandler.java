package com.bonree.ants.manager.netty.handler;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bonree.ants.manager.action.ActionGroup;
import com.bonree.ants.manager.action.AntsAction;
import com.bonree.ants.manager.proto.CmdsRequestProto;
import com.bonree.ants.manager.proto.RequestResponseProto;
import com.bonree.ants.manager.type.AntsHeaders;
import com.bonree.ants.manager.util.JsonUtil;
import com.bonree.ants.manager.util.ProtoBufUtil;
import com.bonree.ants.manager.util.UriUtil;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.timeout.IdleStateEvent;

public class NettyHttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final static Logger LOG = LoggerFactory.getLogger(NettyHttpServerHandler.class);
    private ActionGroup<RequestResponseProto> actions;

    public NettyHttpServerHandler(ActionGroup<RequestResponseProto> actions) {
        this.actions = actions;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        String actionOpt = UriUtil.removePreSuf(msg.uri());
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
                if (!StringUtils.equalsIgnoreCase(actionOpt, reqResp.getActionType())) {
                    LOG.warn("request-type:{}, action-type:{} ,type is not inconformity,em... ignore...", actionOpt, reqResp
                            .getActionType());

                    ctx.channel().close();
                    return;

                }
                actions.getAction(reqResp.getActionType()).dealData(ctx, reqResp);
            } else if (StringUtils.equalsIgnoreCase(contentType, AntsHeaders.CONTENT_JSON)) {
                @SuppressWarnings("unchecked")
                Map<String, Object> jsonMap = JsonUtil.toObject(content, Map.class);
                CmdsRequestProto request = new CmdsRequestProto();
                request.setModuleName((String) jsonMap.get("module"));
                request.setCmdName((String) jsonMap.get("cmd"));
                request.setType(0); // 客户请求
                request.setData((String) jsonMap.get("data"));
                RequestResponseProto reqResp = new RequestResponseProto(actionOpt);
                reqResp.setCmdsRequest(request);
                actions.getAction(reqResp.getActionType()).dealData(ctx, reqResp);
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
