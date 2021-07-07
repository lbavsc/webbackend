package com.four.webbackend.controller.trash;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.four.webbackend.model.CollectDto;
import com.four.webbackend.model.FileInfoDto;
import com.four.webbackend.model.MyPageVo;
import com.four.webbackend.model.ResultEntity;
import com.four.webbackend.service.UserFileService;
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
 * @author lbavsc
 * @version 1.0
 * @className TrashController
 * @description
 * @date 2021/7/7 下午1:18
 **/
@RestController
@Api(tags = "回收站模块")
@RequestMapping("/trash")
public class TrashController {

    private final UserFileService userFileService;

    @Autowired
    public TrashController(UserFileService userFileService) {
        this.userFileService = userFileService;
    }

    @ApiOperation("查看用户回收站列表")
    @GetMapping("/listRecycle")
    @RequiresRoles(logical = Logical.OR, value = {"user"})
    public ResultEntity listRecycle(@ApiParam("当前操作用户token") @RequestHeader() @NotNull(message = "token不能为空") String token,
                                    @ApiParam("查询的页数") @RequestParam(required = false, defaultValue = "1") Integer current,
                                    @ApiParam("一页的数量") @RequestParam(required = false, defaultValue = "10") Integer size) {
        MyPageVo<FileInfoDto> myPageVo = new MyPageVo<>(current, size);
        IPage<FileInfoDto> infoDtoIpage = userFileService.listRecycle(myPageVo, token);


        if (infoDtoIpage.getRecords().size() <= 0) {
            return ResultUtil.error(200, "无数据");
        }
        return ResultUtil.success(infoDtoIpage.getRecords(), infoDtoIpage.getPages(), infoDtoIpage.getTotal());
    }

    @ApiOperation("恢复文件")
    @GetMapping("/restoreFile")
    @RequiresRoles(logical = Logical.OR, value = {"user"})
    public ResultEntity restoreFile(@ApiParam("当前操作用户token") @RequestHeader() @NotNull(message = "token不能为空") String token,
                                    @ApiParam("文件id") @RequestParam Integer userFileId) {
        userFileService.restoreFile(token, userFileId);
        return ResultUtil.success();
    }

    @ApiOperation("彻底删除文件")
    @GetMapping("/realDel")
    @RequiresRoles(logical = Logical.OR, value = {"user"})
    public ResultEntity realDel(@ApiParam("当前操作用户token") @RequestHeader() @NotNull(message = "token不能为空") String token,
                                @ApiParam("文件id") @RequestParam Integer userFileId) {
        userFileService.realDel(token, userFileId);
        return ResultUtil.success();
    }
}
