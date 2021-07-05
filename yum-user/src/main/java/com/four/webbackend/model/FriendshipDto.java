package com.four.webbackend.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lbavsc
 * @version 1.0
 * @className FriendshipDto
 * @description
 * @date 2021/7/5 下午7:20
 **/
@Data
public class FriendshipDto implements Serializable {

    private String uuid;

    private String email;

    private String userName;

}
