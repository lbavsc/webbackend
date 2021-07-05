package com.four.webbackend.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lbavsc
 * @version 1.0
 * @className ObjectNotNullUtils
 * @description 判断bean是否齐全
 * @date 2021/7/5 下午3:18
 **/

public class ObjectNotNullUtils {
    private static final Logger logger = LoggerFactory.getLogger(ObjectNotNullUtils.class);
    /**
     * 判断类中每个属性是否不为空
     *
     * @return 如果有空,则返回false
     */
    public static boolean allFieldIsNull(Object o){
        try {
            for (Field field : o.getClass().getDeclaredFields()) {
                field.setAccessible(true);

                Object object = field.get(o);
                if (object instanceof CharSequence) {
                    if (org.springframework.util.ObjectUtils.isEmpty(object)) {
                        return false;
                    }
                } else {
                    if (null == object) {
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("判断对象属性为空异常");
        }
        return true;
    }


    public static List<String> allFieldIsNullMsg(Object o) {
        List<String> msg = new ArrayList<>();
        try {
            for (Field field : o.getClass().getDeclaredFields()) {
                field.setAccessible(true);

                Object object = field.get(o);
                if (object instanceof CharSequence) {
                    if (org.springframework.util.ObjectUtils.isEmpty(object)) {
                        msg.add(field.getName() + "为空");
                    }
                } else {
                    if (null == object) {
                        msg.add(field.getName() + "为空");
                    }
                }
            }
        } catch (Exception e) {
            logger.error("判断对象属性为空异常");
        }
        return msg;
    }
}
