package org.hebrewcalendar.impl;

import org.hebrewcalendar.HCalendar;
import org.hebrewcalendar.HCalendarType;
import org.hebrewcalendar.HDate;

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
    public final HCalendarType getCalendarType() { return _calendar.getType(); }

    public long getAbsDay()
    {
        return _calendar.absDay(this);
    }

    public final HDate addDays(int numDays)
    {
        return _calendar.addDays(this, numDays);
    }

    public final HDate subtractDays(int numDays)
    {
        return _calendar.subtractDays(this, numDays);
    }

    @Override
    public final boolean isValid()
    {
        if (_year < 1 || _month < 1 || _month > _calendar.monthsInYear(_year))
            return false;
        return (_day >= 1 && _day <= _calendar.monthLength(_year, _month));
    }

    /**
     * 1 - Sunday .. 7 - Saturday
     * @return day of week 1-7
     */
    public final int getDayOfWeek()
    {
        return (int)(getAbsDay()+6)%7 + 1;
    }

    @Override
    public String toString()
    {
        return new StringBuffer()
                .append(getYear())
                .append('-')
                .append(getMonth())
                .append('-')
                .append(getDay())
                .toString();
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
}