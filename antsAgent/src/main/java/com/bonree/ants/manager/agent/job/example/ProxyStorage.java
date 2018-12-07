package com.bonree.ants.manager.agent.job.example;

import java.util.List;
import java.util.Map;

import com.bonree.ants.manager.action.AntsAction;
import com.bonree.ants.manager.cache.HostNodeManager;
import com.bonree.ants.manager.job.DataStorage;
import com.bonree.ants.manager.node.ServerHostNode;
import com.bonree.ants.manager.proto.DatasRequestProto;
import com.bonree.ants.manager.type.AntsHeaders;
import com.bonree.ants.manager.util.JsonUtil;
import com.bonree.ants.manager.util.ProtoBufUtil;
import com.bonree.ants.manager.util.UriUtil;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;

public class ProxyStorage implements DataStorage<ProxyConfig> {

    private HostNodeManager<ServerHostNode> nodeManager;

    @Override
    public void init(ProxyConfig storageConfig) throws Exception {
        nodeManager = HostNodeManager.getServerManager();

    }

    @Override
    public void storage(Map<String, Object> term, ProxyConfig storageConfig) throws Exception {
        List<Channel> channels = nodeManager.listHostChannels();
        DatasRequestProto req = new DatasRequestProto();
        req.setJobName(storageConfig.getJobName());
        req.setTimestamp(System.currentTimeMillis() / 1000);
        req.setData(JsonUtil.toJsonString(term));
        byte[] content = ProtoBufUtil.serialize(req);
        ByteBuf body = Unpooled.wrappedBuffer(content);
        // 发送数据
        channels.stream().forEach(channel -> {
            FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, UriUtil
                    .makeUri(AntsAction.DATAS), body);

            request.headers().set(HttpHeaderNames.CONTENT_TYPE, AntsHeaders.CONTENT_PROTO);
            request.headers().set(HttpHeaderNames.CONTENT_LENGTH, body.readableBytes());
            channel.writeAndFlush(request);
        });
    }

    @Override
    public void stop(ProxyConfig storageConfig) throws Exception {

    }

}
