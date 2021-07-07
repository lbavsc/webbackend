package com.four.webbackend.controller.collect;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.four.webbackend.model.CollectDto;
import com.four.webbackend.model.DirDto;
import com.four.webbackend.model.MyPageVo;
import com.four.webbackend.model.ResultEntity;
import com.four.webbackend.service.CollectService;
import com.four.webbackend.util.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

/**
 * <p>
 * 收藏表 前端控制器
 * </p>
 *
 * @author lbavsc
 * @since 2021-07-05
 */
@RestController
@Api(tags = "收藏模块")
@RequestMapping("/collect")
public class CollectController {

    private final CollectService collectService;

    @Autowired
    public CollectController(CollectService collectService) {
        this.collectService = collectService;
    }


    @ApiOperation("查看用户收藏列表")
    @GetMapping("/listFavor")
    @RequiresRoles(logical = Logical.OR, value = {"user"})
    public ResultEntity listFavor(@ApiParam("当前操作用户token") @RequestHeader() @NotNull(message = "token不能为空") String token,
                                  @ApiParam("查询的页数") @RequestParam(required = false, defaultValue = "1") Integer current,
                                  @ApiParam("一页的数量") @RequestParam(required = false, defaultValue = "10") Integer size) {
        MyPageVo<CollectDto> myPageVo = new MyPageVo<>(current, size);
        IPage<CollectDto> infoDtoIpage = collectService.listFavor(myPageVo, token);


        if (infoDtoIpage.getRecords().size() <= 0) {
            return ResultUtil.error(200, "无数据");
        }
        return ResultUtil.success(infoDtoIpage.getRecords(), infoDtoIpage.getPages(), infoDtoIpage.getTotal());
    }
}

