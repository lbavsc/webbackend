package com.four.webbackend.service.Impl;

import com.four.webbackend.entity.DirEntity;
import com.four.webbackend.mapper.DirMapper;
import com.four.webbackend.service.DirService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.four.webbackend.util.TokenUtil;
import jdk.nashorn.internal.parser.Token;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 文件夹信息表 服务实现类
 * </p>
 *
 * @author lbavsc
 * @since 2021-07-05
 */
@Service
public class DirServiceImpl extends ServiceImpl<DirMapper, DirEntity> implements DirService {

    @Override
    public boolean isExistDir(String token, Integer dirId) {
        Integer userId = TokenUtil.getUserId(token);
        DirEntity dirEntity = baseMapper.selectById(dirId);
        return dirEntity != null && dirEntity.getUserId().equals(userId);
    }
}
