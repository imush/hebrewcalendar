package org.hebrewcalendar;

import org.hebrewcalendar.impl.GregorianCalendar;
import org.hebrewcalendar.impl.HebrewCalendar;
import org.hebrewcalendar.impl.JulianCalendar;

/**
 * Created by itz on 7/20/17.
 */
public interface HCalendar
{
    public HCalendar GREGORIAN = GregorianCalendar.INSTANCE;
    public HCalendar JULIAN = JulianCalendar.INSTANCE;
    public HCalendar HEBREW = HebrewCalendar.INSTANCE;

    public boolean isLeap(int year);

    /**
     * Length of the given month in the given year
     * @param year
     * @param month
     * @return
     */
    public int monthLength(int year, int month);

    public HDate fromYMD(int year, int month, int day);

    public HCalendarType getType();

    public int monthsInYear(int year);
}
