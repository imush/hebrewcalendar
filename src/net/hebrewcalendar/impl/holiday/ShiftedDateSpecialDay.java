package net.hebrewcalendar.impl.holiday;

import net.hebrewcalendar.IDate;
import net.hebrewcalendar.impl.NoSuchHolidayException;
import net.hebrewcalendar.SpecialDay;

public final class ShiftedDateSpecialDay
    extends AbstractRecurringSpecialDay
{
    private final int _shift;
    private final SpecialDay _referenceDay;

    /**
     * @param name Name of the holiday
     * @param referenceDay the referenced {@link SpecialDay}
     * @param shift days to add to reference date, may be negative
     */
    public ShiftedDateSpecialDay(String name, SpecialDay referenceDay, int shift)
    {
        super(referenceDay.getCalendar(), name);
        _referenceDay = referenceDay;
        _shift = shift;
    }

    @Override
    public IDate getNextOccurrence(IDate date, final boolean strict)
            throws NoSuchHolidayException
    {
        return _referenceDay.getNextOccurrence(date.subtractDays(_shift), strict).addDays(_shift);
    }

    @Override
    public IDate getPrevOccurrence(IDate date, final boolean strict)
            throws NoSuchHolidayException
    {
        return _referenceDay.getPrevOccurrence(date.subtractDays(_shift), strict).addDays(_shift);
    }


    @Override
    public boolean matches(IDate date)
    {
        return _referenceDay.matches(date.subtractDays(_shift));
    }
}
