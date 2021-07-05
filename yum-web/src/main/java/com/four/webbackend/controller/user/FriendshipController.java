package com.four.webbackend.controller.user;


import com.four.webbackend.model.FriendshipDto;
import com.four.webbackend.model.ResultEntity;
import com.four.webbackend.model.UserDto;
import com.four.webbackend.service.FriendshipService;
import com.four.webbackend.util.ResultUtil;
import com.four.webbackend.util.TokenUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        String uuid = TokenUtil.getAccount(token);
        List<FriendshipDto> dtos = friendshipService.listBuddy(uuid);

        if (dtos == null) {
            return ResultUtil.error(403, "无数据");
        }

        return ResultUtil.success(dtos);
    }



}

