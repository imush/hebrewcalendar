package org.hebrewcalendar.impl.holiday;

import org.hebrewcalendar.HCalendar;
import org.hebrewcalendar.HDate;
import org.hebrewcalendar.HHoliday;

/**
 * Represents a holiday that occurs yearly on the same date.
 * It is initialized with month and day.
 */
public class NthDayOfWeekFromPivot
    extends AbstractHoliday
{
    private final HHoliday _pivot;
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
     *          e.g. Thanksgiving in US is last Thursday in November, so would use constructor args (11, 5, -1)
     * @param inclusive whether pivot date counts if it matches the day of week
     */
    public NthDayOfWeekFromPivot(HCalendar calendar, String name, HHoliday pivotDate, int dayOfWeek, int n, boolean inclusive)
    {
        super(calendar, name);
        _pivot = pivotDate;
        _dayOfWeek = dayOfWeek;
        _n = n;
        _inclusive = inclusive;
        if (_n == 0 || Math.abs(n) > 5)
            throw new IllegalArgumentException("arg n must be nonzero and no larger than 5 by abs value");
    }

    @Override
    public HDate getNextOccurrenceOnOrAfter(HDate date)
    {
        HDate date0 = getCalendar().convert(date);
        int y0 = date0.getYear();
        for (HDate candidate = date0; candidate.getYear() < y0 + 5; candidate = nextCandidate(candidate)) {
            if (candidate == null)
                return null;
            if (matches(candidate))
                return candidate;
        }
        return null;
    }

    private HDate nextCandidate(HDate date0)
    {
        if (date0.getDayOfWeek() == _dayOfWeek)
            return date0.addDays(7);
        else
            return date0.addDays((_dayOfWeek - date0.getDayOfWeek() + 7)%7);
    }

    @Override
    public boolean matches(HDate date0)
    {
        if (date0.getDayOfWeek() != _dayOfWeek)
            return false;

        int sign = _n > 0 ? 1 : -1;
        HDate startSearch = _inclusive ? date0.addDays(-(_n-sign)*7) : date0.addDays(-(_n-sign)*7-sign);
        HDate endSearch = _inclusive ? date0.addDays(-_n*7) : date0.addDays(-_n*7-sign);

        for (HDate d = startSearch; !d.equals(endSearch); d = d.addDays(-sign)) {
            if (_pivot.matches(d))
                return true;
        }
        return false;
    }
}
