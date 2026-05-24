package net.hebrewcalendar.impl;

import net.hebrewcalendar.ICalendar;
import net.hebrewcalendar.IDate;

public abstract class AbstractCalendar
    implements ICalendar
{
    /**
     * Create an {@link IDate} for this calendar.
     * @param year the year
     * @param month the month
     * @param day the day
     * @return an {@link IDate} object
     */
    public final IDateImpl fromYMD(int year, int month, int day)
    {
        int m = month > 0 ? month : monthsInYear(year) + 1 + month;
        int d = day > 0 ? day : monthLength(year, m) + 1 + day;
        return new IDateImpl(this, year, m, d);
    }


    /**
     * @param year
     * @param month Note that negative month is allowed to count from end of year; m=-1
     *              denotes last month of year
     * @param day
     * @return whether the date would be valid
     */
    @Override
    public boolean isValidDate(int year, int month, int day)
    {
        if (year == 0 || month == 0 || day <= 0 || Math.abs(month) > monthsInYear(year))
            return false;
        int m = month > 0 ? month : monthsInYear(year) + 1 + month;
        return Math.abs(day) <= monthLength(year, m);
    }

    /**
     * Add a given number of days to an {@link IDate}
     * @param date date object
     * @param numDays number of days to add
     * @return a new {@link IDate} object
     */
    @Override
    public final IDateImpl addDays(final IDate date, final int numDays)
    {
        if (numDays < 0)
            return subtractDays(date, -numDays);
        int y = date.getYear();
        int m = date.getMonth();
        int d = date.getDay();
        int inc = numDays;
        ICalendar cal = date.getCalendar();
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
     * Subtract a given number of days from an {@link IDate}
     * @param date date object
     * @param numDays number of days to subtract
     * @return a new {@link IDate} object
     */
    @Override
    public IDateImpl subtractDays(final IDate date, final int numDays)
    {
        if (numDays < 0)
            return addDays(date, -numDays);
        int y = date.getYear();
        int m = date.getMonth();
        int d = date.getDay();
        int inc = numDays;

        ICalendar cal = date.getCalendar();
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

    abstract long absDay(IDate date);

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

    abstract long getStart();

    abstract IDateImpl fromAbs(long absDay);

    @Override
    public final IDate convert(IDate otherDate)
    {
        if (getType().equals(otherDate.getCalendarType()))
            return otherDate;
        long absDay = ((IDateImpl)otherDate).absDay();
        return fromAbs(absDay);
    }
}
