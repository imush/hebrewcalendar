package net.hebrewcalendar.impl.holiday;

import net.hebrewcalendar.HCalendar;
import net.hebrewcalendar.HDate;

/**
 * Represents a holiday that occurs yearly on the same date.
 * It is initialized with month and day.
 */
public class NthDayOfWeekInMonthHoliday
    extends AbstractHoliday
{
    private final int _month;
    private int _n;
    private final int _dayOfWeek;

    /**
     * @param month month. This can be negative to indicate the count
     *              of months from <i>end of year</i>, e.g. month -1 is the last month of the year.
     *              This can be useful for Hebrew year, which has variable number of months,
     *              e.g. Purim is always in the last month before Nisan, which can be either ADAR or ADAR_2.
     *              0 may be used to indicate ANY month.
     * @param dayOfWeek day of week (1=Sunday,... 7= Saturday). This can be negative to indicate the count
     *              of days from <i>end of month</i>.
     * @param n number of occurrence. This can be negative to indicate the <i>count from end of month</i>,
     *          e.g. Thanksgiving in US is last Thursday in November, so would use constructor args (11, 5, -1)

     */
    public NthDayOfWeekInMonthHoliday(HCalendar calendar, String name, int month, int dayOfWeek, int n)
    {
        super(calendar, name);
        _month = month;
        _dayOfWeek = dayOfWeek;
        _n = n;
        if (_n == 0 || Math.abs(n) > 5)
            throw new IllegalArgumentException("arg n must be nonzero and no larger than 5 by abs value");
    }

    public final int getDayOfWeek()
    {
        return _dayOfWeek;
    }

    public final int getMonth()
    {
        return _month;
    }

    public final int getN() { return _n; }

    @Override
    public boolean matches(final HDate date)
    {
        HCalendar cal = getCalendar();
        HDate date0 = cal.convert(date);
        int m0 = date0.getMonth();
        int y0 = date0.getYear();
        int d0 = date0.getDay();
        boolean monthMatch = _month == 0 ||
                _month > 0 && _month == m0 ||
                _month < 0 && m0 == _month + 1 + cal.monthsInYear(y0);
        return monthMatch && d0 == getNthDayOfWeekInMonth(y0, m0);
    }

    private int getNthDayOfWeekInMonth(int year, int month)
    {
        HCalendar cal = getCalendar();
        HDate firstDayOfMonth = cal.fromYMD(year, month, _n > 0 ? 1 : -1);
        int d1 = firstDayOfMonth.getDay();
        int toAddForFirstCorrectDay = _dayOfWeek - firstDayOfMonth.getDayOfWeek();
        if (_n*toAddForFirstCorrectDay < 0)
            toAddForFirstCorrectDay += (_n > 0) ? 7 : -7;
        d1 += toAddForFirstCorrectDay;

        int numWeeksToAdjust = _n > 0 ? _n -1 : _n + 1;
        d1 += numWeeksToAdjust*7;
        return  cal.isValidDate(year, month, d1) ? d1 : 0;
    }
}
