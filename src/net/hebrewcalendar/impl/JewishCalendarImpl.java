package net.hebrewcalendar.impl;

import net.hebrewcalendar.CalendarType;
import net.hebrewcalendar.IDate;
import net.hebrewcalendar.JewishCalendar;
import net.hebrewcalendar.JewishMoment;
import net.hebrewcalendar.JewishSpecialDay;
import net.hebrewcalendar.Parsha;
import net.hebrewcalendar.JewishCalendar.Season;
import java.util.List;

import net.hebrewcalendar.JewishMonth;

/**
 * Implementation of the Hebrew (Jewish) calendar.
 * Singleton access via {@link #INSTANCE}; use through the {@link net.hebrewcalendar.ICalendar#JEWISH} constant.
 */
public class JewishCalendarImpl
    extends AbstractCalendar
    implements JewishCalendar
{
    public static final JewishCalendarImpl INSTANCE = new JewishCalendarImpl();

    private JewishCalendarImpl() {}

    private static final boolean[] LEAP_CYCLE = new boolean[] {
        true, false, false,
        true, false, false,
        true, false,
        true, false, false,
        true, false, false,
        true, false, false,
        true, false };

    private static final JewishTime.Moment   MOLAD_TOHU = new JewishTime.Moment(2, 5, 204);
    private static final JewishTime.Duration ONE_MONTH  = new JewishTime.Duration(29, 12, 793);

    @Override
    public boolean isLeap(int year)
    {
        return LEAP_CYCLE[year%19];
    }

    /**
     * @param year year
     * @param month month number (see {@link net.hebrewcalendar.JewishMonth} for Hebrew month numberings).
     * @return length of Jewish month, 29 or 30 days.
     */
    @Override
    public int monthLength(int year, int month)
    {
        switch(JewishMonth.get(month)) {
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
                return getYearType(year) == YearCheshvanKislevType.FULL ? 30 : 29;
            case KISLEV:
                return getYearType(year) == YearCheshvanKislevType.SHORT ? 29 : 30;
            default:
                throw new IllegalStateException("Bad month " + month);
        }
    }

    /**
     * Returns the molad (conjunction) time for the given year and month.
     * @param year year
     * @param month month
     * @return the {@link JewishTime.Moment} in absolute days, hours and parts from the epoch.
     */
    JewishTime.Moment molad(int year, int month)
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
        return MOLAD_TOHU.add(ONE_MONTH.times(preMonths));
    }

    long absDayRoshHashana(int year)
    {
        JewishTime.Moment moladTime = molad(year, 7);

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
    public CalendarType getType()
    {
        return CalendarType.HEBREW;
    }

    @Override
    public final int monthsInYear(int year)
    {
        return isLeap(year) ? 13 : 12;
    }

    @Override
    long absDay(IDate date)
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

    DateImpl fromAbs(long absDayFromBeginning)
    {
        final int monthsIn19 = 12*19+7;

        JewishTime.Duration cycle19 = ONE_MONTH.times(monthsIn19);

        int cyclesToSkip = (int)(absDayFromBeginning/(cycle19.getDay() + 1));

        DateImpl startDay = new DateImpl(this, cyclesToSkip*19 + 1, 7, 1);
        return startDay.addDays((int)(absDayFromBeginning - absDay(startDay)));
    }

    YearCheshvanKislevType getYearType(int year)
    {
        final long rosh0 = absDay(new DateImpl(this, year, 7, 1));
        final long rosh1 = absDay(new DateImpl(this, year+1, 7, 1));
        final int yearLength  = (int)(rosh1-rosh0);

        int excessLength = isLeap(year) ? yearLength - 383 : yearLength - 353;

        switch(excessLength) {
            case 0: return YearCheshvanKislevType.SHORT;
            case 1: return YearCheshvanKislevType.NORMAL;
            case 2: return YearCheshvanKislevType.FULL;
            default:
                throw new IllegalStateException("Invalid year length " + yearLength + " for year=" + year);
        }
    }

    @Override
    public int getSefira(IDate date)
    {
        final IDate d0 = convert(date);
        final int m0 = d0.getMonth();
        if (m0 > 3 || (m0 == 3 && d0.getDay() > 5) || (m0 == 1 && d0.getDay() < 16))
            return 0;
        final IDate pesach;
        try {
            pesach = JewishSpecialDay.FIRST_DAY_PESACH.getPrevOccurrence(d0, true);
        } catch(NoSuchHolidayException nshe) {
            throw new IllegalArgumentException("Cannot find pesach preceding " + date);
        }
        return (int)(absDay(d0) - absDay(pesach));
    }

    @Override
    public DateImpl firstDayOfYear(int year)
    {
        return fromYMD(year, 7 ,1);
    }

    @Override
    public JewishMoment moladOfMonth(int year, int month)
    {
        return molad(year, month);
    }

    @Override
    public IDate fromMoment(JewishMoment moment)
    {
        return fromAbs(moment.getDay());
    }

    @Override
    public JewishMoment getTekufatRavAda(int hebrewYear, Season season)
    {
        return TekufaCalc.getTekufaTime(TekufaCalc.RAV_ADA, hebrewYear, season.getIndex());
    }

    @Override
    public JewishMoment getTekufatShmuel(int hebrewYear, Season season)
    {
        return TekufaCalc.getTekufaTime(TekufaCalc.SHMUEL, hebrewYear, season.getIndex());
    }

    @Override
    public List<Parsha> getParsha(IDate date, boolean inIsrael)
    {
        return Parshiot.getParsha(date, inIsrael);
    }
}
