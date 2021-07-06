package com.four.webbackend.service;

import com.four.webbackend.entity.FriendshipEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.four.webbackend.model.FriendshipDto;

import java.util.List;

/**
 * <p>
 * 好友关系表 服务类
 * </p>
 *
 * @author lbavsc
 * @since 2021-07-05
 */
public interface FriendshipService extends IService<FriendshipEntity> {

    List<FriendshipDto> listBuddy(String token);

    boolean addBuddy(String token, String identifier);

    boolean deleteBuddy(String token, String uid);
}
