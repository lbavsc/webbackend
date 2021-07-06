package com.four.webbackend.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.four.webbackend.entity.DirEntity;
import com.four.webbackend.entity.UserEntity;
import com.four.webbackend.handler.GlobalExceptionHandler;
import com.four.webbackend.mapper.DirMapper;
import com.four.webbackend.mapper.UserMapper;
import com.four.webbackend.model.*;
import com.four.webbackend.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.four.webbackend.util.*;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.shiro.authc.AccountException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final DirMapper dirMapper;

    @Autowired
    public UserServiceImpl(DirMapper dirMapper) {
        this.dirMapper = dirMapper;
    }

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

        userEntity = baseMapper.selectOne(new QueryWrapper<UserEntity>()
                .eq("email", registVo.getUserEmail()));

        DirEntity dirEntity = new DirEntity();
        dirEntity.setDirName("/");
        dirEntity.setUserId(userEntity.getUserId());
        dirEntity.setOwnedDirId(0);

        dirMapper.insert(dirEntity);
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
        String token = creatToken(userEntity.getUserName(), userEntity.getUserId());

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

        DirEntity dirEntity = dirMapper.selectOne(new QueryWrapper<DirEntity>()
                .eq("user_id", userEntity.getUserId())
                .eq("owned_dir_id", 0));
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userEntity, userDto);
        userDto.setUsed(0L);
        userDto.setTotalCapacity(10737418240L);
        userDto.setRootDir(dirEntity.getDirId());
        return userDto;
    }

    @Override
    public UserDto modify(UserVo userVo, String uuid) {

        if (userVo.getUserEmail() != null && userVo.getCheckCode() == null) {
            return null;
        }


        UserEntity userEntity = baseMapper.selectOne(new QueryWrapper<UserEntity>().eq("user_uuid", uuid));
        BeanUtils.copyProperties(userVo, userEntity);
        baseMapper.updateById(userEntity);

        userEntity = baseMapper.selectOne(new QueryWrapper<UserEntity>().eq("user_uuid", uuid));
        UserDto dto = new UserDto();
        BeanUtils.copyProperties(userEntity, dto);
        return dto;
    }

    @Override
    public boolean modifyPwd(PasswdVo passwdVo, String uuid) {
        UserEntity userEntity = baseMapper.selectOne(new QueryWrapper<UserEntity>().eq("user_uuid", uuid));
        String passwd = PwdToMd5.encrypt(passwdVo.getOldPasswd(), userEntity.getUserName());
        if (!passwd.equals(userEntity.getPassword())) {
            throw new AccountException("旧密码错误");
        }

        userEntity.setPassword(passwd);
        return baseMapper.updateById(userEntity) == 1;
    }

    @Override
    public void logout(String token) {
        Integer userId = TokenUtil.getUserId(token);
        UserEntity userEntity = baseMapper.selectById(userId);
        if (userEntity == null) {
            return;
        }
        if (RedisUtil.hasKey(userEntity.getUserName())) {
            RedisUtil.del(userEntity.getUserName());
        }
    }

    @Override
    public boolean forgotPwd(ForgotPwdVo forgotPwdVo) {
        UserEntity userEntity = baseMapper.selectOne(new QueryWrapper<UserEntity>()
                .eq("email", forgotPwdVo.getUserEmail()));
        if (userEntity == null) {
            return false;
        }
        String passwd = PwdToMd5.encrypt(forgotPwdVo.getPasswd(), userEntity.getUserName());
        userEntity.setPassword(passwd);
        baseMapper.updateById(userEntity);
        return true;
    }


    private String creatToken(String userUuId, int userId) {
        Long currentTimeMillis = System.currentTimeMillis();
        String token = TokenUtil.sign(userUuId, userId, currentTimeMillis);
        RedisUtil.set(userUuId, currentTimeMillis, TokenUtil.REFRESH_EXPIRE_TIME);
        return token;
    }
}
