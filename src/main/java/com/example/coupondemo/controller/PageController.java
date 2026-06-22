package com.example.coupondemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PageController {

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/activity/list")
    public String activityListPage() {
        return "activity_list";
    }

    @GetMapping("/activity/detail/{id}")
    public String activityDetailPage(@PathVariable Long id) {
        return "activity_detail";
    }
}
