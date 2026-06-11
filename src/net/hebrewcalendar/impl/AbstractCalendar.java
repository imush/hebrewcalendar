package net.hebrewcalendar.impl;

import net.hebrewcalendar.ICalendar;
import net.hebrewcalendar.IDate;

public abstract class AbstractCalendar<C extends ICalendar<C>>
    implements ICalendar<C>
{
    /**
     * Create an {@link IDate} for this calendar.
     * @param year the year
     * @param month the month
     * @param day the day
     * @return an {@link IDate} object
     */
    @Override
    @SuppressWarnings("unchecked")
    public final DateImpl<C> fromYMD(int year, int month, int day)
    {
        int m = month > 0 ? month : monthsInYear(year) + 1 + month;
        int d = day > 0 ? day : monthLength(year, m) + 1 + day;
        return new DateImpl<>((C) this, year, m, d);
    }


    /**
     * @param year
     * @param month Note that negative month is allowed to count from end of year; m=-1
     *              denotes last month of year
     * @param day
     * @return whether the date would be valid
     */
    @Override
    public final boolean isValidDate(final int year, final int month, final int day)
    {
        if (year == 0 || month == 0 || day <= 0 || Math.abs(month) > monthsInYear(year))
            return false;
        final int m = month > 0 ? month : monthsInYear(year) + 1 + month;
        return Math.abs(day) <= monthLength(year, m);
    }

    /**
     * Add a given number of days to an {@link IDate}
     * @param date date object
     * @param numDays number of days to add
     * @return a new {@link IDate} object
     */
    @Override
    public final DateImpl<C> addDays(IDate<C> date, final int numDays)
    {
        if (numDays < 0)
            return subtractDays(date, -numDays);
        int y = date.getYear();
        int m = date.getMonth();
        int d = date.getDay();
        int inc = numDays;
        while (inc > monthLength(y, m) - d) {
            inc -= (monthLength(y, m) - d + 1);
            d = 1;
            final int[] nextYearMonth = nextYearMonth(y, m);
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
    public final DateImpl<C> subtractDays(IDate<C> date, final int numDays)
    {
        if (numDays < 0)
            return addDays(date, -numDays);
        int y = date.getYear();
        int m = date.getMonth();
        int d = date.getDay();
        int inc = numDays;

        while (inc >= d) {
            inc -= d;
            final int[] prevYearMonth = prevYearMonth(y, m);
            y = prevYearMonth[0];
            m = prevYearMonth[1];
            d = monthLength(y, m);
        }
        d -= inc;
        return fromYMD(y, m, d);

    }

    abstract long absDay(IDate<C> date);

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
     * @return array of 2 ints [year, month]
     */
    abstract int[] prevYearMonth(int y, int m);

    abstract long getStart();

    abstract DateImpl<C> fromAbs(long absDay);

    @Override
    public final String toString()
    {
        final String name = getType().name();
        return name.charAt(0) + name.substring(1).toLowerCase();
    }

    @Override
    @SuppressWarnings("unchecked")
    public final DateImpl<C> convert(IDate<?> otherDate)
    {
        if (getType().equals(otherDate.getCalendarType()))
            return (DateImpl<C>) otherDate; // safe: same type guarantees same calendar C
        final long absDay = ((DateImpl<?>) otherDate).absDay();
        return fromAbs(absDay);
    }
}
