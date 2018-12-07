package com.bonree.ants.manager.netty;

import java.util.concurrent.TimeUnit;

import com.bonree.ants.manager.action.ActionGroup;
import com.bonree.ants.manager.netty.handler.NettyHttpClientHandler;
import com.bonree.ants.manager.proto.RequestResponseProto;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

public class NettyHttpClientChannel extends ChannelInitializer<SocketChannel> {

    private final int maxHttpContentLength;
    private ActionGroup<RequestResponseProto> actions;


    public NettyHttpClientChannel(int maxHttpContentLength, ActionGroup<RequestResponseProto> actions) {
        this.maxHttpContentLength = maxHttpContentLength;
        this.actions = actions;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new IdleStateHandler(5, 2, 0, TimeUnit.SECONDS));
        pipeline.addLast(new HttpClientCodec());
        pipeline.addLast(new HttpObjectAggregator(maxHttpContentLength));
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new NettyHttpClientHandler(actions));
    }

}
