package com.four.webbackend.model;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author lbavsc
 * @version 1.0
 * @className ForgotPwdVo
 * @description
 * @date 2021/7/6 下午3:00
 **/
@Data
public class ForgotPwdVo implements Serializable {

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "请填写正确的邮箱格式")
    private String userEmail;


    @NotBlank(message = "密码不能为空")
    @Length(min = 6, max = 20, message = "密码最短为6位,最长为20位")
    private String passwd;

    @NotBlank(message = "验证码不能为空")
    @Size(min = 6, max = 6, message = "验证码长度为6")
    private String checkCode;

}
