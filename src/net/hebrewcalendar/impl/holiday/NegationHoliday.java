package net.hebrewcalendar.impl.holiday;

import net.hebrewcalendar.HDate;
import net.hebrewcalendar.HHoliday;

/**
 * An {@link HHoliday} that matches if and only if the inner reference day does not match.
 */
public final class NegationHoliday
    extends AbstractHoliday
{
    private final HHoliday _referenceDay;

    /**
     * @param name Name of the holiday
     * @param referenceHoliday the referenced {@link HHoliday}
     */
    public NegationHoliday(String name, HHoliday referenceHoliday)
    {
        super(referenceHoliday.getCalendar(), name);
        _referenceDay = referenceHoliday;
    }

    /**
     * @param referenceHoliday the referenced {@link HHoliday}
     */
    public NegationHoliday(HHoliday referenceHoliday)
    {
        this("Not " + referenceHoliday.getName(), referenceHoliday);
    }

    @Override
    public boolean matches(final HDate date)
    {
        return !_referenceDay.matches(date);
    }
}
