package org.hebrewcalendar.impl;

import org.hebrewcalendar.HCalendarType;
import org.hebrewcalendar.HDate;

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
}
