package org.hebrewcalendar.impl;

import org.hebrewcalendar.HCalendar;
import org.hebrewcalendar.HDate;

public abstract class AbstractCalendar
    implements HCalendar
{
    /**
     * Create an {@link HDate} for this calendar.
     * @param year the year
     * @param month the month
     * @param day the day
     * @return an {@link HDate} object
     */
    public final HDate fromYMD(int year, int month, int day)
    {
        return new HDateImpl(this, year, month, day);
    }

    /**
     * Add a given number of days to an {@link HDate}
     * @param date date object
     * @param numDays number of days to add
     * @return a new {@link HDate} object
     */
    @Override
    public final HDate addDays(final HDate date, final int numDays)
    {
        if (numDays < 0)
            return subtractDays(date, -numDays);
        int y = date.getYear();
        int m = date.getMonth();
        int d = date.getDay();
        int inc = numDays;
        HCalendar cal = date.getCalendar();
        while (inc > cal.monthLength(y, m) - d) {
            inc -= (cal.monthLength(y, m) - d + 1);
            d = 1;
            int[] nextYearMonth = nextYearMonth(y, m);
            y = nextYearMonth[0];
            m = nextYearMonth[1];
        }

        d += inc;
        return fromYMD(y, m, d);
    }

    /**
     * Subtract a given number of days from an {@link HDate}
     * @param date date object
     * @param numDays number of days to subtract
     * @return a new {@link HDate} object
     */
    @Override
    public HDate subtractDays(final HDate date, final int numDays)
    {
        if (numDays < 0)
            return addDays(date, -numDays);
        int y = date.getYear();
        int m = date.getMonth();
        int d = date.getDay();
        int inc = numDays;

        HCalendar cal = date.getCalendar();
        while (inc >= d) {
            inc -= d;
            int[] prevYearMonth = prevYearMonth(y, m);
            y = prevYearMonth[0];
            m = prevYearMonth[1];
            d = cal.monthLength(y, m);
        }
        d -= inc;
        return fromYMD(y, m, d);

    }

    abstract long absDay(HDate date);

    /**
     * Calculate the next year and month
     * @param y year
     * @param m month
     * @return array of 2 ints [year, month]
     */
    abstract int[] nextYearMonth(int y, int m);

    /**
     * Calculate the previous year and month
     * @param y year
     * @param m month
     * @return array of 2 ints [year, month]
     */
    abstract int[] prevYearMonth(int y, int m);
}
