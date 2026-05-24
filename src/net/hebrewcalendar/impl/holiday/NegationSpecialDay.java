package net.hebrewcalendar.impl.holiday;

import net.hebrewcalendar.IDate;
import net.hebrewcalendar.SpecialDay;

/**
 * An {@link SpecialDay} that matches if and only if the inner reference day does not match.
 */
public final class NegationSpecialDay
    extends AbstractRecurringSpecialDay
{
    private final SpecialDay _referenceDay;

    /**
     * @param name Name of the holiday
     * @param referenceHoliday the referenced {@link SpecialDay}
     */
    public NegationSpecialDay(String name, SpecialDay referenceHoliday)
    {
        super(referenceHoliday.getCalendar(), name);
        _referenceDay = referenceHoliday;
    }

    /**
     * @param referenceHoliday the referenced {@link SpecialDay}
     */
    public NegationSpecialDay(SpecialDay referenceHoliday)
    {
        this("Not " + referenceHoliday.getName(), referenceHoliday);
    }

    @Override
    public boolean matches(final IDate date)
    {
        return !_referenceDay.matches(date);
    }
}
