package com.example.coupondemo.controller;

import com.example.coupondemo.service.UserService;
import com.example.coupondemo.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;
    //@GetMapping("/doLogin")
    @PostMapping("/doLogin")
    public Result<String> doLogin(@RequestParam String phone, @RequestParam String password) {
        try {
            String token = userService.login(phone, password);
            return Result.success(token);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
