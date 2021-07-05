package com.four.webbackend.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.four.webbackend.entity.UserEntity;
import com.four.webbackend.handler.GlobalExceptionHandler;
import com.four.webbackend.mapper.UserMapper;
import com.four.webbackend.model.LoginVo;
import com.four.webbackend.model.RegistVo;
import com.four.webbackend.model.UserDto;
import com.four.webbackend.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.four.webbackend.util.IdUtils;
import com.four.webbackend.util.PwdToMd5;
import com.four.webbackend.util.RedisUtil;
import com.four.webbackend.util.TokenUtil;
import org.apache.shiro.authc.AccountException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author lbavsc
 * @since 2021-07-05
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {

    @Override
    public void regist(RegistVo registVo) {
        String email = registVo.getUserEmail();
        int count = count(new QueryWrapper<UserEntity>()
                .eq("email", email));
        if (count > 0) {
            throw new IllegalArgumentException("该email已注册");
        }

        UserEntity userEntity = UserEntity.builder()
                .userName(registVo.getUserName())
                .email(registVo.getUserEmail())
                .userUuid(IdUtils.getPrimaryKey())
                .password(PwdToMd5.encrypt(registVo.getPasswd(), registVo.getUserName()))
                .build();
        baseMapper.insert(userEntity);
    }

    @Override
    public boolean login(LoginVo loginVo, HttpServletResponse response) {

        UserEntity userEntity = baseMapper.selectOne(new QueryWrapper<UserEntity>()
                .eq("email", loginVo.getUserEmail()));

        if (userEntity == null) {
            return false;
        }
        String passwd = PwdToMd5.encrypt(loginVo.getPasswd(), userEntity.getUserName());

        if (!passwd.equals(userEntity.getPassword())) {
            return false;
        }
        String token = creatToken(userEntity.getUserName());

        response.setHeader("Authorization", token);
        response.setHeader("Access-Control-Expose-Headers", "Authorization");

        return true;

    }

    @Override
    public UserDto info(String userUuid) {
        UserEntity userEntity = baseMapper.selectOne(new QueryWrapper<UserEntity>().eq("user_uuid", userUuid));
        if (userEntity == null) {
            return null;
        }

        return UserDto.builder()
                .userUuid(userEntity.getUserUuid())
                .email(userEntity.getEmail())
                .userName(userEntity.getUserName())
                .build();
    }

    private String creatToken(String userUuId) {
        Long currentTimeMillis = System.currentTimeMillis();
        String token = TokenUtil.sign(userUuId, currentTimeMillis);
        RedisUtil.set(userUuId, currentTimeMillis, TokenUtil.REFRESH_EXPIRE_TIME);
        return token;
    }
}
