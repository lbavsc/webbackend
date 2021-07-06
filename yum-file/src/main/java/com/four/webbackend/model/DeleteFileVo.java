package com.four.webbackend.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author lbavsc
 * @version 1.0
 * @className DeleteFileVo
 * @description
 * @date 2021/7/6 下午2:13
 **/
@Data
public class DeleteFileVo implements Serializable {

    private static final long serialVersionUID = -4583417009331002673L;
    private List<DeleteVo> deleteList;

}
