package com.bonree.ants.manager.netty.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpChunkedInput;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.stream.ChunkedFile;

public class HttpClientHandler extends SimpleChannelInboundHandler<HttpMessage>{

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpMessage msg) throws Exception {
        
    }


}
