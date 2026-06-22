package com.example.coupondemo.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("coupon_activity")
public class CouponActivity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private Integer totalAmount;
    private Integer remainAmount;
    private BigDecimal discountAmount;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer status;
}
