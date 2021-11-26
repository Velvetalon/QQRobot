package com.velvetalon.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.lang.management.ManagementFactory;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 时间工具类
 * 
 * @author ruoyi
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils
{
    public static String YYYY = "yyyy";

    public static String YYYY_MM = "yyyy-MM";

    public static String YYYY_MM_DD = "yyyy-MM-dd";

    public static String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    public static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    
    private static String[] parsePatterns = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM", 
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};

    public static final String DEF_PATTERN = "yyyy-MM-dd";

    public static final String LNG_PATTERN = "yyyy-MM-dd hh:mm";

    public static String secShortType() {
        return todate(new Date(), "yyMMddhhmmss");
    }

    public static String todate(Date date, String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        String dateStr = null;
        try {
            dateStr = formatter.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateStr;
    }

    public static String todate(Calendar calendar, String pattern) {
        Date time = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        String dateStr = null;
        try {
            dateStr = formatter.format(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateStr;
    }


    public static String todate(String date, String pattern, String targetPattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.CHINA);
        SimpleDateFormat targetFormat = new SimpleDateFormat(targetPattern, Locale.CHINA);
        String dateStr = null;
        try {
            dateStr = targetFormat.format(formatter.parse(date));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateStr;
    }

    public static String lastMonth(String year, String month) {
        if (StringUtils.isEmpty(year) || StringUtils.isEmpty(month))
            throw new IllegalArgumentException("年月参数信息不正确");

        try {
            String yearMonth = yearMonth(year, month);
            SimpleDateFormat sdf = new SimpleDateFormat(DEF_PATTERN);
            Date date = addMonth(sdf.parse(yearMonth + "-11"), -1);
            return todate(date, "yyyy-MM");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 判断给定的字符串是否是年初的时间
     *
     * @param date 日期字符串，yyyy-MM
     * @return 是否为年初的标记
     */
    public static boolean isBeginOfYear(String date) {
        if (StringUtils.isEmpty(date) || !isFormat(date, "yyyy-MM"))
            throw new IllegalArgumentException("年月参数信息不正确");

        int month = Integer.parseInt(date.split("-")[1]);
        return month == 1 ? true : false;
    }

    /**
     * 检查给定的字符串是否是指定的日期格式
     *
     * @param dttm   日期字符串
     * @param format 日期格式
     * @return 格式正确与否
     */
    public static boolean isFormat(String dttm, String format) {
        boolean retValue = false;
        if (dttm != null) {
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            try {
                formatter.parse(dttm);
                retValue = true;
            } catch (ParseException e) {
            }
        }
        return retValue;
    }

    /**
     * 得到当前时间字符串，支持三种各个，0为short yyyy-MM-dd， 1为middle yyyy-MM-dd hh:mm，2为long yyyy-MM-dd hh:mm:ss
     * @param type
     * @return
     */
    public static String now(int type) {
        switch (type) {
            case 0 : return str(new Date(), "yyyy-MM-dd");
            case 1 : return str(new Date(), "yyyy-MM-dd HH:mm");
            case 2 : return str(new Date(), "yyyy-MM-dd HH:mm:ss");
            default: return str(new Date(), "yyyy-MM-dd");
        }
    }

    /**
     * 得到当前月份的月初
     *
     * @return
     */
    public static Date initDateByMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取years年后的月末日期
     *
     * @param years
     * @return
     */
    public static Date getMaxMonthDate(int years) {
        SimpleDateFormat dft = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + years);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return calendar.getTime();
    }

    public static String str(Date date) {
        return str(date, DEF_PATTERN);
    }

    public static String str(Date date, String pattern) {
        return todate(date, pattern);
    }

    public static String yearMonth(String year, String month) {
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.parseInt(year));
        calendar.set(Calendar.MONTH, Integer.parseInt(month) - 1);

        return dft.format(calendar.getTime());
    }

    public static String formatDate(String year, String month, String day) {
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.parseInt(year));
        calendar.set(Calendar.MONTH, Integer.parseInt(month) - 1);
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));

        return dft.format(calendar.getTime());
    }

    public static String nowYear() {
        String date = todate(new Date(), DEF_PATTERN);
        return date.split("-")[0];
    }

    public static Date parse(String date) {
        return parse(date, DEF_PATTERN);
    }

    public static Date parse(String date, String pattern) {
        try {
            if (StringUtils.isEmpty(date))
                return null;

            SimpleDateFormat dft = new SimpleDateFormat(pattern);
            return dft.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String lastYear() {
        SimpleDateFormat dft = new SimpleDateFormat("yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1);
        return dft.format(calendar.getTime());
    }

    public static String nowMonth() {
        String date = todate(new Date(), DEF_PATTERN);
        return date.split("-")[1];
    }

    public static String currentTimeMillis() {
        String datePattern = "yyyy/MM/dd HH:mm:ss-SSS";
        return todate(new Date(), datePattern);
    }

    /**
     * 得到指定日期的月份天数
     *
     * @param date 指定日期
     * @return 月份天数
     */
    public static int getDaysOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取指定日期的下一日的日期
     *
     * @param date 指定日期
     * @return 下一日的日期
     */
    public static Date nextDay(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);
        return calendar.getTime();
    }

    public static Date addHours(Date date, int hours) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, hours);
        return calendar.getTime();
    }

    public static Date addMonth(Date date, int month) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, month);
        return calendar.getTime();
    }

    public static boolean lessEqual(Date date1, Date date2) {
        if (date1 == null || date2 == null)
            return false;

        if (date1.getTime() <= date2.getTime())
            return true;
        else
            return false;
    }

    public static Date toDate(LocalDateTime ldt) {
        if (ldt == null)
            return null;

        SimpleDateFormat formatter = new SimpleDateFormat(DEF_PATTERN);
        try {
            return formatter.parse(ldt.toLocalDate().toString());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 时间增加指定分钟
     *
     * @param date
     * @param min
     * @return
     */
    public static String addMin(String date, int min) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date d = sdf.parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            cal.add(Calendar.MINUTE, min);
            date = sdf.format(cal.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 时间格式：yyyy-MM-dd HH:mm
     * 比较大小  DATE1大于DATE2返回1,等于返回0，小于返回-1
     *
     * @param DATE1
     * @param DATE2
     * @return
     */
    public static int compare_date(String DATE1, String DATE2) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        try {
            Date dt1 = df.parse(DATE1);
            Date dt2 = df.parse(DATE2);
            return compareDate(dt1, dt2);
        } catch (Exception var5) {
            var5.printStackTrace();
            return 0;
        }
    }

    public static int compareNow(Date dt2) {
        return compareDate(new Date(), dt2);
    }

    public static int compareDate(Date dt1, Date dt2) {
        try {
            if (dt1.getTime() > dt2.getTime()) {
                return 1;
            } else {
                return dt1.getTime() < dt2.getTime() ? -1 : 0;
            }
        } catch (Exception var5) {
            var5.printStackTrace();
            return 0;
        }
    }

    public static int compareHours(String hours1, String hours2) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");

        try {
            Date dt1 = df.parse(hours1);
            Date dt2 = df.parse(hours2);
            return compareDate(dt1, dt2);
        } catch (Exception var5) {
            var5.printStackTrace();
            return 0;
        }
    }

    /**
     * @param nowTime   当前时间
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     * @author sunran   判断当前时间在时间区间内
     */
    public static boolean isEffectiveDate(Date nowTime, Date startTime, Date endTime) {
        if (nowTime.getTime() == startTime.getTime()
                || nowTime.getTime() == endTime.getTime()) {
            return true;
        }

        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(startTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isRange(String startTime, String endTime) {
        Date startDate = parse(str(new Date()) + " " + startTime + ":00", "yyyy-MM-dd HH:mm:ss");
        Date endDate = parse(str(new Date()) + " " + endTime + ":00", "yyyy-MM-dd HH:mm:ss");
        return isEffectiveDate(new Date(), startDate, endDate);
    }

    public static Date convertString2Date(String format, String dateStr) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            Date date = simpleDateFormat.parse(dateStr);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取固定间隔时刻集合
     *
     * @param start    开始时间
     * @param end      结束时间
     * @param interval 时间间隔(单位：分钟)
     * @return
     */
    public static List<Date> getIntervalTimeList(String start, String end, int interval) {
        Date startDate = convertString2Date("yyyy-MM-dd HH:mm", str(new Date()) + " " + start);
        Date endDate = convertString2Date("yyyy-MM-dd HH:mm", str(new Date()) + " " + end);
        List<Date> list = new ArrayList<>();
        while (startDate.getTime() <= endDate.getTime()) {
            list.add(startDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);
            calendar.add(Calendar.MINUTE, interval);
            if (calendar.getTime().getTime() > endDate.getTime()) {
                if (!startDate.equals(endDate)) {
                    list.add(endDate);
                }
                startDate = calendar.getTime();
            } else {
                startDate = calendar.getTime();
            }

        }
        return list;
    }

    /**
     * 将传入的日期字符串转换为今天,明天,后天,大后天
     *
     * @param date
     * @return
     */
    public static String getDateDetail(String date) {
        if(StringUtils.isEmpty(date) || !(date.length() == 16 || date.length() == 19))
            throw new IllegalArgumentException("日期格式错误");

        Calendar today = Calendar.getInstance();
        Calendar target = Calendar.getInstance();

        DateFormat df = null;
        if(date.length() == 16)
            df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        else
            df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date time = null;
        try {
            time = df.parse(date);
            today.setTime(new Date());
            today.set(Calendar.HOUR, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            target.setTime(time);
            target.set(Calendar.HOUR, 0);
            target.set(Calendar.MINUTE, 0);
            target.set(Calendar.SECOND, 0);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        long intervalMilli = target.getTimeInMillis() - today.getTimeInMillis();
        int xcts = (int) (intervalMilli / (24 * 60 * 60 * 1000));
        String s = showDateDetail(xcts, target);
        if(time != null)
            s += "" + str(time, "HH时mm分");
        return s;

    }

    /**
     * 将日期差显示为日期
     *
     * @param xcts
     * @param target
     * @return
     */
    private static String showDateDetail(int xcts, Calendar target) {
        switch (xcts) {
            case 0:
                return "今天";
            case 1:
                return "明天";
            case 2:
                return "后台";
            case 3:
                return "大后天";
            default:
                return dateToWeek(getNowDateToStr());
        }
    }

    /**
     * 根据日期获取当天是周几
     * @param datetime 日期
     * @return 周几
     */
    public static String dateToWeek(String datetime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String[] weekDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        Calendar cal = Calendar.getInstance();
        Date date;
        try {
            date = sdf.parse(datetime);
            cal.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        return weekDays[w];
    }

    private static String getNowDateToStr() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String nowDateStr = sdf.format(date);//将当前时间格式化为需要的类型
        return nowDateStr;
    }

    /**
     * 获取当前Date型日期
     * 
     * @return Date() 当前日期
     */
    public static Date getNowDate()
    {
        return new Date();
    }

    /**
     * 获取当前日期, 默认格式为yyyy-MM-dd
     * 
     * @return String
     */
    public static String getDate()
    {
        return dateTimeNow(YYYY_MM_DD);
    }

    public static final String getTime()
    {
        return dateTimeNow(YYYY_MM_DD_HH_MM_SS);
    }

    public static final String dateTimeNow()
    {
        return dateTimeNow(YYYYMMDDHHMMSS);
    }

    public static final String dateTimeNow(final String format)
    {
        return parseDateToStr(format, new Date());
    }

    public static final String dateTime(final Date date)
    {
        return parseDateToStr(YYYY_MM_DD, date);
    }

    public static final String parseDateToStr(final String format, final Date date)
    {
        return new SimpleDateFormat(format).format(date);
    }

    public static final Date dateTime(final String format, final String ts)
    {
        try
        {
            return new SimpleDateFormat(format).parse(ts);
        }
        catch (ParseException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * 日期路径 即年/月/日 如2018/08/08
     */
    public static final String datePath()
    {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyy/MM/dd");
    }

    /**
     * 日期路径 即年/月/日 如20180808
     */
    public static final String dateTime()
    {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyyMMdd");
    }

    /**
     * 日期型字符串转化为日期 格式
     */
    public static Date parseDate(Object str)
    {
        if (str == null)
        {
            return null;
        }
        try
        {
            return parseDate(str.toString(), parsePatterns);
        }
        catch (ParseException e)
        {
            return null;
        }
    }
    
    /**
     * 获取服务器启动时间
     */
    public static Date getServerStartDate()
    {
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        return new Date(time);
    }

    /**
     * 计算两个时间差
     */
    public static String getDatePoor(Date endDate, Date nowDate)
    {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        return day + "天" + hour + "小时" + min + "分钟";
    }
}
