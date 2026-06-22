package com.example.coupondemo.service;

import com.example.coupondemo.domain.CouponActivity;

import java.util.List;

public interface CouponActivityService {
    List<CouponActivity> listActivities();
    CouponActivity getById(Long id);
}
