package com.four.webbackend.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lbavsc
 * @version 1.0
 * @className collectDto
 * @description
 * @date 2021/7/7 下午12:50
 **/
@Data
public class CollectDto implements Serializable{

    private static final long serialVersionUID = -8660092099095873913L;
    private Integer collectId;
    private Integer userFileId;
    private String fileName;
    private String fileType;
    private Long fileSize;
}
