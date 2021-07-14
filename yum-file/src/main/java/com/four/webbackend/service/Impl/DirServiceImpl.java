package com.four.webbackend.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.four.webbackend.entity.DirEntity;
import com.four.webbackend.entity.FileEntity;
import com.four.webbackend.entity.UserFileEntity;
import com.four.webbackend.exception.BusinessException;
import com.four.webbackend.handler.GlobalExceptionHandler;
import com.four.webbackend.mapper.DirMapper;
import com.four.webbackend.mapper.FileMapper;
import com.four.webbackend.mapper.UserFileMapper;
import com.four.webbackend.model.*;
import com.four.webbackend.service.DirService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.four.webbackend.util.TokenUtil;
import jdk.nashorn.internal.parser.Token;
import org.apache.shiro.authc.AccountException;
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

        if (dirEntity == null || !dirEntity.getUserId().equals(userId)) {
            throw new BusinessException(403, "没有该目录");
        }

        dirEntity.setDirName(renameFileOrDirVo.getNewName());
        return baseMapper.updateById(dirEntity) == 1;
    }

    @Override
    public boolean deleteDir(HttpServletResponse response, String token, DeleteVo deleteVo) {
        Integer userId = TokenUtil.getUserId(token);
        DirEntity dirEntity = baseMapper.selectById(deleteVo.getFileId());

        if (dirEntity == null || !dirEntity.getUserId().equals(userId)) {
            throw new BusinessException(403, "没有该目录");
        }

        int count = userFileMapper.selectCount(new QueryWrapper<UserFileEntity>()
                .eq("user_id", userId)
                .eq("dir_id", dirEntity.getDirId()));

        int countDir = count(new QueryWrapper<DirEntity>()
                .eq("owned_dir_id", deleteVo.getFileId()));

        if (count > 0 || countDir > 0) {
            throw new BusinessException(403, "只能删除空文件夹");
        }

        baseMapper.deleteById(deleteVo.getFileId());
        return true;

    }

    @Override
    public DirDto getDirContent(String token, String dirId) {
        List<DirInfoDto> dirInfoDtos = new ArrayList<>();
        List<FileInfoDto> fileInfoDtos = new ArrayList<>();
        Integer userId = TokenUtil.getUserId(token);

        DirEntity thisEntity = baseMapper.selectById(dirId);
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
        dto.setParentId(thisEntity.getOwnedDirId());
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

    @Override
    public boolean createDir(String token, Integer dirId, String dirName) {
        Integer userId = TokenUtil.getUserId(token);
        DirEntity dirEntity = new DirEntity();
        dirEntity.setOwnedDirId(dirId);
        if (dirName.isEmpty()) {
            throw new BusinessException(403, "文件夹名不能为空");
        }
        dirEntity.setDirName(dirName);
        dirEntity.setUserId(userId);

        return baseMapper.insert(dirEntity) == 1;
    }

    @Override
    public DirTreeDto getTree(String token) {
        Integer userId = TokenUtil.getUserId(token);
        List<DirEntity> list = baseMapper.selectList(new QueryWrapper<DirEntity>()
                .eq("user_id", userId));

        if (list == null || list.isEmpty()) {
            throw new BusinessException(403, "数据错误");
        }

        DirTreeDto rootDir = new DirTreeDto();
        DirEntity entity = baseMapper.selectOne(new QueryWrapper<DirEntity>()
                .eq("user_id", userId)
                .eq("owned_dir_id", 0));
        rootDir.setId(entity.getDirId());
        rootDir.setLabel("根目录");

        rootDir.setChildren(getChildrenDir(list, entity.getDirId()));

        return rootDir;

    }

    public List<DirTreeDto> getChildrenDir(List<DirEntity> dataSetDirectories, Integer parentId) {
        List<DirTreeDto> result = new ArrayList<>();
        for (DirEntity dataSetDirectory : dataSetDirectories) {
            if (null == dataSetDirectory) {
                continue;
            }

            if (Objects.equals(dataSetDirectory.getOwnedDirId(), parentId)) {
                DirTreeDto dirTreeDto = new DirTreeDto();
                dirTreeDto.setId(dataSetDirectory.getDirId());
                dirTreeDto.setLabel(dataSetDirectory.getDirName());
                dirTreeDto.setChildren(getChildrenDir(dataSetDirectories, dataSetDirectory.getDirId()));
                result.add(dirTreeDto);
            }
        }
        return result;
    }
}
