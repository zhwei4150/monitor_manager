package com.bonree.ants.manager.plugins.syshelper.impl;

import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import java.util.*;

public class MemoryHandler extends BaseGatherHandler {
    private Sigar sigar = null;
    private Mem mem = new Mem();
    public static enum MEMORY {
        SIZE("MEMORY_SIZE"),
        RATE("MEMORY_RATE"),
        USED_SIZE("MEMORY_USED_SIZE");
        private String str;
        MEMORY(String str){
            this.str = str;
        }
        public String getStrValue(){
            return str;
        }
        public static Collection<String> getCollection(){
            MEMORY[] memories = MEMORY.values();
            List<String> list = new ArrayList<>();
            for(MEMORY memory : memories){
                list.add(memory.getStrValue());
            }
            return list;
        }
    };
    public MemoryHandler(List<String> type, Sigar sigar){
        super(type);
        this.sigar = sigar;
    }
    @Override
    public Map<String, Object> gatherInfo() throws Exception {
        mem.gather(sigar);
        Map<String,Object> collections = new HashMap<>();
        if(type.contains(MEMORY.SIZE.getStrValue())){
            collections.put(MEMORY.SIZE.getStrValue(),gatherMemSize(mem));
        }
        if(type.contains(MEMORY.RATE.getStrValue())){
            collections.put(MEMORY.RATE.getStrValue(),gatherMemoryRate(mem));
        }
        if(type.contains(MEMORY.USED_SIZE.getStrValue())){
            collections.put(MEMORY.USED_SIZE.getStrValue(),gatherUsedMem(mem));
        }
        return collections;
    }

    @Override
    public void close() {
        this.sigar.close();
    }

    /**
     * 概述：获取内存大小
     * @return
     * @throws SigarException
     * @author <a href=mailto:zhucg@bonree.com>朱成岗</a>
     */
    public long gatherMemSize(Mem mem) throws SigarException {
        return mem.getTotal();
    }

    /**
     * 概述：采集内存使用率
     * util = (total - free - buff - cache) / total
     * @return
     * @throws SigarException
     * @user <a href=mailto:zhucg@bonree.com>朱成岗</a>
     */
    public double gatherMemoryRate(Mem mem){
        long  actUse = mem.getActualUsed();
        long total = mem.getTotal();
        double usageRate = (double) actUse / total;
        return usageRate;
    }

    /**
     * 概述:采集内存使用的大小
     * @param mem
     * @return
     */
    public long gatherUsedMem(Mem mem){
        return mem.getActualUsed();
    }
}
