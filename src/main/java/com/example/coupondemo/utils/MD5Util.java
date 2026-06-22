package com.example.coupondemo.utils;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {
    private static final String SALT = "coupon_salt_2026";

    public static String md5(String str) {
        return DigestUtils.md5Hex(str);
    }

    public static String inputPassToDbPass(String inputPass) {
        String str = "" + SALT.charAt(0) + SALT.charAt(2) + inputPass + SALT.charAt(5) + SALT.charAt(4);
        return md5(str);
    }
}
