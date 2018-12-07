package com.bonree.ants.manager.common.util;

import java.net.InetSocketAddress;
import org.junit.Assert;

import org.junit.Test;

import com.bonree.ants.manager.cache.HostNodeManager;
import com.bonree.ants.manager.node.HostNode;
import com.bonree.ants.manager.node.ServerHostNode;
import com.bonree.ants.manager.util.NodeUtil;

public class NodeUtilTest {

    @Test
    public void testParseInet() throws Exception {
        InetSocketAddress address = new InetSocketAddress("127.0.0.1", 2121);
        ServerHostNode node = NodeUtil.parseInetAddress(address, ServerHostNode.class);
        System.out.println(node);
    }

    @Test
    public void testMinimumNode() {
        HostNodeManager<ServerHostNode> cacheNodes = HostNodeManager.getServerManager();
        ServerHostNode r_node1 = NodeUtil.minimumNode(cacheNodes.listHostNodes());
        Assert.assertNull(r_node1);

        ServerHostNode node1 = new ServerHostNode("192.168.4.2", 5567);
        cacheNodes.addHostNode(node1);
        ServerHostNode r_node2 = NodeUtil.minimumNode(cacheNodes.listHostNodes());
        Assert.assertNull(r_node2);
        long beginTime = System.currentTimeMillis();
        node1.setTimeMillis(beginTime);

        ServerHostNode node2 = new ServerHostNode("192.168.4.3", 5567);
        cacheNodes.addHostNode(node2);
        ServerHostNode r_node3 = NodeUtil.minimumNode(cacheNodes.listHostNodes());
        Assert.assertEquals(node1, r_node3);

        ServerHostNode node3 = new ServerHostNode("192.168.4.4", 5567);
        node3.setTimeMillis(beginTime - 10000);
        cacheNodes.addHostNode(node3);
        ServerHostNode r_node4 = NodeUtil.minimumNode(cacheNodes.listHostNodes());
        System.out.println(r_node4);
        Assert.assertEquals(node3, r_node4);

    }

    @Test
    public void testNodeEq() {
        HostNode node1 = new HostNode("192.168.4.2", 5556);
        HostNode node2 = new HostNode("192.168.4.2", 5556);
        Assert.assertEquals(true, node1.equals(node2));
    }

}
