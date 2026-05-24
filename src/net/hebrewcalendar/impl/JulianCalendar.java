package net.hebrewcalendar.impl;

import net.hebrewcalendar.CalendarType;

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
    public CalendarType getType()
    {
        return CalendarType.JULIAN;
    }

    long getStart()
    {
        return 1373427;
    }

}
