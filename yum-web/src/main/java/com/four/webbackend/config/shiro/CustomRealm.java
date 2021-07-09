package com.four.webbackend.config.shiro;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.four.webbackend.entity.UserEntity;
import com.four.webbackend.mapper.UserMapper;
import com.four.webbackend.model.JwtToken;
import com.four.webbackend.service.UserService;
import com.four.webbackend.util.TokenUtil;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.ws.Action;
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

    private final UserMapper userMapper;

    @Autowired
    public CustomRealm(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    private boolean exist(String token) {
        boolean isExist = false;

        String uid = TokenUtil.getAccount(token);
        Integer userId = TokenUtil.getUserId(token);
        UserEntity userEntity = userMapper.selectById(userId);
        if (userEntity != null && userEntity.getUserUuid().equals(uid)) {
            isExist = true;
        }
        return isExist;
    }


    /**
     * 用户授权
     *
     * @param principalCollection principalCollection
     * @return AuthorizationInfo
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("用户授权");
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        Set<String> role = new HashSet<>();
        if (exist(principalCollection.toString())) {
            role.add("user");
        }
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

        if (token != null && !exist(token)) {
            throw new AuthenticationException("认证失败！");
        }

        return new SimpleAuthenticationInfo(token, token, "MyRealm");
    }
}
