package com.bonree.ants.manager.plugins.syshelper;

import com.bonree.ants.manager.plugins.common.Closable;

import java.util.Map;

public interface SysGatherInterface extends Closable {
    SysGatherInterface setNextInterface(SysGatherInterface nextor);
    Map<String,Object> gatherAllInfo() throws Exception;
    Map<String,Object> gatherInfo() throws Exception;
    void closeAll() throws Exception;
}
