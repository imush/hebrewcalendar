package org.hebrewcalendar.impl;

import org.hebrewcalendar.HDate;
import org.hebrewcalendar.HHoliday;

/**
 * A conjunction of conditions for two {@HHoliday}s.
 */
public final class ConjunctionHoliday
    extends AbstractHoliday
{
    final HHoliday[] _underlying;

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
     * @param date reference date to search from
     * @return next day when all holidays match, null if not found within 4 years.
     */
    @Override
    public HDate getNextOccurrenceOnOrAfter(HDate date)
    {
        for (HDate d = _underlying[0].getNextOccurrenceOnOrAfter(date); d.before(date.addDays(4*366));
                d = _underlying[0].getNextOccurrenceOnOrAfter(d)) {
            boolean match = true;
            for (int i = 1; i < _underlying.length; i++) {
                if (!_underlying[i].matches(d)) {
                    match = false;
                    break;
                }
            }
            return d;
        }
        return null;
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
