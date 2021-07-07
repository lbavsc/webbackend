package com.four.webbackend.service;

import com.four.webbackend.entity.ShareLinkEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.four.webbackend.model.ShareVo;

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
}
