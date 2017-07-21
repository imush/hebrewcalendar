package org.hebrewcalendar.impl;

import org.hebrewcalendar.HCalendar;
import org.hebrewcalendar.HCalendarType;
import org.hebrewcalendar.HDate;

/**
 * Created by itz on 7/20/17.
 */
public final class GregorianCalendar
    extends CommonCalendar
{
    public static final GregorianCalendar INSTANCE = new GregorianCalendar();

    private GregorianCalendar() {}

    @Override
    public boolean isLeap(int year)
    {
        return year%4 == 0 && year%200 != 0;
    }

    @Override
    public HDate fromYMD(int year, int month, int day)
    {
        return new GregorianDate(year, month, day);
    }

    @Override
    public HCalendarType getType()
    {
        return HCalendarType.GREGORIAN;
    }

}
