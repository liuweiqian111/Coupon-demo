package com.example.coupondemo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.coupondemo.mapper")
public class CouponDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(CouponDemoApplication.class, args);
    }

}
