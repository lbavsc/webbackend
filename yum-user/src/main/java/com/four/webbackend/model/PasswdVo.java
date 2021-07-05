package com.four.webbackend.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lbavsc
 * @version 1.0
 * @className PasswdVo
 * @description
 * @date 2021/7/5 下午6:53
 **/
@Data
public class PasswdVo implements Serializable {

    private String oldPasswd;

    private String newPasswd;
}
