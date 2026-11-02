package com.example.taskflow.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Dates in this app are stored as plain local calendar dates in the form
 * yyyyMMdd (for example 20260315). 0 means "no date". Comparing two of these
 * numbers compares the dates, and there are no time-zone or DST surprises
 * because no time of day is stored.
 */
public final class DateHelper {

    public static final int NO_DATE = 0;

    private DateHelper() {
    }

    /** The device's current local date. */
    public static int today() {
        Calendar c = Calendar.getInstance();
        return toInt(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
    }

    /** month0 is zero-based, exactly like Calendar.MONTH and DatePickerDialog. */
    public static int toInt(int year, int month0, int day) {
        return year * 10000 + (month0 + 1) * 100 + day;
    }

    public static int yearOf(int date) {
        return date / 10000;
    }

    /** Zero-based month, ready for DatePickerDialog. */
    public static int month0Of(int date) {
        return (date / 100) % 100 - 1;
    }

    public static int dayOf(int date) {
        return date % 100;
    }

    public static int addDays(int date, int days) {
        Calendar c = toCalendar(date);
        c.add(Calendar.DAY_OF_MONTH, days);
        return toInt(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
    }

    public static Calendar toCalendar(int date) {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(yearOf(date), month0Of(date), dayOf(date));
        return c;
    }

    /** "15 May 2022", or "No due date" when there is no date. */
    public static String format(int date) {
        if (date == NO_DATE) {
            return "No due date";
        }
        SimpleDateFormat format = new SimpleDateFormat("d MMM yyyy", Locale.getDefault());
        return format.format(toCalendar(date).getTime());
    }

    /** "20 May" style label for a timestamp. */
    public static String formatShort(long millis) {
        SimpleDateFormat format = new SimpleDateFormat("d MMM", Locale.getDefault());
        return format.format(new Date(millis));
    }

    /** Timestamp for noon of a given local date, used for sample "linked on" dates. */
    public static long millisOf(int date) {
        Calendar c = toCalendar(date);
        c.set(Calendar.HOUR_OF_DAY, 12);
        return c.getTimeInMillis();
    }
}
