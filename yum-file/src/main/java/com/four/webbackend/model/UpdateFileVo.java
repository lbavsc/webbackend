package com.four.webbackend.model;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * @author lbavsc
 * @version 1.0
 * @className UpdateFileVo
 * @description
 * @date 2021/7/6 上午10:13
 **/
@Data
public class UpdateFileVo implements Serializable {

    @NotBlank(message = "文件md5值不能为空")
    private String fileMd5;

    private MultipartFile file;

    @NotEmpty(message = "文件所属目录不能为空")
    private Integer dirId;

}
