package org.hebrewcalendar.impl.holiday;

import org.hebrewcalendar.HCalendar;
import org.hebrewcalendar.HDate;
import org.hebrewcalendar.HHoliday;
import org.hebrewcalendar.impl.NoSuchHolidayException;

public abstract class AbstractHoliday
    implements HHoliday
{
    private final HCalendar _calendar;
    private final String _name;

    AbstractHoliday(HCalendar calendar, String name)
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
    public final HCalendar getCalendar()
    {
        return _calendar;
    }

    /**
     * Search for next occurrence
     * @param date date to start search from
     * @param strict when true, an exact match does not qualify, si the date
     *               returned is always strictly later than date.
     * @return next occurrence
     */
    @Override
    public HDate getNextOccurrence(HDate date, boolean strict)
        throws NoSuchHolidayException
    {
        HDate d = strict ? date.addDays(1) : date;

        for (int c =0; c < 3*380; c++) {
            if (matches(d))
                return d;
            d = d.addDays(1);
        }
        throw new NoSuchHolidayException(this, date, "getNextOccurrence failed");
    }

    /**
     * Search for next occurrence
     * @param date date to start search from
     * @param strict when true, an exact match does not qualify, si the date
     *               returned is always strictly earlier than param date.
     * @return previous occurrence
     */
    @Override
    public HDate getPrevOccurrence(HDate date, boolean strict)
            throws NoSuchHolidayException
    {
        HDate d = strict ? date.addDays(-1) : date;

        for (int c =0; c < 3*380; c++) {
            if (matches(d))
                return d;
            d = d.addDays(-1);
        }
        throw new NoSuchHolidayException(this, date, "getPrevOccurrence failed");
    }

}
