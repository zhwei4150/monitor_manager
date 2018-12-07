package com.bonree.ants.manager.plugins.syshelper.impl;

import org.hyperic.sigar.Sigar;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.Map;

public class CPUHanderTest {
    @Test
    @DisplayName("测试采集cpu信息")
    public void gatherCPU001(){
        ConfigReadHelper.initClassPath();
        Sigar sigar = new Sigar();
        CPUHandler cpu = new CPUHandler(new ArrayList<>(CPUHandler.CPU.getCollection()),sigar);
        try {
            Map<String,Object> cpuInfos = cpu.gatherAllInfo();
            System.out.println(cpuInfos);
            Thread.sleep(1000);
            cpuInfos = cpu.gatherAllInfo();
            System.out.println(cpuInfos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
