package net.hebrewcalendar.impl.holiday;

import net.hebrewcalendar.ICalendar;
import net.hebrewcalendar.IDate;
import net.hebrewcalendar.SpecialDay;

/**
 * A disjunction of conditions for two or more {@link SpecialDay}s.
 */
public final class UnionSpecialDay<C extends ICalendar<C>>
    extends AbstractRecurringSpecialDay<C>
{
    private final SpecialDay<C>[] _underlying;

    @SafeVarargs
    public UnionSpecialDay(String name, SpecialDay<C>... underlying)
    {
        super(underlying[0].getCalendar(), name);
        _underlying = underlying;
    }

    @Override
    public boolean matches(IDate<C> date)
    {
        for (SpecialDay<C> h : _underlying) {
            if (h.matches(date))
                return true;
        }
        return false;
    }
}
