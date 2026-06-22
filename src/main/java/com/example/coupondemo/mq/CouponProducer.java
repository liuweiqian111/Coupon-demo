package com.example.coupondemo.mq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponProducer {

    private final RabbitTemplate rabbitTemplate;

    public static final String EXCHANGE = "coupon.exchange";
    public static final String ROUTING_KEY = "coupon.routing.key";
    public static final String QUEUE = "coupon.queue";

    public void send(Long activityId, Long userId, String messageId) {
        CouponMessage message = new CouponMessage();
        message.setActivityId(activityId);
        message.setUserId(userId);
        message.setMessageId(messageId);
        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, message);
        log.info("发送领券消息: activityId={}, userId={}", activityId, userId);
    }
}
