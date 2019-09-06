package com.example.bookkeeping.datepicker;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 说明：日期格式化工具
 * 作者：liuwan1992
 * 添加时间：2018/12/17
 * 修改人：liuwan1992
 * 修改时间：2018/12/18
 */
public class DateFormatUtils {

    private static final String DATE_FORMAT_PATTERN_YMD = "yyyy-MM-dd";
    private static final String DATE_FORMAT_PATTERN_YMD_HM = "yyyy-MM-dd HH:mm";

    /**
     * 时间戳转字符串
     *
     * @param timestamp     时间戳
     * @param isPreciseTime 是否包含时分
     * @return 格式化的日期字符串
     */
    public static String long2Str(long timestamp, boolean isPreciseTime) {
        return long2Str(timestamp, getFormatPattern(isPreciseTime));
    }

    private static String long2Str(long timestamp, String pattern) {
        return new SimpleDateFormat (pattern, Locale.CHINA).format(new Date (timestamp));
    }

    /**
     * 字符串转时间戳
     *
     * @param dateStr       日期字符串
     * @param isPreciseTime 是否包含时分
     * @return 时间戳
     */
    public static long str2Long(String dateStr, boolean isPreciseTime) {
        return str2Long(dateStr, getFormatPattern(isPreciseTime));
    }

    private static long str2Long(String dateStr, String pattern) {
        try {
            return new SimpleDateFormat (pattern, Locale.CHINA).parse(dateStr).getTime();
        } catch (Throwable ignored) {
        }
        return 0;
    }

    private static String getFormatPattern(boolean showSpecificTime) {
        if (showSpecificTime) {
            return DATE_FORMAT_PATTERN_YMD_HM;
        } else {
            return DATE_FORMAT_PATTERN_YMD;
        }
    }
    public static String fomart(Date date, String pattern) {
        return new SimpleDateFormat (pattern, Locale.CHINA).format(date);
    }
    public static String fomart(String dateStr,String pattern) {
        Date date = parse (dateStr,DATE_FORMAT_PATTERN_YMD_HM);
        return new SimpleDateFormat (pattern, Locale.CHINA).format(date);
    }
    public static Date parse(String dateStr) {
        return parse (dateStr,DATE_FORMAT_PATTERN_YMD_HM);
    }
    public static Date parse(String dateStr, String pattern) {
        try {
            return new SimpleDateFormat (pattern, Locale.CHINA).parse (dateStr);
        }catch (Exception e){
            Log.d ("DateFormatUtils",e.getMessage (),e);
        }
        return null;
    }
    public static String getDayofWeek(String dateTime) {
        Calendar cal = Calendar.getInstance();
        if (dateTime.equals("")) {
            cal.setTime(new Date(System.currentTimeMillis()));
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date;
            try {
                date = sdf.parse(dateTime);
            } catch (Exception e) {
                date = null;
                Log.d ("DateFormatUtils",e.getMessage (),e);
            }
            if (date != null) {

                cal.setTime(new Date(date.getTime()));     }
        }
        switch (cal.get(Calendar.DAY_OF_WEEK)){
            case 1:
                return "周日";
            case 2:
                return "周一";
            case 3:
                return "周二";
            case 4:
                return "周三";
            case 5:
                return "周四";
            case 6:
                return "周五";
            case 7:
                return "周六";
            default:return null;
        }
    }
}
