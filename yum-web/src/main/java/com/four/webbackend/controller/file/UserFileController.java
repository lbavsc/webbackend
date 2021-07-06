package com.four.webbackend.controller.file;


import com.four.webbackend.model.ResultEntity;
import com.four.webbackend.service.UserFileService;
import com.four.webbackend.util.ResultUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

/**
 * <p>
 * 用户-文件关系表 前端控制器
 * </p>
 *
 * @author lbavsc
 * @since 2021-07-05
 */
@RestController
@RequestMapping("/user/file")
public class UserFileController {

    private final UserFileService userFileService;

    @Autowired
    public UserFileController(UserFileService userFileService) {
        this.userFileService = userFileService;
    }

    @ApiOperation("检测是否存在同文件")
    @GetMapping("/isExist")
    @RequiresRoles(logical = Logical.OR, value = {"user"})
    public ResultEntity isExist(@ApiParam("当前操作用户token") @RequestHeader() @NotNull(message = "token不能为空") String token,
                                @ApiParam("文件md5值") @RequestParam String fileMd5) {
        return ResultUtil.success(userFileService.isExist(token, fileMd5));
    }
}

