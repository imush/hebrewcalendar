package org.hebrewcalendar.impl;

import org.hebrewcalendar.HCalendarType;
import org.hebrewcalendar.HDate;
import org.hebrewcalendar.HJewishCalendar;

/**
 * Created by itz on 7/20/17.
 */
public class HebrewCalendar
    extends AbstractCalendar
    implements HJewishCalendar
{
    public static final int NISAN = 1;
    public static final int IYAR = 2;
    public static final int SIVAN = 3;
    public static final int TAMUZ = 4;
    public static final int AV = 5;
    public static final int ELUL = 6;
    public static final int TISHREI = 7;
    public static final int CHESHVAN = 8;
    public static final int KISLEV = 9;
    public static final int TEVETH = 10;
    public static final int SHVAT = 11;
    public static final int ADAR = 12;
    public static final int ADAR_2 = 13;

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

    /**
     * @param year year
     * @param month month number (see {@link HebrewCalendar} for Hebrew month numberings).
     * @return length of Jewish month, 29 or 30 days.
     */
    @Override
    public int monthLength(int year, int month)
    {
        switch(month) {
            case NISAN:
            case SIVAN:
            case AV:
            case TISHREI:
            case SHVAT:
                return 30;

            case TEVETH:
            case IYAR:
            case TAMUZ:
            case ELUL:
            case ADAR_2:
                return 29;

            case ADAR:
                return isLeap(year) ? 30 : 29;
            case CHESHVAN:
                return getYearType(year) == YearType.FULL ? 30 : 29;
            case KISLEV:
                return getYearType(year) == YearType.SHORT ? 29 : 30;
            default:
                throw new IllegalStateException("Bad month " + month);
        }
    }

    /**
     * Returns absolute {@link HTime} for given molad
     * @param year year
     * @param month month
     * @return the {@link HTime} in abs days, hoursa nd parts from "beginning".
     */
    HTime molad(int year, int month)
    {
        final int cycles = (year-1)/19;
        // 0th year did not exist; it would be leap
        int preMonths = 235*cycles;

        for (int i = 1; i <= (year-1)%19; i++)
        {
            preMonths += isLeap(i) ? 13 : 12;
        }

        final int numMonthsPastThisYear = month > 6 ? month-7 : month - 1 + (isLeap(year) ? 7 : 6);
        preMonths += numMonthsPastThisYear;
        return FIRST_MOLAD.add(ONE_MONTH.times(preMonths));
    }

    long absDayRoshHashana(int year)
    {
        HTime moladTime = molad(year, 7);

        long candidate = moladTime.getDay();

        // dehiyot
        int dw = (int)((candidate - 1)%7)+1;
        if ( /* molad zoken */
            moladTime.getHour() >= 18 ||
            /* gatra"d b'shanah pdhutah b'rosh */
            (!isLeap(year) && dw == 3 &&
             ( moladTime.getHour() > 9 || (moladTime.getHour() == 9 &&
                     moladTime.getPart() >= 204))) ||
            /*Dehiyyah BeTU'TeKaPoT*/
            (isLeap(year-1)) && dw == 2 &&
                    ( moladTime.getHour() > 15  || (moladTime.getHour() == 15 && moladTime.getPart() >= 589))
         ) {
            // increment day of week
            dw = (dw == 7 ? 1 : dw+1);
            candidate++;
        }

        /* lo ad"u */
        if (dw == 1 || dw == 4 || dw == 6) {
            candidate++;
        }

        return candidate;
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
        // search for abs day last RoshHashana
        final int year = date.getYear();
        final int month = date.getMonth();
        long toReturn = absDayRoshHashana(year)-1;
        int m = 7;
        while (m != month) {
            toReturn += monthLength(date.getYear(), m);
            int[] nextYM = nextYearMonth(year, m);
            m = nextYM[1];
            if (nextYM[0] != year)
                throw new IllegalStateException(
                    "ran through whol year without finding month " + month);
        }
        toReturn += date.getDay();
        return toReturn;
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

    final long getStart()
    {
        return 0;
    }

    HDateImpl fromAbs(long absDayFromBeginning)
    {
        final int monthsIn19 = 12*19+7;

        HTime cycle19 = ONE_MONTH.times(monthsIn19);

        int cyclesToSkip = (int)(absDayFromBeginning/(cycle19.getDay() + 1));

        HDateImpl startDay = new HDateImpl(this, cyclesToSkip*19 + 1, 7, 1);
        return startDay.addDays((int)(absDayFromBeginning - absDay(startDay)));
    }

    @Override
    public YearType getYearType(int year)
    {
        final long rosh0 = absDay(new HDateImpl(this, year, 7, 1));
        final long rosh1 = absDay(new HDateImpl(this, year+1, 7, 1));
        final int yearLength  = (int)(rosh1-rosh0);

        int excessLength = isLeap(year) ? yearLength - 383 : yearLength - 353;

        switch(excessLength) {
            case 0: return YearType.SHORT;
            case 1: return YearType.NORMAL;
            case 2: return YearType.FULL;
            default:
                throw new IllegalStateException("Invalid year length " + yearLength + " for year=" + year);
        }
    }
}
