package org.hebrewcalendar.impl.holiday;

import org.hebrewcalendar.HDate;
import org.hebrewcalendar.HHoliday;

/**
 * An {@link HHoliday} that matches if and only if the inner reference day does not match.
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
    public boolean matches(HDate date)
    {
        return !_referenceDay.matches(date);
    }
}
