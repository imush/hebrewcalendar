package net.hebrewcalendar.impl;

import net.hebrewcalendar.IDate;


/**
 * Abstract base for Gregorian and Julian calendars.
 */
public abstract class CommonCalendar<C extends CommonCalendar<C>>
    extends AbstractCalendar<C>
{
    @Override
    public final int monthLength(int year, int month)
    {
        if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12)
            return 31;
        else if (month == 2)
            return isLeap(year) ? 29 : 28;
        else
            return 30;
    }

    @Override
    public final int monthsInYear(int year)
    {
        return 12;
    }

    @Override
    final int[] nextYearMonth(int year, int month)
    {
        if (month == 12)
            return new int[] {year+1, 1};
        else
            return new int[] {year, month+1};
    }

    @Override
    final int[] prevYearMonth(int year, int month)
    {
        if (month == 1)
            return new int[] {year-1, 12};
        else
            return new int[] {year, month-1};
    }

    @Override
    long absDay(IDate<C> date)
    {
        long toReturn = getStart();

        int d = date.getDay();
        toReturn += d;

        int m = date.getMonth();
        int y = date.getYear();

        while (m > 1) {
            toReturn += monthLength(y,m-1);
            --m;
        }

        y -= 1;

        int fourHundredYearCycles = (y-1)/400;

        boolean isJulian = monthLength(1900, 2)==29;

        long daysin400 = 365*400 + (isJulian ? 100 : 97);
        toReturn += fourHundredYearCycles * daysin400;

        y -= (400*fourHundredYearCycles);

        while (y > 0) {
            toReturn += (isLeap(y) ? 366 : 365);
            --y;
        }
        return toReturn;
    }

    @Override
    @SuppressWarnings("unchecked")
    DateImpl<C> fromAbs(long absDay)
    {
        long absDayFromStart = absDay-getStart();
        final boolean isJulian = monthLength(1900, 2)==29;
        final int daysin400 = 365*400 + (isJulian ? 100 : 97);

        final int cycles = (int)((absDayFromStart-1)/daysin400);
        DateImpl<C> d0 = new DateImpl<>((C) this, 1 + 400*cycles, 1, 1);

        return d0.addDays((int)(absDayFromStart - cycles*daysin400-1));
    }

    @Override
    public DateImpl<C> firstDayOfYear(int year)
    {
        return fromYMD(year, 1, 1);
    }

    /**
     * Returns the anniversary of {@code originalDate} in {@code targetYear}.
     * Maps Feb 29 → March 1 when the target year is not a leap year.
     *
     * @throws IllegalArgumentException if the original date is Feb 30 or later (never valid)
     */
    public DateImpl<C> anniversaryFor(IDate<C> originalDate, int targetYear)
    {
        int month = originalDate.getMonth();
        int day   = originalDate.getDay();
        if (month == 2 && day > 29)
            throw new IllegalArgumentException(
                "Day " + day + " of February is never valid in any year");
        if (month == 2 && day == 29 && !isLeap(targetYear))
            return fromYMD(targetYear, 3, 1);
        return fromYMD(targetYear, month, day);
    }
}
