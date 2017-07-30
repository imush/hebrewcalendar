package org.hebrewcalendar.impl.holiday;

import org.hebrewcalendar.HDate;
import org.hebrewcalendar.HHoliday;

/**
 * A disjunction of conditions for two {@HHoliday}s.
 */
public final class UnionHoliday
    extends AbstractHoliday
{
    final HHoliday[] _underlying;

    public UnionHoliday(String name, HHoliday[] underlying)
    {
        super(underlying[0].getCalendar(), name);
        _underlying = underlying;
    }
    @Override
    public HDate getNextOccurrenceOnOrAfter(HDate date)
    {
        HDate candidate = null;
        for (HHoliday h : _underlying) {
            HDate nextCandidate = h.getNextOccurrenceOnOrAfter(date);
            if (candidate == null || nextCandidate.before(candidate))
                candidate = nextCandidate;
        }
        return candidate;
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
