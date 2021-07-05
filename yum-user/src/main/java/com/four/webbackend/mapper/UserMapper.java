package com.four.webbackend.mapper;

import com.four.webbackend.entity.UserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author lbavsc
 * @since 2021-07-05
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {

}
