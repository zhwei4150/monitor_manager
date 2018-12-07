package com.bonree.ants.manager.plugins.syshelper.impl;

import org.hyperic.sigar.Sigar;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.Map;

public class NetHandlerTest {
    @Test
    @DisplayName("测试网络信息采集")
    public void gatherNet(){
        ConfigReadHelper.initClassPath();
        Sigar sigar = new Sigar();
        NetHandler netHandler = new NetHandler(new ArrayList<>(NetHandler.NET.getCollection()),sigar);
        Map<String,Object> netInfos = null;
        try {
            for(int i = 0; i< 1;i++){

                netInfos = netHandler.gatherAllInfo();
                System.out.println(netInfos);
                Thread.sleep(1000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                netHandler.closeAll();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
