package com.example.seckill.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework .amqp.rabbit.annotation.RabbitListener ;
import org.springframework .stereotype.Service;

/**
 * @author zhoubin
 * @since 1.0.0
 */
@Service
@Slf4j
public class MQReceiver {

    @RabbitListener(queues = "queue")
    public void receive(Object msg) {
        log.info("接受消息： " + msg);
    }
}

