package com.bonree.ants.manager.plugins.gather;

import com.bonree.ants.manager.job.DataCollection;

import java.util.Map;

public class GatherStorm implements DataCollection{
    @Override
    public void init(Object taskConfig) throws Exception{

    }

    @Override
    public Map<String, Object> collect(Object taskConfig) throws Exception{
        return null;
    }

    @Override
    public void stop(Object taskConfig) throws Exception{

    }
}
