package com.four.webbackend.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lbavsc
 * @version 1.0
 * @className fileDto
 * @description
 * @date 2021/7/6 下午2:36
 **/
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileInfoDto implements Serializable {

    private static final long serialVersionUID = -8866260163252955903L;

    private Integer userFileId;

    private String fileName;

    private Long fileSize;

    private String fileType;
}
