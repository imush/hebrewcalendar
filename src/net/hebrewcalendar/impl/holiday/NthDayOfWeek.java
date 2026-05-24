package net.hebrewcalendar.impl.holiday;

import net.hebrewcalendar.ICalendar;
import net.hebrewcalendar.IDate;

public class NthDayOfWeek
        extends AbstractRecurringSpecialDay
{
    private final int _dayOfWeek;

    public NthDayOfWeek(ICalendar cal, int n) {
        super(cal, "dow=" + n);
        _dayOfWeek = n;
    }

    @Override
    public boolean matches(IDate date)
    {
        return date.getDayOfWeek() == _dayOfWeek;
    }
}
