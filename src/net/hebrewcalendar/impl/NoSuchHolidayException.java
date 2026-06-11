package net.hebrewcalendar.impl;

import net.hebrewcalendar.IDate;
import net.hebrewcalendar.SpecialDay;

public class NoSuchHolidayException
        extends Exception
{
    private String _msg;
    private SpecialDay<?> _h;
    private IDate<?> _refDate;

    public NoSuchHolidayException(SpecialDay<?> h, IDate<?> refDate, String msg) {
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
