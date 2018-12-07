package com.bonree.ants.manager.agent.job.example;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bonree.ants.manager.job.DataCollection;

/*******************************************************************************
 * 版权信息：博睿宏远科技发展有限公司
 * Copyright: Copyright (c) 2007博睿宏远科技发展有限公司,Inc.All Rights Reserved.
 * 
 * @date 2018年11月22日 上午10:20:55
 * @Author: <a href=mailto:weizheng@bonree.com>魏征</a>
 * @Description: demo
 ******************************************************************************/
public class SampleMapCollection implements DataCollection<Map<String, String>> {
    private static final Logger LOG = LoggerFactory.getLogger(SampleMapCollection.class);

    @Override
    public void init(Map<String, String> taskConfig) throws Exception {
        LOG.info("init collection:{}", taskConfig);
    }

    @Override
    public Map<String, Object> collect(Map<String, String> taskConfig) throws Exception {
        Map<String, Object> term = new HashMap<>();
        term.put("test", "test");
        LOG.info("collection:{}", term);
        return term;
    }

    @Override
    public void stop(Map<String, String> taskConfig) throws Exception {
        LOG.info("stop collection");
    }

}
