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
 * 分享链接表
 * </p>
 *
 * @author lbavsc
 * @since 2021-07-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("share_link")
@ApiModel(value="ShareLinkEntity对象", description="分享链接表")
public class ShareLinkEntity implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "分享链接id")
    @TableId(value = "share_link_id", type = IdType.AUTO)
    private Integer shareLinkId;

    @ApiModelProperty(value = "用户ID")
    @TableField("user_id")
    private Integer userId;

    @ApiModelProperty(value = "分享链接")
    @TableField("link")
    private String link;

    @ApiModelProperty(value = "文件id或者文件夹id")
    @TableField("file_id")
    private Integer fileId;

    @ApiModelProperty(value = "是否是文件夹")
    @TableField("is_dir")
    private Boolean isDir;

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
