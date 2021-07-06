package com.four.webbackend.controller.file;


import com.four.webbackend.model.ResultEntity;
import com.four.webbackend.model.UpdateFileVo;
import com.four.webbackend.model.UserDto;
import com.four.webbackend.model.UserVo;
import com.four.webbackend.service.DirService;
import com.four.webbackend.service.FileService;
import com.four.webbackend.service.UserFileService;
import com.four.webbackend.util.CheckCodeUtil;
import com.four.webbackend.util.ResultUtil;
import com.four.webbackend.util.TokenUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/modifyInfo")
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

        if (fileService.updateFile(token, updateFileVo)) {
            return null;
        }
        return ResultUtil.success();
    }


}

