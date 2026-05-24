package net.hebrewcalendar;

import net.hebrewcalendar.impl.JewishCalendarImpl;
import net.hebrewcalendar.impl.JulianCalendar;
import net.hebrewcalendar.impl.GregorianCalendar;

/**
 * A calendar system capable of creating and manipulating {@link IDate} objects.
 * Three instances are provided as constants: {@link #GREGORIAN}, {@link #JULIAN}, and {@link #JEWISH}.
 */
public interface ICalendar
{
    ICalendar GREGORIAN = GregorianCalendar.INSTANCE;
    ICalendar JULIAN = JulianCalendar.INSTANCE;
    JewishCalendar JEWISH = JewishCalendarImpl.INSTANCE;

    /**
     * Tests whether a given year is leap. For common calendars (Julian or Gregorian),
     * leapness means addition of one day (Feb 29). For Hebrew, it means addition of
     * a month.
     * @param year year
     * @return whether the year is "leap"
     */
    boolean isLeap(int year);

    /**
     * Length of the given month in the given year
     * @param year year
     * @param month month number (see {@link JewishMonth} for Hebrew month numberings).
     * @return the length of month in days
     */
    int monthLength(int year, int month);

    /**
     * Create a date obj
     * @param year year
     * @param month month
     * @param day day of month
     * @return an {@link IDate} object
     */
    IDate fromYMD(int year, int month, int day);

    /**
     * @return calendar type
     */
    CalendarType getType();

    /**
     * Calculate the number of months in year.
     * @param year year
     * @return always 12 for common (Gregorian or Julian) calendar, 13 for Hebrew leap year.
     */
    int monthsInYear(int year);

    /**
     * Increment date by given number of days.
     * @param date date
     * @param days number of days to add
     * @return a new {@link IDate} object
     */
    IDate addDays(IDate date, int days);

    /**
     * Decrement date by given number of days.
     * @param date date
     * @param days number of days to subtract
     * @return a new {@link IDate} object
     */
    IDate subtractDays(IDate date, int days);

    /**
     * Convert a date from a different calendar
     * @param otherDate date to convert from
     * @return the {@link IDate} representing the same day in this calendar.
     */
    IDate convert(IDate otherDate);

    /**
     * @param year year
     * @return first day of the year
     */
    IDate firstDayOfYear(int year);

    /**
     * Check if given year, month and day represent a valid date in this calendar.
     * @param year year
     * @param month month may be negative to represent count from tail, e.g. -1 is the last month of year
     * @param day day may be negative to represent count from end of month, e.g. -1 is the last day of month.
     * @return whether the given args are valid to construct an {@link IDate}.
     */
    boolean isValidDate(int year, int month, int day);
}
