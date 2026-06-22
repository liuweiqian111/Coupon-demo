package com.example.coupondemo.service.impl;

import com.example.coupondemo.domain.User;
import com.example.coupondemo.mapper.UserMapper;
import com.example.coupondemo.service.UserService;
import com.example.coupondemo.utils.MD5Util;
import com.example.coupondemo.utils.UUIDUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final StringRedisTemplate redisTemplate;

    @Override
    public User getByPhone(String phone) {
        return userMapper.selectById(phone);
    }

    @Override
    public String login(String phone, String password) {
        User user = userMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<User>()
                .eq("phone", phone)
        );
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        String dbPass = MD5Util.inputPassToDbPass(password);
        if (!dbPass.equals(user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        // 生成token，存Redis
        String token = UUIDUtil.uuid();
        redisTemplate.opsForValue().set("token:" + token, String.valueOf(user.getId()), 30, TimeUnit.MINUTES);
        return token;
    }
}
