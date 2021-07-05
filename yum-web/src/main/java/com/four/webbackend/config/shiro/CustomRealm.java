package com.four.webbackend.config.shiro;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.four.webbackend.util.TokenUtil;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * @author lbavsc
 * @version 1.0
 * @className CustomRealm
 * @description 自定义Realm，实现Shiro认证
 * @date 2021/7/5 下午2:48
 **/
@Component
public class CustomRealm extends AuthorizingRealm {

    /**
     * 用户授权
     *
     * @param principalCollection principalCollection
     * @return AuthorizationInfo
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("用户授权");
        String username = TokenUtil.getAccount(principalCollection.toString());

        // TODO: 2021/7/5 从数据库中查询用户信息
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        Set<String> role = new HashSet<>();
        role.add("user");
        info.setRoles(role);
        return info;
    }

    /**
     * 用户身份认证
     *
     * @param authenticationToken authenticationToken
     * @return AuthenticationInfo
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        System.out.println("身份认证");
        String token = (String) authenticationToken.getCredentials();
        String username = TokenUtil.getAccount(token);
        // TODO: 2021/7/5  从数据库中查询用户信息
        //这里要去数据库查找是否存在该用户，这里直接放行
//        if (userEntity == null) {
//            throw new AuthenticationException("认证失败！");
//        }
        return new SimpleAuthenticationInfo(token, token, "MyRealm");
    }
}
