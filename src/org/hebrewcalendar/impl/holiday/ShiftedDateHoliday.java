package org.hebrewcalendar.impl.holiday;

import org.hebrewcalendar.HDate;
import org.hebrewcalendar.HHoliday;

public final class ShiftedDateHoliday
    extends AbstractHoliday
{
    private final int _shift;
    private final HHoliday _referenceDay;

    /**
     * @param name Name of the holiday
     * @param referenceDay the referenced {@link HHoliday}
     * @param shift days to add to reference date, may be negative
     */
    public ShiftedDateHoliday(String name, HHoliday referenceDay, int shift)
    {
        super(referenceDay.getCalendar(), name);
        _referenceDay = referenceDay;
        _shift = shift;
    }

    @Override
    public HDate getNextOccurrenceOnOrAfter(HDate date)
    {
        return _referenceDay.getNextOccurrenceOnOrAfter(date.subtractDays(_shift)).addDays(_shift);
    }

    @Override
    public boolean matches(HDate date)
    {
        return _referenceDay.matches(date.subtractDays(_shift));
    }
}
