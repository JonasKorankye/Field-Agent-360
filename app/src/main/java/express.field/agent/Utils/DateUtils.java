package express.field.agent.Utils;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;


import androidx.annotation.RequiresApi;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;


public class DateUtils {

    private static final Map<String, DateFormat> cache = new HashMap<>();

    public static String formatTime(Context context, Date date) {
        DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(context);
        return timeFormat.format(date);
    }

    public static String formatDate(Context context, Date date) {
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);
        return dateFormat.format(date);
    }

    public static String format(Date date, String format) {
        if (date == null) {
            return "";
        }
        DateFormat formatter = cache.get(format);
        if (formatter == null) {
            formatter = new SimpleDateFormat(format);
            cache.put(format, formatter);
        }

        return formatter.format(date);
    }

    public static Date toDate(String date, String format) {
        DateFormat formatter = cache.get(format);
        if (formatter == null) {
            formatter = new SimpleDateFormat(format);
            cache.put(format, formatter);
        }

        try {
            return formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String convert(String inputDate, String fromFormat, String toFormat) {
        Date date = toDate(inputDate, fromFormat);

        if (date != null) {
            String result = format(date, toFormat);
            return result;
        }

        return "";
    }

    public static boolean IsToday(long timestamp) {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(timestamp);

        Calendar today = Calendar.getInstance();

        return date.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) &&
            date.get(Calendar.YEAR) == today.get(Calendar.YEAR);

    }

    public static boolean IsFromLastWeek(long timestamp) {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(timestamp);

        Calendar today = Calendar.getInstance();
        Calendar lastWeek = Calendar.getInstance();
        lastWeek.add(Calendar.DATE, -7);

        return date.before(today) && date.after(lastWeek);
    }


    public static long TimestampByString(SimpleDateFormat formatter, String date) {
        try {
            Date d = formatter.parse(date);

            return d.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);

        return cal.getTime();
    }

    public static boolean IsToday(String date, String format) {
        if (TextUtils.isEmpty(date)) {
            return false;
        }

        return IsToday(toDate(date, format).getTime());
    }

    public static long TimestampByString(String timestamp) {
        try {
            return Long.parseLong(timestamp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

//    public static DateFormat getSystemDateFormat() {
//        Context context = ContextProviderService.getInstance().inject();
//        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);
//
//        return dateFormat;
//    }

    public static boolean IsSameDate(Date d1, Date d2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        cal1.setTime(d1);
        cal2.setTime(d2);

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    public static int getDifferenceFromDates(Date firstDate, Date secondDate) {
        Calendar a = getCalendarByTime(firstDate);
        Calendar b = getCalendarByTime(secondDate);
        int diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
        if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH) ||
            (a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a.get(Calendar.DATE) > b.get(Calendar.DATE))) {
            diff--;
        }
        return diff;
    }

    public static Calendar getCalendarByTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }


    public static Date parseDateByGivenFormats(final String dateString, final String[] acceptedFormats) {
        ParsePosition position = new ParsePosition(0);
        for (String format : acceptedFormats) {
            if (format == null) {
                continue;
            }
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date date = sdf.parse(dateString, position);
            if (date != null && position.getIndex() == dateString.length()) {
                return date;
            }
            position.setIndex(0);
        }
        return null;
    }

    /**
     * Method for changing date string pattern
     *
     * @param dateStr                  - date string
     * @param sourceFormatPattern      - actual pattern of the initial string date
     * @param destinationFormatPattern - desired final string pattern
     * @return - string date in new pattern
     */
    public static String convertDateStrToNewFormat(String dateStr, String sourceFormatPattern, String destinationFormatPattern) {
        TimeZone utc = TimeZone.getTimeZone("UTC");
        SimpleDateFormat sourceFormat = new SimpleDateFormat(sourceFormatPattern);
        SimpleDateFormat destFormat = new SimpleDateFormat(destinationFormatPattern);
        sourceFormat.setTimeZone(utc);
        Date convertedDate = null;
        try {
            convertedDate = sourceFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (convertedDate != null) {
            return destFormat.format(convertedDate);
        }

        return "";
    }

    public static String getDateTimeAtStartOfDay() {
        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new Date().getTime());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
    }


    public static String getDateTimeAtEndOfDay() {
        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(LocalDate.now());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        return format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
    }
}
