package com.bonree.ants.manager.common.util;


import com.bonree.ants.manager.config.ServerConfig;
import com.bonree.ants.manager.util.ConfigUtil;

public class ConfigUtilTest {

    public void testLoadServerConifg() throws Exception {
        
    }
    
    public static void main(String[] args) throws Exception {
//        String configFileName = "D:/eclipse-workspace/j2ee/bonree_ants_manager/conf/server.yml";
//        ServerConfig serverConfig = ConfigUtil.loadServerConifg(configFileName);
        String str= "prop.broker.servers";
        
        System.out.println(ConfigUtil.trimKafkaConfig(str));
    }
    
}
