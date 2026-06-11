package net.hebrewcalendar.impl.holiday;

import net.hebrewcalendar.ICalendar;
import net.hebrewcalendar.IDate;
import net.hebrewcalendar.impl.NoSuchHolidayException;
import net.hebrewcalendar.SpecialDay;

public final class ShiftedDateSpecialDay<C extends ICalendar<C>>
    extends AbstractRecurringSpecialDay<C>
{
    private final int _shift;
    private final SpecialDay<C> _referenceDay;

    /**
     * @param name Name of the holiday
     * @param referenceDay the referenced {@link SpecialDay}
     * @param shift days to add to reference date, may be negative
     */
    public ShiftedDateSpecialDay(final String name, final SpecialDay<C> referenceDay, final int shift)
    {
        super(referenceDay.getCalendar(), name);
        _referenceDay = referenceDay;
        _shift = shift;
    }

    @Override
    public IDate<C> getNextOccurrence(final IDate<C> date, final boolean strict)
            throws NoSuchHolidayException
    {
        return _referenceDay.getNextOccurrence(date.subtractDays(_shift), strict).addDays(_shift);
    }

    @Override
    public IDate<C> getPrevOccurrence(final IDate<C> date, final boolean strict)
            throws NoSuchHolidayException
    {
        return _referenceDay.getPrevOccurrence(date.subtractDays(_shift), strict).addDays(_shift);
    }


    @Override
    public boolean matches(final IDate<C> date)
    {
        return _referenceDay.matches(date.subtractDays(_shift));
    }
}
