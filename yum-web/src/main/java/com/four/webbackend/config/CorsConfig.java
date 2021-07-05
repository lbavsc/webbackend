package com.four.webbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @author lbavsc
 * @version 1.0
 * @className CorsConfig
 * @description
 * @date 2021/7/5 下午2:29
 **/
@Configuration
public class CorsConfig {

    private CorsConfiguration buildConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        // 设置setAllowCredentials = true后就不能设置为*了，要设置具体的
        corsConfiguration.addAllowedOrigin("http://192.168.2.175:9528");
        corsConfiguration.addAllowedOrigin("http://192.168.2.120:8080");
        corsConfiguration.addAllowedOrigin("http://192.168.2.120:9528");
        corsConfiguration.addAllowedOrigin("http://192.168.2.120:9090");
        corsConfiguration.addAllowedOrigin("http://192.168.2.120:9528");
        corsConfiguration.addAllowedOrigin("http://192.168.2.120:8081");
        corsConfiguration.addAllowedOrigin("http://localhost:8080");
        corsConfiguration.addAllowedOrigin("http://localhost:8081");
        corsConfiguration.addAllowedOrigin("http://localhost:9529");
        corsConfiguration.addAllowedOrigin("http://localhost:9528");
        corsConfiguration.addAllowedOrigin("http://localhost:9530");
        corsConfiguration.addAllowedOrigin("http://localhost:9531");
        // 允许任何头
        corsConfiguration.addAllowedHeader("*");
        // 允许任何方法（post、get等）
        corsConfiguration.addAllowedMethod("*");
        return corsConfiguration;
    }


    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 对接口配置跨域设置
        source.registerCorsConfiguration("/**", buildConfig());
        return new CorsFilter(source);
    }

    @Bean
    public CookieSerializer httpSessionIdResolver() {
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        cookieSerializer.setSameSite(null);
        return cookieSerializer;
    }

}
