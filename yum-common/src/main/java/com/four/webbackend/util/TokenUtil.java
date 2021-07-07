package com.four.webbackend.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @author lbavsc
 * @version 1.0
 * @className TokenUtil
 * @description
 * @date 2021/7/5 下午2:49
 **/
public class TokenUtil {

    private static final Logger logger = LoggerFactory.getLogger(TokenUtil.class);
    /**
     * token到期时间5分钟，毫秒为单位
     */
    public static final long EXPIRE_TIME = 5 * 60 * 1000;
    /**
     * RefreshToken到期时间为30分钟，秒为单位
     */
    public static final long REFRESH_EXPIRE_TIME = 30 * 60;

    /**
     * 密钥盐
     */
    private static final String TOKEN_SECRET = "erfwerugfhrtitur2332#@ED@E1sRDwaesdf43dfwer34qw";


    /**
     *
     * @param account 用户名
     * @param currentTime 过期时间
     * @return token
     */
    public static String sign(String account, int userId, Long currentTime) {

        String token = null;
        try {
            Date expireAt = new Date(currentTime + EXPIRE_TIME);
            token = JWT.create()
                    .withIssuer("signup")
                    .withClaim("account", account)
                    .withClaim("userId", userId)
                    .withClaim("currentTime", currentTime)
                    .withExpiresAt(expireAt)
                    .sign(Algorithm.HMAC256(TOKEN_SECRET));
        } catch (IllegalArgumentException | JWTCreationException je) {
            logger.error(je.getMessage());
        }
        return token;
    }

    /**
     *
     * @param token token
     * @return token是否验证通过
     * @throws Exception
     */
    public static Boolean verify(String token) throws Exception {
        //创建token验证器
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(TOKEN_SECRET)).withIssuer("signup").build();
        DecodedJWT decodedJWT = jwtVerifier.verify(token);
        return true;
    }


    public static String getAccount(String token) {
        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            return decodedJWT.getClaim("account").asString();

        } catch (JWTCreationException e) {
            return null;
        }
    }

    public static Long getCurrentTime(String token) {
        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            return decodedJWT.getClaim("currentTime").asLong();

        } catch (JWTCreationException e) {
            return null;
        }
    }

    public static Integer getUserId(String token) {
        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            return decodedJWT.getClaim("userId").asInt();

        } catch (JWTCreationException e) {
            return null;
        }
    }
}
