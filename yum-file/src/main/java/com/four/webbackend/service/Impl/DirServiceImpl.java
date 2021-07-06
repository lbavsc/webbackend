package com.four.webbackend.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.four.webbackend.entity.DirEntity;
import com.four.webbackend.entity.FileEntity;
import com.four.webbackend.entity.UserFileEntity;
import com.four.webbackend.handler.GlobalExceptionHandler;
import com.four.webbackend.mapper.DirMapper;
import com.four.webbackend.mapper.FileMapper;
import com.four.webbackend.mapper.UserFileMapper;
import com.four.webbackend.model.*;
import com.four.webbackend.service.DirService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.four.webbackend.util.TokenUtil;
import jdk.nashorn.internal.parser.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
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

    private final UserFileMapper userFileMapper;

    private final FileMapper fileMapper;

    @Autowired
    public DirServiceImpl(UserFileMapper userFileMapper, FileMapper fileMapper) {
        this.userFileMapper = userFileMapper;
        this.fileMapper = fileMapper;
    }

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

    @Override
    public boolean deleteDir(String token, DeleteVo deleteVo) {
        HttpServletResponse response = getResponse();
        Integer userId = TokenUtil.getUserId(token);
        DirEntity dirEntity = baseMapper.selectById(deleteVo.getIsDir());

        if (dirEntity == null || !dirEntity.getUserId().equals(userId)) {
            GlobalExceptionHandler.responseError(response, "没有该目录");
            return false;
        }

        int count = userFileMapper.selectCount(new QueryWrapper<UserFileEntity>()
                .eq("user_id", userId)
                .eq("dir_id", dirEntity.getDirId()));

        if (count > 0) {
            GlobalExceptionHandler.responseError(response, "只能删除空文件夹");
            return false;
        }

        baseMapper.deleteById(deleteVo.getFileId());
        return true;

    }

    @Override
    public DirDto getDirContent(String token, String dirId) {
        List<DirInfoDto> dirInfoDtos = new ArrayList<>();
        List<FileInfoDto> fileInfoDtos = new ArrayList<>();
        Integer userId = TokenUtil.getUserId(token);
        // 查询子目录
        List<DirEntity> dirEntities = baseMapper.selectList(new QueryWrapper<DirEntity>()
                .eq("owned_dir_id", dirId)
                .eq("user_id", userId));

        if (dirEntities != null && dirEntities.size() > 0) {
            dirEntities.forEach(entity -> {
                DirInfoDto dto = new DirInfoDto();
                dto.setDirId(entity.getDirId());
                dto.setDirName(entity.getDirName());
                dirInfoDtos.add(dto);
            });
        }


        // 查询目录存在文件
        List<UserFileEntity> userFileEntityList = userFileMapper.selectList(new QueryWrapper<UserFileEntity>()
                .eq("user_id", userId)
                .eq("dir_id", dirId));

        if (userFileEntityList != null && userFileEntityList.size() > 0) {
            userFileEntityList.forEach(entity -> {
                FileEntity fileEntity = fileMapper.selectById(entity.getFileId());
                FileInfoDto dto = new FileInfoDto();
                dto.setUserFileId(entity.getUserFileId());
                dto.setFileName(fileEntity.getFileName());
                dto.setFileSize(fileEntity.getFileSize());
                dto.setFileType(fileEntity.getFileType());
                fileInfoDtos.add(dto);
            });
        }

        DirDto dto = new DirDto();
        dto.setDirInfoDtos(null);
        dto.setFileInfoDtos(null);
        if (dirInfoDtos.size() > 0) {
            dto.setDirInfoDtos(dirInfoDtos);
        }
        if (fileInfoDtos.size() > 0) {
            dto.setFileInfoDtos(fileInfoDtos);
        }

        return dto;
    }

    private HttpServletResponse getResponse() {
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getResponse();
    }
}
