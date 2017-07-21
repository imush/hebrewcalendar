package org.hebrewcalendar.impl;

import org.hebrewcalendar.HCalendar;

import java.util.Date;

/**
 * Created by itz on 7/20/17.
 */
public abstract class CommonCalendar
    implements HCalendar
{
    @Override
    public final int monthLength(int year, int month)
    {
        if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12)
            return 31;
        if (month == 2)
            return isLeap(year) ? 29 : 28;
        else
            return 30;
    }

    @Override
    public final int monthsInYear(int year)
    {
        return 12;
    }
}
