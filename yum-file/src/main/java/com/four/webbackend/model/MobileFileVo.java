package com.four.webbackend.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lbavsc
 * @version 1.0
 * @className MobileFileVo
 * @description
 * @date 2021/7/6 下午12:55
 **/
@Data
public class MobileFileVo implements Serializable {


    private static final long serialVersionUID = -9054014883323715223L;

    private Integer fileId;

    private Integer dirSourceId;

    private Integer dirFromId;

}
