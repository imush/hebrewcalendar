package net.hebrewcalendar.impl;

import net.hebrewcalendar.HCalendarType;

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
        return year%4 == 0 && (year%400 == 0 || year%100 != 0);
    }

    @Override
    public HCalendarType getType()
    {
        return HCalendarType.GREGORIAN;
    }

    long getStart()
    {
        return 1373429;
    }
}
