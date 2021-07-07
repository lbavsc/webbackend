package com.four.webbackend.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Date;

/**
 * @author lbavsc
 * @version 1.0
 * @className ShareListVo
 * @description
 * @date 2021/7/7 上午11:31
 **/
@Data
public class ShareListDto implements Serializable {

    private static final long serialVersionUID = 8309623148040160426L;
    private Integer sahreId;

    private String shareUrl;

    private Integer targetId;

    private Date expire;



}
