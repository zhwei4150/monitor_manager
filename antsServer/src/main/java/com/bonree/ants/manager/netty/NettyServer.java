package com.bonree.ants.manager.netty;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bonree.ants.manager.config.NettyConfig;
import com.bonree.ants.manager.config.ServerConfig;
import com.bonree.ants.manager.netty.process.LifeCycle;
import com.bonree.ants.manager.node.ServerHostNode;
import com.bonree.ants.manager.server.action.ServerActionGroup;
import com.bonree.ants.manager.util.NodeUtil;
import com.google.common.base.Preconditions;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer implements LifeCycle {
    private final static Logger LOG = LoggerFactory.getLogger(NettyServer.class);
    private ServerConfig serverConfig;
    private NettyConfig nettyConfig;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workGroup;
    private ServerActionGroup actions;

    public NettyServer(NettyConfig config, ServerConfig serverConfig, ServerActionGroup actions) {
        this.nettyConfig = Preconditions.checkNotNull(config);
        this.serverConfig = Preconditions.checkNotNull(serverConfig);
        this.actions = Preconditions.checkNotNull(actions);
        this.bossGroup = new NioEventLoopGroup();
        this.workGroup = new NioEventLoopGroup();
    }

    @Override
    public void start() throws Exception {

        InetSocketAddress address = (nettyConfig.getHost() == null ? new InetSocketAddress(nettyConfig
                .getPort()) : new InetSocketAddress(nettyConfig.getHost(), nettyConfig.getPort()));
        ServerHostNode serverNode = NodeUtil.parseInetAddress(address, ServerHostNode.class);
        serverNode.setTimeMillis(System.currentTimeMillis());


        ChannelInitializer<SocketChannel> channelInitializer = new NettyHttpServerChannel(nettyConfig
                .getMaxHttpContentLength(), actions);

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workGroup).channel(NioServerSocketChannel.class)
                .childHandler(channelInitializer).option(ChannelOption.SO_BACKLOG, nettyConfig.getBacklog())
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, nettyConfig.getConnectTimeoutMillis())
                .childOption(ChannelOption.SO_KEEPALIVE, nettyConfig.isKeepAlive())
                // .childOption(ChannelOption.TCP_NODELAY, nettyServerConfig.isTcpNoDelay())
                .childOption(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator())
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

        serverBootstrap.bind(address).sync();
    }

    @Override
    public void stop() throws Exception {
        workGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }

}
