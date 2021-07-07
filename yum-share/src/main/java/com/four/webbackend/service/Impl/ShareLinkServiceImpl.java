package com.four.webbackend.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.four.webbackend.entity.*;
import com.four.webbackend.handler.GlobalExceptionHandler;
import com.four.webbackend.mapper.*;
import com.four.webbackend.model.*;
import com.four.webbackend.service.ShareLinkService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.four.webbackend.util.TokenUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
    public ShareLinkServiceImpl(FileMapper fileMapper, DirMapper dirMapper, UserFileMapper userFileMapper) {
        this.fileMapper = fileMapper;
        this.dirMapper = dirMapper;
        this.userFileMapper = userFileMapper;
    }


    @Override
    public String share(String token, ShareVo shareVo) {
        HttpServletResponse response = getResponse();
        Integer userId = TokenUtil.getUserId(token);

        ShareLinkEntity shareLinkEntity = new ShareLinkEntity();
        Date expire = getExpireDate(shareVo.getExpire());
        if (expire == null) {
            GlobalExceptionHandler.responseError(response, "过期时间设置错误");
            return null;
        }

        if (!shareVo.getIsDir()) {
            UserFileEntity userFileEntity = userFileMapper.selectById(shareVo.getObjectId());

            if (userFileEntity == null || !userFileEntity.getUserId().equals(userId)) {
                GlobalExceptionHandler.responseError(response, "没有该文件");
                return null;
            }
            shareLinkEntity.setFileId(userFileEntity.getFileId());
        } else {
            DirEntity dirEntity = dirMapper.selectById(shareVo.getObjectId());

            if (dirEntity == null || !dirEntity.getUserId().equals(userId)) {
                GlobalExceptionHandler.responseError(response, "没有该文件");
                return null;
            }
            shareLinkEntity.setFileId(dirEntity.getDirId());
        }

        shareLinkEntity.setUserId(userId);
        shareLinkEntity.setIsDir(shareLinkEntity.getIsDir());

        String url = RandomStringUtils.random(16, true, true);
        shareLinkEntity.setLink(url);

        shareLinkEntity.setTargetId(shareVo.getTargetId());
        if (baseMapper.insert(shareLinkEntity) != 1) {
            GlobalExceptionHandler.responseError(response, "分享失败,请重试");
            return null;
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
        HttpServletResponse response = getResponse();
        ShareLinkEntity shareLinkEntity = baseMapper.selectById(shareId);
        Integer userId = TokenUtil.getUserId(token);

        if (shareLinkEntity == null || !shareLinkEntity.getUserId().equals(userId)) {
            GlobalExceptionHandler.responseError(response, "没有该分享链接");
            return false;
        }

        return baseMapper.deleteById(shareId) == 1;

    }

    @Override
    public DirDto getShareUrlContent(String token, String shareUrl) {
        HttpServletResponse response = getResponse();
        Integer userId = TokenUtil.getUserId(token);
        DirDto dirDto = new DirDto();
        ShareLinkEntity shareLinkEntity = baseMapper.selectOne(new QueryWrapper<ShareLinkEntity>()
        .eq("link", shareUrl));

        boolean isPass = shareLinkEntity == null || (!shareLinkEntity.getTargetId().equals(0)) && !shareLinkEntity.getTargetId().equals(userId);
        if (isPass) {
            GlobalExceptionHandler.responseError(response, "没有该分享链接");
            return null;
        }

        List<FileInfoDto> fileInfoDtos = new ArrayList<>() ;

        if (shareLinkEntity.getIsDir()) {
            DirEntity dirEntity = dirMapper.selectById(shareLinkEntity.getFileId());
            if (dirEntity == null) {
                GlobalExceptionHandler.responseError(response, "文件夹不存在");
                return null;
            }
            List<UserFileEntity> userFileEntityList = userFileMapper.selectList(new QueryWrapper<UserFileEntity>()
            .eq("dir_id", dirEntity.getDirId())
            .eq("user_id", shareLinkEntity.getUserId()));

            if (userFileEntityList == null || userFileEntityList.size() <= 0) {
                GlobalExceptionHandler.responseError(response, "文件夹不存在");
                return null;
            }

            for (UserFileEntity userFileEntity : userFileEntityList) {
                FileInfoDto dto = getFileInfo(userFileEntity, response);
                if (dto == null) {
                    return null;
                }
                fileInfoDtos.add(dto);
                dirDto.setFileInfoDtos(fileInfoDtos);
            }

        } else {
            UserFileEntity userFileEntity = userFileMapper.selectById(shareLinkEntity.getFileId());

            if (userFileEntity == null) {
                GlobalExceptionHandler.responseError(response, "文件不存在");
                return null;
            }

            FileInfoDto dto = getFileInfo(userFileEntity, response);
            if (dto == null) {
                return null;
            }
            fileInfoDtos.add(dto);
            dirDto.setFileInfoDtos(fileInfoDtos);
        }

        return dirDto;
    }

    private FileInfoDto getFileInfo(UserFileEntity userFileEntity, HttpServletResponse response) {
        FileEntity fileEntity = fileMapper.selectById(userFileEntity.getFileId());
        if (fileEntity == null) {
            GlobalExceptionHandler.responseError(response, "文件不存在");
            return null;
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

    private HttpServletResponse getResponse() {
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getResponse();
    }
}
