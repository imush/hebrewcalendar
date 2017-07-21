package org.hebrewcalendar.impl;

import org.hebrewcalendar.HCalendarType;

/**
 * Created by itz on 7/20/17.
 */
public class JulianDate
    extends CommonDate<JulianDate>
{
    public JulianDate(int year, int month, int day)
    {
        super(JulianCalendar.INSTANCE, year, month, day);
    }

    public JulianDate fromYMD(int year, int month, int day)
    {
        return new JulianDate(year, month, day);
    }
}
