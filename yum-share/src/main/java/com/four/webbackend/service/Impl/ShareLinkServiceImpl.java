package com.four.webbackend.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.four.webbackend.entity.*;
import com.four.webbackend.exception.BusinessException;
import com.four.webbackend.mapper.*;
import com.four.webbackend.model.*;
import com.four.webbackend.service.ShareLinkService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.four.webbackend.util.DataUtil;
import com.four.webbackend.util.TokenUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 分享链接表 服务实现类
 * </p>
 *
 * @author lbavsc
 * @since 2021-07-05
 */
@Service
public class ShareLinkServiceImpl extends ServiceImpl<ShareLinkMapper, ShareLinkEntity> implements ShareLinkService {

    private final FileMapper fileMapper;
    private final DirMapper dirMapper;
    private final UserFileMapper userFileMapper;
    private final UserMapper userMapper;

    /**
     * 一天
     */
    public static final long ONE_DAY_EXPIRE_TIME = 24 * 60 * 60;

    /**
     * 三天
     */
    public static final long THREE_DAY_EXPIRE_TIME = 3 * 24 * 60 * 60;

    /**
     * 七天
     */
    public static final long SEVEN_DAY_EXPIRE_TIME = 7 * 24 * 60 * 60;

    /**
     * 三十天
     */
    public static final long THIRTY_DAY_EXPIRE_TIME = 30 * 24 * 60 * 60;

    /**
     * 永久
     */
    public static final long PERMANENT_EXPIRE_TIME = 99999L * 24 * 60 * 60;

    @Autowired
    public ShareLinkServiceImpl(FileMapper fileMapper, DirMapper dirMapper, UserFileMapper userFileMapper, UserMapper userMapper) {
        this.fileMapper = fileMapper;
        this.dirMapper = dirMapper;
        this.userFileMapper = userFileMapper;
        this.userMapper = userMapper;
    }


    @Override
    public String share(String token, ShareVo shareVo) {
        Integer userId = TokenUtil.getUserId(token);
        System.err.println(shareVo);
        ShareLinkEntity shareLinkEntity = new ShareLinkEntity();
        Date expire = getExpireDate(shareVo.getExpire());
        if (expire == null) {
            throw new BusinessException(403, "过期时间设置错误");
        }
        shareLinkEntity.setExpire(expire);

        UserEntity targetEntity = userMapper.selectOne(new QueryWrapper<UserEntity>()
                .eq("user_uuid", shareVo.getTargetId()));

        if (targetEntity == null) {
            throw new BusinessException(403, "无uuid为" + shareVo.getTargetId() + "的人员");
        }


        if (!shareVo.getIsDir()) {
            UserFileEntity userFileEntity = userFileMapper.selectById(shareVo.getObjectId());

            if (userFileEntity == null || !userFileEntity.getUserId().equals(userId)) {
                throw new BusinessException(403, "没有该文件");
            }
            shareLinkEntity.setFileId(userFileEntity.getFileId());
        } else {
            DirEntity dirEntity = dirMapper.selectById(shareVo.getObjectId());

            if (dirEntity == null || !dirEntity.getUserId().equals(userId)) {
                throw new BusinessException(403, "没有该文件");
            }
            shareLinkEntity.setFileId(dirEntity.getDirId());

        }

        shareLinkEntity.setUserId(userId);
//        shareLinkEntity.setIsDir(shareLinkEntity.getIsDir());

        shareLinkEntity.setIsDir(shareVo.getIsDir());

        String url = RandomStringUtils.random(16, true, true);
        shareLinkEntity.setLink(url);

        shareLinkEntity.setTargetId(targetEntity.getUserId());


        if (baseMapper.insert(shareLinkEntity) != 1) {
            throw new BusinessException(403, "分享失败,请重试");
        }


        return url;

    }

    @Override
    public List<ShareListDto> listShare(String token) {
        Integer userId = TokenUtil.getUserId(token);
        List<ShareListDto> rest = new ArrayList<>();

        List<ShareLinkEntity> shareListDtos = baseMapper.selectList(new QueryWrapper<ShareLinkEntity>()
                .eq("user_id", userId));

        for (ShareLinkEntity shareListDto : shareListDtos) {
            if (DataUtil.afterDateNow(shareListDto.getExpire())) {
                baseMapper.deleteById(shareListDto.getShareLinkId());
                continue;
            }
            ShareListDto dto = new ShareListDto();
            dto.setSahreId(shareListDto.getShareLinkId());
            dto.setExpire(shareListDto.getExpire());
            dto.setTargetId(shareListDto.getTargetId());
            dto.setShareUrl(shareListDto.getLink());
            rest.add(dto);
        }

        return rest;

    }

    @Override
    public boolean recellShare(String token, Integer shareId) {
        ShareLinkEntity shareLinkEntity = baseMapper.selectById(shareId);
        Integer userId = TokenUtil.getUserId(token);

        if (shareLinkEntity == null || !shareLinkEntity.getUserId().equals(userId)) {
            throw new BusinessException(403, "没有该分享链接");
        }

        return baseMapper.deleteById(shareId) == 1;

    }

