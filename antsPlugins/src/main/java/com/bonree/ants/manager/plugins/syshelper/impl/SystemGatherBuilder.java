package com.bonree.ants.manager.plugins.syshelper.impl;

import com.bonree.ants.manager.plugins.syshelper.Builder;
import com.bonree.ants.manager.plugins.syshelper.SysGatherInterface;
import com.bonree.ants.manager.plugins.utils.LibUtils;
import org.apache.commons.lang3.StringUtils;
import org.hyperic.sigar.Sigar;

import java.util.*;

public class SystemGatherBuilder implements Builder {
    public static final String CPU_ELE    = "CPU";
    public static final String MEMORY_ELE = "MEMORY";
    public static final String NET_ELE    = "NET";
    public static final String DISK_ELE   = "DISK";
    public static final String LIB_ELE    = "libPath";

    private SysGatherInterface  cpuGather  = null;
    private SysGatherInterface  memGather  = null;
    private SysGatherInterface  netGather  = null;
    private SysGatherInterface  diskGather = null;
    private Map<String, Object> config     = null;

    public SystemGatherBuilder(Map<String, Object> config) {
        this.config = config;
    }
    public Map<String,Object> getConfig(){
        return this.config;
    }
    private void checkoutConfig(Collection<String> conifgs,Collection<String> standard,String confName) throws Exception{
        for(String conf : conifgs){
            if(standard.contains(conf)){
                continue;
            }
            throw new IllegalArgumentException(new StringBuilder().append("[conf error] ").append(confName).append(" config contain invaild value :[").append(conf).append("] !!!").toString());
        }
    }
    private SystemGatherBuilder buildPath(Map<String,Object> config)throws Exception{
        if(!config.containsKey(LIB_ELE)){
            return this;
        }
        String path = config.get(LIB_ELE).toString();
        LibUtils.loadLibraryPath(path);
        return this;
    }
    private SystemGatherBuilder buildCpu(Map<String, Object> config, Sigar sigar) throws Exception {
        if (!config.containsKey(CPU_ELE)) {
            return this;
        }
        Collection<String> cpuConfis = CPUHandler.CPU.getCollection();
        List<String> configInfos = (List<String>) config.get(CPU_ELE);
        checkoutConfig(configInfos,cpuConfis,CPU_ELE);
        this.cpuGather = new CPUHandler(configInfos,sigar);
        return this;
    }
    private SystemGatherBuilder buildMemory(Map<String,Object> config, Sigar sigar)throws Exception{
        if (!config.containsKey(MEMORY_ELE)) {
            return this;
        }
        Collection<String> cpuConfis = MemoryHandler.MEMORY.getCollection();
        List<String> configInfos = (List<String>) config.get(MEMORY_ELE);
        checkoutConfig(configInfos,cpuConfis,MEMORY_ELE);
        this.memGather = new MemoryHandler(configInfos,sigar);
        return this;
    }
    private SystemGatherBuilder buildNet(Map<String,Object> config, Sigar sigar)throws Exception{
        if (!config.containsKey(NET_ELE)) {
            return this;
        }
        Collection<String> cpuConfis = NetHandler.NET.getCollection();
        List<String> configInfos = (List<String>) config.get(NET_ELE);
        checkoutConfig(configInfos,cpuConfis,NET_ELE);
        this.netGather = new NetHandler(configInfos,sigar);
        return this;
    }
    private SystemGatherBuilder buildDisk(Map<String,Object> config, Sigar sigar)throws Exception{
        if (!config.containsKey(DISK_ELE)) {
            return this;
        }
        Collection<String> cpuConfis = DiskHandler.DISK.getCollection();
        List<String> configInfos = (List<String>) config.get(DISK_ELE);
        checkoutConfig(configInfos,cpuConfis,DISK_ELE);
        this.diskGather = new DiskHandler(configInfos,sigar);
        return this;
    }
    private void init()throws Exception{
        buildPath(this.getConfig());
        Sigar sigar = new Sigar();
        buildCpu(this.config,sigar);
        buildMemory(this.config,sigar);
        buildNet(this.config,sigar);
        buildDisk(this.config,sigar);
    }


    @Override
    public SysGatherInterface build() throws Exception {
        init();
        SysGatherInterface father = null;
        SysGatherInterface child = null;
        if (cpuGather != null) {
            child = this.cpuGather;
            father = child;
        }
        if (memGather != null) {
            child = child == null ? this.memGather : child.setNextInterface(this.memGather);
            father = father == null ? child : father;
        }
        if (netGather != null) {
            child = child == null ? this.netGather : child.setNextInterface(this.netGather);
            father = father == null ? child : father;
        }
        if (diskGather != null) {
            child = child == null ? this.diskGather : child.setNextInterface(this.diskGather);
            father = father == null ? child : father;
        }
        return father;
    }
}
