package com.four.webbackend.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author lbavsc
 * @version 1.0
 * @className ShareVo
 * @description
 * @date 2021/7/7 上午10:25
 **/
@Data
@ApiModel(value="ShareVo对象")
public class ShareVo implements Serializable {

    private static final long serialVersionUID = -4464725369251510869L;
    @ApiModelProperty(value = "文件夹或者文件id")
    @NotEmpty(message = "id不能为空")
    private Integer objectId;


    @ApiModelProperty(value = "是否是文件夹")
    @NotNull(message = "类型不能为空")
    private Boolean isDir;

    @ApiModelProperty(value = "分享者id")
    @NotEmpty(message = "分享者id不能为空")
    private Long sourceId;

    @ApiModelProperty(value = "分享者id, 如果为0则所有人都可查看该链接")
    @NotEmpty(message = "被分享者id不能为空")
    private Long targetId;

    @ApiModelProperty(value = "过期时间, 1:1天 2:3天 3:7天 4:30天 5:永久有效")
    @NotEmpty(message = "过期时间不能为空")
    private Integer expire;

}
