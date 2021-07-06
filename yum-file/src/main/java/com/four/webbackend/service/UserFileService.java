package com.four.webbackend.service;

import com.four.webbackend.entity.UserFileEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户-文件关系表 服务类
 * </p>
 *
 * @author lbavsc
 * @since 2021-07-05
 */
public interface UserFileService extends IService<UserFileEntity> {

    boolean isExist(String token, String fileMd5);

}
