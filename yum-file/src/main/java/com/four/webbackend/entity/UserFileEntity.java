package com.four.webbackend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户-文件关系表
 * </p>
 *
 * @author lbavsc
 * @since 2021-07-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_file")
@ApiModel(value="UserFileEntity对象", description="用户-文件关系表")
public class UserFileEntity implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "关系id")
    @TableId(value = "user_file_id", type = IdType.AUTO)
    private Integer userFileId;

    @ApiModelProperty(value = "用户ID")
    @TableField("user_id")
    private Integer userId;

    @ApiModelProperty(value = "文件所属文件夹id")
    @TableField("dir_id")
    private Integer dirId;

    @ApiModelProperty(value = "文件ID")
    @TableField("file_id")
    private Integer fileId;

    @ApiModelProperty(value = "文件md5值")
    @TableField("md5")
    private String md5;

    @ApiModelProperty(value = "乐观锁")
    @TableField("version")
    @Version
    private Integer version;

    @ApiModelProperty(value = "逻辑删除")
    @TableField("id_dalete")
    private Integer idDalete;

    @ApiModelProperty(value = "创建时间")
    @TableField("gmt_create")
    private Date gmtCreate;

    @ApiModelProperty(value = "修改时间")
    @TableField("gmt_update")
    private Date gmtUpdate;


}
