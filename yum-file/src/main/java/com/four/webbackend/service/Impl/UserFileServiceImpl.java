package com.four.webbackend.service.Impl;

import com.four.webbackend.entity.UserFileEntity;
import com.four.webbackend.mapper.UserFileMapper;
import com.four.webbackend.service.UserFileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户-文件关系表 服务实现类
 * </p>
 *
 * @author lbavsc
 * @since 2021-07-05
 */
@Service
public class UserFileServiceImpl extends ServiceImpl<UserFileMapper, UserFileEntity> implements UserFileService {

}
