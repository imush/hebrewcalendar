package org.hebrewcalendar.impl;

import org.hebrewcalendar.HDate;
import org.hebrewcalendar.HHoliday;

public class NoSuchHolidayException
        extends Exception
{
    private String _msg;
    private HHoliday _h;
    private HDate _refDate;

    public NoSuchHolidayException(HHoliday h, HDate refDate, String msg) {
        _h = h;
        _msg = msg;
        _refDate = refDate;
    }

    @Override
    public String getMessage()
    {
        return _msg + ", holiday=" + _h + ", refDate=" + _refDate;
    }
}
