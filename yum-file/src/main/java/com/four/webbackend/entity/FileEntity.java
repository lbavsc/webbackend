package com.four.webbackend.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 文件信息表
 * </p>
 *
 * @author lbavsc
 * @since 2021-07-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("file")
@ApiModel(value="FileEntity对象", description="文件信息表")
public class FileEntity implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "文件id")
    @TableId(value = "file_id", type = IdType.AUTO)
    private Integer fileId;

    @ApiModelProperty(value = "文件路径")
    @TableField("url")
    private String url;

    @ApiModelProperty(value = "文件md5值")
    @TableField("md5")
    private String md5;

    @ApiModelProperty(value = "文件类型")
    @TableField("file_type")
    private String fileType;

    @ApiModelProperty(value = "文件名")
    @TableField("file_name")
    private String fileName;

    @ApiModelProperty(value = "文件大小(KB计算)")
    @TableField("file_size")
    private Long fileSize;

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
