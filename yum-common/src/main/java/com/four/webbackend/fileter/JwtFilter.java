package com.four.webbackend.fileter;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.four.webbackend.handler.GlobalExceptionHandler;
import com.four.webbackend.model.JwtToken;
import com.four.webbackend.util.RedisUtil;
import com.four.webbackend.util.TokenUtil;
import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author lbavsc
 * @version 1.0
 * @className JWTFilter
 * @description 自定义jwt过滤器，对token进行处理
 * @date 2021/7/5 下午2:35
 **/
public class JwtFilter extends BasicHttpAuthenticationFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    /**
     * 判断是否允许通过
     *
     * @param request     request
     * @param response    response
     * @param mappedValue mappedValue
     * @return 是否允许通过
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        try {
            return executeLogin(request, response);
        } catch (Exception e) {
            GlobalExceptionHandler.responseError(response, "shiro fail");
            return false;
        }
    }


    /**
     * 是否进行登录请求
     *
     * @param request  request
     * @param response response
     * @return 是否进行登录请求
     */
    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        String token = ((HttpServletRequest) request).getHeader("token");
        return token != null;
    }

    /**
     * 创建shiro token
     *
     * @param request  request
     * @param response response
     * @return shiro token
     */
    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {
        String jwtToken = ((HttpServletRequest) request).getHeader("token");
        if (jwtToken != null) {
            return new JwtToken(jwtToken);
        }
        return null;
    }

    /**
     * isAccessAllowed为false时调用，验证失败
     *
     * @param request  request
     * @param response response
     * @return isAccessAllowed为false时调用，验证失败
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) {
        try {
            if (response.getWriter().toString() == null) {
                GlobalExceptionHandler.responseError(response, "token验证失败,请重新登陆");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * shiro验证成功调用
     *
     * @param token    token
     * @param subject  subject
     * @param request  request
     * @param response response
     * @return shiro验证成功调用
     * @throws Exception Exception
     */
    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response) throws Exception {
        String jwttoken = (String) token.getPrincipal();
        if (jwttoken != null) {
            try {
                if (TokenUtil.verify(jwttoken)) {
                    //判断Redis是否存在所对应的RefreshToken
                    String account = TokenUtil.getAccount(jwttoken);
                    Long currentTime = TokenUtil.getCurrentTime(jwttoken);
                    if (RedisUtil.hasKey(account)) {
                        Long currentTimeMillisRedis = (Long) RedisUtil.get(account);
                        assert currentTimeMillisRedis != null;
                        if (currentTimeMillisRedis.equals(currentTime)) {
                            return refreshToken(request, response);
                        }
                    }
                }
                return false;
            } catch (TokenExpiredException e) {
                logger.error("token验证错误:TokenExpiredException  " + e);
                return refreshToken(request, response);
            }
        }
        return true;
    }


    /**
     * 拦截器的前置方法，此处进行跨域处理
     *
     * @param request  request
     * @param response response
     * @return 是否运行通过
     * @throws Exception Exception
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Access-Control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, x-token, token, fileName");
        httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");
        if (RequestMethod.OPTIONS.name().equals(httpServletRequest.getMethod())) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return true;
        }

        //如果不带token，不去验证shiro
        if (!isLoginAttempt(request, response)) {
            GlobalExceptionHandler.responseError(httpServletResponse, "没有token");
            return false;
        }

        return super.preHandle(request, response);

    }


    /**
     * 刷新AccessToken，进行判断RefreshToken是否过期，未过期就返回新的AccessToken且继续正常访问
     *
     * @param request  request
     * @param response response
     * @return 是否刷新token
     */
    private boolean refreshToken(ServletRequest request, ServletResponse response) {
        String token = ((HttpServletRequest) request).getHeader("token");
        String account = TokenUtil.getAccount(token);
        Integer userId = TokenUtil.getUserId(token);
        Long currentTime = TokenUtil.getCurrentTime(token);
        // 判断Redis中RefreshToken是否存在
        if (RedisUtil.hasKey(account)) {
            // Redis中RefreshToken还存在，获取RefreshToken的时间戳
            Long currentTimeMillisRedis = (Long) RedisUtil.get(account);
            // 获取当前AccessToken中的时间戳，与RefreshToken的时间戳对比，如果当前时间戳一致，进行AccessToken刷新
            assert currentTimeMillisRedis != null;
            assert userId != null;
            if (currentTimeMillisRedis.equals(currentTime)) {
                // 获取当前最新时间戳
                Long currentTimeMillis = System.currentTimeMillis();
                RedisUtil.set(account, currentTimeMillis,
                        TokenUtil.REFRESH_EXPIRE_TIME);
                // 刷新AccessToken，设置时间戳为当前最新时间戳
                token = TokenUtil.sign(account, userId, currentTimeMillis);
                HttpServletResponse httpServletResponse = (HttpServletResponse) response;
                httpServletResponse.setHeader("Authorization", token);
                httpServletResponse.setHeader("Access-Control-Expose-Headers", "Authorization, fileName");
                return true;
            }
        }
        return false;
    }
}
