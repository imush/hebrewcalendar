package org.hebrewcalendar;

import org.hebrewcalendar.impl.GregorianCalendar;
import org.hebrewcalendar.impl.HebrewCalendar;
import org.hebrewcalendar.impl.JulianCalendar;

/**
 * Created by itz on 7/20/17.
 */
public interface HCalendar
{
    HCalendar GREGORIAN = GregorianCalendar.INSTANCE;
    HCalendar JULIAN = JulianCalendar.INSTANCE;
    HCalendar HEBREW = HebrewCalendar.INSTANCE;

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
     * @param month month number (see {@link HebrewCalendar} for Hebrew month numberings).
     * @return the length of month in days
     */
    int monthLength(int year, int month);

    /**
     * Create a date obj
     * @param year year
     * @param month month
     * @param day day of month
     * @return an {@link HDate} object
     */
    HDate fromYMD(int year, int month, int day);

    /**
     * @return calendar type
     */
    HCalendarType getType();

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
     * @return a new {@link HDate} object
     */
    HDate addDays(HDate date, int days);

    /**
     * Decrement date by given number of days.
     * @param date date
     * @param days number of days to subtract
     * @return a new {@link HDate} object
     */
    HDate subtractDays(HDate date, int days);

    /**
     * Convert a date from a different calendar
     * @param otherDate date to convert from
     * @return the {@link HDate} representing the same day in this calendar.
     */
    HDate convert(HDate otherDate);
}
