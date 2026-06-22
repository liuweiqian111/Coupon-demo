package com.example.coupondemo.mq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponProducer {

    private final RocketMQTemplate rocketMQTemplate;
    public static final String TOPIC = "coupon-topic";

    public void send(Long activityId, Long userId, String messageId) {
        CouponMessage message = new CouponMessage();
        message.setActivityId(activityId);
        message.setUserId(userId);
        message.setMessageId(messageId);
        rocketMQTemplate.syncSend(TOPIC, message);
        log.info("发送领券消息: activityId={}, userId={}", activityId, userId);
    }
}
