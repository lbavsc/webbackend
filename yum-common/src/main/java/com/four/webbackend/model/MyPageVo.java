package com.four.webbackend.model;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author lbavsc
 * @version 1.0
 * @className MyPageVo
 * @description
 * @date 2021/7/7 下午12:47
 **/
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class MyPageVo<T> extends Page<T> {
    private static final long serialVersionUID = -3871430648444673441L;

    private Integer selectInt;
    private String selectStr;
    private String name;

    public MyPageVo(long current, long size) {
        super(current, size);
    }
}