package com.four.webbackend.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.four.webbackend.entity.UserFileEntity;
import com.four.webbackend.mapper.FileMapper;
import com.four.webbackend.mapper.UserFileMapper;
import com.four.webbackend.service.UserFileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.four.webbackend.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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

    @Override
    public boolean isExist(String token, String fileMd5) {
        boolean rest = false;
        Integer userId = TokenUtil.getUserId(token);
        List<UserFileEntity> userFileEntityList = baseMapper.selectList(new QueryWrapper<UserFileEntity>()
                .select("file_id")
                .eq("user_id", userId));

        for (UserFileEntity userFileEntity : userFileEntityList) {
            if (fileMd5.equals(userFileEntity.getMd5())) {
                rest = true;
                break;
            }
        }
        return rest;
    }
}
