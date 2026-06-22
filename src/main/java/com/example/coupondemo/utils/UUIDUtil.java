package com.example.coupondemo.utils;

import java.util.UUID;

public class UUIDUtil {
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}
