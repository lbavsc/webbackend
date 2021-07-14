package com.four.webbackend.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author lbavsc
 * @version 1.0
 * @className DirTreeDto
 * @description
 * @date 2021/7/12 下午5:35
 **/
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DirTreeDto implements Serializable {

    private static final long serialVersionUID = -1234004350322232424L;
    //目录节点id
    private Integer id;

    // 名称
    private String label;

    //子节点对象
    private List<DirTreeDto> children;
}
