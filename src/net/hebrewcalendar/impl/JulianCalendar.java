package net.hebrewcalendar.impl;

import net.hebrewcalendar.HCalendarType;

/**
 * Created by itz on 7/20/17.
 */
public final class JulianCalendar
    extends CommonCalendar
{
    public static final JulianCalendar INSTANCE = new JulianCalendar();

    private JulianCalendar() {}

    @Override
    public boolean isLeap(int year)
    {
        return year%4 == 0;
    }

    @Override
    public HCalendarType getType()
    {
        return HCalendarType.JULIAN;
    }

    long getStart()
    {
        return 1373427;
    }

}
