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
    extends AbstractCalendar<JewishCalendar>
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
    public final boolean isLeap(final int year)
    {
        return LEAP_CYCLE[year%19];
    }

    /**
     * @param year year
     * @param month month number (see {@link net.hebrewcalendar.JewishMonth} for Hebrew month numberings).
     * @return length of Jewish month, 29 or 30 days.
     */
    @Override
    public final int monthLength(final int year, final int month)
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
    JewishTime.Moment molad(final int year, final int month)
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

    long absDayRoshHashana(final int year)
    {
        final JewishTime.Moment moladTime = molad(year, 7);

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
    public final CalendarType getType()
    {
        return CalendarType.HEBREW;
    }

    @Override
    public final int monthsInYear(int year)
    {
        return isLeap(year) ? 13 : 12;
    }

    @Override
    final long absDay(final IDate<JewishCalendar> date)
    {
        // search for abs day last RoshHashana
        final int year = date.getYear();
        final int month = date.getMonth();
        long toReturn = absDayRoshHashana(year)-1;
        int m = 7;
        while (m != month) {
            toReturn += monthLength(date.getYear(), m);
            final int[] nextYM = nextYearMonth(year, m);
            m = nextYM[1];
            if (nextYM[0] != year)
                throw new IllegalStateException(
                    "ran through whole year without finding month " + month);
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

    @Override
    @SuppressWarnings("unchecked")
    final DateImpl<JewishCalendar> fromAbs(final long absDayFromBeginning)
    {
        final int monthsIn19 = 12*19+7;

        final JewishTime.Duration cycle19 = ONE_MONTH.times(monthsIn19);

        final int cyclesToSkip = (int)(absDayFromBeginning/(cycle19.getDay() + 1));

        final DateImpl<JewishCalendar> startDay = new DateImpl<>((JewishCalendar) this, cyclesToSkip*19 + 1, 7, 1);
        return startDay.addDays((int)(absDayFromBeginning - absDay(startDay)));
    }

    YearCheshvanKislevType getYearType(final int year)
    {
        final long rosh0 = absDay(new DateImpl<>((JewishCalendar) this, year, 7, 1));
        final long rosh1 = absDay(new DateImpl<>((JewishCalendar) this, year+1, 7, 1));
        final int yearLength  = (int)(rosh1-rosh0);

        final int excessLength = isLeap(year) ? yearLength - 383 : yearLength - 353;

        switch(excessLength) {
            case 0: return YearCheshvanKislevType.SHORT;
            case 1: return YearCheshvanKislevType.NORMAL;
            case 2: return YearCheshvanKislevType.FULL;
            default:
                throw new IllegalStateException("Invalid year length " + yearLength + " for year=" + year);
        }
    }

    @Override
    public final int getSefira(final IDate<JewishCalendar> date)
    {
        final int m0 = date.getMonth();
        if (m0 > 3 || (m0 == 3 && date.getDay() > 5) || (m0 == 1 && date.getDay() < 16))
            return 0;
        final IDate<JewishCalendar> pesach;
        try {
            pesach = JewishSpecialDay.FIRST_DAY_PESACH.getPrevOccurrence(date, true);
        } catch(NoSuchHolidayException nshe) {
            throw new IllegalArgumentException("Cannot find pesach preceding " + date);
        }
        return (int)(absDay(date) - absDay(pesach));
    }

    @Override
    public final DateImpl<JewishCalendar> firstDayOfYear(final int year)
    {
        return fromYMD(year, 7 ,1);
    }

    @Override
    public final JewishMoment moladOfMonth(final int year, final int month)
    {
        return molad(year, month);
    }

    @Override
    public final IDate<JewishCalendar> fromMoment(final JewishMoment moment)
    {
        return fromAbs(moment.getDay());
    }

    @Override
    public final JewishMoment getTekufatRavAda(final int hebrewYear, final Season season)
    {
        return TekufaCalc.getTekufaTime(TekufaCalc.RAV_ADA, hebrewYear, season.getIndex());
    }

    @Override
    public final JewishMoment getTekufatShmuel(final int hebrewYear, final Season season)
    {
        return TekufaCalc.getTekufaTime(TekufaCalc.SHMUEL, hebrewYear, season.getIndex());
    }

    @Override
    public final List<Parsha> getParsha(final IDate<JewishCalendar> date, final boolean inIsrael)
    {
        return Parshiot.getParsha(date, inIsrael);
    }

    @Override
    public final IDate<JewishCalendar> anniversaryFor(final IDate<JewishCalendar> originalDate, final int year)
    {
        int month = originalDate.getMonth();
        final int day   = originalDate.getDay();
        final boolean origIsLeap   = isLeap(originalDate.getYear());
        final boolean targetIsLeap = isLeap(year);

        if (month == 12 && !origIsLeap && targetIsLeap)
            month = 13;  // non-leap Adar → Adar II in leap year
        else if (month == 13 && !targetIsLeap)
            month = 12;  // Adar II → Adar in non-leap year

        try {
            return fromYMD(year, month, day);
        } catch (IllegalStateException e) {
            // Cheshvan 30 or Kislev 30 not present in this year → Rosh Chodesh of next month
            final int[] next = nextYearMonth(year, month);
            return fromYMD(next[0], next[1], 1);
        }
    }

    @Override
    public final IDate<JewishCalendar> yahrzeitFor(final IDate<JewishCalendar> deathDate, final int year)
    {
        int month = deathDate.getMonth();
        final int day   = deathDate.getDay();

        if (month == 13 && !isLeap(year))
            month = 12;  // Adar II → Adar in non-leap year

        // For 30 Cheshvan or 30 Kislev the behaviour depends on whether that day existed
        // in the year immediately following death (first yahrzeit year):
        // - It existed: observe on the 30th when the target year has it; otherwise 1st of next month
        //   (falls through to the standard try/catch below — same as anniversaryFor).
        // - It did not: always the last day of that month in the target year (29 or 30).
        if (day == 30 && (month == JewishMonth.CHESHVAN.getOrdinalNumber()
                       || month == JewishMonth.KISLEV.getOrdinalNumber())) {
            final int firstYear = deathDate.getYear() + 1;
            if (monthLength(firstYear, month) != 30) {
                return fromYMD(year, month, monthLength(year, month));
            }
            // first year had the 30th: fall through to standard try/catch
        }

        try {
            return fromYMD(year, month, day);
        } catch (IllegalStateException e) {
            final int[] next = nextYearMonth(year, month);
            return fromYMD(next[0], next[1], 1);
        }
    }
}
