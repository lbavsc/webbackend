package com.four.webbackend.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lbavsc
 * @version 1.0
 * @className CopyFileVo
 * @description
 * @date 2021/7/6 下午1:31
 **/
@Data
public class CopyFileVo implements Serializable {

    private static final long serialVersionUID = 2154480122129260280L;

    private Integer fileId;

    private Integer dirSourceId;

    private Integer dirFromId;
}
