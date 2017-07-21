package org.hebrewcalendar;

/**
 * Created by itz on 7/20/17.
 */
public interface HJewishCalendar
    extends HCalendar
{
    /**
     * Enum to represent the length of months Kislev and Cheshvan:
     * <br> SHORT - both months have length 29
     * <br> NORMAL - Cheshvan is 29 days long and Kislev 30 days long
     * <br> FULL - both months have length 30
     */
    public enum YearType {
        SHORT, NORMAL, FULL;
    }

    public YearType getYearType(int year);
}
