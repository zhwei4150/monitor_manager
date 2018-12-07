package com.bonree.ants.manager.plugins.syshelper.impl;

import com.bonree.ants.manager.plugins.utils.NetUtils;
import com.bonree.ants.manager.util.BrStringUtils;
import org.hyperic.sigar.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class NetHandler extends BaseGatherHandler {
    private static Logger  LOG     = LoggerFactory.getLogger(NetHandler.class);
    private        Sigar   sigar   = null;
    private Map<String,Long> sendMap = new HashMap<>();
    private Map<String,Long> acceptMap = new HashMap<>();
    private Map<String,Long> ioMap = new HashMap<>();
    public static enum NET {
        IP("IP"),
        DEVICE_NAME("DEVICE_NAME"),
        SEND_SPEED("NET_SEND_SPEED"),
        ACCEPT_SPEED("NET_ACCEPT_SPEED"),
        // TODO: 网卡采集的指标可进一步完善
        // TODO: 网卡速度获取指标未设置
        SPEED("NET_IO");
        private String str;
        NET(String str){
            this.str = str;
        }
        public String getStrValue(){
            return str;
        }
        public static Collection<String> getCollection(){
            NET[] nets = NET.values();
            List<String> list = new ArrayList<>();
            for(NET net : nets){
                list.add(net.getStrValue());
            }
            return list;
        }
    };
    public NetHandler(List<String> type,Sigar sigar){
        super(type);
        this.sigar = sigar;
    }
    @Override
    public Map<String, Object> gatherInfo() throws Exception {
        return gatherNetInfos(this.type);
    }

    @Override
    public void close() {
        this.sigar.close();
    }

    /**
     * 采集网卡状态信息
     * TODO：重构为职责链模式，减少ifelse判断逻辑
     * @param gatherEles
     * @return
     * @throws Exception
     */
    public Map<String,Object> gatherNetInfos(Collection<String> gatherEles)throws Exception{
        Map<String,Object> map = new HashMap<>();
        List<String> netDevNames = Arrays.asList(sigar.getNetInterfaceList());
        NetInterfaceConfig netInterfaceConfig = null;
        String ip = null;
        NetInterfaceStat stat = null;
        for(String netDevName : netDevNames){
            if(BrStringUtils.isEmpty(netDevName)){
                continue;
            }
            netInterfaceConfig = sigar.getNetInterfaceConfig(netDevName);
            // 1.过滤非法的ip
            ip = netInterfaceConfig.getAddress();
            if(NetUtils.filterIp(ip)){
                continue;
            }
            // 2.过滤网卡不存在的
            if((netInterfaceConfig.getFlags() & 1L) <=0L){
                continue;
            }
            if(this.type.contains(NET.DEVICE_NAME.getStrValue())){
                if(!map.containsKey(NET.DEVICE_NAME.getStrValue())){
                    map.put(NET.DEVICE_NAME.getStrValue(), new ArrayList<>());
                }
                ((List<Object>)map.get(NET.DEVICE_NAME.getStrValue())).add(netDevName);
            }
            if(this.type.contains(NET.IP.getStrValue())){
                if(!map.containsKey(NET.IP.getStrValue())){
                    map.put(NET.IP.getStrValue(), new ArrayList<>());
                }
                ((List<Object>)map.get(NET.IP.getStrValue())).add(ip);
            }
            stat = sigar.getNetInterfaceStat(netDevName);
            if(this.type.contains(NET.SEND_SPEED.getStrValue())){
                if(!map.containsKey(NET.SEND_SPEED.getStrValue())){
                    map.put(NET.SEND_SPEED.getStrValue(), new ArrayList<Long>());
                }
                long send = stat.getTxBytes();
                if(sendMap.containsKey(netDevName)){
                    long pre = sendMap.get(netDevName);
                    ((List<Long>) map.get(NET.SEND_SPEED.getStrValue())).add((send - pre));
                }else{
                    ((List<Long>) map.get(NET.SEND_SPEED.getStrValue())).add(0L);
                }
                sendMap.put(netDevName,send);

            }
            if(this.type.contains(NET.ACCEPT_SPEED.getStrValue())){
                if(!map.containsKey(NET.ACCEPT_SPEED.getStrValue())){
                    map.put(NET.ACCEPT_SPEED.getStrValue(), new ArrayList<Long>());
                }
                long send = stat.getRxBytes();
                if(acceptMap.containsKey(netDevName)){
                    long pre = acceptMap.get(netDevName);
                    ((List<Long>) map.get(NET.ACCEPT_SPEED.getStrValue())).add((send - pre));
                }else{
                    ((List<Long>) map.get(NET.ACCEPT_SPEED.getStrValue())).add(0L);
                }
                acceptMap.put(netDevName,send);

            }
        }
        return map;
    }
}
