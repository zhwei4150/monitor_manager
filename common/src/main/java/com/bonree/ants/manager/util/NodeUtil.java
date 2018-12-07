package com.bonree.ants.manager.util;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;

import com.bonree.ants.manager.node.HostNode;
import com.bonree.ants.manager.node.ServerHostNode;

/*******************************************************************************
 * 版权信息：博睿宏远科技发展有限公司
 * Copyright: Copyright (c) 2007博睿宏远科技发展有限公司,Inc.All Rights Reserved.
 * 
 * @date 2018年12月4日 上午10:19:24
 * @Author: <a href=mailto:weizheng@bonree.com>魏征</a>
 * @Description: Node工具类
 ******************************************************************************/
public class NodeUtil {

    /** 概述：返回HOST的地址和端口，这里统一使用IP地址进行通信
     * @param addrsss 
     * @param cls
     * @return
     * @throws Exception
     * @user <a href=mailto:weizheng@bonree.com>魏征</a>
     */
    public static <T extends HostNode> T parseInetAddress(SocketAddress addrsss, Class<T> cls) throws Exception {
        InetSocketAddress netAdress = (InetSocketAddress) addrsss;
        T node = cls.getConstructor(String.class, int.class).newInstance(netAdress.getAddress().getHostAddress(), netAdress.getPort());
        return node;
    }

    /** 概述：找出时间戳最小的server
     * @param nodes
     * @return
     * @user <a href=mailto:weizheng@bonree.com>魏征</a>
     */
    public static ServerHostNode minimumNode(List<ServerHostNode> nodes) {
        ServerHostNode node = nodes.stream().filter(n1 -> n1.getTimeMillis() != 0).min((n1, n2) -> n1
                .getTimeMillis() > n2.getTimeMillis() ? 1 : n1.getTimeMillis() == n2.getTimeMillis() ? 0 : -1)
                .orElse(null);
        return node;
    }

    public static boolean nodeEqual(HostNode node1, HostNode node2) {
        if (node1 == null || node2 == null) {
            return false;
        } else {
            return (node1.getHost().equals(node2.getHost()) && node1.getPort()==node2.getPort());
        }
    }

}
