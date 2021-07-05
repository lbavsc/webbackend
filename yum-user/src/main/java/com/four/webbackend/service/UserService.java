package com.four.webbackend.service;

import com.four.webbackend.entity.UserEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.four.webbackend.model.LoginVo;
import com.four.webbackend.model.RegistVo;
import com.four.webbackend.model.UserDto;

import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author lbavsc
 * @since 2021-07-05
 */
public interface UserService extends IService<UserEntity> {

    void regist(RegistVo registVo);

    boolean login(LoginVo loginVo,  HttpServletResponse response);

    UserDto info(String userUuid);
}
