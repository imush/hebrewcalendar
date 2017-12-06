package org.hebrewcalendar.impl.holiday;

import org.hebrewcalendar.HDate;
import org.hebrewcalendar.HHoliday;

/**
 * A conjunction of conditions for two {@link HHoliday}s.
 */
public final class ConjunctionHoliday
    extends AbstractHoliday
{
    private final HHoliday[] _underlying;

    /**
     * @param name name
     * @param underlying underlying {@link HHoliday}s
     */
    public ConjunctionHoliday(String name, HHoliday[] underlying)
    {
        super(underlying[0].getCalendar(), name);
        _underlying = underlying;
    }

    /**
     * @param date date to check
     * @return true if all underlying holidays match, false otherwise
     */
    @Override
    public boolean matches(HDate date)
    {
        for (HHoliday h : _underlying) {
            if (!h.matches(date))
                return false;
        }
        return true;
    }
}
