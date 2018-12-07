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

public class KafkaConsumerTask extends NonTaskPipe<Map<String, Object>> {

    private final static Logger LOG = LoggerFactory.getLogger(KafkaConsumerTask.class);

    private final AntsKafkaConsumer consumer;
    private final Object config;
    private final String taskName;
    private final ExecutorService executor;

    public KafkaConsumerTask(String taskName, Object config, AntsKafkaConsumer consumer) {
        this.taskName = taskName;
        this.config = config;
        this.consumer = consumer;
        executor = Executors.newSingleThreadExecutor(r -> new Thread(r, taskName + "-kafka-consumer-" + Thread
                .currentThread().getId()));
    }

    public String getTaskName() {
        return taskName;
    }

    @Override
    public void init() throws Exception {
        consumer.init(config);
    }

    @Override
    public void start() throws Exception {
        executor.execute(() -> {
            try {
                consumer.startConsumer(config);
            } catch (Exception e) {
                LOG.error("failed to consumer kafka! job:{},error:", taskName, e);
            }
        });
    }

    @Override
    public void stop() throws Exception {
        consumer.stopConsumer();
        executor.awaitTermination(1, TimeUnit.SECONDS);
        executor.shutdown();
    }

    @Override
    public MessageTaker<Map<String, Object>> taker() {
        return consumer.taker();
    }
    
    

}
