package com.bonree.ants.manager.plugins.syshelper;

import java.util.Map;

public interface SigarNetGather {
    SigarNetGather setNextor(SigarNetGather sigarNetGather);
    Map<String,Object> gatherNetStat(String devName);
}
