package net.hebrewcalendar;

/**
 * An immutable date in a specific calendar system.
 * Obtain instances via {@link HCalendar#fromYMD} or conversion methods.
 */
public interface HDate
    extends Comparable<HDate>
{
    /**
     * @return the year
     */
    int getYear();

    /**
     * @return the month. For Hebrew month numbering, see {@link net.hebrewcalendar.HebrewMonth}.
     */
    int getMonth();

    /**
     * @return day of month
     */
    int getDay();

    /**
     * Increment by given number of days
     * @param numDays days to add
     * @return new date object
     */
    HDate addDays(int numDays);

    /**
     * Decrement by given number of days
     * @param numDays days to subtract
     * @return new date object
     */
    HDate subtractDays(int numDays);

    /**
     * @return the {@link HCalendarType} for which this date was created.
     */
    HCalendarType getCalendarType();

    /**
     * Sunday = 1, ..., Saturday = 7
     * @return day of week
     */
    int getDayOfWeek();

    /**
     * @return the {@link HCalendar} for which this date was created.
     */
    HCalendar getCalendar();

    /**
     * Compare dates and return true if this {@link HDate} is strictly before the arg one
     * @return true when this date is strictly before otherDate
     */
    boolean before(HDate otherDate);

    /**
     * Compare dates and return true if this {@link HDate} is strictly after the arg one
     * @return true when this date is strictly after otherDate
     */
    boolean after(HDate otherDate);

}
