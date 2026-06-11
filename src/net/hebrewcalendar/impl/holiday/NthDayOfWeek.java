package net.hebrewcalendar.impl.holiday;

import net.hebrewcalendar.ICalendar;
import net.hebrewcalendar.IDate;

public class NthDayOfWeek<C extends ICalendar<C>>
        extends AbstractRecurringSpecialDay<C>
{
    private final int _dayOfWeek;

    public NthDayOfWeek(final C cal, final int n) {
        super(cal, "dow=" + n);
        _dayOfWeek = n;
    }

    @Override
    public boolean matches(final IDate<C> date)
    {
        return date.getDayOfWeek() == _dayOfWeek;
    }
}
