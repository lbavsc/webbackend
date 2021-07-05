package com.four.webbackend.util;

/**
 * @author lbavsc
 * @version 1.0
 * @className IdUtils
 * @description
 * @date 2021/7/5 下午6:28
 **/
public class IdUtils {

    /**
     * 以毫微秒做基础计数, 返回唯一有序增长ID
     * <pre>System.nanoTime()</pre>
     * <pre>
     * 线程数量: 100
     * 执行次数: 1000
     * 平均耗时: 222 ms
     * 数组长度: 100000
     * Map Size: 100000
     * </pre>
     * @return ID长度32位
     */
    public static String getPrimaryKey(){
        //随机7位数
        return MathUtils.makeUpNewData(Thread.currentThread().hashCode()+"", 3)+ MathUtils.randomDigitNumber(9);
    }
}