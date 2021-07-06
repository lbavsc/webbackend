package com.four.webbackend.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lbavsc
 * @version 1.0
 * @className RenameFileOrDirVo
 * @description
 * @date 2021/7/6 下午1:04
 **/
@Data
public class RenameFileOrDirVo implements Serializable {

    private static final long serialVersionUID = 4089185297698968548L;

    private Integer objectId;

    private String newName;

    private Boolean isDir;
}
