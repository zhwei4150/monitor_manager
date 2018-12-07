package com.bonree.ants.manager.agent.job.example;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bonree.ants.manager.job.DataStorage;

/*******************************************************************************
 * 版权信息：博睿宏远科技发展有限公司
 * Copyright: Copyright (c) 2007博睿宏远科技发展有限公司,Inc.All Rights Reserved.
 * 
 * @date 2018年11月22日 上午10:23:29
 * @Author: <a href=mailto:weizheng@bonree.com>魏征</a>
 * @Description: demo
 ******************************************************************************/
public class SampleMapStorage implements DataStorage<Map<String, String>> {
    private static final Logger LOG = LoggerFactory.getLogger(SampleMapStorage.class);

    @Override
    public void init(Map<String, String> storageProperty) throws Exception {
        LOG.info("init storage:" + storageProperty);
    }

    @Override
    public void storage(Map<String, Object> term, Map<String, String> storageProperty) throws Exception {
        LOG.info("storage data:{}", term);
    }

    @Override
    public void stop(Map<String, String> storageProperty) throws Exception {
        LOG.info("stop storage");
    }

}
