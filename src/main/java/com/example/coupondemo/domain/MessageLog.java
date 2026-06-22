package com.example.coupondemo.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("message_log")
public class MessageLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String messageId;
    private Long userId;
    private Long activityId;
    private Integer status;
    private LocalDateTime createTime;
}
