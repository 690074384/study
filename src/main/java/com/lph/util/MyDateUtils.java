package com.lph.util;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Date;

/**
 * 日期工具类
 *
 * @author lvpenghui
 * @since 2019-4-18 11:40:58
 */
public class MyDateUtils extends DateUtils {

    /**
     * 获取当前yyyy-mm-dd格式的日期
     *
     * @return 日期字符串
     */
    public static String getDate() {
        return getDate("yyyy-MM-dd");
    }

    /**
     * 得到当前日期字符串 格式（yyyy-MM-dd）
     *
     * @param pattern pattern可以为"yyyy-MM-dd"、"HH:mm:ss"、"E"
     * @return 日期字符串
     */
    public static String getDate(String pattern) {
        return DateFormatUtils.format(new Date(), pattern);
    }

    /**
     * 得到日期字符串，默认"yyyy-MM-dd"
     *
     * @param date    Date类型的对象
     * @param pattern pattern可以为"yyyy-MM-dd"、"HH:mm:ss"、"E"
     * @return 日期字符串
     */
    private static String formateDate(Date date, Object... pattern) {
        String formatDate;
        if (null != pattern && pattern.length > 0) {
            formatDate = DateFormatUtils.format(date, pattern[0].toString());
        } else {
            formatDate = DateFormatUtils.format(date, "yyyy-MM-dd");
        }
        return formatDate;
    }

    /**
     * 得到日期类型的字符串，转换格式"yyyy-MM-dd HH:mm:ss"
     * @param date
     * @return
     */
    public static String formateDate(Date date){
        return formateDate(date,"yyyy-MM-dd HH:mm:ss");
    }
}
