package com.four.webbackend.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author lbavsc
 * @version 1.0
 * @className BusinessException
 * @description
 * @date 2021/7/9 下午1:04
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class BusinessException extends RuntimeException {

    private int code;

    private String message;

    public  BusinessException(int code,  String msg){
        this.code = code;
        this.message = msg;
    }
}