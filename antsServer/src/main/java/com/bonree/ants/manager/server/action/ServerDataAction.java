package com.bonree.ants.manager.server.action;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bonree.ants.manager.action.AntsAction;
import com.bonree.ants.manager.proto.DatasRequestProto;
import com.bonree.ants.manager.proto.RequestResponseProto;
import com.bonree.ants.manager.server.cache.DataCacheManager;
import com.bonree.ants.manager.type.AntsHeaders;
import com.bonree.ants.manager.util.HttpUtil;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;

public class ServerDataAction implements AntsAction<RequestResponseProto> {
    private static final Logger LOG = LoggerFactory.getLogger(ServerDataAction.class);

    private DataCacheManager dataCache;

    public ServerDataAction(DataCacheManager dataCache) {
        this.dataCache = dataCache;
    }

    @Override
    public void dealData(ChannelHandlerContext ctx, RequestResponseProto msg) throws Exception {
        LOG.info("datas");
        DatasRequestProto datasRequest = msg.getDatasRequest();
        int type = msg.getDatasRequest().getType();
        if (type == 1) { // type等于1，为agent的请求
            dataCache.putCache(datasRequest.getJobName(), datasRequest.getTimestamp(), datasRequest.getData());

        } else if (type == 0) { // type等于0，为client的请求
            LOG.info("receive a client message!");
            // 解析jsondata
            String result = "result";
            ByteBuf responseContent = Unpooled.wrappedBuffer(result.getBytes());
            FullHttpResponse response = HttpUtil.constructResponse(AntsHeaders.CONTENT_JSON, responseContent);
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void dealIdleStateEvent(ChannelHandlerContext ctx, Object evt) throws Exception {

    }

    @Override
    public void dealexceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

    }

    @Override
    public void close() throws Exception {
        // TODO Auto-generated method stub

    }

}
