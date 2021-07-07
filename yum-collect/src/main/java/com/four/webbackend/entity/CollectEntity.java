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
 * 收藏表
 * </p>
 *
 * @author lbavsc
 * @since 2021-07-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("collect")
@ApiModel(value="CollectEntity对象", description="收藏表")
public class CollectEntity implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "收藏id")
    @TableId(value = "collect_id", type = IdType.AUTO)
    private Integer collectId;

    @ApiModelProperty(value = "用户ID")
    @TableField("user_id")
    private Integer userId;

    @ApiModelProperty(value = "文件ID")
    @TableField("file_id")
    private Integer fileId;

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
