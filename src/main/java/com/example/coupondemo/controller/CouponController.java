package com.example.coupondemo.controller;

import com.example.coupondemo.service.UserCouponService;
import com.example.coupondemo.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/coupon")
@RequiredArgsConstructor
public class CouponController {

    private final UserCouponService userCouponService;

    @RequestMapping(value = "/receive/{activityId}", method = {RequestMethod.GET, RequestMethod.POST})
    public Result<String> receive(@PathVariable Long activityId,
                                   @RequestAttribute("userId") Long userId) {
        String msg = userCouponService.receive(activityId, userId);
        if (msg.contains("领取中")) {
            return Result.success(msg);
        }
        return Result.error(msg);
    }
}
