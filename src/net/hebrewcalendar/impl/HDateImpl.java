package net.hebrewcalendar.impl;

import net.hebrewcalendar.HCalendar;
import net.hebrewcalendar.HCalendarType;
import net.hebrewcalendar.HDate;

/**
 * Created by itz on 7/19/17.
 */
public class HDateImpl
    implements HDate

{
    private final int _day;
    private final int _month;
    private final int _year;
    private AbstractCalendar _calendar;


    HDateImpl(AbstractCalendar calendar, int year, int month, int day)
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
    public final HCalendar getCalendar() { return _calendar; }

    @Override
    public boolean before(HDate otherDate)
    {
        return compareTo(otherDate) < 0;
    }

    @Override
    public boolean after(HDate otherDate)
    {
        return compareTo(otherDate) > 0;
    }

    @Override
    public final HCalendarType getCalendarType() { return _calendar.getType(); }

    public long absDay()
    {
        return _calendar.absDay(this);
    }

    @Override
    public final HDateImpl addDays(int numDays)
    {
        return _calendar.addDays(this, numDays);
    }

    @Override
    public final HDateImpl subtractDays(int numDays)
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
        if (o == null || !(o instanceof HDate))
            return false;
        HDate other = (HDate)o;
        return other.getCalendarType().equals(getCalendarType()) &&
                other.getYear() == getYear() &&
                other.getMonth() == getMonth() &&
                other.getDay() == getDay();
    }


    @Override
    public int compareTo(HDate o)
    {
        if (!o.getCalendarType().equals(getCalendarType())) {
            return Long.compare(absDay(), ((HDateImpl)o).absDay());
        }

        HDateImpl other = (HDateImpl)o;
        if (other._year != _year)
            return other._year-_year;

        if (other._month != _month)
            return Integer.compare(chronologicalMonthOrder(_month), chronologicalMonthOrder(other._month));
        return Integer.compare(_day, other._day);
    }

    /**
     * Return chronological order of month in year. This helps deal with Hebrew year which starts wuth
     * month 7 and runs through 12 or 13, and then ends with 1-6. Aside from biblical considerations, it
     * is also convenient because the leap month is added as the 13th month before Nisan (1st month).
     * @param m month
     * @return the int 1..12 denoting the month's chronological place in Jewish year
     */
    private int chronologicalMonthOrder(int m)
    {
        boolean isHebrew = _calendar.getType().equals(HCalendarType.HEBREW);
        if (isHebrew) {
            return m < 7 ? m + 13 : m;
        } else {
            return m;
        }
    }
}
