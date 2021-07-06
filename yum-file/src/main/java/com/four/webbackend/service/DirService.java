package com.four.webbackend.service;

import com.four.webbackend.entity.DirEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.four.webbackend.model.RenameFileOrDirVo;

/**
 * <p>
 * 文件夹信息表 服务类
 * </p>
 *
 * @author lbavsc
 * @since 2021-07-05
 */
public interface DirService extends IService<DirEntity> {


    boolean isExistDir(String token, Integer dirId);

    boolean rename(String token, RenameFileOrDirVo renameFileOrDirVo);
}
