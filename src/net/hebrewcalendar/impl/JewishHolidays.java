package net.hebrewcalendar.impl;

import net.hebrewcalendar.impl.holiday.*;
import net.hebrewcalendar.HCalendar;
import net.hebrewcalendar.HHoliday;

public class JewishHolidays
{
    private JewishHolidays(){}

    public static final HHoliday EREV_PESACH = new MonthDayHoliday(HCalendar.HEBREW, "Erev Pesach", 1, 14);
    public static final HHoliday FIRST_DAY_PESACH = new MonthDayHoliday(HCalendar.HEBREW, "Pesach", 1, 15);
    public static final HHoliday SECOND_DAY_PESACH_C = new MonthDayHoliday(HCalendar.HEBREW, "2nd day of Pesach", 1, 16);
    public static final HHoliday SEVENTH_DAY_PESACH = new MonthDayHoliday(HCalendar.HEBREW, "7th day of Pesach", 1, 21);
    public static final HHoliday LAST_DAY_PESACH = new MonthDayHoliday(HCalendar.HEBREW, "Last day of Pesach", 1, 22);
    public static final HHoliday CHOL_HAMOED_PESACH_1I = new MonthDayHoliday(HCalendar.HEBREW, "Chol Hamoed Pesach", 1, 16);
    public static final HHoliday CHOL_HAMOED_PESACH_2I = new MonthDayHoliday(HCalendar.HEBREW, "Chol Hamoed Pesach", 1, 17);
    public static final HHoliday CHOL_HAMOED_PESACH_3I = new MonthDayHoliday(HCalendar.HEBREW, "Chol Hamoed Pesach", 1, 18);
    public static final HHoliday CHOL_HAMOED_PESACH_4I = new MonthDayHoliday(HCalendar.HEBREW, "Chol Hamoed Pesach", 1, 19);
    public static final HHoliday CHOL_HAMOED_PESACH_5I = new MonthDayHoliday(HCalendar.HEBREW, "Chol Hamoed Pesach", 1, 20);
    public static final HHoliday CHOL_HAMOED_PESACH_1C = new MonthDayHoliday(HCalendar.HEBREW, "Chol Hamoed Pesach", 1, 17);
    public static final HHoliday CHOL_HAMOED_PESACH_2C = new MonthDayHoliday(HCalendar.HEBREW, "Chol Hamoed Pesach", 1, 18);
    public static final HHoliday CHOL_HAMOED_PESACH_3C = new MonthDayHoliday(HCalendar.HEBREW, "Chol Hamoed Pesach", 1, 19);
    public static final HHoliday CHOL_HAMOED_PESACH_4C = new MonthDayHoliday(HCalendar.HEBREW, "Chol Hamoed Pesach", 1, 20);

    public static final HHoliday SHABBOS_HAGODOL = new NthDayOfWeekFromPivot(HCalendar.HEBREW, "Shabbos Hagodol", FIRST_DAY_PESACH, 7, -1, false);

    public static final HHoliday LAG_BAOMER = new MonthDayHoliday(HCalendar.HEBREW, "Lag Baomer", 2, 18);

    public static final HHoliday SHAVUOS = new MonthDayHoliday(HCalendar.HEBREW, "Shavuos", 3, 6);
    public static final HHoliday SHAVUOS_2C = new MonthDayHoliday(HCalendar.HEBREW, "2nd day of Shavuos", 3, 7);

    public static final HHoliday TAMUZ_3 = new MonthDayHoliday(HCalendar.HEBREW, "12th of Tamuz", 4, 3);
    public static final HHoliday TAMUZ_12 = new MonthDayHoliday(HCalendar.HEBREW, "12th of Tamuz", 4, 12);
    public static final HHoliday TAMUZ_13 = new MonthDayHoliday(HCalendar.HEBREW, "12th of Tamuz", 4, 13);
    public static final HHoliday TAMUZ_17 = new MonthDayHoliday(HCalendar.HEBREW, "Fast of 17th of Tamuz", 4, 17);
    public static final HHoliday AV_9 = new MonthDayHoliday(HCalendar.HEBREW, "Fast of 9th day of Av", 5, 9);

    public static final HHoliday CHAI_ELUL = new MonthDayHoliday(HCalendar.HEBREW, "Chai Elul", 6, 18);

    public static final HHoliday ROSH_HASHANA_1 = new MonthDayHoliday(HCalendar.HEBREW, "First day Rosh Hashana", 7, 1);
    public static final HHoliday ROSH_HASHANA_2 = new MonthDayHoliday(HCalendar.HEBREW, "2nd day Rosh Hashana", 7, 2);

