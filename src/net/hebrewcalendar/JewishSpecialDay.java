package net.hebrewcalendar;

import net.hebrewcalendar.impl.NoSuchHolidayException;
import net.hebrewcalendar.impl.holiday.*;

import java.util.*;

public enum JewishSpecialDay
        implements SpecialDay
{
    NISAN_11(new MonthDaySpecialDay(ICalendar.JEWISH, "11 Nisan", 1, 11)),
    EREV_PESACH(new MonthDaySpecialDay(ICalendar.JEWISH, "Erev Pesach", 1, 14)),
    FIRST_DAY_PESACH(new MonthDaySpecialDay(ICalendar.JEWISH, "Pesach", 1, 15)),
    SECOND_DAY_PESACH_C(new MonthDaySpecialDay(ICalendar.JEWISH, "2nd day of Pesach", 1, 16)),
    SEVENTH_DAY_PESACH(new MonthDaySpecialDay(ICalendar.JEWISH, "7th day of Pesach", 1, 21)),
    LAST_DAY_PESACH_C(new MonthDaySpecialDay(ICalendar.JEWISH, "Last day of Pesach", 1, 22)),
    CHOL_HAMOED_PESACH_1I(new MonthDaySpecialDay(ICalendar.JEWISH, "Chol Hamoed Pesach", 1, 16)),
    CHOL_HAMOED_PESACH_2I(new MonthDaySpecialDay(ICalendar.JEWISH, "Chol Hamoed Pesach", 1, 17)),
    CHOL_HAMOED_PESACH_3I(new MonthDaySpecialDay(ICalendar.JEWISH, "Chol Hamoed Pesach", 1, 18)),
    CHOL_HAMOED_PESACH_4I(new MonthDaySpecialDay(ICalendar.JEWISH, "Chol Hamoed Pesach", 1, 19)),
    CHOL_HAMOED_PESACH_5I(new MonthDaySpecialDay(ICalendar.JEWISH, "Chol Hamoed Pesach", 1, 20)),
    CHOL_HAMOED_PESACH_1C(new MonthDaySpecialDay(ICalendar.JEWISH, "Chol Hamoed Pesach", 1, 17)),
    CHOL_HAMOED_PESACH_2C(new MonthDaySpecialDay(ICalendar.JEWISH, "Chol Hamoed Pesach", 1, 18)),
    CHOL_HAMOED_PESACH_3C(new MonthDaySpecialDay(ICalendar.JEWISH, "Chol Hamoed Pesach", 1, 19)),
    CHOL_HAMOED_PESACH_4C(new MonthDaySpecialDay(ICalendar.JEWISH, "Chol Hamoed Pesach", 1, 20)),
    SHABBAT_HAGADOL(new NthDayOfWeekFromPivot(ICalendar.JEWISH, "Shabbat Hagadol", FIRST_DAY_PESACH, 7, -1, false)),
    // Arba Parshiyot — pivot uses month=-1 (last month of year: Adar in regular year, Adar II in leap)
    SHABBAT_SHEKALIM(new NthDayOfWeekFromPivot(ICalendar.JEWISH, "Shabbat Shekalim",
            new MonthDaySpecialDay(ICalendar.JEWISH, "1 Adar", -1, 1), 7, -1, true)),
    SHABBAT_ZACHOR(new NthDayOfWeekFromPivot(ICalendar.JEWISH, "Shabbat Zachor",
            new MonthDaySpecialDay(ICalendar.JEWISH, "13 Adar", -1, 13), 7, -1, true)),
    SHABBAT_PARA(new NthDayOfWeekFromPivot(ICalendar.JEWISH, "Shabbat Para",
            new MonthDaySpecialDay(ICalendar.JEWISH, "1 Nisan", 1, 1), 7, -2, true)),
    SHABBAT_HACHODESH(new NthDayOfWeekFromPivot(ICalendar.JEWISH, "Shabbat Hachodesh",
            new MonthDaySpecialDay(ICalendar.JEWISH, "1 Nisan", 1, 1), 7, -1, true)),
    PESACH_SHENI(new MonthDaySpecialDay(ICalendar.JEWISH, "Pesach Sheni", 2, 14)),
    LAG_BAOMER(new MonthDaySpecialDay(ICalendar.JEWISH, "Lag Baomer", 2, 18)),
    SHAVUOT(new MonthDaySpecialDay(ICalendar.JEWISH, "Shavuot", 3, 6)),
    SHAVUOT_2C(new MonthDaySpecialDay(ICalendar.JEWISH, "2nd day of Shavuot", 3, 7)),
    TAMUZ_3(new MonthDaySpecialDay(ICalendar.JEWISH, "3 Tamuz", 4, 3)),
    TAMUZ_12(new MonthDaySpecialDay(ICalendar.JEWISH, "12 Tamuz", 4, 12)),
    TAMUZ_13(new MonthDaySpecialDay(ICalendar.JEWISH, "13 Tamuz", 4, 13)),
    FAST_TAMUZ_17(new UnionSpecialDay("Fast of 17 Tamuz", new SpecialDay[]{
            // not Shabbos
            new ConjunctionSpecialDay("17 Tamuz no Shabbos", new SpecialDay[]{new MonthDaySpecialDay(ICalendar.JEWISH, "17th day of Tamuz", 4, 17),
                    new NegationSpecialDay(new NthDayOfWeek(ICalendar.JEWISH, 7))}),
            // nidche
            new ConjunctionSpecialDay("18 Tamuz Sunday", new SpecialDay[]{new MonthDaySpecialDay(ICalendar.JEWISH, "18th day of Tamuz", 4, 18),
                    new NthDayOfWeek(ICalendar.JEWISH, 1)})
    })),

    FAST_AV_9(new UnionSpecialDay("Fast of 9th Av", new SpecialDay[]{
            // not Shabbos
            new ConjunctionSpecialDay("9 Av no Shabbos", new SpecialDay[]{new MonthDaySpecialDay(ICalendar.JEWISH, "Fast of 9th day of Av", 5, 9),
                    new NegationSpecialDay(new NthDayOfWeek(ICalendar.JEWISH, 7))}),
            // nidche
            new ConjunctionSpecialDay("9 Av no Shabbos", new SpecialDay[]{new MonthDaySpecialDay(ICalendar.JEWISH, "Fast of 9th day of Av", 5, 10),
                    new NthDayOfWeek(ICalendar.JEWISH, 1)})
    })),
    SHABBAT_CHAZON(new NthDayOfWeekFromPivot(ICalendar.JEWISH, "Shabbat Chazon",
            new MonthDaySpecialDay(ICalendar.JEWISH, "9 Av", 5, 9), 7, -1, true)),
    CHAI_ELUL(new MonthDaySpecialDay(ICalendar.JEWISH, "Chai Elul", 6, 18)),
    ROSH_HASHANA_1(new MonthDaySpecialDay(ICalendar.JEWISH, "First day Rosh Hashana", 7, 1)),
    ROSH_HASHANA_2(new MonthDaySpecialDay(ICalendar.JEWISH, "2nd day Rosh Hashana", 7, 2)),
    ROSH_CHODESH(new ConjunctionSpecialDay("Rosh Chodesh", new SpecialDay[]{new UnionSpecialDay("Rosh Chodesh or Rosh Hashana", new SpecialDay[]{new MonthDaySpecialDay(ICalendar.JEWISH, "Rosh Chodesh", 0, 1), new MonthDaySpecialDay(ICalendar.JEWISH, "Rosh Chodesh", 0, 30)}), new NegationSpecialDay("exclude Rosh Hashana", ROSH_HASHANA_1)})),
    TZOM_GEDALIA(new MonthDaySpecialDay(ICalendar.JEWISH, "Tzom Gedalia", 7, 3)),
    EREV_YOM_KIPPUR(new MonthDaySpecialDay(ICalendar.JEWISH, "Erev Yom Kippur", 7, 9)),
    YOM_KIPPUR(new MonthDaySpecialDay(ICalendar.JEWISH, "Yom Kippur", 7, 10)),
    FIRST_DAY_SUKKOT(new MonthDaySpecialDay(ICalendar.JEWISH, "1st day Sukkot", 7, 15)),
    SECOND_DAY_SUKKOT_C(new MonthDaySpecialDay(ICalendar.JEWISH, "2nd day Sukkot", 7, 16)),
    SHMINI_ATZERES_C(new MonthDaySpecialDay(ICalendar.JEWISH, "Shemini Atzeret", 7, 22)),
    SIMCHAT_TORAH_C(new MonthDaySpecialDay(ICalendar.JEWISH, "Simchat Torah", 7, 23)),
    CHOL_HAMOED_SUKKOT_1I(new MonthDaySpecialDay(ICalendar.JEWISH, "Chol Hamoed Sukkot", 7, 16)),
    CHOL_HAMOED_SUKKOT_2I(new MonthDaySpecialDay(ICalendar.JEWISH, "Chol Hamoed Sukkot", 7, 17)),
    CHOL_HAMOED_SUKKOT_3I(new MonthDaySpecialDay(ICalendar.JEWISH, "Chol Hamoed Sukkot", 7, 18)),
    CHOL_HAMOED_SUKKOT_4I(new MonthDaySpecialDay(ICalendar.JEWISH, "Chol Hamoed Sukkot", 7, 19)),
    CHOL_HAMOED_SUKKOT_5I(new MonthDaySpecialDay(ICalendar.JEWISH, "Chol Hamoed Sukkot", 7, 20)),
    HOSHANA_RABBA(new MonthDaySpecialDay(ICalendar.JEWISH, "Hoshana Rabba", 7, 21)),
    CHOL_HAMOED_SUKKOT_1C(new MonthDaySpecialDay(ICalendar.JEWISH, "Chol Hamoed Sukkot", 7, 17)),
    CHOL_HAMOED_SUKKOT_2C(new MonthDaySpecialDay(ICalendar.JEWISH, "Chol Hamoed Sukkot", 7, 18)),
    CHOL_HAMOED_SUKKOT_3C(new MonthDaySpecialDay(ICalendar.JEWISH, "Chol Hamoed Sukkot", 7, 19)),
    CHOL_HAMOED_SUKKOT_4C(new MonthDaySpecialDay(ICalendar.JEWISH, "Chol Hamoed Sukkot", 7, 20)),
    SIMCHAT_TORAH_I(new MonthDaySpecialDay(ICalendar.JEWISH, "Simchat Torah", 7, 22)),
    TAL_UMATAR_I(new TalUMatar(true, "Tal Umatar")),
    TAL_UMATAR_C(new TalUMatar(false, "Tal Umatar")),
    NINETEENTH_KISLEV(new MonthDaySpecialDay(ICalendar.JEWISH, "19 Kislev", 9, 19)),
    FIRST_DAY_CHANUKAH(new MonthDaySpecialDay(ICalendar.JEWISH, "1st day Chanukah", 9, 25)),
    SECOND_DAY_CHANUKAH(new ShiftedDateSpecialDay("2nd day Chanukah", FIRST_DAY_CHANUKAH, 1)),
    THIRD_DAY_CHANUKAH(new ShiftedDateSpecialDay("3rd day Chanukah", FIRST_DAY_CHANUKAH, 2)),
    FOURTH_DAY_CHANUKAH(new ShiftedDateSpecialDay("4th day Chanukah", FIRST_DAY_CHANUKAH, 3)),
    FIFTH_DAY_CHANUKAH(new ShiftedDateSpecialDay("5th day Chanukah", FIRST_DAY_CHANUKAH, 4)),
    SIXTH_DAY_CHANUKAH(new ShiftedDateSpecialDay("6th day Chanukah", FIRST_DAY_CHANUKAH, 5)),
    SEVENTH_DAY_CHANUKAH(new ShiftedDateSpecialDay("7th day Chanukah", FIRST_DAY_CHANUKAH, 6)),
    EIGHTH_DAY_CHANUKAH(new ShiftedDateSpecialDay("8th day Chanukah", FIRST_DAY_CHANUKAH, 7)),
    TENTH_TEVES(new MonthDaySpecialDay(ICalendar.JEWISH, "10 Tevet", 10, 10)),
    YUD_SHVAT(new MonthDaySpecialDay(ICalendar.JEWISH, "10 Shvat", 11, 10)),
    TU_BESHVAT(new MonthDaySpecialDay(ICalendar.JEWISH, "Tu Bishvat", 11, 15)),
    PURIM(new MonthDaySpecialDay(ICalendar.JEWISH, "Purim", -1, 14)),
    SHUSHAN_PURIM(new MonthDaySpecialDay(ICalendar.JEWISH, "Shushan Purim", -1, 15)),
    PURIM_KATAN(new ConjunctionSpecialDay("Purim Katan", new SpecialDay[] {
            new MonthDaySpecialDay(ICalendar.JEWISH, "12 Adar I", 12, 14),
            new NegationSpecialDay("exclude Purim", PURIM)})),
    TAANIT_ESTHER(new TaanitEstherSpecialDay("Fast of Esther")),

    ERUV_TAVSHILIN_I(new EruvTavshilin("Eruv Tavshilin", new SpecialDay[]{
            // 7th day Pesach: Pesach starts Shabbat → 7th day falls on Fri.
            // Shavuot: Pesach starts Thu → Shavuot is Fri.
            // RH1 on Thu → RH2 on Fri (two consecutive YomTov days trigger ET on Wed).
            // Omitted — can never fall on Fri (calendar rules prevent it), and in Israel the
            // following day is Chol Hamoed, not YomTov: 1st day Pesach, 1st day Sukkot, Simchat Torah.
            // Omitted — Yom Kippur can never fall on Fri (lo adu rosh).
            SEVENTH_DAY_PESACH, SHAVUOT,
            ROSH_HASHANA_1, ROSH_HASHANA_2
    })),

    BIRKAT_HACHAMA(new BirkatHachama("Birkat HaChama")),

    ERUV_TAVSHILIN_C(new EruvTavshilin("Eruv Tavshilin", new SpecialDay[]{
            // 1st day Pesach on Thu → 2nd day (Fri) is also YomTov; 7th and last day can fall on Fri directly.
            // Shavuot can fall on Fri; 2nd day Shavuot can fall on Fri.
            // RH1 on Thu → RH2 on Fri.
            // 1st day Sukkot on Thu → 2nd day (Fri) is also YomTov.
            // Shmini Atzeret on Thu → Simchat Torah on Fri; Simchat Torah itself can fall on Fri.
            // Omitted — Yom Kippur can never fall on Fri (lo adu rosh).
            FIRST_DAY_PESACH, SECOND_DAY_PESACH_C, SEVENTH_DAY_PESACH, LAST_DAY_PESACH_C,
            SHAVUOT, SHAVUOT_2C,
            ROSH_HASHANA_1, ROSH_HASHANA_2,
            FIRST_DAY_SUKKOT, SECOND_DAY_SUKKOT_C, SHMINI_ATZERES_C, SIMCHAT_TORAH_C
    }));

   
    private static Set<JewishSpecialDay> createCollection(JewishSpecialDay[] hh) {
        HashSet<JewishSpecialDay> buildSet = new HashSet<>();
        buildSet.addAll(Arrays.asList(hh));
        return Collections.unmodifiableSet(buildSet);
    }

    private static final Set<JewishSpecialDay> ISRAEL_SPECIFIC_DAYS = createCollection(
            new JewishSpecialDay[]{
                    CHOL_HAMOED_PESACH_1I, CHOL_HAMOED_PESACH_2I, CHOL_HAMOED_PESACH_3I, CHOL_HAMOED_PESACH_4I,
                    CHOL_HAMOED_PESACH_5I, CHOL_HAMOED_SUKKOT_1I, CHOL_HAMOED_SUKKOT_2I, CHOL_HAMOED_SUKKOT_3I,
                    CHOL_HAMOED_SUKKOT_4I, CHOL_HAMOED_SUKKOT_5I, SIMCHAT_TORAH_I,
                    ERUV_TAVSHILIN_I, TAL_UMATAR_I
            }
    );

    private static final Set<JewishSpecialDay> CHUTZ_LAARETZ_SPECIFIC = createCollection(
            new JewishSpecialDay[]{
                    SECOND_DAY_PESACH_C, LAST_DAY_PESACH_C,
                    CHOL_HAMOED_PESACH_1C, CHOL_HAMOED_PESACH_2C, CHOL_HAMOED_PESACH_3C, CHOL_HAMOED_PESACH_4C,
                    SHAVUOT_2C, SECOND_DAY_SUKKOT_C,
                    CHOL_HAMOED_SUKKOT_1C, CHOL_HAMOED_SUKKOT_2C, CHOL_HAMOED_SUKKOT_3C, CHOL_HAMOED_SUKKOT_4C,
                    SHMINI_ATZERES_C, SIMCHAT_TORAH_C,
                    ERUV_TAVSHILIN_C, TAL_UMATAR_C
            }
    );

    private static final Set<JewishSpecialDay> ERUV_TAVSHILIN_DAYS = createCollection(
            new JewishSpecialDay[]{ERUV_TAVSHILIN_I, ERUV_TAVSHILIN_C}
    );

    private static final Set<JewishSpecialDay> TAL_UMATAR_DAYS = createCollection(
            new JewishSpecialDay[]{TAL_UMATAR_I, TAL_UMATAR_C}
    );

    private static final Set<JewishSpecialDay> ARBA_PARSHIYOT_DAYS = createCollection(
            new JewishSpecialDay[]{SHABBAT_SHEKALIM, SHABBAT_ZACHOR, SHABBAT_PARA, SHABBAT_HACHODESH}
    );

    private static final Set<JewishSpecialDay> CHANUKAH_DAYS = createCollection(
            new JewishSpecialDay[]{
                    FIRST_DAY_CHANUKAH, SECOND_DAY_CHANUKAH, THIRD_DAY_CHANUKAH, FOURTH_DAY_CHANUKAH,
                    FIFTH_DAY_CHANUKAH, SIXTH_DAY_CHANUKAH, SEVENTH_DAY_CHANUKAH, EIGHTH_DAY_CHANUKAH
            }
    );

    private static final Set<String> CHANUKAH_NAMES;
    static {
        Set<String> s = new HashSet<>();
        for (JewishSpecialDay d : CHANUKAH_DAYS) s.add(d.getName());
        CHANUKAH_NAMES = Collections.unmodifiableSet(s);
    }

    private static final Set<JewishSpecialDay> FAST_DAYS = createCollection(
            new JewishSpecialDay[]{
                    TAANIT_ESTHER, TZOM_GEDALIA, TENTH_TEVES, FAST_TAMUZ_17, FAST_AV_9
            });

    private static final Set<JewishSpecialDay> YOM_TOV_DAYS = createCollection(
            new JewishSpecialDay[]{
                    FIRST_DAY_PESACH, SECOND_DAY_PESACH_C, SEVENTH_DAY_PESACH, LAST_DAY_PESACH_C,
                    SHAVUOT, SHAVUOT_2C, ROSH_HASHANA_1, ROSH_HASHANA_2, YOM_KIPPUR,
                    FIRST_DAY_SUKKOT, SECOND_DAY_SUKKOT_C, SIMCHAT_TORAH_I, SIMCHAT_TORAH_C, SHMINI_ATZERES_C
            });

    private static final Set<JewishSpecialDay> CHABAD_DAYS = createCollection(
            new JewishSpecialDay[]{
                    NINETEENTH_KISLEV, YUD_SHVAT, NISAN_11, TAMUZ_3, TAMUZ_12, TAMUZ_13, CHAI_ELUL
            });

    private static final Set<JewishSpecialDay> CHOL_HAMOED_DAYS = createCollection(
            new JewishSpecialDay[]{
                    CHOL_HAMOED_PESACH_1I, CHOL_HAMOED_PESACH_2I, CHOL_HAMOED_PESACH_3I,
                    CHOL_HAMOED_PESACH_4I, CHOL_HAMOED_PESACH_5I,
                    CHOL_HAMOED_PESACH_1C, CHOL_HAMOED_PESACH_2C, CHOL_HAMOED_PESACH_3C,
                    CHOL_HAMOED_PESACH_4C,
                    CHOL_HAMOED_SUKKOT_1I, CHOL_HAMOED_SUKKOT_2I, CHOL_HAMOED_SUKKOT_3I,
                    CHOL_HAMOED_SUKKOT_4I, CHOL_HAMOED_SUKKOT_5I, HOSHANA_RABBA,
                    CHOL_HAMOED_SUKKOT_1C, CHOL_HAMOED_SUKKOT_2C, CHOL_HAMOED_SUKKOT_3C,
                    CHOL_HAMOED_SUKKOT_4C
            });

    public boolean isYomTov()         { return YOM_TOV_DAYS.contains(this); }
    public boolean isCholHamoed()     { return CHOL_HAMOED_DAYS.contains(this); }
    public boolean isFast()           { return FAST_DAYS.contains(this); }
    public boolean isEruvTavshilin()  { return ERUV_TAVSHILIN_DAYS.contains(this); }
    public boolean isChabad()         { return CHABAD_DAYS.contains(this); }
    public boolean isTalUMatar()      { return TAL_UMATAR_DAYS.contains(this); }
    public boolean isArbaParshiyot()  { return ARBA_PARSHIYOT_DAYS.contains(this); }
    public boolean isChanukah()       { return CHANUKAH_DAYS.contains(this); }

    public static boolean isChanukahName(String name) { return CHANUKAH_NAMES.contains(name); }
    
    private final SpecialDay _delegate;

    JewishSpecialDay(SpecialDay delegate) {
        _delegate = delegate;
    }


    @Override
    public String getName()
    {
        return _delegate.getName();
    }

    @Override
    public IDate getNextOccurrence(IDate date, boolean strict)
            throws NoSuchHolidayException
    {
        return _delegate.getNextOccurrence(date, strict);
    }

    @Override
    public IDate getPrevOccurrence(IDate date, boolean strict)
            throws NoSuchHolidayException
    {
        return _delegate.getPrevOccurrence(date, strict);
    }

    @Override
    public ICalendar getCalendar()
    {
        return ICalendar.JEWISH;
    }

    @Override
    public boolean matches(IDate date)
    {
        return _delegate.matches(date);
    }

    /**
     * Determine if the special day applies in given location
     * @param inIsrael true for Eretz Israel, false outside
     * @return whether the given holiday applies to local calendar.
     */
    public final boolean applies(boolean inIsrael) {
        return inIsrael ? !CHUTZ_LAARETZ_SPECIFIC.contains(this) : !ISRAEL_SPECIFIC_DAYS.contains(this);
    }
}
