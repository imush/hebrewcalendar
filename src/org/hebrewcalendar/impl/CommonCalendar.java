package org.hebrewcalendar.impl;

import org.hebrewcalendar.HCalendar;
import org.hebrewcalendar.HDate;

import java.util.Date;


/**
 * Created by itz on 7/20/17.
 */
public abstract class CommonCalendar
    extends AbstractCalendar
{
    public static final long COMMON_START = 1373429;

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
    long absDay(HDate date)
    {
        long toReturn = COMMON_START;

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

        int daysin400 = 365*400 + (isJulian ? 100 : 97);
        toReturn += fourHundredYearCycles * daysin400;

        y -= (400*fourHundredYearCycles);

        while (y > 0) {
            toReturn += (isLeap(y) ? 366 : 365);
            --y;
        }
        return toReturn;
    }

}
