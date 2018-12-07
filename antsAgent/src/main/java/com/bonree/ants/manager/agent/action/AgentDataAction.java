package com.bonree.ants.manager.agent.action;

import com.bonree.ants.manager.action.AntsAction;
import com.bonree.ants.manager.proto.RequestResponseProto;

import io.netty.channel.ChannelHandlerContext;

public class AgentDataAction implements AntsAction<RequestResponseProto> {

    public AgentDataAction() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void dealData(ChannelHandlerContext ctx, RequestResponseProto msg) throws Exception {

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
