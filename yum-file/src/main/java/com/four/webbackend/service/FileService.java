package com.four.webbackend.service;

import com.four.webbackend.entity.FileEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.four.webbackend.model.*;

import javax.servlet.http.HttpServletResponse;

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

    boolean mobileFile(String token, MobileFileVo mobileFileVo);

    boolean rename(String token, RenameFileOrDirVo renameFileOrDirVo);

    boolean copyFile(String token, CopyFileVo copyFileVo);

    boolean downloadFile(String token, HttpServletResponse response, Integer userFileId);

    boolean deleteFile(String token, DeleteVo deleteVo);

    FileInfoDto getFileInfo(String token, Integer userFileId);
}
