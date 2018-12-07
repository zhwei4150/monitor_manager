package com.bonree.ants.manager.plugins.syshelper.impl;

import org.hyperic.sigar.Sigar;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.Map;

public class MemoryHanderTest {
    @Test
    @DisplayName("测试采集内存信息信息")
    public void gatherMemory(){
        ConfigReadHelper.initClassPath();
        Sigar sigar = new Sigar();
        MemoryHandler memory = new MemoryHandler(new ArrayList<>(MemoryHandler.MEMORY.getCollection()),sigar);
        try {
            Map<String,Object> memoryInfos = memory.gatherAllInfo();
            System.out.println(memoryInfos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
