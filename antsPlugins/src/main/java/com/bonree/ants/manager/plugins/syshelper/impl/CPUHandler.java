package com.bonree.ants.manager.plugins.syshelper.impl;

import org.hyperic.sigar.Cpu;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import java.util.*;

/***
 * CPUHandler 配置信息 CORE，PER_RATE,RATE
 */
public class CPUHandler extends BaseGatherHandler {
    private Sigar sigar = null;
    public static enum CPU {
        CORE("CPU_CORE"),
        RATE("CPU_RATE"),
        PER_RATE("PER_CPU_RATE");
        private String str;
        CPU(String str){
            this.str = str;
        }
        public static Collection<String> getCollection(){
            CPU[] cpus=  CPU.values();
            List<String> arrays = new ArrayList<>();
            for(CPU cpu : cpus){
                arrays.add(cpu.getStrValue());
            }
            return arrays;
        }
        public String getStrValue(){
            return this.str;
        }
    };
    public CPUHandler(List<String> type, Sigar sigar){
        super(type);
        this.sigar = sigar;
    }
    @Override
    public Map<String, Object> gatherInfo() throws Exception {
        Map<String,Object> collection = new HashMap<>();
        if(type.contains(CPU.CORE.getStrValue())){
            collection.put(CPU.CORE.getStrValue(), gatherCpuCoreCount());
        }
        if(type.contains(CPU.RATE.getStrValue())){
            collection.put(CPU.RATE.getStrValue(), gatherCpuRate(sigar.getCpu()));
        }
        if(type.contains(CPU.PER_RATE.getStrValue())){
            collection.put(CPU.RATE.getStrValue(), gatherCpuRate(sigar.getCpu()));
        }
        return collection;
    }

    @Override
    public void close() {
        this.sigar.close();
    }

    /**
     * 概述：采集cpus使用率
     * 比较特殊的是CPU总使用率的计算(util),目前的算法是: util = 1 - idle - iowait - steal
     * @return
     * @throws SigarException
     * @author <a href=mailto:zhucg@bonree.com>朱成岗</a>
     */
    public double gatherCpuRate(Cpu cpu) throws SigarException {
        cpu.gather(sigar);
        long userTime = cpu.getUser();
        long sysTime = cpu.getSys();
        long niceTime = cpu.getNice();
        long idleTime = cpu.getIdle();
        long iowaitTime = cpu.getWait();
        long irqTime = cpu.getIrq();
        long softirqTime = cpu.getSoftIrq();
        long stlTime = cpu.getStolen();
        long cpuTotalTime = userTime + sysTime+niceTime+idleTime+iowaitTime+irqTime+softirqTime+stlTime;
        double idleRate = (double) idleTime/cpuTotalTime;
        double iowaitRate = (double) iowaitTime /cpuTotalTime;
        double stlRate = (double) stlTime/cpuTotalTime;
        return (1 - (idleRate+iowaitRate+stlRate));
    }

    /**
     * 概述：获取cpu核心数
     * @return
     * @throws SigarException
     * @user <a href=mailto:zhucg@bonree.com>朱成岗</a>
     */
    public int gatherCpuCoreCount()throws SigarException{
        return sigar.getCpuList().length;
    }

    public List<Double> gatherPerCpuRate() throws SigarException {
        List<Cpu> cpus = Arrays.asList(sigar.getCpuList());
        List<Double> perCpuRate = new ArrayList<>();
        for(Cpu cpu :cpus){
            perCpuRate.add(gatherCpuRate(cpu));
        }
        return perCpuRate;
    }
}
