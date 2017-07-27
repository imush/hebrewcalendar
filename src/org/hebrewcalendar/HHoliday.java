package org.hebrewcalendar;

public interface HHoliday
{
    String getName();

    /**
     * @param date reference date to search from
     * @return next {@link HDate} on or after date with the same calendar on which this
     * holiday occurs.
     */
    HDate getNextOccurrenceOnOrAfter(HDate date);

    /**
     * @return the calendar for which this holiday was created
     */
    HCalendar getCalendar();

    /**
     *
     * @param date
     * @return true if and only if this holiday occurs on the given date
     */
    boolean matches(HDate date);
}
