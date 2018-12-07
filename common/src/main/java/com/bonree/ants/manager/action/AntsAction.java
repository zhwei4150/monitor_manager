package com.bonree.ants.manager.action;

import io.netty.channel.ChannelHandlerContext;

public interface AntsAction<T> {
    String BEATS = "beats";
    String DATAS = "datas";
    String COMMANDS = "commands";
    String FILES = "files";

    default public void dealActive(ChannelHandlerContext ctx) throws Exception {

    }

    public void dealData(ChannelHandlerContext ctx, T msg) throws Exception;

    public void dealIdleStateEvent(ChannelHandlerContext ctx, Object evt) throws Exception;

    public void dealexceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception;

    public void close() throws Exception;

}
