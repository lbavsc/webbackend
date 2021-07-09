package com.four.webbackend.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.four.webbackend.constant.FriendshipConstant;
import com.four.webbackend.entity.FriendshipEntity;
import com.four.webbackend.entity.UserEntity;
import com.four.webbackend.exception.BusinessException;
import com.four.webbackend.handler.GlobalExceptionHandler;
import com.four.webbackend.mapper.FriendshipMapper;
import com.four.webbackend.mapper.UserMapper;
import com.four.webbackend.model.FriendshipDto;
import com.four.webbackend.service.FriendshipService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.four.webbackend.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 好友关系表 服务实现类
 * </p>
 *
 * @author lbavsc
 * @since 2021-07-05
 */
@Service
public class FriendshipServiceImpl extends ServiceImpl<FriendshipMapper, FriendshipEntity> implements FriendshipService {

    UserMapper userMapper;

    @Autowired
    public FriendshipServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public List<FriendshipDto> listBuddy(String token) {

        String uuid = TokenUtil.getAccount(token);

        Integer userId = TokenUtil.getUserId(token);
        List<FriendshipDto> rest = new ArrayList<>();
        UserEntity userEntity = userMapper.selectOne(new QueryWrapper<UserEntity>()
                .eq("user_uuid", uuid));
        if (userEntity == null || userEntity.getUserId() == null) {
            throw new BusinessException(403, "没有uuid为" + uuid + "的用户");
        }
        if (!userEntity.getUserId().equals(userId)) {
            throw new BusinessException(403, "token错误,uuid与userId不匹配");
        }


        List<FriendshipEntity> friendshipEntities = baseMapper.selectList(new QueryWrapper<FriendshipEntity>()
                .eq("status", FriendshipConstant.PASS_REVIEW)
                .and(wrapper -> {
                    wrapper.eq("user1_id", userEntity.getUserId())
                            .or().eq("user2_id", userEntity.getUserId());
                }));
        friendshipEntities.forEach(entity -> {
            FriendshipDto friendshipDto = new FriendshipDto();
            UserEntity temp = null;

            if (userId.equals(entity.getUser1Id())) {
                temp = userMapper.selectOne(new QueryWrapper<UserEntity>()
                        .eq("user_id", entity.getUser2Id()));
            } else if (userId.equals(entity.getUser2Id())) {
                temp = userMapper.selectOne(new QueryWrapper<UserEntity>()
                        .eq("user_id", entity.getUser1Id()));
            }
            if (temp == null) {
                return;
            }
            friendshipDto.setFriendshipId(entity.getFriendshipId());
            friendshipDto.setEmail(temp.getEmail());
            friendshipDto.setUuid(temp.getUserUuid());
            friendshipDto.setUserName(temp.getUserName());

            rest.add(friendshipDto);
        });

        return rest;
    }

    @Override
    public boolean addBuddy(String token, String uid) {

        String uuid = TokenUtil.getAccount(token);
        Integer userId = TokenUtil.getUserId(token);
        UserEntity userEntity = userMapper.selectOne(new QueryWrapper<UserEntity>()
                .eq("user_uuid", uuid));


        if (userEntity == null) {
            throw new BusinessException(403, "没有uuid为" + uuid + "的用户");

        }

        assert uuid != null;
        if (uuid.equals(uid)) {
            throw new BusinessException(403, "您不能添加自己为好友");
        }

        if (!userEntity.getUserId().equals(userId)) {
            throw new BusinessException(403, "token错误,uuid与userId不匹配");

        }
        UserEntity buddyEntity = userMapper.selectOne(new QueryWrapper<UserEntity>()
                .and(wrapper -> wrapper.eq("user_uuid", uid)
                        .or().eq("email", uid)));
        if (buddyEntity == null) {
            throw new BusinessException(403, "没有uuid(邮箱)为" + uid + "的用户");

        }

        Integer count = baseMapper.selectCount(new QueryWrapper<FriendshipEntity>()
                .eq("user1_id", TokenUtil.getUserId(token))
                .eq("user2_id", userEntity.getUserId())
                .eq("status", FriendshipConstant.UNDER_REVIEW));
        if (count > 0) {
            throw new BusinessException(403, "已有此条申请记录,请重试");
        }

        int buddy = baseMapper.selectCount(new QueryWrapper<FriendshipEntity>()
                .eq("status", FriendshipConstant.PASS_REVIEW)
                .or(wrapper -> wrapper.eq("user1_id", TokenUtil.getUserId(token))
                        .eq("user2_id", userEntity.getUserId()))
                .or(wrapper -> wrapper.eq("user2_id", TokenUtil.getUserId(token))
                        .eq("user1_id", userEntity.getUserId())));
        if (buddy > 0) {
            throw new BusinessException(403, "对方已是您的好友");
        }

        FriendshipEntity friendshipEntity = new FriendshipEntity();
        friendshipEntity.setUser1Id(userEntity.getUserId());
        friendshipEntity.setUser2Id(buddyEntity.getUserId());
        friendshipEntity.setStatus(FriendshipConstant.UNDER_REVIEW);


        return baseMapper.insert(friendshipEntity) == 1;
    }

