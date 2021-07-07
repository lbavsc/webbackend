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
 * 分享链接关系表
 * </p>
 *
 * @author lbavsc
 * @since 2021-07-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("share_nexus")
@ApiModel(value="ShareNexusEntity对象", description="分享链接关系表")
public class ShareNexusEntity implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "分享关系id")
    @TableId(value = "share_nexus_id", type = IdType.AUTO)
    private Integer shareNexusId;

    @ApiModelProperty(value = "链接ID")
    @TableField("link_id")
    private Integer linkId;

    @ApiModelProperty(value = "分享者ID")
    @TableField("user_id")
    private Integer userId;

    @ApiModelProperty(value = "被分享者ID")
    @TableField("target_id")
    private Integer targetId;

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
