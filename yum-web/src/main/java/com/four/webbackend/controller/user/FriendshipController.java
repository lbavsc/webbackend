package com.four.webbackend.controller.user;


import com.four.webbackend.model.FriendshipDto;
import com.four.webbackend.model.ResultEntity;
import com.four.webbackend.model.UserDto;
import com.four.webbackend.service.FriendshipService;
import com.four.webbackend.util.ResultUtil;
import com.four.webbackend.util.TokenUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Tag;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>
 * 好友关系表 前端控制器
 * </p>
 *
 * @author lbavsc
 * @since 2021-07-05
 */
@RestController
@Api(tags = "好友模块")
@RequestMapping("/friendship")
public class FriendshipController {

    private final FriendshipService friendshipService;

    @Autowired
    public FriendshipController(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    @ApiOperation("获取好友列表")
    @GetMapping("/listBuddy")
    @RequiresRoles(logical = Logical.OR, value = {"user"})
    public ResultEntity listBuddy(@ApiParam("当前操作用户token") @RequestHeader() @NotNull(message = "token不能为空") String token) {

        List<FriendshipDto> dtos = friendshipService.listBuddy(token);

        if (dtos == null) {
            return null;
        }
        if (dtos.size() == 0) {
            return ResultUtil.error(403, "无数据");
        }

        return ResultUtil.success(dtos);
    }

    @ApiOperation("添加好友")
    @GetMapping("/addBuddy")
    @RequiresRoles(logical = Logical.OR, value = {"user"})
    public ResultEntity addBuddy(@ApiParam("当前操作用户token") @RequestHeader() @NotNull(message = "token不能为空") String token,
                                 @ApiParam("人员的UUID或者邮箱") @RequestParam String uid) {

        if (!friendshipService.deleteBuddy(token, uid)) {
            return null;
        }

        return ResultUtil.success();
    }

    @ApiOperation("删除好友")
    @GetMapping("/deleteBuddy")
    @RequiresRoles(logical = Logical.OR, value = {"user"})
    public ResultEntity deleteBuddy(@ApiParam("当前操作用户token") @RequestHeader() @NotNull(message = "token不能为空") String token,
                                    @ApiParam("人员的UUID或者邮箱") @RequestParam String identifier) {

        ;
        if (!friendshipService.addBuddy(token, identifier)) {
            return null;
        }
        return ResultUtil.success();
    }

}

