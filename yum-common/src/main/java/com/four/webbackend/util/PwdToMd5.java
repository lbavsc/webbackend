package com.four.webbackend.util;

import org.springframework.util.DigestUtils;

/**
 * @author lbavsc
 * @version 1.0
 * @className PwdToMd5
 * @description
 * @date 2021/7/5 下午5:38
 **/
public class PwdToMd5 {

    public static String encrypt(String password, String userName) {
        String salt = password + userName;
        return  DigestUtils.md5DigestAsHex(salt.getBytes());
    }
}
