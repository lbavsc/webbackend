package com.four.webbackend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.four.webbackend.entity.CollectEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.four.webbackend.model.CollectDto;
import com.four.webbackend.model.MyPageVo;

/**
 * <p>
 * 收藏表 服务类
 * </p>
 *
 * @author lbavsc
 * @since 2021-07-05
 */
public interface CollectService extends IService<CollectEntity> {

    IPage<CollectDto> listFavor(MyPageVo<CollectDto> myPageVo, String token);

    boolean favorFile(String token, Integer userFileId);
}