    public static final HHoliday ROSH_CHODESH = new ConjunctionHoliday(
            "Rosh Chodesh",
            new HHoliday[]{
                    new UnionHoliday("Rosh Chodesh or Rosh Hashana",
                            new HHoliday[]{
                                    new MonthDayHoliday(HCalendar.HEBREW, "Rosh Chodesh", 0, 1),
                                    new MonthDayHoliday(HCalendar.HEBREW, "Rosh Chodesh", 0, 30)
                            }),
                    new NegationHoliday("exclude Rosh Hashana", ROSH_HASHANA_1)
            }
    );
    public static final HHoliday TZOM_GEDALIA = new MonthDayHoliday(HCalendar.HEBREW, "Tzom Gedalia", 7, 3);

    public static final HHoliday YOM_KIPPUR = new MonthDayHoliday(HCalendar.HEBREW, "", 7, 10);

    public static final HHoliday FIRST_DAY_SUKKOS = new MonthDayHoliday(HCalendar.HEBREW, "1st day Sukkos", 7, 15);
    public static final HHoliday SECOND_DAY_SUKKOS_C = new MonthDayHoliday(HCalendar.HEBREW, "2nd day Sukkos", 7, 16);
    public static final HHoliday SHMINI_ATZERES_C = new MonthDayHoliday(HCalendar.HEBREW, "Shmini Atzeres", 7, 22);
    public static final HHoliday SIMCHAS_TORAH_C = new MonthDayHoliday(HCalendar.HEBREW, "Simchas Torah", 7, 23);

    public static final HHoliday CHOL_HAMOED_SUKKOS_1I = new MonthDayHoliday(HCalendar.HEBREW, "Chol Hamoed Sukkos", 7, 16);
    public static final HHoliday CHOL_HAMOED_SUKKOS_2I = new MonthDayHoliday(HCalendar.HEBREW, "Chol Hamoed Sukkos", 7, 17);
    public static final HHoliday CHOL_HAMOED_SUKKOS_3I = new MonthDayHoliday(HCalendar.HEBREW, "Chol Hamoed Sukkos", 7, 18);
    public static final HHoliday CHOL_HAMOED_SUKKOS_4I = new MonthDayHoliday(HCalendar.HEBREW, "Chol Hamoed Sukkos", 7, 19);
    public static final HHoliday CHOL_HAMOED_SUKKOS_5I = new MonthDayHoliday(HCalendar.HEBREW, "Chol Hamoed Sukkos", 7, 20);
    public static final HHoliday HOSHANA_RABBA = new MonthDayHoliday(HCalendar.HEBREW, "Hoshana Rabba", 7, 21);
    public static final HHoliday CHOL_HAMOED_SUKKOS_1C = new MonthDayHoliday(HCalendar.HEBREW, "Chol Hamoed Sukkos", 7, 17);
    public static final HHoliday CHOL_HAMOED_SUKKOS_2C = new MonthDayHoliday(HCalendar.HEBREW, "Chol Hamoed Sukkos", 7, 18);
    public static final HHoliday CHOL_HAMOED_SUKKOS_3C = new MonthDayHoliday(HCalendar.HEBREW, "Chol Hamoed Sukkos", 7, 19);
    public static final HHoliday CHOL_HAMOED_SUKKOS_4C = new MonthDayHoliday(HCalendar.HEBREW, "Chol Hamoed Sukkos", 7, 20);
    public static final HHoliday SIMCHAS_TORAH_I = new MonthDayHoliday(HCalendar.HEBREW, "Simchas Torah", 7, 22);

    public static final HHoliday NINETEENTH_KISLEV = new MonthDayHoliday(HCalendar.HEBREW, "First day Chanukah", 9, 19);

    public static final HHoliday FIRST_DAY_CHANUKAH = new MonthDayHoliday(HCalendar.HEBREW, "1st day Chanukah", 9, 25);
    public static final HHoliday SECOND_DAY_CHANUKAH = new ShiftedDateHoliday("2nd day Chanukah", FIRST_DAY_CHANUKAH, 1);
    public static final HHoliday THIRD_DAY_CHANUKAH = new ShiftedDateHoliday("3rd day Chanukah", FIRST_DAY_CHANUKAH, 2);
    public static final HHoliday FOURTH_DAY_CHANUKAH = new ShiftedDateHoliday("4th day Chanukah", FIRST_DAY_CHANUKAH, 3);
    public static final HHoliday FIFTH_DAY_CHANUKAH = new ShiftedDateHoliday("5th day Chanukah", FIRST_DAY_CHANUKAH, 4);
    public static final HHoliday SIXTH_DAY_CHANUKAH = new ShiftedDateHoliday("6th day Chanukah", FIRST_DAY_CHANUKAH, 5);
    public static final HHoliday SEVENTH_DAY_CHANUKAH = new ShiftedDateHoliday("7th day Chanukah", FIRST_DAY_CHANUKAH, 6);
    public static final HHoliday EIGHTH_DAY_CHANUKAH = new ShiftedDateHoliday("8th day Chanukah", FIRST_DAY_CHANUKAH, 7);

