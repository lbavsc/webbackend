package com.four.webbackend.controller.file;


import com.four.webbackend.model.CopyFileVo;
import com.four.webbackend.model.DirDto;
import com.four.webbackend.model.ResultEntity;
import com.four.webbackend.service.DirService;
import com.four.webbackend.util.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

/**
 * <p>
 * 文件夹信息表 前端控制器
 * </p>
 *
 * @author lbavsc
 * @since 2021-07-05
 */
@RestController
@Api(tags = "文件夹模块")
@RequestMapping("/dir")
public class DirController {

    private final DirService dirService;

    @Autowired
    public DirController(DirService dirService) {
        this.dirService = dirService;
    }

    @ApiOperation("查看文件夹内容")
    @GetMapping("/getDirContent")
    @RequiresRoles(logical = Logical.OR, value = {"user"})
    public ResultEntity getDirContent(@ApiParam("当前操作用户token") @RequestHeader() @NotNull(message = "token不能为空") String token,
                                      @RequestParam String dirId) {
        DirDto dto = dirService.getDirContent(token, dirId);
        if (dto == null) {
            return ResultUtil.error(403, "无数据");
        }
        return ResultUtil.success(dto);
    }
}

