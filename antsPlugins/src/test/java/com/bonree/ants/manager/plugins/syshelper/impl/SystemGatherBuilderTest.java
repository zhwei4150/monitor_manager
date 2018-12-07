package com.bonree.ants.manager.plugins.syshelper.impl;

import com.bonree.ants.manager.plugins.syshelper.SysGatherInterface;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.HashMap;
import java.util.Map;

public class SystemGatherBuilderTest {
    @Test
    @DisplayName("测试采集程序创建功能")
    public void buildTest001(){
        Map<String, Object> configs = new HashMap<>();
        String path = Class.class.getClass().getResource("/").getPath();
        configs.put(SystemGatherBuilder.CPU_ELE,CPUHandler.CPU.getCollection());
        configs.put(SystemGatherBuilder.MEMORY_ELE,MemoryHandler.MEMORY.getCollection());
        configs.put(SystemGatherBuilder.NET_ELE,NetHandler.NET.getCollection());
        configs.put(SystemGatherBuilder.DISK_ELE,DiskHandler.DISK.getCollection());
        configs.put(SystemGatherBuilder.LIB_ELE,path+"lib/") ;
        System.out.println(configs);
        try {
            SystemGatherBuilder builder = new SystemGatherBuilder(configs);
            SysGatherInterface gather = builder.build();
            Map<String,Object> gatherInfo = gather.gatherAllInfo();
            System.out.println(gatherInfo);
            gatherInfo = gather.gatherAllInfo();
            System.out.println(gatherInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
