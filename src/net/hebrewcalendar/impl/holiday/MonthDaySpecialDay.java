package net.hebrewcalendar.impl.holiday;

import net.hebrewcalendar.ICalendar;
import net.hebrewcalendar.IDate;

/**
 * Represents a holiday that occurs yearly on the same date.
 * It is initialized with month and day.
 */
public class MonthDaySpecialDay<C extends ICalendar<C>>
    extends AbstractRecurringSpecialDay<C>
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
    public MonthDaySpecialDay(final C calendar, final String name, final int month, final int day)
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
    public boolean matches(final IDate<C> date)
    {
        final ICalendar<C> cal = getCalendar();
        final boolean monthMatch = _month == 0 ||
                (_month > 0 && _month == date.getMonth()) ||
                (_month < 0 && date.getMonth() == cal.monthsInYear(date.getYear()) + 1 + _month);
        return monthMatch &&
                (_day == 0 ||
                (_day > 0 && _day == date.getDay()) ||
                (_day < 0 && date.getDay() == cal.monthLength(date.getYear(), date.getMonth()) + 1 + _day));
    }

    public String toString()
    {
        return "MonthDaySpecialDay[cal=" + getCalendar().getType() + ", name= " + getName() + ", m=" + _month + ", d=" + _day +"]";
    }
}
