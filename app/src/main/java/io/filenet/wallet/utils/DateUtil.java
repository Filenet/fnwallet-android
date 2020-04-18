package io.filenet.wallet.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {
    public static int getDaysOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date);
        calendar2.add(Calendar.DAY_OF_MONTH, days);
        int day;
        for(day = days; day >= 0; day--) {
            Calendar calendar3 = Calendar.getInstance();
            calendar3.setTime(date);
            calendar3.add(Calendar.DAY_OF_MONTH, day + 3);
            if (calendar2.get(Calendar.MONTH) == calendar3.get(Calendar.MONTH)) {
                return day;
            }
        }
        return day;
    }
    public static long getExpiredTime(Date date){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.setTime(date);
        Calendar calendar2 = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar2.setTime(date);
        calendar2.add(Calendar.MONTH, 1);
        long expiredTime1 = calendar2.getTimeInMillis() - calendar.getTimeInMillis();
        calendar2.set(Calendar.DAY_OF_MONTH, calendar2.getActualMaximum(Calendar.DAY_OF_MONTH)-2);
        calendar2.set(Calendar.HOUR_OF_DAY, 0);
        calendar2.set(Calendar.MINUTE, 0);
        calendar2.set(Calendar.SECOND, 0);
        calendar2.set(Calendar.MILLISECOND, 0);
        long expiredTime2 = calendar2.getTimeInMillis() - calendar.getTimeInMillis();
        return expiredTime1 < expiredTime2 ? expiredTime1 : expiredTime2;
    }
    public static boolean isOneDay(){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        long a = date.getTime() - 28800000;
        if (format.format(new Date(a)).equals("20191231")){
            return true;
        }
        return false;
    }


}
