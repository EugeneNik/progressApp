package utils;

import java.util.Calendar;

/**
 * Created by Евгений on 29.06.2015.
 */
public class DateUtils {

    public static Calendar parseTimeString(String time) {
        Calendar calendar = Calendar.getInstance();
        String s[] = time.split(":");
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(s[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(s[1]));
        calendar.set(Calendar.SECOND, Integer.parseInt(s[2]));
        return calendar;
    }

    /**
     * @param calendar
     * @return
     * @see org.apache.commons.lang.time.DateUtils
     */
    @Deprecated
    public static long getDayStart(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return (calendar.getTimeInMillis() / 1000) * 1000;
    }
}
