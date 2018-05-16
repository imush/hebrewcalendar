package net.hebrewcalendar;

import net.hebrewcalendar.impl.NoSuchHolidayException;
import net.hebrewcalendar.impl.holiday.MonthDayHoliday;
import net.hebrewcalendar.impl.holiday.NthDayOfWeekInMonthHoliday;

public enum HUSHoliday implements HHoliday
{
    NEW_YEAR(new MonthDayHoliday(HCalendar.GREGORIAN, "New Year", 1, 1)),
    MLKJ_DAY(new NthDayOfWeekInMonthHoliday(HCalendar.GREGORIAN, "MLKJ Day", 1, 2, 3)),
    WASHINGTONS_BIRTHDAY(new NthDayOfWeekInMonthHoliday(HCalendar.GREGORIAN, "Washington's birthday", 2, 2, 3)),
    JULY_4(new MonthDayHoliday(HCalendar.GREGORIAN, "July 4", 7, 4)),
    MEMORIAL_DAY(new NthDayOfWeekInMonthHoliday(HCalendar.GREGORIAN, "Memorial Day", 5, 2, -1)),
    LABOR_DAY(new NthDayOfWeekInMonthHoliday(HCalendar.GREGORIAN, "Labor Day", 9, 2, 1)),
    THANKSGIVING(new NthDayOfWeekInMonthHoliday(HCalendar.GREGORIAN, "Thanksgiving", 11, 5, 4)),
    X_HOLIDAY(new MonthDayHoliday(HCalendar.GREGORIAN, "Dec 25", 12, 25));

    private HHoliday _delegate;

    HUSHoliday(HHoliday delegate) {
        _delegate = delegate;
    }

    @Override
    public String getName()
    {
        return _delegate.getName();
    }

    @Override
    public HDate getNextOccurrence(HDate date, boolean strict)
            throws NoSuchHolidayException
    {
        return _delegate.getNextOccurrence(date, strict);
    }

    @Override
    public HDate getPrevOccurrence(HDate date, boolean strict)
            throws NoSuchHolidayException
    {
        return _delegate.getPrevOccurrence(date, strict);
    }

    @Override
    public HCalendar getCalendar()
    {
        return HCalendar.GREGORIAN;
    }

    @Override
    public boolean matches(HDate date)
    {
        return _delegate.matches(date);
    }
}
