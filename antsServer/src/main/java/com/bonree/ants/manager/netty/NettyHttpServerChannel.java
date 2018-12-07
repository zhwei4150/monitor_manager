package com.bonree.ants.manager.netty;

import java.util.concurrent.TimeUnit;

import com.bonree.ants.manager.action.ActionGroup;
import com.bonree.ants.manager.netty.handler.NettyHttpServerHandler;
import com.bonree.ants.manager.proto.RequestResponseProto;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

/*******************************************************************************
 * 版权信息：博睿宏远科技发展有限公司
 * Copyright: Copyright (c) 2007博睿宏远科技发展有限公司,Inc.All Rights Reserved.
 * 
 * @date 2018年10月29日 下午2:40:53
 * @Author: <a href=mailto:weizheng@bonree.com>魏征</a>
 * @Description: 
 ******************************************************************************/
public class NettyHttpServerChannel extends ChannelInitializer<SocketChannel> {

    private final int maxHttpContentLength;
    private ActionGroup<RequestResponseProto> actions;

    public NettyHttpServerChannel(int maxHttpContentLength, ActionGroup<RequestResponseProto> actions) {

        this.maxHttpContentLength = maxHttpContentLength;
        this.actions = actions;

    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new IdleStateHandler(5, 0, 0, TimeUnit.SECONDS));
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(maxHttpContentLength));
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new NettyHttpServerHandler(actions));

    }

}