    public static final HHoliday TENTH_TEVES = new MonthDayHoliday(HCalendar.HEBREW,"Fast of 10 Tevet", 10, 10);

    public static final HHoliday YUD_SHVAT = new MonthDayHoliday(HCalendar.HEBREW,"10 Shvat", 11, 10);
    public static final HHoliday TU_BESHVAT = new MonthDayHoliday(HCalendar.HEBREW,"Tu Beshvat", 11, 15);

    public static final HHoliday PURIM = new MonthDayHoliday(HCalendar.HEBREW,"Tu Beshvat", -1, 14);
    public static final HHoliday SHUSHAN_PURIM = new MonthDayHoliday(HCalendar.HEBREW,"Tu Beshvat", -1, 15);
    public static final HHoliday PURIM_KATAN = new ConjunctionHoliday(
            "Purim Katan",
            new HHoliday[]{
                    new MonthDayHoliday(HCalendar.HEBREW, "Tu Beshvat", 12, 14),
                    new NegationHoliday("exclude Purim", PURIM)
            }
    );

    public static final HHoliday[] YOMIM_TOVIM_ISRAEL = new HHoliday[] {
            CHOL_HAMOED_PESACH_1I,
            CHOL_HAMOED_PESACH_2I,
            CHOL_HAMOED_PESACH_3I,
            CHOL_HAMOED_PESACH_4I,
            CHOL_HAMOED_PESACH_5I,
            CHOL_HAMOED_SUKKOS_1I,
            CHOL_HAMOED_SUKKOS_2I,
            CHOL_HAMOED_SUKKOS_3I,
            CHOL_HAMOED_SUKKOS_4I,
            CHOL_HAMOED_SUKKOS_5I,
            SIMCHAS_TORAH_I
    };

    public static final HHoliday[] YOMIM_TOVIM_CHUTZ = new HHoliday[] {
            SECOND_DAY_PESACH_C,
            CHOL_HAMOED_PESACH_1C,
            CHOL_HAMOED_PESACH_2C,
            CHOL_HAMOED_PESACH_3C,
            CHOL_HAMOED_PESACH_4C,
            LAST_DAY_PESACH,
            SHAVUOS_2C,
            SECOND_DAY_SUKKOS_C,
            CHOL_HAMOED_SUKKOS_1C,
            CHOL_HAMOED_SUKKOS_2C,
            CHOL_HAMOED_SUKKOS_3C,
            CHOL_HAMOED_SUKKOS_4C,
            SHMINI_ATZERES_C,
            SIMCHAS_TORAH_I
    };

    public static final HHoliday[] COMMON = new HHoliday[] {
            ROSH_CHODESH,
            SHABBOS_HAGODOL,
            EREV_PESACH,
            FIRST_DAY_PESACH,
            SEVENTH_DAY_PESACH,
            SHAVUOS,
            LAG_BAOMER,
            TAMUZ_17,
            AV_9,
            ROSH_HASHANA_1,
            ROSH_HASHANA_2,
            TZOM_GEDALIA,
            YOM_KIPPUR,
            FIRST_DAY_SUKKOS,
            HOSHANA_RABBA,
            FIRST_DAY_CHANUKAH,
            SECOND_DAY_CHANUKAH,
            THIRD_DAY_CHANUKAH,
            FOURTH_DAY_CHANUKAH,
            FIFTH_DAY_CHANUKAH,
            SIXTH_DAY_CHANUKAH,
            SEVENTH_DAY_CHANUKAH,
            EIGHTH_DAY_CHANUKAH,
            TENTH_TEVES,
            TU_BESHVAT,
            PURIM_KATAN,
            PURIM,
            SHUSHAN_PURIM
    };

    public static final HHoliday[] CHABAD = new HHoliday[] {
            TAMUZ_3, TAMUZ_12, TAMUZ_13, CHAI_ELUL,
            NINETEENTH_KISLEV, YUD_SHVAT
    };

    public static final HHoliday[] YOMIM_TOVIM = new HHoliday[] {
            FIRST_DAY_PESACH, SECOND_DAY_PESACH_C, SEVENTH_DAY_PESACH, LAST_DAY_PESACH,
            SHAVUOS, SHAVUOS_2C, ROSH_HASHANA_1, ROSH_HASHANA_2, FIRST_DAY_SUKKOS, SECOND_DAY_SUKKOS_C,
            SHMINI_ATZERES_C, SIMCHAS_TORAH_I, SIMCHAS_TORAH_C
    };
}
