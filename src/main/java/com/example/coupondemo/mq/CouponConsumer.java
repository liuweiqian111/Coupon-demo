package com.example.coupondemo.mq;

import com.example.coupondemo.domain.MessageLog;
import com.example.coupondemo.domain.UserCoupon;
import com.example.coupondemo.mapper.CouponActivityMapper;
import com.example.coupondemo.mapper.MessageLogMapper;
import com.example.coupondemo.mapper.UserCouponMapper;
import com.example.coupondemo.utils.UUIDUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponConsumer {

    private final UserCouponMapper userCouponMapper;
    private final MessageLogMapper messageLogMapper;
    private final CouponActivityMapper couponActivityMapper;
    private final StringRedisTemplate redisTemplate;

    @RabbitListener(queues = CouponProducer.QUEUE)
    @Transactional
    public void receive(CouponMessage message) {
        log.info("消费领券消息: {}", message);

        // 1. 幂等校验
        MessageLog exist = messageLogMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<MessageLog>()
                .eq("message_id", message.getMessageId())
        );
        if (exist != null) {
            log.info("消息已处理，跳过: {}", message.getMessageId());
            return;
        }

        // 2. 保存消息日志
        MessageLog logRecord = new MessageLog();
        logRecord.setMessageId(message.getMessageId());
        logRecord.setUserId(message.getUserId());
        logRecord.setActivityId(message.getActivityId());
        logRecord.setStatus(0);
        messageLogMapper.insert(logRecord);

        try {
            // 3. 插入领券记录
            UserCoupon userCoupon = new UserCoupon();
            userCoupon.setUserId(message.getUserId());
            userCoupon.setActivityId(message.getActivityId());
            userCoupon.setCouponCode(UUIDUtil.uuid());
            userCoupon.setStatus(1);
            userCouponMapper.insert(userCoupon);

            // 4. 更新数据库库存
            couponActivityMapper.update(null,
                new com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<com.example.coupondemo.domain.CouponActivity>()
                    .eq("id", message.getActivityId())
                    .setSql("remain_amount = remain_amount - 1")
            );

            // 5. 清理活动缓存，确保页面能读到最新库存
            redisTemplate.delete("activity:list");
            redisTemplate.delete("activity:" + message.getActivityId());

            // 6. 更新消息状态
            logRecord.setStatus(1);
            messageLogMapper.updateById(logRecord);
            log.info("领券成功: userId={}, activityId={}", message.getUserId(), message.getActivityId());
        } catch (Exception e) {
            log.error("领券失败: {}", e.getMessage(), e);
            logRecord.setStatus(2);
            messageLogMapper.updateById(logRecord);
            throw new AmqpRejectAndDontRequeueException("领券消息处理失败，不重新入队", e);
        }
    }
}
