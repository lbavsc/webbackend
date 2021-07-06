package com.four.webbackend.controller.user;

import com.four.webbackend.model.ResultEntity;
import com.four.webbackend.util.MailSendUtil;
import com.four.webbackend.util.ResultUtil;
import com.four.webbackend.util.ValidateCodeUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;

/**
 * @author lbavsc
 * @version 1.0
 * @className CheckCodeController
 * @description
 * @date 2021/7/5 下午4:47
 **/
@RestController
@Api(tags = "验证码相关接口")
public class CheckCodeController {


    private final MailSendUtil mailSend;

    @Autowired
    public CheckCodeController(MailSendUtil mailSend) {
        this.mailSend = mailSend;
    }

    /**
     * 获取验证码接口
     *
     * @param request 接收
     * @param response 返回
     */
    @ApiOperation("获取登录图形验证码")
    @GetMapping("check_code")
    public void getCheckCode(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 设置发送到客户端的响应的内容类型
            response.setContentType("image/png");
            // 没有缓存
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Expire", "0");
            response.setHeader("Pragma", "no-cache");
            ValidateCodeUtil validateCode = new ValidateCodeUtil();
            validateCode.getRandomCodeImage(request, response, ValidateCodeUtil.IMG);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @ApiOperation("注册邮箱验证码")
    @GetMapping("/email/check_code")
    public ResultEntity mailSendCheckCode(@RequestParam @NotBlank(message = "邮箱不能为空") String userEmail) {

        ValidateCodeUtil validateCode = new ValidateCodeUtil();
        mailSend.mailSendCheckCode(userEmail, validateCode.getEmailCheckCode(userEmail));
        return ResultUtil.success("邮件发送成功");
    }

    @ApiOperation("忘记密码邮箱验证码")
    @GetMapping("/email/check_code/reset")
    public ResultEntity mailSendCheckCodeResetPassword(@RequestParam @NotBlank(message = "邮箱不能为空") String userEmail) {

        // TODO: 2021/7/5 查询数据库里是否存在该人员,存在则发送文件

        ValidateCodeUtil validateCode = new ValidateCodeUtil();
        mailSend.mailSendCheckCodeResetPasswd(userEmail, validateCode.getEmailCheckCode(userEmail));
        return ResultUtil.success("邮件发送成功");
    }
}
