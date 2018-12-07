package com.bonree.ants.manager.plugins.syshelper.impl;

import com.bonree.ants.manager.plugins.syshelper.SysGatherInterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseGatherHandler implements SysGatherInterface {
    protected SysGatherInterface nextor;
    protected List<String>       type = null;
    public BaseGatherHandler(List<String> type){
        this.type = type;
    }
    @Override
    public SysGatherInterface setNextInterface(SysGatherInterface nextor) {
        this.nextor = nextor;
        return nextor;
    }

    @Override
    public Map<String, Object> gatherAllInfo() throws Exception {
        Map<String,Object> map = new HashMap<>();
        if(nextor != null){
            Map<String,Object> nextMap = nextor.gatherAllInfo();
            if(nextMap != null){
                map.putAll(nextMap);
            }
        }
        Map<String,Object> cMap = gatherInfo();
        if(cMap != null){
            map.putAll(cMap);
        }
        return map;
    }
    @Override
    public void closeAll() throws Exception{
        if(this.nextor != null){
            this.nextor.closeAll();
        }
        this.close();

    }

}
