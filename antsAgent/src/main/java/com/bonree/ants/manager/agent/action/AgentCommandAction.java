package com.bonree.ants.manager.agent.action;

import com.bonree.ants.manager.action.AntsAction;
import com.bonree.ants.manager.proto.RequestResponseProto;

import io.netty.channel.ChannelHandlerContext;

public class AgentCommandAction implements AntsAction<RequestResponseProto>{

    public AgentCommandAction() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void dealData(ChannelHandlerContext ctx, RequestResponseProto msg) throws Exception {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void dealIdleStateEvent(ChannelHandlerContext ctx, Object evt) throws Exception {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void dealexceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void close() throws Exception {
        // TODO Auto-generated method stub
        
    }

}
