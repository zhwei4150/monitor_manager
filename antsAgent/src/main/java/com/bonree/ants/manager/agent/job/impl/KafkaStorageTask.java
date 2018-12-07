package com.bonree.ants.manager.agent.job.impl;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bonree.ants.manager.agent.job.AntsKafkaConsumer;
import com.bonree.ants.manager.agent.job.MessageTaker;
import com.bonree.ants.manager.agent.job.NonTaskPipe;
import com.bonree.ants.manager.agent.job.QueueTaskPipe;
import com.bonree.ants.manager.job.DataStorage;

/*******************************************************************************
 * 版权信息：博睿宏远科技发展有限公司
 * Copyright: Copyright (c) 2007博睿宏远科技发展有限公司,Inc.All Rights Reserved.
 * 
 * @date 2018年11月22日 上午10:13:33
 * @Author: <a href=mailto:weizheng@bonree.com>魏征</a>
 * @Description: kafka数据生产任务
 ******************************************************************************/
public class KafkaStorageTask extends NonTaskPipe<Map<String, Object>> {

    private final static Logger LOG = LoggerFactory.getLogger(KafkaStorageTask.class);

    private AntsKafkaConsumer consumer;

    private final Object storageConfig;

    private final DataStorage dataStorage;

    private boolean closeFlag = false;

    private final String taskName;

    private final ExecutorService executor;

    public KafkaStorageTask(String taskName, Object storageConfig, DataStorage dataStorage, AntsKafkaConsumer consumer) {
        this.taskName = taskName;
        this.storageConfig = storageConfig;
        this.dataStorage = dataStorage;
        this.consumer = consumer;
        executor = Executors.newFixedThreadPool(2, r -> new Thread(r, taskName + "-kafka-storage-" + Thread
                .currentThread().getId()));

    }

    @Override
    public void init() throws Exception {
        consumer.init(storageConfig);
        dataStorage.init(storageConfig);
    }

    @Override
    public void start() throws Exception {
        executor.execute(() -> {
            try {
                while (!closeFlag) {
                    Map<String, Object> term = upTaker.take(500, TimeUnit.MILLISECONDS);
                    if (term != null) {
                        dataStorage.storage(term, storageConfig);
                    }
                }
            } catch (Exception e) {
                LOG.error("failed to start storage task! job:{},error:", taskName, e);
            }
        });

        executor.execute(() -> {
            try {
                consumer.startConsumer(storageConfig);
            } catch (Exception e) {
                LOG.error("failed to consumer kafka! job:{},error:", taskName, e);
            }
        });

    }

    @Override
    public void stop() throws Exception {

        closeFlag = true;
        dataStorage.stop(storageConfig);

        consumer.stopConsumer();
        executor.awaitTermination(1, TimeUnit.SECONDS);
        executor.shutdown();
    }

    @Override
    public MessageTaker<Map<String, Object>> taker() {
        return consumer.taker();
    }
}
