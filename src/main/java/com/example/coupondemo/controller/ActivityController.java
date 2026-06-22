package com.example.coupondemo.controller;

import com.example.coupondemo.domain.CouponActivity;
import com.example.coupondemo.service.CouponActivityService;
import com.example.coupondemo.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activity")
@RequiredArgsConstructor
public class ActivityController {

    private final CouponActivityService couponActivityService;

    @GetMapping("/list")
    public Result<List<CouponActivity>> list() {
        return Result.success(couponActivityService.listActivities());
    }

    @GetMapping("/detail/{id}")
    public Result<CouponActivity> detail(@PathVariable Long id) {
        return Result.success(couponActivityService.getById(id));
    }
}
