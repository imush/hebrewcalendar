package net.hebrewcalendar.impl.holiday;

import net.hebrewcalendar.ICalendar;
import net.hebrewcalendar.IDate;
import net.hebrewcalendar.SpecialDay;

/**
 * An {@link SpecialDay} that matches if and only if the inner reference day does not match.
 */
public final class NegationSpecialDay<C extends ICalendar<C>>
    extends AbstractRecurringSpecialDay<C>
{
    private final SpecialDay<C> _referenceDay;

    /**
     * @param name Name of the holiday
     * @param referenceHoliday the referenced {@link SpecialDay}
     */
    public NegationSpecialDay(String name, SpecialDay<C> referenceHoliday)
    {
        super(referenceHoliday.getCalendar(), name);
        _referenceDay = referenceHoliday;
    }

    /**
     * @param referenceHoliday the referenced {@link SpecialDay}
     */
    public NegationSpecialDay(SpecialDay<C> referenceHoliday)
    {
        this("Not " + referenceHoliday.getName(), referenceHoliday);
    }

    @Override
    public boolean matches(final IDate<C> date)
    {
        return !_referenceDay.matches(date);
    }
}
