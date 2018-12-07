package com.bonree.ants.manager.agent.job.example;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bonree.ants.manager.agent.job.AntsKafkaConsumer;
import com.bonree.ants.manager.agent.job.MessageTaker;
import com.bonree.ants.manager.agent.job.impl.QueueMessagTaker;
import com.bonree.ants.manager.util.ConfigUtil;
import com.bonree.ants.manager.util.JsonUtil;
import com.google.common.collect.Lists;

/*******************************************************************************
 * 版权信息：博睿宏远科技发展有限公司
 * Copyright: Copyright (c) 2007博睿宏远科技发展有限公司,Inc.All Rights Reserved.
 * 
 * @date 2018年11月22日 上午10:12:15
 * @Author: <a href=mailto:weizheng@bonree.com>魏征</a>
 * @Description: kafka数据消费，针对0.10版本
 ******************************************************************************/
public class AntsKafkaConsumerV10 implements AntsKafkaConsumer<Map<String, String>, Map<String, Object>> {

    private static final Logger LOG = LoggerFactory.getLogger(AntsKafkaConsumerV10.class);

    private BlockingQueue<Map<String, Object>> queue;

    private Properties props = new Properties();
    private String topic;
    private boolean closeFlag = false;
    private KafkaConsumer<String, String> consumer = null;
    
    @Override
    public void init(Map<String, String> config) throws Exception {
        queue = new ArrayBlockingQueue<>(10000);
        this.topic = config.get("topic");
        for (Entry<String, String> cf : config.entrySet()) {
            if (ConfigUtil.isKafkaConfig(cf.getKey())) {
                LOG.info("overwrite kafka:{}:{}",ConfigUtil.trimKafkaConfig(cf.getKey()),cf.getValue());
                props.put(ConfigUtil.trimKafkaConfig(cf.getKey()), cf.getValue());
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void startConsumer(Map<String, String> config) throws Exception {
        LOG.info("start kafka consumer");
        if (topic == null) {
            throw new Exception("no topic!please config the topic");
        }
        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Lists.newArrayList(topic));
        while (!closeFlag) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            if (!records.isEmpty()) {
                for (ConsumerRecord<String, String> record : records) {
                    String jsonStr = record.value();
                    queue.put(JsonUtil.toObject(jsonStr, Map.class));
                }
            }
        }
    }

    @Override
    public void stopConsumer() throws Exception {
        closeFlag = true;
        Thread.sleep(200);
        if (consumer != null) {
            consumer.close();
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public MessageTaker<Map<String, Object>> taker() {
        return new QueueMessagTaker(queue);
    }
}
