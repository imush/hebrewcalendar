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
    final long absDay(final IDate<C> date)
    {
        final int year       = date.getYear();
        final int month      = date.getMonth();
        final int day        = date.getDay();
        final boolean julian = monthLength(1900, 2) == 29;
        final int daysIn400  = julian ? 146100 : 146097;
        final int priorYears = year - 1;
        final int fullCycles = priorYears / 400;

        long result = getStart() + day;

        for (int m = 1; m < month; m++)
            result += monthLength(year, m);

        result += (long) fullCycles * daysIn400;
        for (int y = fullCycles * 400 + 1; y <= priorYears; y++)
            result += isLeap(y) ? 366 : 365;

        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    final DateImpl<C> fromAbs(long absDay)
    {
        final long absDayFromStart = absDay-getStart();
        final boolean isJulian = monthLength(1900, 2)==29;
        final int daysin400 = 365*400 + (isJulian ? 100 : 97);

        final int cycles = (int)((absDayFromStart-1)/daysin400);
        final DateImpl<C> d0 = new DateImpl<>((C) this, 1 + 400*cycles, 1, 1);

        return d0.addDays((int)(absDayFromStart - cycles*daysin400-1));
    }

    @Override
    public final DateImpl<C> firstDayOfYear(int year)
    {
        return fromYMD(year, 1, 1);
    }

    /**
     * Returns the anniversary of {@code originalDate} in {@code targetYear}.
     * If the day exceeds the month length in the target year, falls to the 1st of the next month.
     */
    @Override
    public final DateImpl<C> anniversaryFor(final IDate<C> originalDate, final int targetYear)
    {
        final int month = originalDate.getMonth();
        final int day   = originalDate.getDay();
        if (day > monthLength(targetYear, month)) {
            final int[] next = nextYearMonth(targetYear, month);
            return fromYMD(next[0], next[1], 1);
        }
        return fromYMD(targetYear, month, day);
    }
}
