package com.four.webbackend.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.four.webbackend.constant.FriendshipConstant;
import com.four.webbackend.entity.FriendshipEntity;
import com.four.webbackend.entity.UserEntity;
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
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.ServletOutputStream;
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
        HttpServletResponse response = getResponse();
        String uuid = TokenUtil.getAccount(token);
        Integer userId = TokenUtil.getUserId(token);
        List<FriendshipDto> rest = new ArrayList<>();
        UserEntity userEntity = userMapper.selectOne(new QueryWrapper<UserEntity>()
                .select("user_id")
                .eq("user_uuid", uuid));

        if (userEntity == null || userEntity.getUserId() == null) {
            GlobalExceptionHandler.responseError(response, "没有uuid为" + uuid + "的用户");
            return null;
        }
        if (!userEntity.getUserId().equals(userId)) {
            GlobalExceptionHandler.responseError(response, "token错误,uuid与userId不匹配");
            return null;
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
                        .select("user_uuid", "email", "userName")
                        .eq("user2_id", entity.getUser2Id()));
            } else if (userId.equals(entity.getUser2Id())) {
                temp = userMapper.selectOne(new QueryWrapper<UserEntity>()
                        .select("user_uuid", "email", "userName")
                        .eq("user1_id", entity.getUser1Id()));
            }
            if (temp == null) {
                return;
            }
            friendshipDto.setEmail(temp.getEmail());
            friendshipDto.setUuid(temp.getUserUuid());
            friendshipDto.setUserName(temp.getUserName());

            rest.add(friendshipDto);
        });

        return rest;
    }

    @Override
    public boolean addBuddy(String token, String identifier) {
        HttpServletResponse response = getResponse();
        String uuid = TokenUtil.getAccount(token);
        Integer userId = TokenUtil.getUserId(token);
        UserEntity userEntity = userMapper.selectOne(new QueryWrapper<UserEntity>()
                .select("user_id")
                .eq("user_uuid", uuid));
        if (userEntity == null) {
            GlobalExceptionHandler.responseError(response, "没有uuid为" + uuid + "的用户");
            return false;
        }

        if (!userEntity.getUserId().equals(userId)) {
            GlobalExceptionHandler.responseError(response, "token错误,uuid与userId不匹配");
            return false;
        }
        UserEntity buddyEntity = userMapper.selectOne(new QueryWrapper<UserEntity>()
                .select("user_id")
                .and(wrapper -> {
                    wrapper.eq("user_uuid", identifier)
                            .or().eq("email", identifier);
                }));
        if (buddyEntity == null) {
            GlobalExceptionHandler.responseError(response, "没有uuid(邮箱)为" + identifier + "的用户");
        }

        if (listBuddy(uuid).size() > 0) {
            GlobalExceptionHandler.responseError(response, "对方已是您的好友");
            return false;
        }
        FriendshipEntity friendshipEntity = new FriendshipEntity();
        friendshipEntity.setUser1Id(userEntity.getUserId());
        assert buddyEntity != null;
        friendshipEntity.setUser2Id(buddyEntity.getUserId());
        friendshipEntity.setStatus(FriendshipConstant.UNDER_REVIEW);

        baseMapper.insert(friendshipEntity);
        return true;
    }

    @Override
    public boolean deleteBuddy(String token, String uid) {
        String uuid = TokenUtil.getAccount(token);
        Integer userId = TokenUtil.getUserId(token);

        HttpServletResponse response = getResponse();
        UserEntity userEntity = userMapper.selectOne(new QueryWrapper<UserEntity>()
                .select("user_id")
                .eq("user_uuid", uuid));
        if (userEntity == null) {
            GlobalExceptionHandler.responseError(response, "没有uuid为" + uuid + "的用户");
            return false;
        }
        if (!userEntity.getUserId().equals(userId)) {
            GlobalExceptionHandler.responseError(response, "token错误,uuid与userId不匹配");
            return false;
        }
        UserEntity buddyEntity = userMapper.selectOne(new QueryWrapper<UserEntity>()
                .select("user_id")
                .eq("user_uuid", uid));
        if (buddyEntity == null) {
            GlobalExceptionHandler.responseError(response, "没有uuid为" + uid + "的用户");
            return false;
        }
        baseMapper.delete(new QueryWrapper<FriendshipEntity>()
                .eq("user1_id", userEntity.getUserId())
                .eq("user2_id", buddyEntity.getUserId())
                .or(wrapper -> {
                    wrapper.eq("user1_id", buddyEntity.getUserId())
                            .eq("user2_id", userEntity.getUserId());
                }));

        return true;
    }

    private HttpServletResponse getResponse() {
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getResponse();
    }
}
