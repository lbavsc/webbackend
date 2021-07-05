package com.four.webbackend.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author lbavsc
 * @version 1.0
 * @className DataUtil
 * @description
 * @date 2021/7/5 下午3:21
 **/
public class DataUtil {

    /**
     * 判断当前日期是否大于某个日期
     *
     * @param date yyyy-MM-dd
     * @return
     */
    public static boolean afterDateNow(String date) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy年M月d日");

        //把String转为LocalDate
        LocalDate localTime = LocalDate.parse(date, dtf);
        //判断当前日期是否大于指定日期
        return LocalDate.now().isAfter(localTime);
    }

    /**
     * 判断当前日期是否大于某个日期
     *
     * @param date yyyy-MM-dd
     * @return
     */
    public static boolean afterDateNow(Date date) {
        Date now = new Date();
        return now.after(date);
    }

    /**
     * 判断当前日期是否大于某个日期
     *
     * @param date yyyy-MM-dd
     * @return
     */
    public static boolean beforeDateNow(Date date) {
        Date now = new Date();
        return now.before(date);
    }

    /**
     * 判断某个日期是否在某个日期之前
     */
    public static boolean beforeDate(Date date1, Date date2) {
        return date1.before(date2);
    }
}
