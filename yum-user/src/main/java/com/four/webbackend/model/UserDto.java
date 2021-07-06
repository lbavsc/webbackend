package com.four.webbackend.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author lbavsc
 * @version 1.0
 * @className UserDto
 * @description
 * @date 2021/7/5 下午6:16
 **/
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto implements Serializable {

    private static final long serialVersionUID = -3948872611932143594L;

    @ApiModelProperty(value = "用户uuid")
    private String userUuid;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "总容量")
    private Long totalCapacity;

    @ApiModelProperty(value = "已使用容量")
    private Long used;
}
