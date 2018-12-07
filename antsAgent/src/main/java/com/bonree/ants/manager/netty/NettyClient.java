package com.bonree.ants.manager.netty;

import com.bonree.ants.manager.agent.action.AgentActionGroup;
import com.bonree.ants.manager.config.NettyConfig;
import com.bonree.ants.manager.netty.process.LifeCycle;
import com.google.common.base.Preconditions;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient implements LifeCycle {

    private Bootstrap bootstrap;
    private EventLoopGroup workGroup;
    private NettyConfig clientConfig;

    private ChannelInitializer<SocketChannel> channelInitializer;
    private AgentActionGroup actions;

    public NettyClient(NettyConfig clientConfig, AgentActionGroup hostManager) {
        this.clientConfig = Preconditions.checkNotNull(clientConfig);
        this.actions = Preconditions.checkNotNull(actions);
        this.workGroup = new NioEventLoopGroup();
    }

    @Override
    public void start() throws Exception {

        channelInitializer = new NettyHttpClientChannel(clientConfig.getMaxHttpContentLength(), actions);

        bootstrap = new Bootstrap();
        bootstrap.group(workGroup).channel(NioSocketChannel.class).handler(channelInitializer)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, clientConfig.getConnectTimeoutMillis())
                .option(ChannelOption.SO_KEEPALIVE, clientConfig.isKeepAlive())
                .option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator())
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        doConnect();
    }

    public void doConnect() {
        try {
            bootstrap.connect(clientConfig.getHost(), clientConfig.getPort()).sync();
        } catch (InterruptedException e) {
            // 需要重新连接
        }
    }

    public void reConnect() {

    }

    @Override
    public void stop() throws Exception {
        workGroup.shutdownGracefully();
    }

}
