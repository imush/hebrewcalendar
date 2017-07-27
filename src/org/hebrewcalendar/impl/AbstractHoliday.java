package org.hebrewcalendar.impl;

import org.hebrewcalendar.HCalendar;
import org.hebrewcalendar.HDate;
import org.hebrewcalendar.HHoliday;

public abstract class AbstractHoliday
    implements HHoliday
{
    private final HCalendar _calendar;
    private final String _name;

    AbstractHoliday(HCalendar calendar, String name)
    {
        _calendar = calendar;
        _name = name;
    }

    @Override
    public final String getName()
    {
        return _name;
    }

    @Override
    public final HCalendar getCalendar()
    {
        return _calendar;
    }
}
