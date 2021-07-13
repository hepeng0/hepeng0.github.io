package com.hepeng.example.demo.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * description KafkaConsumer
 * @author hepeng [he.peng@unisinsight.com]
 * @date 2020/5/28 10:51
 * @since 1.0
 */
@Component
@Slf4j
public class KafkaConsumer {
    @KafkaListener(topics = "demo")
    public void dcgListener(ConsumerRecord<String, String> consumerRecord){
        log.info("监听到kafka消息："+consumerRecord);
    }
}
