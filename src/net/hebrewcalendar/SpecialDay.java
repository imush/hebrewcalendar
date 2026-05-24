package net.hebrewcalendar;

import net.hebrewcalendar.impl.NoSuchHolidayException;

/**
 * A recurring Jewish holiday or special day, capable of finding its occurrences on a calendar.
 */
public interface SpecialDay
{
    String getName();

    /**
     * @param date reference date to search from
     * @param strict when true, will search strictly after date; when false, the
     *               arg date may also be returned.
     * @return next {@link IDate} on or after date with the same calendar on which this holiday occurs.
     * @throws NoSuchHolidayException if not found within 3 years.
     */
    IDate getNextOccurrence(IDate date, boolean strict)
            throws NoSuchHolidayException;


    /**
     * @param date reference date to search from
     * @param strict when true, will search strictly before date; when false, the
     *               arg date may also be returned.
     * @return previous {@link IDate} on or before date with the same calendar on which this holiday occurs.
     * @throws NoSuchHolidayException if not found within 3 years.
     */
    IDate getPrevOccurrence(IDate date, boolean strict)
            throws NoSuchHolidayException;


    /**
     * @return the calendar for which this holiday was created
     */
    ICalendar getCalendar();

    /**
     *
     * @param date date to compare
     * @return true if and only if this holiday occurs on the given date
     */
    boolean matches(IDate date);
}
