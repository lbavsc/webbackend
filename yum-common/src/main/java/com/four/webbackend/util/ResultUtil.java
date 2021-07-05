package com.four.webbackend.util;

import com.four.webbackend.model.ResultEntity;

/**
 * @author lbavsc
 * @version 1.0
 * @className ResultUtil
 * @description
 * @date 2021/7/5 下午2:40
 **/
public class ResultUtil {

    /**
     * 成功且带数据
     **/
    public static ResultEntity success(Object object) {
        ResultEntity resultEntity = new ResultEntity();
        resultEntity.setCode(ResultCodeEnum.SUCCESS.getCode());
        resultEntity.setMessage(ResultCodeEnum.SUCCESS.getMsg());
        resultEntity.setData(object);
        return resultEntity;
    }

    /**
     * 成功且带数据带页数
     **/
    public static ResultEntity success(Object object, long pages) {
        ResultEntity resultEntity = new ResultEntity();
        resultEntity.setCode(ResultCodeEnum.SUCCESS.getCode());
        resultEntity.setMessage(ResultCodeEnum.SUCCESS.getMsg());
        resultEntity.setPages(pages);
        resultEntity.setData(object);
        return resultEntity;
    }

    /**
     * 成功且带数据带页数
     **/
    public static ResultEntity success(Object object, long pages, long total) {
        ResultEntity resultEntity = new ResultEntity();
        resultEntity.setCode(ResultCodeEnum.SUCCESS.getCode());
        resultEntity.setMessage(ResultCodeEnum.SUCCESS.getMsg());
        resultEntity.setPages(pages);
        resultEntity.setTotal(total);
        resultEntity.setData(object);
        return resultEntity;
    }


    /**
     * 成功且带数据带页数
     **/
    public static ResultEntity success(int code, String msg) {
        ResultEntity resultEntity = new ResultEntity();
        resultEntity.setCode(code);
        resultEntity.setMessage(msg);
        return resultEntity;
    }

    /**
     * 成功但不带数据
     **/
    public static ResultEntity success() {
        ResultEntity resultEntity = new ResultEntity();
        resultEntity.setCode(ResultCodeEnum.SUCCESS.getCode());
        resultEntity.setMessage(ResultCodeEnum.SUCCESS.getMsg());
        resultEntity.setData(null);
        return resultEntity;
    }

    /**
     * 失败但不带数据
     **/
    public static ResultEntity error(int code, String msg) {
        ResultEntity resultEntity = new ResultEntity();
        resultEntity.setCode(code);
        resultEntity.setMessage(msg);
        return resultEntity;
    }



    /**
     * 失败但带数据
     **/
    public static ResultEntity error(int code, String msg, Object data) {
        ResultEntity resultEntity = new ResultEntity();
        resultEntity.setCode(code);
        resultEntity.setMessage(msg);
        resultEntity.setData(data);
        return resultEntity;
    }

    /**
     * 失败但带数据
     **/
    public static ResultEntity error(int code, Object data) {
        ResultEntity resultEntity = new ResultEntity();
        resultEntity.setCode(code);
        resultEntity.setMessage(String.valueOf(data));
        if (data==null) {
            resultEntity.setMessage("错误");
        }
        return resultEntity;
    }


}
