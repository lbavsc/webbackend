package com.four.webbackend.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lbavsc
 * @version 1.0
 * @className DeleteVo
 * @description
 * @date 2021/7/6 下午2:17
 **/
@Data
public class DeleteVo implements Serializable {
    private static final long serialVersionUID = 2594951407359942663L;
    private Integer fileId;

    private Boolean isDir;
}