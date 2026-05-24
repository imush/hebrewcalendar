package net.hebrewcalendar.impl.holiday;

import net.hebrewcalendar.IDate;
import net.hebrewcalendar.SpecialDay;

/**
 * A disjunction of conditions for two {@link SpecialDay}s.
 */
public final class UnionSpecialDay
    extends AbstractRecurringSpecialDay
{
    private final SpecialDay[] _underlying;

    public UnionSpecialDay(String name, SpecialDay[] underlying)
    {
        super(underlying[0].getCalendar(), name);
        _underlying = underlying;
    }

    @Override
    public boolean matches(IDate date)
    {
        for (SpecialDay h : _underlying) {
            if (h.matches(date))
                return true;
        }
        return false;
    }
}
