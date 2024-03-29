package com.four.webbackend.controller.share;


import com.four.webbackend.model.*;
import com.four.webbackend.service.ShareLinkService;
import com.four.webbackend.util.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

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
        if (shareUrl == null) {
            return null;
        }

        return ResultUtil.success(shareUrl);
    }

    @ApiOperation("获取用户分享列表")
    @PostMapping("/listShare")
    @RequiresRoles(logical = Logical.OR, value = {"user"})
    public ResultEntity listShare(@ApiParam("当前操作用户token") @RequestHeader() @NotNull(message = "token不能为空") String token) {

        List<ShareListDto> shareListVos = shareLinkService.listShare(token);

        if (shareListVos == null || shareListVos.size() <= 0) {
            return ResultUtil.error(403, "无数据");
        }


        return ResultUtil.success(shareListVos);
    }

    @ApiOperation("获取用户收到的分享列表")
    @PostMapping("/listShare/received")
    @RequiresRoles(logical = Logical.OR, value = {"user"})
    public ResultEntity listShareReceived(@ApiParam("当前操作用户token") @RequestHeader() @NotNull(message = "token不能为空") String token) {

        List<ShareListDto> shareListVos = shareLinkService.listShareReceived(token);

        if (shareListVos == null || shareListVos.size() <= 0) {
            return ResultUtil.error(403, "无数据");
        }


        return ResultUtil.success(shareListVos);
    }

    @ApiOperation("取消分享")
    @GetMapping("/recellShare")
    @RequiresRoles(logical = Logical.OR, value = {"user"})
    public ResultEntity recellShare(@ApiParam("当前操作用户token") @RequestHeader() @NotNull(message = "token不能为空") String token,
                                    @RequestParam @NotEmpty(message = "分享id不能为空") Integer shareId) {

        if (!shareLinkService.recellShare(token, shareId)) {
            return null;
        }

        return ResultUtil.success();
    }

    @ApiOperation("获取分享链接内容")
    @GetMapping("/get")
    @RequiresRoles(logical = Logical.OR, value = {"user"})
    public ResultEntity getShareUrlContent(@ApiParam("当前操作用户token") @RequestHeader() @NotNull(message = "token不能为空") String token,
                                           @RequestParam @NotEmpty(message = "分享id不能为空") String shareUrl) {

        DirDto dto = shareLinkService.getShareUrlContent(token, shareUrl);
        if (dto == null) {
            return null;
        }

        return ResultUtil.success(dto);
    }

    @ApiOperation("将分享的文件保存到自己云盘")
    @PostMapping("/saveShare")
    @RequiresRoles(logical = Logical.OR, value = {"user"})
    public ResultEntity saveShare(@ApiParam("当前操作用户token") @RequestHeader() @NotNull(message = "token不能为空") String token,
                                @RequestBody SaveVo saveVo) {

        if (!shareLinkService.saveShare(token, saveVo.getShareUrl(), saveVo.getObjectId(), saveVo.getTargetDir(), saveVo.getIsDir())) {
            return null;
        }

        return ResultUtil.success();
    }
}

