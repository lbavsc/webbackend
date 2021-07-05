package com.four.webbackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author lbavsc
 * @version 1.0
 * @className SwaggerConfig
 * @description swagger配置类
 * @date 2021/7/5 下午2:06
 **/
@Configuration
@EnableSwagger2
public class SwaggerConfig {


    @Value("${swagger.enable}")
    private boolean swaggerEnable;

    @Bean
    Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.four.webbackend"))
                .paths(PathSelectors.any())
                .build()
                .enable(swaggerEnable);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("API测试文档")
                .description("接口测试文档")
                .termsOfServiceUrl("http://localhost:8080")
                .version("1.0")
                .contact(new Contact("lxx",
                        "https://www.github.com/lbavsc",
                        "471242574@qq.com"))
                .build();
    }

}
