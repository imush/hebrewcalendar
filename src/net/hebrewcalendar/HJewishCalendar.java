package net.hebrewcalendar;

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
    enum YearType {
        SHORT, NORMAL, FULL
    }

    YearType getYearType(int year);

    /**
     * Get sefira count for the day; return 0 where not applicable
     * @param date day to test
     * @return sefira count (1-49), or 0 if it is not between Pesach and Shavuot
     */
    int getSefira(HDate date);
}
