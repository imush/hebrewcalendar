package net.hebrewcalendar;

/**
 * An immutable date in a specific calendar system.
 * Obtain instances via {@link ICalendar#fromYMD} or conversion methods.
 */
public interface IDate
    extends Comparable<IDate>
{
    /**
     * @return the year
     */
    int getYear();

    /**
     * @return the month. For Hebrew month numbering, see {@link net.hebrewcalendar.JewishMonth}.
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
    IDate addDays(int numDays);

    /**
     * Decrement by given number of days
     * @param numDays days to subtract
     * @return new date object
     */
    IDate subtractDays(int numDays);

    /**
     * @return the {@link CalendarType} for which this date was created.
     */
    CalendarType getCalendarType();

    /**
     * Sunday = 1, ..., Saturday = 7
     * @return day of week
     */
    int getDayOfWeek();

    /**
     * @return the {@link ICalendar} for which this date was created.
     */
    ICalendar getCalendar();

    /**
     * Compare dates and return true if this {@link IDate} is strictly before the arg one
     * @return true when this date is strictly before otherDate
     */
    boolean before(IDate otherDate);

    /**
     * Compare dates and return true if this {@link IDate} is strictly after the arg one
     * @return true when this date is strictly after otherDate
     */
    boolean after(IDate otherDate);

}
