package org.hebrewcalendar.impl.holiday;

import org.hebrewcalendar.HDate;
import org.hebrewcalendar.HHoliday;
import org.hebrewcalendar.impl.NoSuchHolidayException;

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
    public HDate getNextOccurrence(HDate date, final boolean strict)
            throws NoSuchHolidayException
    {
        return _referenceDay.getNextOccurrence(date.subtractDays(_shift), strict).addDays(_shift);
    }

    @Override
    public HDate getPrevOccurrence(HDate date, final boolean strict)
            throws NoSuchHolidayException
    {
        return _referenceDay.getPrevOccurrence(date.subtractDays(_shift), strict).addDays(_shift);
    }


    @Override
    public boolean matches(HDate date)
    {
        return _referenceDay.matches(date.subtractDays(_shift));
    }
}
