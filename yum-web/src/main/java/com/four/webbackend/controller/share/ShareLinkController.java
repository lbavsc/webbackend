package com.four.webbackend.controller.share;


import com.four.webbackend.model.ResultEntity;
import com.four.webbackend.model.ShareVo;
import com.four.webbackend.model.UserDto;
import com.four.webbackend.service.ShareLinkService;
import com.four.webbackend.util.ResultUtil;
import com.four.webbackend.util.TokenUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

/**
 * <p>
 * 分享链接表 前端控制器
 * </p>
 *
 * @author lbavsc
 * @since 2021-07-05
 */
@RestController
@RequestMapping("/share")
@Api(tags = "分享链接模块")
public class ShareLinkController {

    private final ShareLinkService shareLinkService;

    @Autowired
    public ShareLinkController(ShareLinkService shareLinkService) {
        this.shareLinkService = shareLinkService;
    }

    @ApiOperation("分享文件或者文件夹")
    @PostMapping("/share")
    @RequiresRoles(logical = Logical.OR, value = {"user"})
    public ResultEntity share(@ApiParam("当前操作用户token") @RequestHeader() @NotNull(message = "token不能为空") String token,
                              @RequestBody ShareVo shareVo) {

        String shareUrl = shareLinkService.share(token, shareVo);
        if (shareUrl == null){
            return null;
        }

        return ResultUtil.success(shareUrl);
    }

}

