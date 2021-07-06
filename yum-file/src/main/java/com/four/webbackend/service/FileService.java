package com.four.webbackend.service;

import com.four.webbackend.entity.FileEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.four.webbackend.model.UpdateFileVo;

/**
 * <p>
 * 文件信息表 服务类
 * </p>
 *
 * @author lbavsc
 * @since 2021-07-05
 */
public interface FileService extends IService<FileEntity> {


    boolean updateFile(String token, UpdateFileVo updateFileVo);
}
