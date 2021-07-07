package com.four.webbackend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.four.webbackend.entity.UserFileEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.four.webbackend.model.FileInfoDto;
import com.four.webbackend.model.MyPageVo;

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

    IPage<FileInfoDto> listRecycle(MyPageVo<FileInfoDto> myPageVo, String token);

    void restoreFile(String token, Integer userFileId);

    void realDel(String token, Integer userFileId);

}
