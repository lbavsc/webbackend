package com.four.webbackend.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author lbavsc
 * @version 1.0
 * @className DirDto
 * @description
 * @date 2021/7/6 下午2:36
 **/
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DirDto implements Serializable {

    private List<DirInfoDto> dirInfoDtos;

    private List<FileInfoDto> fileInfoDtos;
}
