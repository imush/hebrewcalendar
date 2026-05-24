package net.hebrewcalendar.impl;

import net.hebrewcalendar.ICalendar;
import net.hebrewcalendar.CalendarType;
import net.hebrewcalendar.IDate;

/**
 * Concrete implementation of {@link net.hebrewcalendar.IDate}.
 * Instances are created by {@link AbstractCalendar#fromYMD} and conversion methods; not intended for direct construction.
 */
public class IDateImpl
    implements IDate

{
    private final int _day;
    private final int _month;
    private final int _year;
    private AbstractCalendar _calendar;


    IDateImpl(AbstractCalendar calendar, int year, int month, int day)
    {
        _calendar = calendar;
        _year = year;
        _month = month;
        _day = day;
        if (!_calendar.isValidDate(year, month, day))
            throw new IllegalStateException("Invalid date created for calendar " + _calendar.getType() + ": " + this);
    }

    @Override
    public final int getYear() { return _year;}

    @Override
    public final int getMonth() { return _month;}

    @Override
    public final int getDay() { return _day; }

    @Override
    public final ICalendar getCalendar() { return _calendar; }

    @Override
    public boolean before(IDate otherDate)
    {
        return compareTo(otherDate) < 0;
    }

    @Override
    public boolean after(IDate otherDate)
    {
        return compareTo(otherDate) > 0;
    }

    @Override
    public final CalendarType getCalendarType() { return _calendar.getType(); }

    public long absDay()
    {
        return _calendar.absDay(this);
    }

    @Override
    public final IDateImpl addDays(int numDays)
    {
        return _calendar.addDays(this, numDays);
    }

    @Override
    public final IDateImpl subtractDays(int numDays)
    {
        return _calendar.subtractDays(this, numDays);
    }

    final boolean isValid()
    {
        return _year >= 1 && _month >= 1 && _month <= _calendar.monthsInYear(_year) &&
            _day >= 1 && _day <= _calendar.monthLength(_year, _month);
    }

    /**
     * 1 - Sunday .. 7 - Saturday
     * @return day of week 1-7
     */
    public final int getDayOfWeek()
    {
        return (int)(absDay()+6)%7 + 1;
    }

    @Override
    public String toString()
    {

        return String.format("%04d-%02d-%02d", getYear(), getMonth(), getDay()) + _calendar.getType().toString().substring(0,1);
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == null || !(o instanceof IDate))
            return false;
        IDate other = (IDate)o;
        return other.getCalendarType().equals(getCalendarType()) &&
                other.getYear() == getYear() &&
                other.getMonth() == getMonth() &&
                other.getDay() == getDay();
    }


    @Override
    public int compareTo(IDate o)
    {
        if (!o.getCalendarType().equals(getCalendarType())) {
            return Long.compare(absDay(), ((IDateImpl)o).absDay());
        }

        IDateImpl other = (IDateImpl)o;
        if (other._year != _year)
            return other._year-_year;

        if (other._month != _month)
            return Integer.compare(chronologicalMonthOrder(_month), chronologicalMonthOrder(other._month));
        return Integer.compare(_day, other._day);
    }

    /**
     * Return chronological order of month in year. This helps deal with Hebrew year which starts with
     * month 7 and runs through 12 or 13, and then ends with 1-6. Aside from biblical considerations, it
     * is also convenient because the leap month is added as the 13th month before Nisan (1st month).
     * @param m month
     * @return the int 1..12 denoting the month's chronological place in Jewish year
     */
    private int chronologicalMonthOrder(int m)
    {
        boolean isHebrew = _calendar.getType().equals(CalendarType.HEBREW);
        if (isHebrew) {
            return m < 7 ? m + 13 : m;
        } else {
            return m;
        }
    }
}
