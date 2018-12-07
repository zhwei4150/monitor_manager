package com.bonree.ants.manager.agent.job.example;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bonree.ants.manager.job.DataStorage;

/*******************************************************************************
 * 版权信息：博睿宏远科技发展有限公司
 * Copyright: Copyright (c) 2007博睿宏远科技发展有限公司,Inc.All Rights Reserved.
 * 
 * @date 2018年11月22日 上午10:16:16
 * @Author: <a href=mailto:weizheng@bonree.com>魏征</a>
 * @Description: demo
 ******************************************************************************/
public class SampleDemoStorage implements DataStorage<SampleStorageConfig> {

    private final static Logger LOG = LoggerFactory.getLogger(SampleDemoStorage.class);

    public SampleDemoStorage() {
    }

    @Override
    public void init(SampleStorageConfig storageProperty) throws Exception {
        LOG.info("init storage task:{}", storageProperty);

    }

    @Override
    public void storage(Map<String, Object> term, SampleStorageConfig storageConfig) {
        LOG.info("data:{}", term);
    }

    @Override
    public void stop(SampleStorageConfig storageConfig) {
    }

}
