package net.hebrewcalendar.impl.holiday;

import net.hebrewcalendar.IDate;
import net.hebrewcalendar.ICalendar;
import net.hebrewcalendar.SpecialDay;

/**
 * Represents a special day that occurs on Nth day from another special day (holiday).
 */
public class NthDayOfWeekFromPivot<C extends ICalendar<C>>
    extends AbstractRecurringSpecialDay<C>
{
    private final SpecialDay<C> _pivot;
    private final int _n;
    private final int _dayOfWeek;
    private final boolean _inclusive;

    /**
     * @param calendar calendar for which the holiday is defined
     * @param name of holiday
     * @param pivotDate the event to count from.
     * @param dayOfWeek day of week (1=Sunday,... 7= Saturday). This can be negative to indicate the count
     *              of days from <i>end of month</i>.
     * @param n number of occurrence. This can be negative to indicate the <i>count from end of month</i>,
     *          e.g. Shabbat Hagadol is the last Shabbat before Pesach would use (1,15,-1, not inclusive)
     * @param inclusive whether pivot date counts if it matches the day of week
     */
    public NthDayOfWeekFromPivot(final C calendar, final String name, final SpecialDay<C> pivotDate, final int dayOfWeek, final int n, final boolean inclusive)
    {
        super(calendar, name);
        _pivot = pivotDate;
        _dayOfWeek = dayOfWeek;
        _n = n;
        _inclusive = inclusive;
        if (_n == 0 || Math.abs(n) > 5)
            throw new IllegalArgumentException("arg n must be nonzero and no larger than 5 by abs value");
    }

    /**
     *
     * @param date0 date to start from
     * @return true when matches
     */
    @Override
    public boolean matches(final IDate<C> date0)
    {
        if (date0.getDayOfWeek() != _dayOfWeek)
            return false;

        final int sign = _n > 0 ? 1 : -1;
        final IDate<C> startSearch = _inclusive ? date0.addDays(-(_n-sign)*7) : date0.addDays(-(_n-sign)*7-sign);
        final IDate<C> endSearch = _inclusive ? date0.addDays(-_n*7) : date0.addDays(-_n*7-sign);

        for (IDate<C> d = startSearch; !d.equals(endSearch); d = d.addDays(-sign)) {
            if (_pivot.matches(d))
                return true;
        }
        return false;
    }
}
