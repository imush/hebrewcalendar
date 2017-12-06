package org.hebrewcalendar.impl.holiday;

import org.hebrewcalendar.HDate;
import org.hebrewcalendar.HHoliday;

/**
 * A disjunction of conditions for two {@link HHoliday}s.
 */
public final class UnionHoliday
    extends AbstractHoliday
{
    private final HHoliday[] _underlying;

    public UnionHoliday(String name, HHoliday[] underlying)
    {
        super(underlying[0].getCalendar(), name);
        _underlying = underlying;
    }

    @Override
    public boolean matches(HDate date)
    {
        for (HHoliday h : _underlying) {
            if (h.matches(date))
                return true;
        }
        return false;
    }
}
