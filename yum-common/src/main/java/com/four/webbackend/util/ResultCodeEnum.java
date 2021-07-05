package com.four.webbackend.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author lbavsc
 */

@Getter
@ToString
@AllArgsConstructor
public enum ResultCodeEnum {

    // 找不到网页
    UNKNOWN_ERROR(404, "找不到网页"),
    SUCCESS(200, "成功"),
    USER_NOT_EXIST(1001, "用户不存在"),
    USER_IS_EXISTS(2001, "用户已存在"),
    DATA_IS_NULL(3001, "数据为空"),
    PERMISSION_ERROR(10300, "权限异常"),
    USER_NAME_NOT_EXIST(10200, "用户名为空"),
    USER_TYPE_NOT(10210, "用户类型为空"),
    ;

    private final int code;
    private final String msg;
}