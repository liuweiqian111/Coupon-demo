package com.example.coupondemo.service;

import com.example.coupondemo.domain.User;

public interface UserService {
    User getByPhone(String phone);
    String login(String phone, String password);
}
