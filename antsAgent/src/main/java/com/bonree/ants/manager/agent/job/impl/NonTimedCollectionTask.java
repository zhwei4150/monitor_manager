package com.bonree.ants.manager.agent.job.impl;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bonree.ants.manager.agent.job.QueueTaskPipe;
import com.bonree.ants.manager.job.DataCollection;

/*******************************************************************************
 * 版权信息：博睿宏远科技发展有限公司
 * Copyright: Copyright (c) 2007博睿宏远科技发展有限公司,Inc.All Rights Reserved.
 * 
 * @date 2018年11月22日 上午10:10:47
 * @Author: <a href=mailto:weizheng@bonree.com>魏征</a>
 * @Description: 非定时数据采集任务创建
 ******************************************************************************/
public class NonTimedCollectionTask extends QueueTaskPipe<Map<String, Object>> {

    private static final Logger LOG = LoggerFactory.getLogger(NonTimedCollectionTask.class);

    private final Object collectionProperty;

    private final DataCollection dataCollection;

    private ExecutorService executor;

    private final String taskName;

    private boolean closeFlag = false;

    public NonTimedCollectionTask(String taskName, Object collectionProperty, DataCollection dataCollection) {
        this.taskName = taskName;
        this.collectionProperty = collectionProperty;
        this.dataCollection = dataCollection;
        executor = Executors.newSingleThreadExecutor(r -> new Thread(r, taskName + "-collection-" + Thread
                .currentThread().getId()));

    }

    public Object getCollectionProperty() {
        return collectionProperty;
    }

    public DataCollection getDataCollection() {
        return dataCollection;
    }

    @Override
    public void start() throws Exception {

        executor.execute(() -> {
            try {
                while (!closeFlag) {
                    Map<String, Object> term = dataCollection.collect(collectionProperty);
                    queue.put(term);
                }

            } catch (Exception e) {
                LOG.error("failed collect data! job:{},error:", taskName, e);
            }
        });

    }

    @Override
    public void stop() throws Exception {
        closeFlag = true;
        dataCollection.stop(collectionProperty);
        executor.awaitTermination(500, TimeUnit.MILLISECONDS);
        executor.shutdown();
    }

    @Override
    public void init() throws Exception {
        dataCollection.init(collectionProperty);

    }

}
