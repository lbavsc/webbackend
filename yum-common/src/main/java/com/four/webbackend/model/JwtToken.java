package com.four.webbackend.model;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * @author lbavsc
 * @version 1.0
 * @className JwtToken
 * @description
 * @date 2021/7/5 下午2:56
 **/
public class JwtToken implements AuthenticationToken {

    private final String token;

    public JwtToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
