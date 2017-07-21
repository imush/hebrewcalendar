package org.hebrewcalendar;

import org.hebrewcalendar.impl.HebrewDate;

/**
 * Created by itz on 7/20/17.
 */
public interface HDate
{
    /**
     * @return the year
     */
    int getYear();

    /**
     * @return the month. For Hebrew month numbering, see {@link HebrewDate}.
     */
    int getMonth();

    /**
     * @return day of month
     */
    int getDay();

    /**
     * @return check that the date is valid
     */
    boolean isValid();

    /**
     * Increment or decrement by given number of days
     * @param numDays
     * @return
     */
    HDate addDays(int numDays);

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
}
