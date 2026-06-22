package com.example.coupondemo.mq;

import com.example.coupondemo.domain.MessageLog;
import com.example.coupondemo.domain.UserCoupon;
import com.example.coupondemo.mapper.CouponActivityMapper;
import com.example.coupondemo.mapper.MessageLogMapper;
import com.example.coupondemo.mapper.UserCouponMapper;
import com.example.coupondemo.utils.UUIDUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(topic = "coupon-topic", consumerGroup = "coupon-consumer-group")
public class CouponConsumer implements RocketMQListener<CouponMessage> {

    private final UserCouponMapper userCouponMapper;
    private final MessageLogMapper messageLogMapper;
    private final CouponActivityMapper couponActivityMapper;

    @Transactional
    @Override
    public void onMessage(CouponMessage message) {
        log.info("消费领券消息: {}", message);

        MessageLog exist = messageLogMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<MessageLog>()
                .eq("message_id", message.getMessageId())
        );
        if (exist != null) {
            log.info("消息已处理，跳过: {}", message.getMessageId());
            return;
        }

        MessageLog logRecord = new MessageLog();
        logRecord.setMessageId(message.getMessageId());
        logRecord.setUserId(message.getUserId());
        logRecord.setActivityId(message.getActivityId());
        logRecord.setStatus(0);
        messageLogMapper.insert(logRecord);

        try {
            UserCoupon userCoupon = new UserCoupon();
            userCoupon.setUserId(message.getUserId());
            userCoupon.setActivityId(message.getActivityId());
            userCoupon.setCouponCode(UUIDUtil.uuid());
            userCoupon.setStatus(1);
            userCouponMapper.insert(userCoupon);

            couponActivityMapper.update(null,
                new com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<com.example.coupondemo.domain.CouponActivity>()
                    .eq("id", message.getActivityId())
                    .setSql("remain_amount = remain_amount - 1")
            );

            logRecord.setStatus(1);
            messageLogMapper.updateById(logRecord);
            log.info("领券成功: userId={}, activityId={}", message.getUserId(), message.getActivityId());
        } catch (Exception e) {
            log.error("领券失败: {}", e.getMessage());
            logRecord.setStatus(2);
            messageLogMapper.updateById(logRecord);
            throw e;
        }
    }
}
