package net.hebrewcalendar.impl.holiday;

import net.hebrewcalendar.IDate;
import net.hebrewcalendar.SpecialDay;

/**
 * A conjunction of conditions for two {@link SpecialDay}s.
 */
public final class ConjunctionSpecialDay
    extends AbstractRecurringSpecialDay
{
    private final SpecialDay[] _underlying;

    /**
     * @param name name
     * @param underlying underlying {@link SpecialDay}s
     */
    public ConjunctionSpecialDay(String name, SpecialDay[] underlying)
    {
        super(underlying[0].getCalendar(), name);
        _underlying = underlying;
    }

    /**
     * @param underlying underlying {@link SpecialDay}s
     */
    public ConjunctionSpecialDay(SpecialDay[] underlying)
    {
        this("ConjunctionSpecialDay", underlying);
    }

    /**
     * @param date date to check
     * @return true if all underlying holidays match, false otherwise
     */
    @Override
    public boolean matches(IDate date)
    {
        for (SpecialDay h : _underlying) {
            if (!h.matches(date))
                return false;
        }
        return true;
    }
}
