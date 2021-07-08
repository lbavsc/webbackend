package com.four.webbackend.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.four.webbackend.entity.FileEntity;
import com.four.webbackend.entity.UserFileEntity;
import com.four.webbackend.mapper.FileMapper;
import com.four.webbackend.mapper.UserFileMapper;
import com.four.webbackend.model.FileInfoDto;
import com.four.webbackend.model.MyPageVo;
import com.four.webbackend.service.UserFileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.four.webbackend.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    private final FileMapper fileMapper;

    @Autowired
    public UserFileServiceImpl(FileMapper fileMapper) {
        this.fileMapper = fileMapper;
    }

    @Override
    public boolean isExist(String token, String fileMd5) {

        Integer userId = TokenUtil.getUserId(token);

        int count = count(new QueryWrapper<UserFileEntity>()
                .select("file_id")
                .eq("md5", fileMd5)
                .eq("user_id", userId));

        return count > 0;
    }

    @Override
    public IPage<FileInfoDto> listRecycle(MyPageVo<FileInfoDto> myPageVo, String token) {

        IPage<UserFileEntity> iPage = baseMapper.selectPageVo(myPageVo, TokenUtil.getUserId(token));

        IPage<FileInfoDto> rest = new Page<>();

        rest.setTotal(iPage.getTotal());
        rest.setPages(iPage.getPages());
        rest.setSize(iPage.getSize());

        List<FileInfoDto> fileInfoDtos = new ArrayList<>();

        iPage.getRecords().forEach(entity -> {
            FileEntity fileEntity = fileMapper.selectById(entity.getFileId());
            FileInfoDto dto = new FileInfoDto();

            dto.setUserFileId(entity.getUserFileId());
            dto.setFileSize(fileEntity.getFileSize());
            dto.setFileType(fileEntity.getFileType());
            dto.setFileName(fileEntity.getFileName());

            fileInfoDtos.add(dto);
        });
        rest.setRecords(fileInfoDtos);
        return rest;
    }

    @Override
    public void restoreFile(String token, Integer userFileId) {
        Integer userId = TokenUtil.getUserId(token);

        baseMapper.restoreFile(userId, userFileId);


    }

    @Override
    public void realDel(String token, Integer userFileId) {
        Integer userId = TokenUtil.getUserId(token);
        baseMapper.realDel(userId, userFileId);

    }


}
