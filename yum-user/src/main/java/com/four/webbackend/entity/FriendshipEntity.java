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
 * 好友关系表
 * </p>
 *
 * @author lbavsc
 * @since 2021-07-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("friendship")
@ApiModel(value="FriendshipEntity对象", description="好友关系表")
public class FriendshipEntity implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "好友关系id")
    @TableId(value = "friendship_id", type = IdType.AUTO)
    private Integer friendshipId;

    @ApiModelProperty(value = "用户1 ID")
    @TableField("user1_id")
    private Integer user1Id;

    @ApiModelProperty(value = "用户2 ID")
    @TableField("user2_id")
    private Integer user2Id;

    @ApiModelProperty(value = "状态:-1:不同意 0:等待处理 1:成立的好友关系 (默认用户1为申请者)")
    @TableField("status")
    private Integer status;

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
