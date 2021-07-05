package com.four.webbackend.model;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * @author lbavsc
 * @version 1.0
 * @className LoginVo
 * @description
 * @date 2021/7/5 下午4:41
 **/
@Data
public class LoginVo implements Serializable {


    private static final long serialVersionUID = 5172165663927653271L;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "请填写正确的邮箱格式")
    private String userEmail;

    @NotBlank(message = "密码不能为空")
    @Length(min = 6, max = 20, message = "密码最短为6位,最长为20位")
    private String passwd;

    @NotBlank(message = "验证码不能为空")
    @Length(min = 4, max = 4, message = "验证码长度为4")
    private String checkCode;

}
