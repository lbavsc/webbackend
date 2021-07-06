package com.four.webbackend.controller.user;


import com.four.webbackend.model.*;
import com.four.webbackend.service.UserService;
import com.four.webbackend.util.CheckCodeUtil;
import com.four.webbackend.util.ResultUtil;
import com.four.webbackend.util.TokenUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author lbavsc
 * @since 2021-07-05
 */
@RestController
@Api(tags = "用户模块")
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ApiOperation("登录")
    @PostMapping("/login")
    public ResultEntity login(@ApiParam("登录表单") @RequestBody @Valid LoginVo loginVo, HttpSession session, HttpServletResponse response) {
        if (!CheckCodeUtil.isCheckCaptcha(loginVo.getCheckCode(), session)) {
            return ResultUtil.error(401, "验证码不正确");
        }

        if (!userService.login(loginVo, response)) {
            return ResultUtil.error(403, "登录出错,请检查密码或邮箱是否正确");
        }
        return ResultUtil.success();
    }

    @ApiOperation("注册")
    @PostMapping("/register")
    public ResultEntity regist(@Valid @RequestBody RegistVo registVo) {
        if (!CheckCodeUtil.isEmailCheck(registVo.getUserEmail(), registVo.getCheckCode())) {
            return ResultUtil.error(401, "验证码不正确");
        }


        userService.regist(registVo);

        return ResultUtil.success();
    }

    @ApiOperation("获得个人信息")
    @GetMapping("/getInfo")
    @RequiresRoles(logical = Logical.OR, value = {"user"})
    public ResultEntity info(@ApiParam("当前操作用户token") @RequestHeader() @NotNull(message = "token不能为空") String token) {
        UserDto dto = userService.info(TokenUtil.getAccount(token));
        if (dto == null) {
            return ResultUtil.error(403, "获取个人信息失败,请重试");
        }
        return ResultUtil.success(dto);
    }

    @ApiOperation("修改个人信息")
    @PostMapping("/modifyInfo")
    @RequiresRoles(logical = Logical.OR, value = {"user"})
    public ResultEntity modifyInfo(@ApiParam("当前操作用户token") @RequestHeader() @NotNull(message = "token不能为空") String token,
                                   @ApiParam("资料表单") @RequestBody UserVo userVo) {

        if (userVo.getUserEmail() != null) {
            if (userVo.getCheckCode() == null) {
                return ResultUtil.error(403, "修改邮箱需要填写验证码");
            }
            if (!CheckCodeUtil.isEmailCheck(userVo.getUserEmail(), userVo.getCheckCode())) {
                return ResultUtil.error(403, "验证码不正确");
            }
        }
        String uuid = TokenUtil.getAccount(token);
        UserDto dto = userService.modify(userVo, uuid);
        if (dto == null) {
            return ResultUtil.error(403, "修改失败,请重试");
        }
        return ResultUtil.success();
    }

    @ApiOperation("修改密码")
    @PostMapping("/modify/pwd")
    @RequiresRoles(logical = Logical.OR, value = {"user"})
    public ResultEntity modifyPwd(@ApiParam("当前操作用户token") @RequestHeader() @NotNull(message = "token不能为空") String token,
                                   @ApiParam("修改密码表单") @RequestBody PasswdVo passwdVo) {

        String uuid = TokenUtil.getAccount(token);
        if (userService.modifyPwd(passwdVo, uuid)) {
            return ResultUtil.error(403, "修改失败,请重试");
        }
        return ResultUtil.success();
    }

    @ApiOperation("忘记密码")
    @PostMapping("/forgot/pwd")
    public ResultEntity forgotPwd(@ApiParam("当前操作用户token") @RequestHeader() @NotNull(message = "token不能为空") String token,
                                  @ApiParam("修改密码表单") @RequestBody ForgotPwdVo forgotPwdVo) {

        if (!CheckCodeUtil.isEmailCheck(forgotPwdVo.getUserEmail(), forgotPwdVo.getCheckCode())) {
            return ResultUtil.error(401, "验证码不正确");
        }
        if (!userService.forgotPwd(forgotPwdVo)) {
            return ResultUtil.error(403, "修改失败,请重试");
        }


        return ResultUtil.success();
    }

    @ApiOperation("退出登录")
    @PostMapping("/logout")
    @RequiresRoles(logical = Logical.OR, value = {"user"})
    public ResultEntity logout(@ApiParam("当前操作用户token") @RequestHeader() @NotNull(message = "token不能为空") String token) {

        userService.logout(token);
        return ResultUtil.success();
    }



}

