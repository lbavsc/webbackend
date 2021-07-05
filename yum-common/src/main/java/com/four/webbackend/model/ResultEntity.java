package com.four.webbackend.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author lbavsc
 * @version 1.0
 * @className ResultEntity
 * @description
 * @date 2021/7/5 下午2:39
 **/
@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultEntity implements Serializable {

    private static final long serialVersionUID = 1575880832415420562L;
    private int code;
    private String message;
    private Long pages;
    private Long total;
    private Object data;
}
