package com.four.webbackend.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.four.webbackend.entity.FileEntity;

import com.four.webbackend.entity.UserFileEntity;
import com.four.webbackend.exception.BusinessException;
import com.four.webbackend.handler.GlobalExceptionHandler;
import com.four.webbackend.mapper.FileMapper;

import com.four.webbackend.mapper.UserFileMapper;
import com.four.webbackend.model.*;
import com.four.webbackend.service.FileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.four.webbackend.util.TokenUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 文件信息表 服务实现类
 * </p>
 *
 * @author lbavsc
 * @since 2021-07-05
 */
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, FileEntity> implements FileService {

    private final UserFileMapper userFileMapper;

    @Autowired
    public FileServiceImpl(UserFileMapper userFileMapper) {
        this.userFileMapper = userFileMapper;
    }

    @Override
    public boolean updateFile(String token, UpdateFileVo updateFileVo) {
        if (updateFileVo.getFile() == null) {
            return updateFileNotExists(token, updateFileVo);
        } else {
            return updateFileExists(token, updateFileVo);
        }
    }

    @Override
    public boolean mobileFile(String token, MobileFileVo mobileFileVo) {
        UserFileEntity userFileEntity = userFileMapper.selectById(mobileFileVo.getUserFileId());
        Integer userId = TokenUtil.getUserId(token);

        if (userFileEntity == null || !userFileEntity.getUserId().equals(userId)) {
            throw new BusinessException(403, "该目录不存在此文件");
        }

        userFileEntity.setDirId(mobileFileVo.getDirFromId());

        return userFileMapper.updateById(userFileEntity) == 1;
    }


    @Override
    public boolean rename(String token, RenameFileOrDirVo renameFileOrDirVo) {
        Integer userId = TokenUtil.getUserId(token);
        UserFileEntity userFileEntity = userFileMapper.selectById(renameFileOrDirVo.getObjectId());

        if (userFileEntity == null || !userFileEntity.getUserId().equals(userId)) {
            throw new BusinessException(403, "没有该文件");
        }

        FileEntity fileEntity = baseMapper.selectById(userFileEntity.getFileId());

        if (fileEntity == null) {
            throw new BusinessException(403, "没有该文件");
        }

        // FIXME: 2021/7/6 存在设计漏洞,修改一个文件名,其他同用户同文件名也会变化,文件名不应该存储在文件信息表里,应该存在文件-用户关系表里,这样的话,同文件在不同文件夹也可拥有不同文件名
        fileEntity.setFileName(renameFileOrDirVo.getNewName());
        return baseMapper.updateById(fileEntity) == 1;
    }

    @Override
    public boolean copyFile(String token, CopyFileVo copyFileVo) {

        Integer userId = TokenUtil.getUserId(token);
        UserFileEntity userFileEntity = userFileMapper.selectById(copyFileVo.getUserFileId());

        if (userFileEntity == null || userFileEntity.getUserId().equals(userId)) {
            throw new BusinessException(403, "没有该文件");
        }

        userFileEntity.setUserFileId(null);
        userFileEntity.setDirId(copyFileVo.getDirFromId());
        userFileMapper.insert(userFileEntity);


        return false;
    }

    @Override
    public boolean downloadFile(String token, HttpServletResponse response, Integer userFileId) {

        UserFileEntity userFileEntity = userFileMapper.selectById(userFileId);

        if (userFileEntity == null || !userFileEntity.getUserId().equals(TokenUtil.getUserId(token))) {
            throw new BusinessException(403, "没有该文件");

        }
        FileEntity fileEntity = baseMapper.selectById(userFileEntity.getFileId());

        if (fileEntity == null) {
            userFileMapper.deleteById(userFileId);
            throw new BusinessException(403, "没有该文件");
        }
        // TODO: 2021/7/6 从openstack里获取到文件,然后发送给前端

        return true;
    }

    @Override
    public boolean deleteFile(String token, DeleteVo deleteVo) {
        Integer userId = TokenUtil.getUserId(token);

        UserFileEntity userFileEntity = userFileMapper.selectById(deleteVo.getFileId());

        if (userFileEntity == null || !userFileEntity.getUserId().equals(userId)) {
            throw new BusinessException(403, "没有id为" + deleteVo.getFileId() + "的文件");
        }

        userFileMapper.deleteById(userFileEntity);
        return true;

    }

    @Override
    public FileInfoDto getFileInfo(String token, Integer userFileId) {
        FileInfoDto fileInfoDto = new FileInfoDto();
        UserFileEntity userFileEntity = userFileMapper.selectById(userFileId);

        if (userFileEntity == null || !userFileEntity.getUserId().equals(TokenUtil.getUserId(token))) {
            throw new BusinessException(403, "无此文件");
        }

        FileEntity fileEntity = baseMapper.selectById(userFileEntity.getFileId());

        if (fileEntity == null) {
            userFileMapper.deleteById(userFileEntity);
            throw new BusinessException(403, "无此文件");
        }

        fileInfoDto.setUserFileId(userFileEntity.getUserFileId());
        fileInfoDto.setFileName(fileEntity.getFileName());
        fileInfoDto.setFileSize(fileEntity.getFileSize());
        fileInfoDto.setFileType(fileEntity.getFileType());

        return fileInfoDto;
    }


    /**
     * 上传带文件
     *
     * @param token        token
     * @param updateFileVo updateFileVo
     */
    private boolean updateFileExists(String token, UpdateFileVo updateFileVo) {
        String md5 = getMd5(updateFileVo.getFile());
        if (!md5.equals(updateFileVo.getFileMd5())) {
            throw new BusinessException(403, "md5值不匹配");
        }

        FileEntity fileEntity = new FileEntity();

        String url = RandomStringUtils.random(16, true, true);
        // FIXME: 2021/7/6 此处应为上传到openstack里的链接
        fileEntity.setUrl(url);
        fileEntity.setFileName(updateFileVo.getFile().getOriginalFilename());
        fileEntity.setFileSize(updateFileVo.getFile().getSize());
        fileEntity.setFileType(updateFileVo.getFile().getContentType());
        fileEntity.setMd5(md5);


        baseMapper.insert(fileEntity);

        // 通过md5值和文件夹id确定唯一值
        fileEntity = baseMapper.selectOne(new QueryWrapper<FileEntity>()
                .eq("url", url));
        // 查看同目录是否存在同一文件
        UserFileEntity temp = userFileMapper.selectOne(new QueryWrapper<UserFileEntity>()
                .eq("md5", updateFileVo.getFileMd5())
                .eq("dir_id", updateFileVo.getDirId())
                .eq("user_id", TokenUtil.getUserId(token)));

        if (temp != null) {
            return false;
        }

        // 构建文件-用户关系
        UserFileEntity userFileEntity = new UserFileEntity();
        userFileEntity.setUserId(TokenUtil.getUserId(token));
        userFileEntity.setFileId(fileEntity.getFileId());
        userFileEntity.setDirId(updateFileVo.getDirId());
        userFileEntity.setMd5(md5);

        userFileMapper.insert(userFileEntity);

        return true;
    }

    /**
     * 上传不带文件
     *
     * @param token        token
     * @param updateFileVo updateFileVo
     */
    private boolean updateFileNotExists(String token, UpdateFileVo updateFileVo) {
        Integer userId = TokenUtil.getUserId(token);

        // 查询看是否用户是否已存在该文件
        List<UserFileEntity> userFileEntity = userFileMapper.selectList(new QueryWrapper<UserFileEntity>()
                .eq("user_id", userId)
                .eq("md5", updateFileVo.getFileMd5()));

        // 若未存在,则提示用户上传文件
        if (userFileEntity == null || userFileEntity.size() <= 0) {
            throw new BusinessException(403, "请上传文件");
        }

        // 查询目录是否存在该文件
        int count = userFileMapper.selectCount(new QueryWrapper<UserFileEntity>()
                .eq("user_id", userId)
                .eq("dir_id", updateFileVo.getDirId())
                .eq("md5", updateFileVo.getFileMd5()));
        if (count > 0) {
            throw new BusinessException(403, "该目录已存在此文件");
        }

        // 构建文件-用户关系
        UserFileEntity entity = new UserFileEntity();
        entity.setFileId(userFileEntity.get(0).getFileId());
        entity.setDirId(updateFileVo.getDirId());
        entity.setUserId(userId);
        entity.setMd5(updateFileVo.getFileMd5());

        userFileMapper.insert(entity);

        return true;
    }


    /**
     * 获取上传文件的md5
     *
     * @param file file
     * @return md5值
     */
    public String getMd5(MultipartFile file) {

        try {
            byte[] uploadBytes = file.getBytes();
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digest = md5.digest(uploadBytes);
            return new BigInteger(1, digest).toString(16);
        } catch (Exception ignored) {

        }
        return null;

    }
}
