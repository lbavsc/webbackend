package com.four.webbackend.service;

import com.four.webbackend.entity.ShareLinkEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.four.webbackend.model.DirDto;
import com.four.webbackend.model.ShareListDto;
import com.four.webbackend.model.ShareVo;

import java.util.List;

/**
 * <p>
 * 分享链接表 服务类
 * </p>
 *
 * @author lbavsc
 * @since 2021-07-05
 */
public interface ShareLinkService extends IService<ShareLinkEntity> {


    String share(String token, ShareVo shareVo);

    List<ShareListDto> listShare(String token);

    boolean recellShare(String token, Integer shareId);

    DirDto getShareUrlContent(String token, String shareUrl);
}
