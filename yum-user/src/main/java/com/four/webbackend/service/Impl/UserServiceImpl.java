package com.four.webbackend.service.Impl;

import com.four.webbackend.entity.UserEntity;
import com.four.webbackend.mapper.UserMapper;
import com.four.webbackend.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
