package com.four.webbackend.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;

/**
 * @author lbavsc
 * @version 1.0
 * @className CheckCodeUtil
 * @description
 * @date 2021/7/5 下午5:48
 **/
public class CheckCodeUtil {
    private static final Logger logger = LoggerFactory.getLogger(CheckCodeUtil.class);

    public static Boolean isCheckCaptcha(String code, HttpSession session) {
        String sessionCode;
        sessionCode = String.valueOf(session.getAttribute(ValidateCodeUtil.SESSION_KEY)).toLowerCase();
        logger.info("session里的验证码：" + sessionCode  + "\nsession: " + session.getId());

        String receivedCode = code.toLowerCase();
        logger.info("用户的验证码：" + receivedCode);
        //验证码验证后进行失效处理
//        session.removeAttribute(ValidateCodeUtil.SESSION_KEY);
        return !"".equals(sessionCode) && !"".equals(receivedCode) && sessionCode.equals(receivedCode);
    }

    public static Boolean isEmailCheck(String email, String emailCode) {
        String code = (String) RedisUtil.get(email);
        if (code == null) {
            return false;
        }
        RedisUtil.del(email);
        return emailCode.equals(code);
    }
}
