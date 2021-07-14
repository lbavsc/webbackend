package com.four.webbackend.model;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * @author lbavsc
 * @version 1.0
 * @className SaveVo
 * @description
 * @date 2021/7/13 下午3:25
 **/
@Data
public class SaveVo implements Serializable {


    private static final long serialVersionUID = 3263482027319950461L;
    @NotEmpty(message = "分享id不能为空") String shareUrl;
    @NotEmpty(message = "分享id不能为空") Integer objectId;
    @NotEmpty(message = "目标目录") Integer targetDir;
    @NotEmpty(message = "是否是文件夹") Boolean isDir;
}