    @Override
    public List<FriendshipDto> getFriendApplication(String token) {
        Integer userId = TokenUtil.getUserId(token);

        // 查询添加"我"为好友的记录
        List<FriendshipEntity> entities = baseMapper.selectList(new QueryWrapper<FriendshipEntity>()
                .eq("user2_id", userId)
                .eq("status", FriendshipConstant.UNDER_REVIEW));

        List<FriendshipDto> rest = new ArrayList<>();

        entities.forEach(entity -> {
            FriendshipDto friendshipDto = new FriendshipDto();

            // 查询添加人的信息
            UserEntity temp = userMapper.selectOne(new QueryWrapper<UserEntity>()
                    .eq("user_id", entity.getUser1Id()));

            friendshipDto.setFriendshipId(entity.getFriendshipId());
            friendshipDto.setEmail(temp.getEmail());
            friendshipDto.setUuid(temp.getUserUuid());
            friendshipDto.setUserName(temp.getUserName());

            rest.add(friendshipDto);
        });

        return rest;
    }

    @Override
    public boolean deleteBuddy(String token, String uid) {
        String uuid = TokenUtil.getAccount(token);
        Integer userId = TokenUtil.getUserId(token);


        UserEntity userEntity = userMapper.selectOne(new QueryWrapper<UserEntity>()
                .eq("user_uuid", uuid));
        if (userEntity == null) {
            throw new BusinessException(403, "没有uuid为" + uuid + "的用户");
        }
        if (!userEntity.getUserId().equals(userId)) {
            throw new BusinessException(403, "token错误,uuid与userId不匹配");
        }
        UserEntity buddyEntity = userMapper.selectOne(new QueryWrapper<UserEntity>()
                .eq("user_uuid", uid));
        if (buddyEntity == null) {
            throw new BusinessException(403, "没有uuid为" + uid + "的用户");
        }
        baseMapper.delete(new QueryWrapper<FriendshipEntity>()
                .eq("user1_id", userEntity.getUserId())
                .eq("user2_id", buddyEntity.getUserId())
                .eq("status", FriendshipConstant.PASS_REVIEW)
                .or(wrapper -> wrapper.eq("user1_id", buddyEntity.getUserId())
                        .eq("user2_id", userEntity.getUserId())
                        .eq("status", FriendshipConstant.PASS_REVIEW)));

        return true;
    }


    @Override
    public boolean friendhandle(String token, Integer friendshipId, Boolean isPass) {
        FriendshipEntity friendshipEntity = baseMapper.selectById(friendshipId);

        if (friendshipEntity == null || !friendshipEntity.getUser2Id().equals(TokenUtil.getUserId(token))) {
            throw new BusinessException(403, "处理失败,无此条申请");
        }
        if (isPass) {
            friendshipEntity.setStatus(FriendshipConstant.PASS_REVIEW);
            baseMapper.updateById(friendshipEntity);
        } else {
            baseMapper.deleteById(friendshipEntity);
        }


        return true;
    }
}
