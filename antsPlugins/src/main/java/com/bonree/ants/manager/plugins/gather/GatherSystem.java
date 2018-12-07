package com.bonree.ants.manager.plugins.gather;

import com.bonree.ants.manager.job.DataCollection;
import com.bonree.ants.manager.plugins.syshelper.SysGatherInterface;
import com.bonree.ants.manager.plugins.syshelper.impl.*;
import org.hyperic.sigar.Sigar;

import java.util.Map;

public class GatherSystem implements DataCollection<Map<String, Object>> {
    public static final String PropertyPath = "siger.lib.path";
    public static final String CPU = "cpu";
    public static final String MEMORY = "memory";
    public static final String NET = "net";
    public static final String PARTITION = "partition";
    public SysGatherInterface local = null;

    @Override
    public void init(Map<String, Object> taskConfig) throws Exception {
        SystemGatherBuilder builder = new SystemGatherBuilder(taskConfig);
        local = builder.build();
    }

    @Override
    public Map<String, Object> collect(Map<String, Object> taskConfig) throws Exception {
        return local.gatherAllInfo();
    }

    @Override
    public void stop(Map<String, Object> taskConfig) throws Exception {
        local.closeAll();
    }
}
