package com.tiza.support.util;

import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Description: DateUtil
 * Author: DIYILIU
 * Update: 2018-05-07 23:17
 */
public class DateUtil {
    public static Date stringToDate(String datetime) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date date = null;
        if (StringUtils.isNotEmpty(datetime)) {
            try {

                date = format.parse(datetime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return date;
    }

    public static Date strToDate(String datetime) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        Date date = null;
        if (StringUtils.isNotEmpty(datetime)) {
            try {

                date = format.parse(datetime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return date;
    }

    public static String dateToString(Date date) {

        if (date == null) {

            return null;
        }

        return String.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS", date);
    }

    public static String dateToString(Date date, String format) {

        if (date == null) {

            return null;
        }

        return String.format(format, date);
    }

    public static String getDate(byte b, byte b1, byte b2) {
        return String.format("%02d-%02d-%02d", b, b1, b2);
    }

    public static String getTimeStr(byte b, byte b1, byte b2) {
        return String.format("%02d:%02d:%02d", b, b1, b2);
    }

    public static Long getTimeMillis(String time) throws Exception {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return df.parse(time).getTime();
    }

    /**
     * 日历转换成数字
     *
     * @param cal
     * @return yyyyMM
     */
    public static int getYM(Calendar cal) {
        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH) + 1;
        return y * 100 + m;
    }

    public static int getTableYm(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(calendar.DATE, -1);// 把日期往后增加一天.整数往后推,负数往前移动


        return getYM(calendar);
    }

    private static final long SECOND = 1000L;
    private static final long MINUTE = 60 * 1000L;
    private static final long HOUR = 60 * 60 * 1000L;
    private static final long DAY = 24 * 60 * 60 * 1000L;

    public static String formatMilliseconds(long time) {
        if (time < SECOND) {

            return time + "毫秒";
        } else if (time < MINUTE) {
            long sec = time / SECOND;

            if (sec > 0) {
                return sec + "秒";
            } else {
                return "小于1秒";
            }
        } else if (time < HOUR) {
            long min = time / MINUTE;
            long sec = (time - min * MINUTE) / SECOND;

            String str = "";
            if (min > 0) {
                str += min + "分钟";
            }
            if (sec > 0) {
                str += sec + "秒";
            }

            return str;
        } else if (time < DAY) {
            long hour = time / HOUR;
            long min = (time - hour * HOUR) / MINUTE;

            String str = "";
            if (hour > 0) {
                str += hour + "小时";
            }
            if (min > 0) {
                str += min + "分钟";
            }

            return str;
        } else {
            long day = time / DAY;
            long hour = (time - day * DAY) / HOUR;

            String str = "";
            if (day > 0) {
                str += day + "天";
            }
            if (hour > 0) {
                str += hour + "小时";
            }

            return str;

        }
    }
}
