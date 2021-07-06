package com.four.webbackend.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lbavsc
 * @version 1.0
 * @className DirInfoDto
 * @description
 * @date 2021/7/6 下午2:35
 **/
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DirInfoDto implements Serializable {

    private static final long serialVersionUID = -5161917904415553467L;

    private Integer dirId;

    private String dirName;

}
