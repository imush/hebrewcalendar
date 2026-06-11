package net.hebrewcalendar.impl.holiday;

import net.hebrewcalendar.ICalendar;
import net.hebrewcalendar.IDate;
import net.hebrewcalendar.SpecialDay;

/**
 * A conjunction of conditions for two or more {@link SpecialDay}s.
 */
public final class ConjunctionSpecialDay<C extends ICalendar<C>>
    extends AbstractRecurringSpecialDay<C>
{
    private final SpecialDay<C>[] _underlying;

    /**
     * @param name name
     * @param underlying underlying {@link SpecialDay}s (varargs)
     */
    @SafeVarargs
    public ConjunctionSpecialDay(String name, SpecialDay<C>... underlying)
    {
        super(underlying[0].getCalendar(), name);
        _underlying = underlying;
    }

    /**
     * @param underlying underlying {@link SpecialDay}s (varargs)
     */
    @SafeVarargs
    public ConjunctionSpecialDay(SpecialDay<C>... underlying)
    {
        this("ConjunctionSpecialDay", underlying);
    }

    /**
     * @param date date to check
     * @return true if all underlying holidays match, false otherwise
     */
    @Override
    public boolean matches(IDate<C> date)
    {
        for (SpecialDay<C> h : _underlying) {
            if (!h.matches(date))
                return false;
        }
        return true;
    }
}
