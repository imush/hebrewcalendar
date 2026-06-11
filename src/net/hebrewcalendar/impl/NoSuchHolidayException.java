package net.hebrewcalendar.impl;

import net.hebrewcalendar.IDate;
import net.hebrewcalendar.SpecialDay;

public class NoSuchHolidayException
        extends Exception
{
    private final String _msg;
    private final SpecialDay<?> _h;
    private final IDate<?> _refDate;

    public NoSuchHolidayException(final SpecialDay<?> h, final IDate<?> refDate, final String msg) {
        _h = h;
        _msg = msg;
        _refDate = refDate;
    }

    @Override
    public final String getMessage()
    {
        return _msg + ", holiday=" + _h + ", refDate=" + _refDate;
    }
}
