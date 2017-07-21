package org.hebrewcalendar.impl;

import org.hebrewcalendar.HCalendar;
import org.hebrewcalendar.HDate;

public class HebrewDate
    extends AbstractDate<HebrewDate>
{
    HebrewDate(int year, int month, int day)
    {
        super(HCalendar.HEBREW, year, month, day);
    }

    @Override
    HebrewDate fromYMD(int year, int month, int day)
    {
        return null;
    }

    @Override
    public long getAbsDay()
    {
        return 0;
    }

    @Override
    public HDate addDays(int numDays)
    {
        return null;
    }
}
