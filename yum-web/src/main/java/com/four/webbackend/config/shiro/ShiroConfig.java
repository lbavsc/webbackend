package com.four.webbackend.config.shiro;

import com.four.webbackend.fileter.JwtFilter;
import org.apache.shiro.authz.Authorizer;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author lbavsc
 * @version 1.0
 * @className ShiroConfig
 * @description
 * @date 2021/7/5 下午2:33
 **/
@Configuration
public class ShiroConfig {
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager){
        ShiroFilterFactoryBean shiroFilterFactoryBean=new ShiroFilterFactoryBean();
        Map<String, Filter> filterMap=new LinkedHashMap<>();
        filterMap.put("jwt", new JwtFilter());
        shiroFilterFactoryBean.setFilters(filterMap);
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        //不要用HashMap来创建Map，会有某些配置失效，要用链表的LinkedHashmap
        Map<String,String> filterRuleMap=new LinkedHashMap<>();
        //放行接口
        filterRuleMap.put("/","anon");
        filterRuleMap.put("/factoring/user/verification_code", "anon");
        filterRuleMap.put("/factoring/user/verification_code2", "anon");
        filterRuleMap.put("/login","anon");
        filterRuleMap.put("/lib/**","anon");
        filterRuleMap.put("/static/**","anon");
        filterRuleMap.put("/factoring/user/**/login","anon");

        filterRuleMap.put("/swagger-ui.html", "anon");
        filterRuleMap.put("/swagger-resources/**", "anon");
        filterRuleMap.put("/v2/api-docs/**", "anon");
        filterRuleMap.put("/webjars/springfox-swagger-ui/**", "anon");

        filterRuleMap.put("/factoring/company_overview/get", "anon");
        filterRuleMap.put("/factoring/company_overview/list", "anon");
        filterRuleMap.put("/factoring/product_introduction/get", "anon");
        filterRuleMap.put("/factoring/product_introduction/list", "anon");
        filterRuleMap.put("/factoring/home_product_introduction/get", "anon");
        filterRuleMap.put("/factoring/conntact_us/get", "anon");
        filterRuleMap.put("/factoring/recruitment/get", "anon");
        filterRuleMap.put("/factoring/recruitment/list", "anon");

        filterRuleMap.put("/druid/**", "anon");
        filterRuleMap.put("/factoring/druid/**", "anon");
        //拦截所有接口
        filterRuleMap.put("/**","jwt");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterRuleMap);
        return shiroFilterFactoryBean;

    }


    @Bean
    public SecurityManager securityManager(CustomRealm customRealm, Authorizer authorizer){
        //设置自定义Realm
        DefaultWebSecurityManager securityManager=new DefaultWebSecurityManager();

        //关闭shiro自带的session
        DefaultSubjectDAO subjectDAO=new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator defaultSessionStorageEvaluator=new DefaultSessionStorageEvaluator();
        defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
        subjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
        securityManager.setSubjectDAO(subjectDAO);

        securityManager.setAuthorizer(authorizer);
        securityManager.setRealm(customRealm);
        return securityManager;
    }

    /**
     * 配置代理会导致doGetAuthorizationInfo执行两次
     */
//    @Bean
//    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator(){
//        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator=new DefaultAdvisorAutoProxyCreator();
//        //强制使用从cglib动态代理机制，防止重复代理可能引起代理出错问题
//        defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
//        return defaultAdvisorAutoProxyCreator;
//    }


    /**
     * 授权属性源配置
     * @param securityManager
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager){
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor=new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);

        return authorizationAttributeSourceAdvisor;

    }

    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor(){
        return new LifecycleBeanPostProcessor();
    }
}
