package org.hebrewcalendar.impl;

import org.hebrewcalendar.HCalendar;
import org.hebrewcalendar.HDate;
import org.hebrewcalendar.HHoliday;

/**
 * An {@HHoliday} that matches if and only if the inner reference day does not match.
 */
public final class NegationHoliday
    extends AbstractHoliday
{
    private final HHoliday _referenceDay;

    /**
     * @param name Name of the holiday
     * @param referenceDay the referenced {@link HHoliday}
     */
    public NegationHoliday(String name, HHoliday referenceDay)
    {
        super(referenceDay.getCalendar(), name);
        _referenceDay = referenceDay;
     }

    @Override
    public HDate getNextOccurrenceOnOrAfter(HDate date)
    {
        HDate candidate = date;
        for (int i = 0; i < 365*4; i++) {
            if (_referenceDay.matches(candidate))
                candidate = candidate.addDays(1);
            else
                return candidate;
        }
        return null;
    }

    @Override
    public boolean matches(HDate date)
    {
        return !_referenceDay.matches(date);
    }
}
