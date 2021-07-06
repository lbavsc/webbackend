package com.four.webbackend.service.Impl;

import com.four.webbackend.entity.DirEntity;
import com.four.webbackend.handler.GlobalExceptionHandler;
import com.four.webbackend.mapper.DirMapper;
import com.four.webbackend.model.RenameFileOrDirVo;
import com.four.webbackend.service.DirService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.four.webbackend.util.TokenUtil;
import jdk.nashorn.internal.parser.Token;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

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

    @Override
    public boolean rename(String token, RenameFileOrDirVo renameFileOrDirVo) {
        Integer userId = TokenUtil.getUserId(token);
        DirEntity dirEntity = baseMapper.selectById(renameFileOrDirVo.getObjectId());
        HttpServletResponse response = getResponse();
        if (dirEntity == null || dirEntity.getUserId().equals(userId)) {
            GlobalExceptionHandler.responseError(response, "没有该目录");
            return false;
        }

        dirEntity.setDirName(renameFileOrDirVo.getNewName());
        return baseMapper.updateById(dirEntity) == 1;
    }

    private HttpServletResponse getResponse() {
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getResponse();
    }
}
