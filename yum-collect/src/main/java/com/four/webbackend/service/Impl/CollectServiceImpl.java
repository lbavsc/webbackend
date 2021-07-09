package com.four.webbackend.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.four.webbackend.entity.CollectEntity;
import com.four.webbackend.entity.FileEntity;
import com.four.webbackend.entity.UserFileEntity;
import com.four.webbackend.exception.BusinessException;
import com.four.webbackend.handler.GlobalExceptionHandler;
import com.four.webbackend.mapper.CollectMapper;
import com.four.webbackend.mapper.FileMapper;
import com.four.webbackend.mapper.UserFileMapper;
import com.four.webbackend.model.CollectDto;
import com.four.webbackend.model.MyPageVo;
import com.four.webbackend.service.CollectService;
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
 * 收藏表 服务实现类
 * </p>
 *
 * @author lbavsc
 * @since 2021-07-05
 */
@Service
public class CollectServiceImpl extends ServiceImpl<CollectMapper, CollectEntity> implements CollectService {

    private final FileMapper fileMapper;

    private final UserFileMapper userFileMapper;

    @Autowired
    public CollectServiceImpl(FileMapper fileMapper, UserFileMapper userFileMapper) {
        this.fileMapper = fileMapper;
        this.userFileMapper = userFileMapper;
    }


    @Override
    public IPage<CollectDto> listFavor(MyPageVo<CollectDto> myPageVo, String token) {
        Integer userId = TokenUtil.getUserId(token);
        IPage<CollectEntity> iPage = baseMapper.selectPageVo(myPageVo, userId);
        IPage<CollectDto> rest = new Page<>();

        rest.setTotal(iPage.getTotal());
        rest.setPages(iPage.getPages());
        rest.setSize(iPage.getSize());

        List<CollectDto> dtos = new ArrayList<>();
        iPage.getRecords().forEach(entity -> {
            UserFileEntity userFileEntity = userFileMapper.selectById(entity.getFileId());
            if (userFileEntity == null) {
                baseMapper.deleteById(entity);
                return;
            }
            FileEntity fileEntity = fileMapper.selectById(userFileEntity.getFileId());
            if (fileEntity == null) {
                userFileMapper.delete(new QueryWrapper<UserFileEntity>().eq("file_id", userFileEntity.getUserId()));
                baseMapper.deleteById(entity);
                return;
            }
            CollectDto collectDto = new CollectDto();

            collectDto.setUserFileId(userFileEntity.getUserFileId());
            collectDto.setFileName(fileEntity.getFileName());
            collectDto.setFileSize(fileEntity.getFileSize());
            collectDto.setFileType(fileEntity.getFileType());
            collectDto.setCollectId(entity.getCollectId());

            dtos.add(collectDto);
        });

        rest.setRecords(dtos);

        return rest;

    }

    @Override
    public boolean favorFile(String token, Integer userFileId) {
        UserFileEntity userFileEntity = userFileMapper.selectById(userFileId);
        if (userFileEntity == null || userFileEntity.getUserId().equals(TokenUtil.getUserId(token))) {
            throw new BusinessException(403, "没有该文件");
        }

        CollectEntity collectEntity = new CollectEntity();
        collectEntity.setUserId(userFileEntity.getUserId());
        collectEntity.setFileId(userFileEntity.getUserFileId());

        return baseMapper.insert(collectEntity) == 1;
    }

    @Override
    public boolean recallFile(String token, Integer collectId) {
        CollectEntity collectEntity = baseMapper.selectById(collectId);
        if (collectEntity == null || !collectEntity.getUserId().equals(TokenUtil.getUserId(token))) {
            throw new BusinessException(403, "没有该文件的收藏信息");
        }
        return baseMapper.deleteById(collectId) == 1;
    }
}
