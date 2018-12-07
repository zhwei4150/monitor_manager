package com.bonree.ants.manager.plugins.syshelper.impl;

import org.hyperic.sigar.Sigar;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.Map;

public class DiskHandlerTest {
    @Test
    @DisplayName("采集硬盘信息")
    public void gatherDisk(){
        ConfigReadHelper.initClassPath();
        Sigar sigar = new Sigar();
        DiskHandler diskHandler = new DiskHandler(new ArrayList<>(DiskHandler.DISK.getCollection()),sigar);
        Map<String,Object> diskInfos = null;
        try {
            for(int i = 0; i<10 ;i++){
                diskInfos = diskHandler.gatherAllInfo();
                System.out.println(diskInfos);
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                diskHandler.closeAll();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
