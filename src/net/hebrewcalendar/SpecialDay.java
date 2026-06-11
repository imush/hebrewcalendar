package net.hebrewcalendar;

import net.hebrewcalendar.impl.NoSuchHolidayException;

/**
 * A recurring holiday or special day tied to a specific calendar, capable of finding
 * its occurrences relative to any reference date.
 *
 * @param <C> the calendar type this special day is defined for
 */
public interface SpecialDay<C extends ICalendar<C>>
{
    String getName();

    /**
     * @param date reference date to search from
     * @param strict when true, will search strictly after date; when false, the
     *               arg date may also be returned.
     * @return next occurrence on or after date, in calendar {@code C}
     * @throws NoSuchHolidayException if not found within 3 years.
     */
    IDate<C> getNextOccurrence(IDate<C> date, boolean strict)
            throws NoSuchHolidayException;


    /**
     * @param date reference date to search from
     * @param strict when true, will search strictly before date; when false, the
     *               arg date may also be returned.
     * @return previous occurrence on or before date, in the same calendar as {@code date}
     * @throws NoSuchHolidayException if not found within 3 years.
     */
    IDate<C> getPrevOccurrence(IDate<C> date, boolean strict)
            throws NoSuchHolidayException;


    /**
     * @return the calendar for which this holiday was created
     */
    C getCalendar();

    /**
     * @param date date to compare
     * @return true if and only if this holiday occurs on the given date
     */
    boolean matches(IDate<C> date);
}
