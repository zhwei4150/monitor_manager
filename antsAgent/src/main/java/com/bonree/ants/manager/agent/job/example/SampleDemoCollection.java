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
 * @date 2018年11月22日 上午10:16:09
 * @Author: <a href=mailto:weizheng@bonree.com>魏征</a>
 * @Description: demo
 ******************************************************************************/
public class SampleDemoCollection implements DataCollection<SampleJobConfig> {

    private final static Logger LOG = LoggerFactory.getLogger(SampleDemoCollection.class);

    public SampleDemoCollection() {
    }

    @Override
    public void init(SampleJobConfig taskConfig) {
        LOG.info("init collection task:{}", taskConfig);
    }

    @Override
    public Map<String, Object> collect(SampleJobConfig taskConfig) {
        LOG.info("start to collect!");
        Map<String, Object> record = new HashMap<>();
        record.put("test", "test");
        return record;
    }

    @Override
    public void stop(SampleJobConfig taskConfig) {
        LOG.info("stop collection task:{}", taskConfig);
    }

}
