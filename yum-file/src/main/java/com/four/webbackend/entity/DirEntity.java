package com.four.webbackend.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;
import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 文件夹信息表
 * </p>
 *
 * @author lbavsc
 * @since 2021-07-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("dir")
@ApiModel(value="DirEntity对象", description="文件夹信息表")
public class DirEntity implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "文件夹id")
    @TableId(value = "dir_id", type = IdType.AUTO)
    private Integer dirId;

    @ApiModelProperty(value = "文件夹所有者ID")
    @TableField("user_id")
    private Integer userId;

    @ApiModelProperty(value = "文件夹名")
    @TableField("dir_name")
    private String dirName;

    @ApiModelProperty(value = "所属文件夹, 为0则代表是该人员根目录")
    @TableField("owned_dir_id")
    private Integer ownedDirId;

    @ApiModelProperty(value = "乐观锁")
    @TableField("version")
    @Version
    private Integer version;

    @ApiModelProperty(value = "逻辑删除")
    @TableField("is_delete")
    @TableLogic
    private Integer isDelete;

    @ApiModelProperty(value = "创建时间")
    @TableField("gmt_create")
    private Date gmtCreate;

    @ApiModelProperty(value = "修改时间")
    @TableField("gmt_update")
    private Date gmtUpdate;

}
