package com.four.webbackend.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author lbavsc
 * @version 1.0
 * @className UserVo
 * @description
 * @date 2021/7/5 下午6:38
 **/
@Data
public class UserVo implements Serializable {

    private static final long serialVersionUID = 6707058454072773168L;

    private String userEmail;

    private String userName;

    private String checkCode;

    @NotBlank(message = "密码不能为空")
    private String passwd;
}
