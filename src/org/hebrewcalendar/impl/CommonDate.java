package org.hebrewcalendar.impl;

import org.hebrewcalendar.HCalendar;

/**
 * Created by itz on 7/19/17.
 */
public abstract class CommonDate<C extends CommonDate>
    extends AbstractDate<C>
{
    final static long COMMON_START = 1373429;

    CommonDate(HCalendar cal, int y, int m, int d)
    {
        super (cal, y, m, d);
    }

    @Override
    public final C addDays(final int numDays)
    {
        if (numDays < 0)
            return subtractDays(-numDays);
        int y = getYear();
        int m = getMonth();
        int d = getDay();
        int inc = numDays;
        HCalendar cal = getCalendar();
        while (inc > cal.monthLength(y, m) - d) {
            inc -= (cal.monthLength(y, m) - d + 1);
            d = 1;
            m++;
            if (m == 13) {
                m = 1;
                y++;
            }
        }

        d += inc;
        return fromYMD(y, m, d);
    }

    public C subtractDays(final int numDays)
    {
        if (numDays < 0)
            return addDays(-numDays);
        int y = getYear();
        int m = getMonth();
        int d = getDay();
        int inc = numDays;

        HCalendar cal = getCalendar();
        while (inc >= d) {
            inc -= d;
            m--;
            if (m == 0) {
                m = 12;
                y--;
            }
            d = cal.monthLength(y, m);
        }
        d -= inc;
        return fromYMD(y, m, d);

    }


    public long getAbsDay()
    {
        long toReturn = COMMON_START;

        int d = getDay();
        toReturn += d;

        int m = getMonth();
        int y = getYear();

        HCalendar cal = getCalendar();
        while (m > 1) {
            toReturn += cal.monthLength(y,m);
            --m;
        }

        y -= 1;

        int twoHundredYearCycles = (y-1)/200;

        int monthsInTwoHundredYears = 365*200+49 + cal.monthLength(2000, 2)-28;
        toReturn += twoHundredYearCycles * monthsInTwoHundredYears;

        y -= (200*twoHundredYearCycles);

        while (y > 1) {
            toReturn += (cal.isLeap(y) ? 366 : 365);
            --y;
        }
        return toReturn;
    }

}
