package com.example.coupondemo.service.impl;

import com.example.coupondemo.domain.CouponActivity;
import com.example.coupondemo.domain.UserCoupon;
import com.example.coupondemo.mapper.CouponActivityMapper;
import com.example.coupondemo.mapper.UserCouponMapper;
import com.example.coupondemo.mq.CouponProducer;
import com.example.coupondemo.service.UserCouponService;
import com.example.coupondemo.utils.UUIDUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCouponServiceImpl implements UserCouponService {

    private final StringRedisTemplate redisTemplate;
    private final CouponActivityMapper couponActivityMapper;
    private final UserCouponMapper userCouponMapper;
    private final CouponProducer couponProducer;

    @Override
    public String receive(Long activityId, Long userId) {
        // 1. 检查活动是否存在
        CouponActivity activity = couponActivityMapper.selectById(activityId);
        if (activity == null || activity.getStatus() != 1) {
            return "活动不存在或已结束";
        }

        // 2. 分布式锁：防止同一用户并发重复领取
        String lockKey = "lock:receive:" + userId + ":" + activityId;
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", 10, TimeUnit.SECONDS);
        if (Boolean.FALSE.equals(locked)) {
            return "请勿重复领取";
        }

        try {
            // 3. 检查是否已领取
            String receivedKey = "received:" + userId + ":" + activityId;
            if (Boolean.TRUE.equals(redisTemplate.hasKey(receivedKey))) {
                return "您已领取过该优惠券";
            }

            // 4. Redis预减库存
            String stockKey = "stock:" + activityId;
            Long remain = redisTemplate.opsForValue().decrement(stockKey);
            if (remain == null || remain < 0) {
                // 库存不足，回补
                redisTemplate.opsForValue().increment(stockKey);
                return "优惠券已被领完";
            }

            // 5. 发送MQ异步处理
            String messageId = UUIDUtil.uuid();
            couponProducer.send(activityId, userId, messageId);

            // 6. 标记已领取（防止重复）
            redisTemplate.opsForValue().set(receivedKey, "1", 1, TimeUnit.DAYS);

            return "领取中，请稍后查看";
        } finally {
            // 释放锁
            redisTemplate.delete(lockKey);
        }
    }
}
