package com.bonree.ants.manager.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bonree.ants.manager.config.NettyConfig;
import com.bonree.ants.manager.config.ServerConfig;
import com.bonree.ants.manager.netty.NettyServer;
import com.bonree.ants.manager.server.action.ServerActionGroup;
import com.bonree.ants.manager.util.ConfigUtil;

/*******************************************************************************
 * 版权信息：博睿宏远科技发展有限公司
 * Copyright: Copyright (c) 2007博睿宏远科技发展有限公司,Inc.All Rights Reserved.
 * 
 * @date 2018年11月9日 下午2:01:51
 * @Author: <a href=mailto:weizheng@bonree.com>魏征</a>
 * @Description: monitor server启动入口
 ******************************************************************************/
public class ServerBoot {

    private static final Logger LOG = LoggerFactory.getLogger(ServerBoot.class);

    public static void main(String[] args) {
        try {
            String configFileName = "D:/eclipse-workspace/j2ee/bonree_ants_manager/conf/server.yml";
            
            ServerConfig serverConfig = ConfigUtil.loadServerConifg(configFileName);

            NettyConfig nettyConfig = NettyConfig.newBuilder().setHost(serverConfig.getHost()).setPort(serverConfig
                    .getPort()).setKeepAlive(true).build();

            ServerActionGroup actions = ServerActionGroup.createActionGroup(serverConfig);

            NettyServer server = new NettyServer(nettyConfig, serverConfig, actions);

            server.start();
        } catch (Exception e) {
            LOG.error("start server error:{}", e);
        }
    }
}
