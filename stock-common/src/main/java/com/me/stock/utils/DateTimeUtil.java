package com.me.stock.utils;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Description 日期时间工具类
 */
public class DateTimeUtil {
    /**
     * 获取指定日期下股票的上一个有效交易日时间
     * @return 上一个有效交易日时间
     */
    public static LocalDateTime getPreviousTradingDay(LocalDateTime dateTime){
        //获取指定日期对应的工作日
        DayOfWeek weekNum = dateTime.getDayOfWeek();
        //判断所属工作日
        LocalDateTime preDateTime=null;
        //周一，那么T-1就是周五
        if (weekNum==DayOfWeek.MONDAY){
            //日期后退3天
          preDateTime=dateTime.minusDays(3);
        }
        //周末，那么T-1就是周五
        else if (weekNum==DayOfWeek.SUNDAY){
            preDateTime=dateTime.minusDays(2);
        }
        else {
            preDateTime=dateTime.minusDays(1);
        }
        return getDateTimeWithoutSecond(preDateTime);
    }


    /**
     * 判断是否是工作日
     * @return true：在工作日 false:不在工作日
     */
    public static boolean isWorkDay(LocalDateTime dateTime){
        //获取工作日
        DayOfWeek weekNum = dateTime.getDayOfWeek();
        return weekNum.getValue()>=1 && weekNum.getValue()<=5;
    }

    /**
     * 获取上一天日期
     * @param dateTime
     * @return
     */
    public static LocalDateTime getPreDateTime(LocalDateTime dateTime){
        return dateTime.minusDays(1);
    }

    /**
     * 日期转String
     * @param dateTime 日期
     * @param pattern 日期正则格式
     * @return
     */
    public static String parseToString(LocalDateTime dateTime,String pattern){
       return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 获取股票日期格式字符串
     * @param dateTime
     * @return
     */
    public static String parseToString4Stock(LocalDateTime dateTime){
        return parseToString(dateTime,"yyyyMMddHHmmss");
    }


    /**
     * 获取指定日期的收盘日期
     * @param dateTime
     * @return
     */
    public static LocalDateTime getCloseDate(LocalDateTime dateTime){
       return dateTime.withHour(14).withMinute(58).withSecond(0).withNano(0);
    }

    /**
     * 获取指定日期的开盘日期
     * @param dateTime
     * @return
     */
    public static LocalDateTime getOpenDate(LocalDateTime dateTime){
       return dateTime.withHour(9).withMinute(30).withSecond(0).withNano(0);
    }

    /**
     * 获取最近的股票有效时间，精确到分钟
     * @param target
     * @return
     */
    public static String getLastDateString4Stock(LocalDateTime target){
        LocalDateTime dateTime = getLastDate4Stock(target);
        dateTime=getDateTimeWithoutSecond(dateTime);
        return parseToString4Stock(dateTime);
    }
    /**
     * 获取最近的股票有效时间，精确到分钟
     * @param target
     * @return
     */
    public static LocalDateTime getLastDate4Stock(LocalDateTime target){
        //判断是否是工作日
        if (isWorkDay(target)) {
            //当前日期开盘前
            if (target.isBefore(getOpenDate(target))) {
                target=getCloseDate(getPreviousTradingDay(target));
            }else if (isMarketOffTime(target)){
                target=target.withHour(11).withMinute(28).withSecond(0).withNano(0);
            }else if (target.isAfter(getCloseDate(target))){
                //当前日期收盘后
                target=getCloseDate(target);
            }
        }else{
            //非工作日
            target=getCloseDate(getPreviousTradingDay(target));
        }
         target = getDateTimeWithoutSecond(target);
        return target;
    }

    /**
     * 判断当前时间是否在大盘的中午休盘时间段
     * @return
     */
    public static boolean isMarketOffTime(LocalDateTime target){
        //上午休盘开始时间
        LocalDateTime start = target.withHour(11).withMinute(28).withSecond(0).withNano(0);
        //下午开盘时间
        LocalDateTime end = target.withHour(13).withMinute(0).withSecond(0).withNano(0);
        if (target.isAfter(start) && target.isBefore(end)) {
            return true;
        }
        return false;
    }

    /**
     * 将秒时归零
     * @param dateTime 指定日期
     * @return
     */
    public static LocalDateTime getDateTimeWithoutSecond(LocalDateTime dateTime){
        LocalDateTime newDate = dateTime.withSecond(0).withNano(0);
        return newDate;
    }


    /**
     * 将秒时归零
     * @param dateTime 指定日期字符串，格式必须是：yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static LocalDateTime getDateTimeWithoutSecond(String dateTime){
        LocalDateTime parse = LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return getDateTimeWithoutSecond(parse);
    }
}
