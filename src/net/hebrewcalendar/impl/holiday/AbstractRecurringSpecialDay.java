package net.hebrewcalendar.impl.holiday;

import net.hebrewcalendar.ICalendar;
import net.hebrewcalendar.IDate;
import net.hebrewcalendar.SpecialDay;
import net.hebrewcalendar.impl.NoSuchHolidayException;

public abstract class AbstractRecurringSpecialDay<C extends ICalendar<C>>
    implements SpecialDay<C>
{
    private final C _calendar;
    private final String _name;

    AbstractRecurringSpecialDay(C calendar, String name)
    {
        _calendar = calendar;
        _name = name;
    }

    @Override
    public final String getName()
    {
        return _name;
    }

    @Override
    public final C getCalendar()
    {
        return _calendar;
    }

    /**
     * Search for next occurrence
     * @param date date to start search from
     * @param strict when true, an exact match does not qualify, so the date
     *               returned is always strictly later than date.
     * @return next occurrence in this holiday's calendar
     */
    @Override
    public IDate<C> getNextOccurrence(IDate<C> date, boolean strict)
        throws NoSuchHolidayException
    {
        IDate<C> d = strict ? date.addDays(1) : date;

        for (int c = 0; c < 3*380; c++) {
            if (matches(d))
                return d;
            d = d.addDays(1);
        }
        throw new NoSuchHolidayException(this, date, "getNextOccurrence failed");
    }

    /**
     * Search for previous occurrence
     * @param date date to start search from
     * @param strict when true, an exact match does not qualify, so the date
     *               returned is always strictly earlier than param date.
     * @return previous occurrence in the same calendar as {@code date}
     */
    @Override
    public IDate<C> getPrevOccurrence(IDate<C> date, boolean strict)
            throws NoSuchHolidayException
    {
        IDate<C> d = strict ? date.addDays(-1) : date;

        for (int c = 0; c < 3*380; c++) {
            if (matches(d))
                return d;
            d = d.addDays(-1);
        }
        throw new NoSuchHolidayException(this, date, "getPrevOccurrence failed");
    }

}
