package com.example.coupondemo.mq;

import lombok.Data;

import java.io.Serializable;

@Data
public class CouponMessage implements Serializable {
    private Long activityId;
    private Long userId;
    private String messageId;
}
