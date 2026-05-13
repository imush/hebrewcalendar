package net.hebrewcalendar;

/**
 * Created by itz on 7/20/17.
 */
public interface HJewishCalendar
    extends HCalendar
{
    YearCheshvanKislevType getYearType(int year);

    /**
     * Get sefira count for the day; return 0 where not applicable
     * @param date day to test
     * @return sefira count (1-49), or 0 if it is not between Pesach and Shavuot
     */
    int getSefira(HDate date);
}
