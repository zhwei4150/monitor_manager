package com.bonree.ants.manager.task;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.bonree.ants.manager.util.JsonUtil;

public class KafkaProducerTest {

    KafkaProducer<Integer, String> producer;

    public KafkaProducerTest() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "192.168.101.86:9092");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("serializer.class", "kafka.serializer.StringEncoder");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("request.required.acks", "1");
        producer = new KafkaProducer<Integer, String>(properties);
    }

    public void dealData() throws Exception {

        try {
            while (true) {
                Map<String, Object> term = new HashMap<>();
                term.put("test", "test");
                String str = JsonUtil.toJsonString(term);
                ProducerRecord<Integer, String> record = new ProducerRecord<Integer, String>("test_flume_wz", str);
                producer.send(record);
                Thread.sleep(500);
            }
        } finally {

        }
    }

    public static void main(String[] args) throws Exception {
        new KafkaProducerTest().dealData();
    }

}
