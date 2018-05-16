package net.hebrewcalendar.impl.holiday;

import net.hebrewcalendar.HCalendar;
import net.hebrewcalendar.HDate;

/**
 * Represents a holiday that occurs yearly on the same date.
 * It is initialized with month and day.
 */
public class MonthDayHoliday
    extends AbstractHoliday
{
    private final int _month;
    private final int _day;

    /**
     * @param month month. This can be negative to indicate the count
     *              of months from <i>end of year</i>, e.g. month -1 is the last month of the year.
     *              This can be useful for Hebrew year, which has variable number of months,
     *              e.g. Purim is always in the last month before Nisan, which can be either ADAR or ADAR_2.
     *              0 can be used to indicate ANY month, e.g. month=0, day=1 will match first day of every month.
     * @param day day of month. This can be negative to indicate the count
     *              of days from <i>end of month</i>, e.g. -1 will indicate the LAST day of month. day=0 can be used
     *            to match ANY day of the month, e.g. month=4, day = 0 will match the entire April in Gregorian calendar.
     */
    public MonthDayHoliday(HCalendar calendar, String name, int month, int day)
    {
        super(calendar, name);
        _month = month;
        _day = day;
    }

    public final int getDayOfMonth()
    {
        return _day;
    }

    public final int getMonth()
    {
        return _month;
    }

    @Override
    public boolean matches(HDate date)
    {
        final HCalendar cal = getCalendar();
        final HDate toMatch = cal.convert(date);
        final boolean monthMatch = _month == 0 ||
                (_month > 0 && _month == toMatch.getMonth()) ||
                (_month < 0 && toMatch.getMonth() == cal.monthsInYear(toMatch.getYear()) + 1 + _month);
        return monthMatch &&
                (_day == 0 ||
                (_day > 0 && _day == toMatch.getDay()) ||
                (_day < 0 && toMatch.getDay() == cal.monthLength(toMatch.getYear(), toMatch.getMonth()) + 1 + _day));
    }

    public String toString()
    {
        return "MonthDayHoliday[cal=" + getCalendar().getType() + ", name= " + getName() + ", m=" + _month + ", d=" + _day +"]";
    }
}
