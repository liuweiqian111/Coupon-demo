package com.example.coupondemo.service.impl;

import com.alibaba.fastjson2.JSON;
import com.example.coupondemo.domain.CouponActivity;
import com.example.coupondemo.mapper.CouponActivityMapper;
import com.example.coupondemo.service.CouponActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CouponActivityServiceImpl implements CouponActivityService {

    private final CouponActivityMapper couponActivityMapper;
    private final StringRedisTemplate redisTemplate;

    @Override
    public List<CouponActivity> listActivities() {
        String key = "activity:list";
        String json = redisTemplate.opsForValue().get(key);
        if (json != null) {
            List<CouponActivity> list = JSON.parseArray(json, CouponActivity.class);
            refreshRemainAmountFromRedis(list);
            return list;
        }
        // 查DB：只查进行中的活动
        List<CouponActivity> list = couponActivityMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<CouponActivity>()
                .eq("status", 1)
                .ge("end_time", LocalDateTime.now())
                .le("start_time", LocalDateTime.now())
        );
        if (!list.isEmpty()) {
            redisTemplate.opsForValue().set(key, JSON.toJSONString(list), 5, TimeUnit.MINUTES);
            refreshRemainAmountFromRedis(list);
        }
        return list;
    }

    @Override
    public CouponActivity getById(Long id) {
        String key = "activity:" + id;
        String json = redisTemplate.opsForValue().get(key);
        if (json != null) {
            CouponActivity activity = JSON.parseObject(json, CouponActivity.class);
            refreshRemainAmountFromRedis(activity);
            return activity;
        }
        CouponActivity activity = couponActivityMapper.selectById(id);
        if (activity != null) {
            redisTemplate.opsForValue().set(key, JSON.toJSONString(activity), 5, TimeUnit.MINUTES);
            refreshRemainAmountFromRedis(activity);
        }
        return activity;
    }

    private void refreshRemainAmountFromRedis(List<CouponActivity> activities) {
        activities.forEach(this::refreshRemainAmountFromRedis);
    }

    private void refreshRemainAmountFromRedis(CouponActivity activity) {
        String remainAmount = redisTemplate.opsForValue().get("stock:" + activity.getId());
        if (remainAmount != null) {
            activity.setRemainAmount(Integer.valueOf(remainAmount));
        }
    }
}
