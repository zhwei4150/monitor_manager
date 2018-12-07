package com.bonree.ants.manager.cache;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.bonree.ants.manager.node.AgentHostNode;
import com.bonree.ants.manager.node.HostNode;
import com.bonree.ants.manager.node.ServerHostNode;

import io.netty.channel.Channel;

/*******************************************************************************
 * 版权信息：博睿宏远科技发展有限公司
 * Copyright: Copyright (c) 2007博睿宏远科技发展有限公司,Inc.All Rights Reserved.
 * 
 * @date 2018年11月12日 上午10:37:50
 * @Author: <a href=mailto:weizheng@bonree.com>魏征</a>
 * @Description: 维护node的状态
 ******************************************************************************/
public class HostNodeManager<T extends HostNode> {

    private Map<String, T> cacheMap;

    private Map<String, Channel> channels;

    private Random random = new Random();

    private static HostNodeManager<ServerHostNode> serverManager;
    private static HostNodeManager<AgentHostNode> agentManager;

    public synchronized static HostNodeManager<ServerHostNode> getServerManager() {
        if (serverManager == null) {
            serverManager = new HostNodeManager<>();
        }
        return serverManager;
    }

    public synchronized static HostNodeManager<AgentHostNode> getAgentManager() {
        if (agentManager == null) {
            agentManager = new HostNodeManager<>();
        }
        return agentManager;
    }

    private HostNodeManager() {
        cacheMap = new ConcurrentHashMap<>();
        channels = new ConcurrentHashMap<>();
    }

    public void addHostChannel(HostNode node, Channel channel) {
        if (node == null) {
            return;
        }
        channels.put(node.geneKey(), channel);
    }

    public void addHostNode(T node) {
        if (node == null) {
            return;
        }
        cacheMap.put(node.geneKey(), node);
    }

    public void removeHostChannel(HostNode node) {
        if (node == null) {
            return;
        }
        channels.remove(node.geneKey());
    }

    public void removeHostNode(T node) {
        if (node == null) {
            return;
        }
        cacheMap.remove(node.geneKey());
    }

    public T getRandomHostNode() {
        List<T> nodes = listHostNodes();
        if (!nodes.isEmpty()) {
            int n = random.nextInt(nodes.size());
            return nodes.get(n);
        }
        return null;
    }

    public T getHostNode(String hostName) {
        Optional<String> entityKey = cacheMap.keySet().stream().filter(key -> key.contains(hostName)).findFirst();
        return entityKey.isPresent() ? cacheMap.get(entityKey.get()) : null;
    }

    public T getHostNode(HostNode node) {
        return node != null ? cacheMap.get(node.geneKey()) : null;
    }

    public Channel getHostChannel(HostNode node) {
        return node != null ? channels.get(node.geneKey()) : null;
    }

    public List<T> listHostNodes() {
        return cacheMap.keySet().stream().map(x -> cacheMap.get(x)).collect(Collectors.toList());
    }

    public List<Channel> listHostChannels() {
        return channels.keySet().stream().map(x -> channels.get(x)).collect(Collectors.toList());
    }

}
