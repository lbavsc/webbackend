package com.four.webbackend.model;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.io.Serializable;


/**
 * @author lbavsc
 * @version 1.0
 * @className RegistVo
 * @description
 * @date 2021/7/5 下午4:58
 **/
@Data
public class RegistVo implements Serializable {


    private static final long serialVersionUID = 4175575657170691137L;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "请填写正确的邮箱格式")
    private String userEmail;

    @NotBlank(message = "用户名不能为空")
    @Length(min = 2, max = 10, message = "用户名最短为2位,最长为10位")
    private String userName;

    @NotBlank(message = "密码不能为空")
    @Length(min = 6, max = 20, message = "密码最短为6位,最长为20位")
    private String passwd;

    @NotBlank(message = "验证码不能为空")
    @Size(min = 6, max = 6, message = "验证码长度为6")
    private String checkCode;
}
