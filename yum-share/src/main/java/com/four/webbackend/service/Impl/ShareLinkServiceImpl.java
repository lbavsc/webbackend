package com.four.webbackend.service.Impl;

import com.four.webbackend.entity.DirEntity;
import com.four.webbackend.entity.FileEntity;
import com.four.webbackend.entity.ShareLinkEntity;
import com.four.webbackend.entity.UserFileEntity;
import com.four.webbackend.handler.GlobalExceptionHandler;
import com.four.webbackend.mapper.DirMapper;
import com.four.webbackend.mapper.FileMapper;
import com.four.webbackend.mapper.ShareLinkMapper;
import com.four.webbackend.mapper.UserFileMapper;
import com.four.webbackend.model.ShareVo;
import com.four.webbackend.service.ShareLinkService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.four.webbackend.util.TokenUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
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

    // 1天
    public static final long ONE_DAY_EXPIRE_TIME = 24 * 60 * 60;

    // 3天
    public static final long THREE_DAY_EXPIRE_TIME = 3 * 24 * 60 * 60;

    // 7天
    public static final long SEVEN_DAY_EXPIRE_TIME = 3 * 24 * 60 * 60;

    // 30天
    public static final long THIRTY_DAY_EXPIRE_TIME = 3 * 24 * 60 * 60;

    // 30天
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
        String shareUrl;
        Integer userId = TokenUtil.getUserId(token);
        if (shareVo.getIsDir()) {
            shareUrl = shareDir(response, userId, shareVo);
        } else {
            shareUrl = shareFile(response, userId, shareVo);
        }

        return shareUrl;

    }

    private String shareFile(HttpServletResponse response, Integer userId, ShareVo shareVo) {

        UserFileEntity userFileEntity = userFileMapper.selectById(shareVo.getObjectId());

        if (userFileEntity == null || !userFileEntity.getUserId().equals(userId)) {
            GlobalExceptionHandler.responseError(response, "没有该文件");
            return null;
        }


        ShareLinkEntity shareLinkEntity = new ShareLinkEntity();
        Date expire = getExpireDate(shareVo.getExpire());
        if (expire == null) {
            GlobalExceptionHandler.responseError(response, "过期时间设置错误");
            return null;
        }
        shareLinkEntity.setExpire(expire);

        String url = RandomStringUtils.random(16, true, true);
        shareLinkEntity.setLink(url);

        shareLinkEntity.setFileId(userFileEntity.getFileId());

        shareLinkEntity.setUserId(userId);

        shareLinkEntity.setIsDir(false);


        if (baseMapper.insert(shareLinkEntity) == 1) {
            return url;
        }
        return null;
    }


    private String shareDir(HttpServletResponse response, Integer userId, ShareVo shareVo) {

        DirEntity dirEntity = dirMapper.selectById(shareVo.getObjectId());

        if (dirEntity == null || !dirEntity.getUserId().equals(userId)) {
            GlobalExceptionHandler.responseError(response, "没有该文件");
            return null;
        }

        ShareLinkEntity shareLinkEntity = new ShareLinkEntity();
        Date expire = getExpireDate(shareVo.getExpire());
        if (expire == null) {
            GlobalExceptionHandler.responseError(response, "过期时间设置错误");
            return null;
        }
        shareLinkEntity.setExpire(expire);

        String url = RandomStringUtils.random(16, true, true);
        shareLinkEntity.setLink(url);

        shareLinkEntity.setFileId(dirEntity.getDirId());

        shareLinkEntity.setUserId(userId);

        shareLinkEntity.setIsDir(true);


        if (baseMapper.insert(shareLinkEntity) == 1) {
            return url;
        }
        return null;
    }

    private Date getExpireDate(Integer expire) {
        Date date = null;
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
            ;
        }

        return date;
    }

    private HttpServletResponse getResponse() {
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getResponse();
    }
}
