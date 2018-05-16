package net.hebrewcalendar.impl.holiday;

import net.hebrewcalendar.HCalendar;
import net.hebrewcalendar.HDate;

public class NthDayOfWeek
        extends AbstractHoliday
{
    private final int _dayOfWeek;

    public NthDayOfWeek(HCalendar cal, int n) {
        super(cal, "dow=" + n);
        _dayOfWeek = n;
    }

    @Override
    public boolean matches(HDate date)
    {
        return date.getDayOfWeek() == _dayOfWeek;
    }
}
