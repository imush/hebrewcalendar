package net.hebrewcalendar;

import net.hebrewcalendar.impl.NoSuchHolidayException;
import net.hebrewcalendar.impl.holiday.*;

import java.util.*;

public enum HJewishHoliday
        implements HHoliday
{
    NISAN_11(new MonthDayHoliday(HCalendar.HEBREW, "11 Nisan", 1, 11)),
    EREV_PESACH(new MonthDayHoliday(HCalendar.HEBREW, "Erev Pesach", 1, 14)),
    FIRST_DAY_PESACH(new MonthDayHoliday(HCalendar.HEBREW, "Pesach", 1, 15)),
    SECOND_DAY_PESACH_C(new MonthDayHoliday(HCalendar.HEBREW, "2nd day of Pesach", 1, 16)),
    SEVENTH_DAY_PESACH(new MonthDayHoliday(HCalendar.HEBREW, "7th day of Pesach", 1, 21)),
    LAST_DAY_PESACH_C(new MonthDayHoliday(HCalendar.HEBREW, "Last day of Pesach", 1, 22)),
    CHOL_HAMOED_PESACH_1I(new MonthDayHoliday(HCalendar.HEBREW, "Chol Hamoed Pesach", 1, 16)),
    CHOL_HAMOED_PESACH_2I(new MonthDayHoliday(HCalendar.HEBREW, "Chol Hamoed Pesach", 1, 17)),
    CHOL_HAMOED_PESACH_3I(new MonthDayHoliday(HCalendar.HEBREW, "Chol Hamoed Pesach", 1, 18)),
    CHOL_HAMOED_PESACH_4I(new MonthDayHoliday(HCalendar.HEBREW, "Chol Hamoed Pesach", 1, 19)),
    CHOL_HAMOED_PESACH_5I(new MonthDayHoliday(HCalendar.HEBREW, "Chol Hamoed Pesach", 1, 20)),
    CHOL_HAMOED_PESACH_1C(new MonthDayHoliday(HCalendar.HEBREW, "Chol Hamoed Pesach", 1, 17)),
    CHOL_HAMOED_PESACH_2C(new MonthDayHoliday(HCalendar.HEBREW, "Chol Hamoed Pesach", 1, 18)),
    CHOL_HAMOED_PESACH_3C(new MonthDayHoliday(HCalendar.HEBREW, "Chol Hamoed Pesach", 1, 19)),
    CHOL_HAMOED_PESACH_4C(new MonthDayHoliday(HCalendar.HEBREW, "Chol Hamoed Pesach", 1, 20)),
    SHABBOS_HAGODOL(new NthDayOfWeekFromPivot(HCalendar.HEBREW, "Shabbos Hagodol", FIRST_DAY_PESACH, 7, -1, false)),
    LAG_BAOMER(new MonthDayHoliday(HCalendar.HEBREW, "Lag Baomer", 2, 18)),
    SHAVUOS(new MonthDayHoliday(HCalendar.HEBREW, "Shavuos", 3, 6)),
    SHAVUOS_2C(new MonthDayHoliday(HCalendar.HEBREW, "2nd day of Shavuos", 3, 7)),
    TAMUZ_3(new MonthDayHoliday(HCalendar.HEBREW, "3 Tamuz", 4, 3)),
    TAMUZ_12(new MonthDayHoliday(HCalendar.HEBREW, "12 Tamuz", 4, 12)),
    TAMUZ_13(new MonthDayHoliday(HCalendar.HEBREW, "13 Tamuz", 4, 13)),
    FAST_TAMUZ_17(new UnionHoliday("Fast of 17 Tamuz", new HHoliday[]{
            // not Shabbos
            new ConjunctionHoliday("17 Tamuz no Shabbos", new HHoliday[]{new MonthDayHoliday(HCalendar.HEBREW, "17th day of Tamuz", 4, 17),
                    new NegationHoliday(new NthDayOfWeek(HCalendar.HEBREW, 7))}),
            // nidche
            new ConjunctionHoliday("18 Tamuz Sunday", new HHoliday[]{new MonthDayHoliday(HCalendar.HEBREW, "18th day of Tamuz", 4, 18),
                    new NthDayOfWeek(HCalendar.HEBREW, 1)})
    })),

    FAST_AV_9(new UnionHoliday("Fast of 9th Av", new HHoliday[]{
            // not Shabbos
            new ConjunctionHoliday("9 Av no Shabbos", new HHoliday[]{new MonthDayHoliday(HCalendar.HEBREW, "Fast of 9th day of Av", 5, 9),
                    new NegationHoliday(new NthDayOfWeek(HCalendar.HEBREW, 7))}),
            // nidche
            new ConjunctionHoliday("9 Av no Shabbos", new HHoliday[]{new MonthDayHoliday(HCalendar.HEBREW, "Fast of 9th day of Av", 5, 10),
                    new NthDayOfWeek(HCalendar.HEBREW, 1)})
    })),
    CHAI_ELUL(new MonthDayHoliday(HCalendar.HEBREW, "Chai Elul", 6, 18)),
    ROSH_HASHANA_1(new MonthDayHoliday(HCalendar.HEBREW, "First day Rosh Hashana", 7, 1)),
    ROSH_HASHANA_2(new MonthDayHoliday(HCalendar.HEBREW, "2nd day Rosh Hashana", 7, 2)),
    ROSH_CHODESH(new ConjunctionHoliday("Rosh Chodesh", new HHoliday[]{new UnionHoliday("Rosh Chodesh or Rosh Hashana", new HHoliday[]{new MonthDayHoliday(HCalendar.HEBREW, "Rosh Chodesh", 0, 1), new MonthDayHoliday(HCalendar.HEBREW, "Rosh Chodesh", 0, 30)}), new NegationHoliday("exclude Rosh Hashana", ROSH_HASHANA_1)})),
    TZOM_GEDALIA(new MonthDayHoliday(HCalendar.HEBREW, "Tzom Gedalia", 7, 3)),
    YOM_KIPPUR(new MonthDayHoliday(HCalendar.HEBREW, "", 7, 10)),
    FIRST_DAY_SUKKOS(new MonthDayHoliday(HCalendar.HEBREW, "1st day Sukkos", 7, 15)),
    SECOND_DAY_SUKKOS_C(new MonthDayHoliday(HCalendar.HEBREW, "2nd day Sukkos", 7, 16)),
    SHMINI_ATZERES_C(new MonthDayHoliday(HCalendar.HEBREW, "Shmini Atzeres", 7, 22)),
    SIMCHAS_TORAH_C(new MonthDayHoliday(HCalendar.HEBREW, "Simchas Torah", 7, 23)),
    CHOL_HAMOED_SUKKOS_1I(new MonthDayHoliday(HCalendar.HEBREW, "Chol Hamoed Sukkos", 7, 16)),
    CHOL_HAMOED_SUKKOS_2I(new MonthDayHoliday(HCalendar.HEBREW, "Chol Hamoed Sukkos", 7, 17)),
    CHOL_HAMOED_SUKKOS_3I(new MonthDayHoliday(HCalendar.HEBREW, "Chol Hamoed Sukkos", 7, 18)),
    CHOL_HAMOED_SUKKOS_4I(new MonthDayHoliday(HCalendar.HEBREW, "Chol Hamoed Sukkos", 7, 19)),
    CHOL_HAMOED_SUKKOS_5I(new MonthDayHoliday(HCalendar.HEBREW, "Chol Hamoed Sukkos", 7, 20)),
    HOSHANA_RABBA(new MonthDayHoliday(HCalendar.HEBREW, "Hoshana Rabba", 7, 21)),
    CHOL_HAMOED_SUKKOS_1C(new MonthDayHoliday(HCalendar.HEBREW, "Chol Hamoed Sukkos", 7, 17)),
    CHOL_HAMOED_SUKKOS_2C(new MonthDayHoliday(HCalendar.HEBREW, "Chol Hamoed Sukkos", 7, 18)),
    CHOL_HAMOED_SUKKOS_3C(new MonthDayHoliday(HCalendar.HEBREW, "Chol Hamoed Sukkos", 7, 19)),
    CHOL_HAMOED_SUKKOS_4C(new MonthDayHoliday(HCalendar.HEBREW, "Chol Hamoed Sukkos", 7, 20)),
    SIMCHAS_TORAH_I(new MonthDayHoliday(HCalendar.HEBREW, "Simchas Torah", 7, 22)),
    NINETEENTH_KISLEV(new MonthDayHoliday(HCalendar.HEBREW, "First day Chanukah", 9, 19)),
    FIRST_DAY_CHANUKAH(new MonthDayHoliday(HCalendar.HEBREW, "1st day Chanukah", 9, 25)),
    SECOND_DAY_CHANUKAH(new ShiftedDateHoliday("2nd day Chanukah", FIRST_DAY_CHANUKAH, 1)),
    THIRD_DAY_CHANUKAH(new ShiftedDateHoliday("3rd day Chanukah", FIRST_DAY_CHANUKAH, 2)),
    FOURTH_DAY_CHANUKAH(new ShiftedDateHoliday("4th day Chanukah", FIRST_DAY_CHANUKAH, 3)),
    FIFTH_DAY_CHANUKAH(new ShiftedDateHoliday("5th day Chanukah", FIRST_DAY_CHANUKAH, 4)),
    SIXTH_DAY_CHANUKAH(new ShiftedDateHoliday("6th day Chanukah", FIRST_DAY_CHANUKAH, 5)),
    SEVENTH_DAY_CHANUKAH(new ShiftedDateHoliday("7th day Chanukah", FIRST_DAY_CHANUKAH, 6)),
    EIGHTH_DAY_CHANUKAH(new ShiftedDateHoliday("8th day Chanukah", FIRST_DAY_CHANUKAH, 7)),
    TENTH_TEVES(new MonthDayHoliday(HCalendar.HEBREW, "10 Tevet", 10, 10)),
    YUD_SHVAT(new MonthDayHoliday(HCalendar.HEBREW, "10 Shvat", 11, 10)),
    TU_BESHVAT(new MonthDayHoliday(HCalendar.HEBREW, "Tu Bishvat", 11, 15)),
    PURIM(new MonthDayHoliday(HCalendar.HEBREW, "Purim", -1, 14)),
    SHUSHAN_PURIM(new MonthDayHoliday(HCalendar.HEBREW, "Shushan Purim", -1, 15)),
    PURIM_KATAN(new ConjunctionHoliday("Purim Katan", new HHoliday[] {
            new MonthDayHoliday(HCalendar.HEBREW, "12 Adar I", 12, 14),
            new NegationHoliday("exclude Purim", PURIM)}));

   
    private static Set<HJewishHoliday> createCollection(HJewishHoliday[] hh) {
        HashSet<HJewishHoliday> buildSet = new HashSet<>();
        buildSet.addAll(Arrays.asList(hh));
        return Collections.unmodifiableSet(buildSet);
    }

    public static final Set<HJewishHoliday> ISRAEL_SPECIFIC_DAYS = createCollection(
            new HJewishHoliday[]{
                    CHOL_HAMOED_PESACH_1I, CHOL_HAMOED_PESACH_2I, CHOL_HAMOED_PESACH_3I, CHOL_HAMOED_PESACH_4I,
                    CHOL_HAMOED_PESACH_5I, CHOL_HAMOED_SUKKOS_1I, CHOL_HAMOED_PESACH_2I, CHOL_HAMOED_SUKKOS_3C,
                    CHOL_HAMOED_SUKKOS_4I, CHOL_HAMOED_SUKKOS_5I, SIMCHAS_TORAH_I
            }
    );

    public static final Set<HJewishHoliday> CHUTZ_LAARETZ_SPECIFIC = createCollection(
            new HJewishHoliday[]{
                    SECOND_DAY_PESACH_C, LAST_DAY_PESACH_C,
                    CHOL_HAMOED_PESACH_1C, CHOL_HAMOED_PESACH_2C, CHOL_HAMOED_PESACH_3C, CHOL_HAMOED_PESACH_4C,
                    SHAVUOS_2C, SECOND_DAY_SUKKOS_C,
                    CHOL_HAMOED_SUKKOS_1C, CHOL_HAMOED_SUKKOS_2C, CHOL_HAMOED_SUKKOS_3C, CHOL_HAMOED_SUKKOS_4C,
                    SIMCHAS_TORAH_C
            }
    );

    public static final Set<HJewishHoliday> FAST_DAYS = createCollection(
            new HJewishHoliday[]{
                    TZOM_GEDALIA, TENTH_TEVES, FAST_TAMUZ_17, FAST_AV_9
            });

    public static final Set<HJewishHoliday> YOM_TOV_DAYS = createCollection(
            new HJewishHoliday[]{
                    FIRST_DAY_PESACH, SECOND_DAY_PESACH_C, SEVENTH_DAY_PESACH, LAST_DAY_PESACH_C,
                    SHAVUOS, SHAVUOS_2C, ROSH_HASHANA_1, ROSH_HASHANA_2, YOM_KIPPUR,
                    FIRST_DAY_SUKKOS, SECOND_DAY_SUKKOS_C, SIMCHAS_TORAH_I, SIMCHAS_TORAH_C, SHMINI_ATZERES_C
            });

    public static final Set<HJewishHoliday> CHABAD_DAYS = createCollection(
            new HJewishHoliday[]{
                    NINETEENTH_KISLEV, YUD_SHVAT, NISAN_11, TAMUZ_3, TAMUZ_12, TAMUZ_13, CHAI_ELUL
            });
    
    private final HHoliday _delegate;

    HJewishHoliday(HHoliday delegate) {
        _delegate = delegate;
    }


    @Override
    public String getName()
    {
        return _delegate.getName();
    }

    @Override
    public HDate getNextOccurrence(HDate date, boolean strict)
            throws NoSuchHolidayException
    {
        return _delegate.getNextOccurrence(date, strict);
    }

    @Override
    public HDate getPrevOccurrence(HDate date, boolean strict)
            throws NoSuchHolidayException
    {
        return _delegate.getPrevOccurrence(date, strict);
    }

    @Override
    public HCalendar getCalendar()
    {
        return HCalendar.HEBREW;
    }

    @Override
    public boolean matches(HDate date)
    {
        return _delegate.matches(date);
    }

    /**
     * Determine if the special day applies in given localtion
     * @param inIsrael true for Eretz Israel, false outside
     * @return whether the given holiday applies to local calendar.
     */
    public final boolean applies(boolean inIsrael) {
        return inIsrael ? !CHUTZ_LAARETZ_SPECIFIC.contains(this) : !ISRAEL_SPECIFIC_DAYS.contains(this);
    }
}
