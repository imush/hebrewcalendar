package net.hebrewcalendar;

import net.hebrewcalendar.impl.GregorianCalendar;
import net.hebrewcalendar.impl.NoSuchHolidayException;
import net.hebrewcalendar.impl.holiday.MonthDaySpecialDay;
import net.hebrewcalendar.impl.holiday.NthDayOfWeekInMonthSpecialDay;

public enum USHoliday implements SpecialDay<GregorianCalendar>
{
    NEW_YEAR(new MonthDaySpecialDay<>(ICalendar.GREGORIAN, "New Year", 1, 1)),
    MLKJ_DAY(new NthDayOfWeekInMonthSpecialDay<>(ICalendar.GREGORIAN, "MLKJ Day", 1, 2, 3)),
    WASHINGTONS_BIRTHDAY(new NthDayOfWeekInMonthSpecialDay<>(ICalendar.GREGORIAN, "Washington's birthday", 2, 2, 3)),
    JULY_4(new MonthDaySpecialDay<>(ICalendar.GREGORIAN, "July 4", 7, 4)),
    MEMORIAL_DAY(new NthDayOfWeekInMonthSpecialDay<>(ICalendar.GREGORIAN, "Memorial Day", 5, 2, -1)),
    LABOR_DAY(new NthDayOfWeekInMonthSpecialDay<>(ICalendar.GREGORIAN, "Labor Day", 9, 2, 1)),
    THANKSGIVING(new NthDayOfWeekInMonthSpecialDay<>(ICalendar.GREGORIAN, "Thanksgiving", 11, 5, 4)),
    X_HOLIDAY(new MonthDaySpecialDay<>(ICalendar.GREGORIAN, "Dec 25", 12, 25));

    private final SpecialDay<GregorianCalendar> _delegate;

    USHoliday(final SpecialDay<GregorianCalendar> delegate) {
        _delegate = delegate;
    }

    @Override
    public final String getName()
    {
        return _delegate.getName();
    }

    @Override
    public final IDate<GregorianCalendar> getNextOccurrence(final IDate<GregorianCalendar> date, final boolean strict)
            throws NoSuchHolidayException
    {
        return _delegate.getNextOccurrence(date, strict);
    }

    @Override
    public final IDate<GregorianCalendar> getPrevOccurrence(final IDate<GregorianCalendar> date, final boolean strict)
            throws NoSuchHolidayException
    {
        return _delegate.getPrevOccurrence(date, strict);
    }

    @Override
    public final GregorianCalendar getCalendar()
    {
        return ICalendar.GREGORIAN;
    }

    @Override
    public final boolean matches(final IDate<GregorianCalendar> date)
    {
        return _delegate.matches(date);
    }
}
