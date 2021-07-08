package com.four.webbackend.controller.file;


import com.four.webbackend.model.*;
import com.four.webbackend.service.DirService;
import com.four.webbackend.service.FileService;
import com.four.webbackend.service.UserFileService;
import com.four.webbackend.util.CheckCodeUtil;
import com.four.webbackend.util.ResultUtil;
import com.four.webbackend.util.TokenUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * 文件信息表 前端控制器
 * </p>
 *
 * @author lbavsc
 * @since 2021-07-05
 */
@RestController
@Api(tags = "文件模块")
@RequestMapping("/file")
public class FileController {

    private final FileService fileService;

    private final UserFileService userFileService;

    private final DirService dirService;

    @Autowired
    public FileController(FileService fileService,
                          UserFileService userFileService,
                          DirService dirService) {
        this.fileService = fileService;
        this.userFileService = userFileService;
        this.dirService = dirService;
    }

    @ApiOperation("上传文件")
    @PostMapping("/update")
    @RequiresRoles(logical = Logical.OR, value = {"user"})
    public ResultEntity modifyInfo(@ApiParam("当前操作用户token") @RequestHeader() @NotNull(message = "token不能为空") String token,
                                   UpdateFileVo updateFileVo) {

        // 没有上传文件且该人员目录不存在同文件时
        if (updateFileVo.getFile() == null && !userFileService.isExist(token, updateFileVo.getFileMd5())) {
            return ResultUtil.error(403, "请上传文件");
        }


        if (!dirService.isExistDir(token, updateFileVo.getDirId())) {
            return ResultUtil.error(403, "目录不存在");
        }

        // 如果服务端已存在同文件且上传文件不为空
        if (updateFileVo.getFile() != null && userFileService.isExist(token, updateFileVo.getFileMd5())) {
            updateFileVo.setFile(null);
        }

        if (!fileService.updateFile(token, updateFileVo)) {
            return null;
        }
        return ResultUtil.success();
    }

    @ApiOperation("移动文件")
    @PostMapping("/mobileFile")
    @RequiresRoles(logical = Logical.OR, value = {"user"})
    public ResultEntity mobileFile(@ApiParam("当前操作用户token") @RequestHeader() @NotNull(message = "token不能为空") String token,
                                   MobileFileVo mobileFileVo) {

        if (!fileService.mobileFile(token, mobileFileVo)) {
            return null;
        }

        return ResultUtil.success();
    }

    @ApiOperation("重命名文件或文件夹")
    @PostMapping("/rename")
    @RequiresRoles(logical = Logical.OR, value = {"user"})
    public ResultEntity rename(@ApiParam("当前操作用户token") @RequestHeader() @NotNull(message = "token不能为空") String token,
                               RenameFileOrDirVo renameFileOrDirVo) {

        if (renameFileOrDirVo.getIsDir()) {
            if (!dirService.rename(token, renameFileOrDirVo)) {
                return null;
            }
        } else {
            if (!fileService.rename(token, renameFileOrDirVo)) {
                return null;
            }
        }
        return ResultUtil.success();
    }

    @ApiOperation("复制文件")
    @PostMapping("/copyFile")
    @RequiresRoles(logical = Logical.OR, value = {"user"})
    public ResultEntity copyFile(@ApiParam("当前操作用户token") @RequestHeader() @NotNull(message = "token不能为空") String token,
                                 CopyFileVo copyFileVo) {

        if (!fileService.copyFile(token, copyFileVo)) {
            return null;
        }
        return ResultUtil.success();
    }

    @ApiOperation("下载文件")
    @PostMapping("/downloadFile")
    @RequiresRoles(logical = Logical.OR, value = {"user"})
    public ResultEntity downloadFile(@ApiParam("当前操作用户token") @RequestHeader() @NotNull(message = "token不能为空") String token,
                                     @ApiParam("文件ID") @RequestParam Integer userFileId,
                                     HttpServletResponse response) {

        if (fileService.downloadFile(token, response, userFileId)) {
//            return null;
            return ResultUtil.success("下载文件成功");
        }
        return ResultUtil.error(403, "发生错误");
    }

    @ApiOperation("删除文件或文件夹")
    @PostMapping("/deleteFile")
    @RequiresRoles(logical = Logical.OR, value = {"user"})
    public ResultEntity deleteFile(@ApiParam("当前操作用户token") @RequestHeader() @NotNull(message = "token不能为空") String token,
                                   @RequestBody DeleteFileVo deleteFileVo) {

        for (DeleteVo deleteVo : deleteFileVo.getDeleteList()) {
            if (deleteVo.getIsDir()) {
                if (!dirService.deleteDir(token, deleteVo)) {
                    return null;
                }
            } else {
                if (!fileService.deleteFile(token, deleteVo)) {
                    return null;
                }
            }
        }
        return ResultUtil.success();
    }

    @ApiOperation("查看文件信息")
    @GetMapping("/getFileInfo")
    @RequiresRoles(logical = Logical.OR, value = {"user"})
    public ResultEntity getFileInfo(@ApiParam("当前操作用户token") @RequestHeader() @NotNull(message = "token不能为空") String token,
                                   @RequestParam Integer fileId) {

        FileInfoDto dto = fileService.getFileInfo(token,fileId);
        if (dto == null) {
            return null;
        }
        return ResultUtil.success(dto);
    }

}

