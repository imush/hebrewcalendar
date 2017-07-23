package org.hebrewcalendar.impl;

import org.hebrewcalendar.HCalendar;
import org.hebrewcalendar.HCalendarType;
import org.hebrewcalendar.HDate;

/**
 * Created by itz on 7/20/17.
 */
public class HebrewCalendar
    extends AbstractCalendar
{
    public static final HebrewCalendar INSTANCE = new HebrewCalendar();

    private HebrewCalendar() {}

    private static final boolean[] LEAP_CYCLE = new boolean[] {
        true, false, false,
        true, false, false,
        true, false,
        true, false, false,
        true, false, false,
        true, false, false,
        true, false };

    private static final HTime FIRST_MOLAD = new HTime(2, 5, 204);
    private static final HTime ONE_MONTH = new HTime(29, 12, 793);

    @Override
    public boolean isLeap(int year)
    {
        return LEAP_CYCLE[year%19];
    }

    @Override
    public int monthLength(int year, int month)
    {
        return 29;
    }

    /**
     * Returns absolute {@link HTime} for given molad
     * @param year
     * @param month
     * @return
     */
    HTime molad(int year, int month)
    {
        final int cycles = year/19;
        // 0th year did not exist; it would be leap
        int preMonths = 235*cycles-13;

        for (int i = 0; i < year % 19; i++)
        {
            preMonths += isLeap(i) ? 13 : 12;
        }

        final int numMonthsPastThisYear = month > 6 ? month-7 : month - 1 + (isLeap(year) ? 7 : 6);
        preMonths += numMonthsPastThisYear;
        HTime moladTime = FIRST_MOLAD.add(ONE_MONTH.times(preMonths-13));

        //System.out.println("molad for " + year + " " + month + " " + moladTime +
        // " c=" + cycles + " thisYearPastMonths=" + numMonthsPastThisYear + " premonths=" + preMonths);
        return moladTime;
    }

    @Override
    public HCalendarType getType()
    {
        return HCalendarType.HEBREW;
    }

    @Override
    public final int monthsInYear(int year)
    {
        return isLeap(year) ? 13 : 12;
    }

    @Override
    long absDay(HDate date)
    {
        return 0;
    }

    @Override
    final int[] nextYearMonth(int year, int month)
    {
        if (month == 6)
            return new int[] {year+1, 7};
        if (month < monthsInYear(year))
            return new int[] {year, month+1};
        else
            return new int[] {year, 1};

    }

    @Override
    final int[] prevYearMonth(int year, int month)
    {
        if (month == 7)
            return new int[] {year-1, 6};
        if (month > 1)
            return new int[] {year, month -1};
        return new int[] {year, monthsInYear(year)};
    }

}