    @Override
    public DirDto getShareUrlContent(String token, String shareUrl) {
        Integer userId = TokenUtil.getUserId(token);
        DirDto dirDto = new DirDto();
        ShareLinkEntity shareLinkEntity = baseMapper.selectOne(new QueryWrapper<ShareLinkEntity>()
                .eq("link", shareUrl));

        boolean isPass = shareLinkEntity == null || (!shareLinkEntity.getTargetId().equals(0)) && !shareLinkEntity.getTargetId().equals(userId);
        if (isPass || DataUtil.afterDateNow(shareLinkEntity.getExpire())) {
            throw new BusinessException(403, "没有该分享链接");
        }

        List<FileInfoDto> fileInfoDtos = new ArrayList<>();

        if (shareLinkEntity.getIsDir()) {
            DirEntity dirEntity = dirMapper.selectById(shareLinkEntity.getFileId());
            if (dirEntity == null) {
                throw new BusinessException(403, "文件夹不存在");
            }
            List<UserFileEntity> userFileEntityList = userFileMapper.selectList(new QueryWrapper<UserFileEntity>()
                    .eq("dir_id", dirEntity.getDirId())
                    .eq("user_id", shareLinkEntity.getUserId()));

            if (userFileEntityList == null || userFileEntityList.size() <= 0) {
                throw new BusinessException(403, "文件夹不存在");
            }

            for (UserFileEntity userFileEntity : userFileEntityList) {
                FileInfoDto dto = getFileInfo(userFileEntity);
                fileInfoDtos.add(dto);
                dirDto.setFileInfoDtos(fileInfoDtos);
            }

        } else {
            UserFileEntity userFileEntity = userFileMapper.selectById(shareLinkEntity.getFileId());

            if (userFileEntity == null) {
                throw new BusinessException(403, "文件不存在");
            }

            FileInfoDto dto = getFileInfo(userFileEntity);
            fileInfoDtos.add(dto);
            dirDto.setFileInfoDtos(fileInfoDtos);
        }

        return dirDto;
    }

    @Override
    public boolean saveShare(String token, String shareUrl, Integer objectId, Integer targetDir, Boolean isDir) {
        Integer userId = TokenUtil.getUserId(token);
        ShareLinkEntity shareLinkEntity = baseMapper.selectOne(new QueryWrapper<ShareLinkEntity>()
                .eq("link", shareUrl));
        boolean isPass = shareLinkEntity == null || (!shareLinkEntity.getTargetId().equals(0)) && !shareLinkEntity.getTargetId().equals(userId);
        if (isPass) {
            throw new BusinessException(403, "没有该分享链接");
        }

        DirEntity dirEntity = dirMapper.selectById(targetDir);
        if (dirEntity == null || !dirEntity.getUserId().equals(userId)) {
            throw new BusinessException(403, "目标目录ID不正确");
        }

        if (isDir) {
            if (!shareLinkEntity.getFileId().equals(objectId)) {
                throw new BusinessException(403, "无权限");
            }

            List<UserFileEntity> userFileEntityList = userFileMapper.selectList(new QueryWrapper<UserFileEntity>()
                    .eq("dir_id", objectId));

            if (userFileEntityList == null || userFileEntityList.size() <= 0) {
                throw new BusinessException(403, "文件夹内无文件");
            }


            userFileEntityList.forEach(entity -> {
                entity.setUserFileId(null);
                entity.setDirId(targetDir);
                entity.setUserId(userId);
                userFileMapper.insert(entity);
            });


        } else {
            UserFileEntity userFileEntity = userFileMapper.selectById(objectId);

            if (userFileEntity == null) {
                throw new BusinessException(403, "无此文件");
            }
            userFileEntity.setUserFileId(null);
            userFileEntity.setDirId(targetDir);
            userFileEntity.setUserId(userId);
            userFileMapper.insert(userFileEntity);
        }

        return true;
    }


    private FileInfoDto getFileInfo(UserFileEntity userFileEntity) {
        FileEntity fileEntity = fileMapper.selectById(userFileEntity.getFileId());
        if (fileEntity == null) {
            throw new BusinessException(403, "文件不存在");
        }
        FileInfoDto dto = new FileInfoDto();
        dto.setFileName(fileEntity.getFileName());
        dto.setUserFileId(userFileEntity.getUserFileId());
        dto.setFileType(fileEntity.getFileType());
        dto.setFileSize(fileEntity.getFileSize());
        return dto;
    }


    private Date getExpireDate(Integer expire) {
        Date date;
        long currentTimeMillis = System.currentTimeMillis();
        if (expire == null) {
            expire = 1;
        }

        if (expire == 1) {
            date = new Date(currentTimeMillis + ONE_DAY_EXPIRE_TIME);
        } else if (expire == 2) {
            date = new Date(currentTimeMillis + THREE_DAY_EXPIRE_TIME);
        } else if (expire == 3) {
            date = new Date(currentTimeMillis + SEVEN_DAY_EXPIRE_TIME);
        } else if (expire == 4) {
            date = new Date(currentTimeMillis + THIRTY_DAY_EXPIRE_TIME);
        } else if (expire == 5) {
            date = new Date(currentTimeMillis + PERMANENT_EXPIRE_TIME);
        } else {
            return null;
        }

        return date;
    }
}
