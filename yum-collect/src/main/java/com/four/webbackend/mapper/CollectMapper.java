package com.four.webbackend.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.four.webbackend.entity.CollectEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.four.webbackend.model.CollectDto;
import com.four.webbackend.model.MyPageVo;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 收藏表 Mapper 接口
 * </p>
 *
 * @author lbavsc
 * @since 2021-07-05
 */
@Mapper
public interface CollectMapper extends BaseMapper<CollectEntity> {

    IPage<CollectEntity> selectPageVo(MyPageVo<CollectDto> myPageVo, Integer userId);
}
