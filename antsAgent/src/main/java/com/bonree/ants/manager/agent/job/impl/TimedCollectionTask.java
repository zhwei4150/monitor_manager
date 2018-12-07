package com.bonree.ants.manager.agent.job.impl;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bonree.ants.manager.agent.job.QueueTaskPipe;
import com.bonree.ants.manager.job.DataCollection;

/*******************************************************************************
 * 版权信息：博睿宏远科技发展有限公司
 * Copyright: Copyright (c) 2007博睿宏远科技发展有限公司,Inc.All Rights Reserved.
 * 
 * @date 2018年11月22日 上午10:28:11
 * @Author: <a href=mailto:weizheng@bonree.com>魏征</a>
 * @Description: 定时收集任务
 ******************************************************************************/
public class TimedCollectionTask extends QueueTaskPipe<Map<String, Object>> {

    private static final Logger LOG = LoggerFactory.getLogger(TimedCollectionTask.class);

    private final Object collectionProperty;
    private final DataCollection dataCollection;

    private ScheduledExecutorService scheduledService;

    private final String taskName;

    public TimedCollectionTask(String taskName, Object collectionProperty, DataCollection dataCollection) {
        this.taskName = taskName;
        this.collectionProperty = collectionProperty;
        this.dataCollection = dataCollection;
        scheduledService = Executors
                .newScheduledThreadPool(1, r -> new Thread(r, taskName + "-collection-timed-" + Thread.currentThread()
                        .getId()));
    }

    @Override
    public void start() throws Exception {

        scheduledService.scheduleWithFixedDelay(() -> {
            try {
                Map<String, Object> term = dataCollection.collect(collectionProperty);
                queue.put(term);
            } catch (Exception e) {
                LOG.error("failed start collection task! job:{},error:", taskName, e);
            }

        }, 1, 5, TimeUnit.SECONDS);

    }

    @Override
    public void stop() throws Exception {
        dataCollection.stop(collectionProperty);
        scheduledService.shutdown();
    }

    @Override
    public void init() throws Exception {
        dataCollection.init(collectionProperty);

    }

}
