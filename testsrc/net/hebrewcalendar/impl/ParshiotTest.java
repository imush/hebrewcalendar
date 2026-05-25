package net.hebrewcalendar.impl;

import net.hebrewcalendar.ICalendar;
import net.hebrewcalendar.IDate;
import net.hebrewcalendar.JewishCalendar;
import net.hebrewcalendar.JewishSpecialDay;
import net.hebrewcalendar.Parsha;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.List;

import static org.junit.Assert.*;

public class ParshiotTest {

    private static final ICalendar       GREG   = ICalendar.GREGORIAN;
    private static final JewishCalendar JEWISH = ICalendar.JEWISH;

    // ── year-type key ─────────────────────────────────────────────────────────

    private static String yearTypeKey(int hebrewYear) {
        IDate rosh   = JEWISH.fromYMD(hebrewYear, 7, 1);
        IDate pesach = JEWISH.fromYMD(hebrewYear, 1, 15);
        int r   = rosh.getDayOfWeek();
        int len = JewishCalendarImpl.INSTANCE.getYearType(hebrewYear).ordinal();
        int p   = pesach.getDayOfWeek();
        return r + "," + len + "," + p;
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private static LocalDate firstShabbatOfYear(int hebrewYear) {
        IDate rosh = JEWISH.fromYMD(hebrewYear, 7, 1);
        IDate g = GREG.convert(rosh);
        return LocalDate.of(g.getYear(), g.getMonth(), g.getDay())
                        .with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
    }

    private static LocalDate shabbatBeforeShavuot(int hebrewYear) {
        IDate shavuot = JEWISH.fromYMD(hebrewYear, 3, 6);
        IDate g = GREG.convert(shavuot);
        LocalDate ld = LocalDate.of(g.getYear(), g.getMonth(), g.getDay());
        LocalDate shabbat = ld.minusDays(1);
        while (shabbat.getDayOfWeek() != DayOfWeek.SATURDAY) shabbat = shabbat.minusDays(1);
        return shabbat;
    }

    private static List<Parsha> getParsha(LocalDate shabbat, boolean inIsrael) {
        IDate d = GREG.fromYMD(shabbat.getYear(), shabbat.getMonthValue(), shabbat.getDayOfMonth());
        return Parshiot.getParsha(d, inIsrael);
    }

    // ── tests ─────────────────────────────────────────────────────────────────

    @Test
    public void allFourteenYearTypesExist() {
        Set<String> expected = new HashSet<>(Arrays.asList(
            "2,2,5", "2,0,3", "3,1,5",
            "5,2,1", "5,1,7", "7,2,3", "7,0,1",
            "2,2,7", "2,0,5", "3,1,7",
            "5,2,3", "5,0,1", "7,2,5", "7,0,3"
        ));
        Set<String> found = new HashSet<>();
        for (int y = 5750; y <= 5900; y++) found.add(yearTypeKey(y));
        Set<String> missing = new HashSet<>(expected);
        missing.removeAll(found);
        assertTrue("Missing year types: " + missing, missing.isEmpty());
    }

    @Test
    public void firstShabbatAfterSimchatTorahIsBereishit() {
        for (int y = 5750; y <= 5820; y++) {
            IDate simchatTorah = JEWISH.fromYMD(y, 7, 22);
            IDate g = GREG.convert(simchatTorah);
            LocalDate ld = LocalDate.of(g.getYear(), g.getMonth(), g.getDay());
            LocalDate shabbat = ld.plusDays(1);
            while (shabbat.getDayOfWeek() != DayOfWeek.SATURDAY) shabbat = shabbat.plusDays(1);

            List<Parsha> parsha = getParsha(shabbat, false);
            assertFalse("Year " + y + ": expected Bereishit but got empty (Yom Tov)", parsha.isEmpty());
            assertEquals("Year " + y + ": first Shabbat after Simchat Torah",
                Parsha.BEREISHIT, parsha.get(0));
        }
    }

    @Test
    public void shabbatBeforePesachIsTzavOrMetzora() {
        for (int y = 5750; y <= 5820; y++) {
            IDate pesach = JEWISH.fromYMD(y, 1, 15);
            IDate g = GREG.convert(pesach);
            LocalDate ld = LocalDate.of(g.getYear(), g.getMonth(), g.getDay());
            LocalDate shabbat = ld.minusDays(1);
            while (shabbat.getDayOfWeek() != DayOfWeek.SATURDAY) shabbat = shabbat.minusDays(1);

            List<Parsha> parsha = getParsha(shabbat, false);
            assertFalse("Year " + y + ": Shabbat before Pesach should not be Yom Tov", parsha.isEmpty());

            boolean leap = JEWISH.isLeap(y);
            Parsha expected = !leap       ? Parsha.TZAV
                            : isLeapThuRH(y) ? Parsha.ACHAREI_MOT
                            : Parsha.METZORA;
            assertEquals("Year " + y + " (leap=" + leap + "): Shabbat before Pesach",
                expected, parsha.get(0));
        }
    }

    /**
     * Standard case: Shabbat before Shavuot is Bamidbar.
     * Exceptions are tested separately below.
     */
    @Test
    public void shabbatBeforeShavuotIsBamidbar() {
        for (int y = 5750; y <= 5820; y++) {
            if (isLeapThuRH(y) || isLeapSatPesach(y)) continue; // exceptions tested separately
            LocalDate shabbat = shabbatBeforeShavuot(y);
            List<Parsha> parsha = getParsha(shabbat, false);
            assertFalse("Year " + y + ": Shabbat before Shavuot should not be Yom Tov", parsha.isEmpty());
            assertEquals("Year " + y + ": Shabbat before Shavuot", Parsha.BAMIDBAR, parsha.get(0));
        }
    }

    /**
     * Exception case 1: leap year with Thursday Rosh Hashana (types K and L).
     * Both Israel and Diaspora read Naso on the Shabbat before Shavuot.
     */
    @Test
    public void shabbatBeforeShavuotLeapThuRH_isNaso() {
        boolean foundAtLeastOne = false;
        for (int y = 5750; y <= 5820; y++) {
            if (!isLeapThuRH(y)) continue;
            foundAtLeastOne = true;
            LocalDate shabbat = shabbatBeforeShavuot(y);

            List<Parsha> diaspora = getParsha(shabbat, false);
            List<Parsha> israel   = getParsha(shabbat, true);
            assertFalse("Year " + y + ": diaspora should not be empty", diaspora.isEmpty());
            assertFalse("Year " + y + ": Israel should not be empty", israel.isEmpty());
            assertEquals("Year " + y + " diaspora: Shabbat before Shavuot", Parsha.NASO, diaspora.get(0));
            assertEquals("Year " + y + " Israel:   Shabbat before Shavuot", Parsha.NASO, israel.get(0));
        }
        assertTrue("No leap-Thursday-RH years found in range", foundAtLeastOne);
    }

    /**
     * Exception case 2: leap year with Saturday Pesach (types H and J).
     * Diaspora reads Bamidbar, Israel reads Naso on the Shabbat before Shavuot.
     */
    @Test
    public void shabbatBeforeShavuotLeapSatPesach_diasporaBamidbarIsraelNaso() {
        boolean foundAtLeastOne = false;
        for (int y = 5750; y <= 5820; y++) {
            if (!isLeapSatPesach(y)) continue;
            foundAtLeastOne = true;
            LocalDate shabbat = shabbatBeforeShavuot(y);

            List<Parsha> diaspora = getParsha(shabbat, false);
            List<Parsha> israel   = getParsha(shabbat, true);
            assertFalse("Year " + y + ": diaspora should not be empty", diaspora.isEmpty());
            assertFalse("Year " + y + ": Israel should not be empty", israel.isEmpty());
            assertEquals("Year " + y + " diaspora: Shabbat before Shavuot", Parsha.BAMIDBAR, diaspora.get(0));
            assertEquals("Year " + y + " Israel:   Shabbat before Shavuot", Parsha.NASO, israel.get(0));
        }
        assertTrue("No leap-Saturday-Pesach years found in range", foundAtLeastOne);
    }

    @Test
    public void may30_2026() {
        IDate date = GREG.fromYMD(2026, 5, 30);
        assertEquals("May 30 2026 should be Saturday", 7, date.getDayOfWeek());

        List<Parsha> diaspora = Parshiot.getParsha(date, false);
        assertFalse("Diaspora: expected a parsha on May 30 2026", diaspora.isEmpty());
        assertEquals("Diaspora: May 30 2026 should be Naso", Parsha.NASO, diaspora.get(0));

        List<Parsha> israel = Parshiot.getParsha(date, true);
        assertFalse("Israel: expected a parsha on May 30 2026", israel.isEmpty());
        assertEquals("Israel: May 30 2026 should be Behaalotecha", Parsha.BEHAALOTECHA, israel.get(0));
    }

    @Test
    public void testParshaArrayLength() {
        for (ParshiotYearType t : ParshiotYearType.values())
            assertEquals(t.schedule(false).size(), t.schedule(true).size());
    }

    /**
     * Every ParshiotYearType schedule (both Israel and Diaspora) must contain
     * all 53 parshiyot. Some may appear twice (e.g. Vayeilech at start and end
     * of a year), but none may be absent.
     */
    /**
     * In non-leap years, Vayakhel-Pekudei is split if and only if
     * Rosh Chodesh Tevet falls on Tuesday-Wednesday (i.e. Kislev has 30 days
     * and Kislev 30 is a Tuesday), which uniquely identifies year type F51.
     * From 5 klalim of R. Chaim Mordechai Brecher, printed in the back of Mikraot Gedolot.
     */
    @Test
    public void vayakhelPekudeiSplitIffRoshChodeshTevetTuesdayWednesday() {
        for (int y = 5750; y <= 5820; y++) {
            if (JEWISH.isLeap(y)) continue;
            YearCheshvanKislevType yearType = JewishCalendarImpl.INSTANCE.getYearType(y);
            // 2-day RC Tevet (Kislev 30 + Tevet 1) exists only when Kislev has 30 days
            boolean rcTuesdayWednesday = false;
            if (yearType != YearCheshvanKislevType.SHORT) {
                IDate kislev30 = JEWISH.fromYMD(y, 9, 30);
                rcTuesdayWednesday = (kislev30.getDayOfWeek() == 3); // Tue=3, Wed=4 follows
            }
            int rosh   = JEWISH.fromYMD(y, 7,  1).getDayOfWeek();
            int pesach = JEWISH.fromYMD(y, 1, 15).getDayOfWeek();
            for (boolean inIsrael : new boolean[]{false, true}) {
                String ctx = "Year " + y + " " + (inIsrael ? "Israel" : "Diaspora");
                List<List<Parsha>> schedule =
                    ParshiotYearType.forYear(rosh, yearType, pesach).schedule(inIsrael);
                boolean combined = schedule.stream()
                    .anyMatch(slot -> slot.contains(Parsha.VAYAKHEL) && slot.contains(Parsha.PEKUDEI));
                if (rcTuesdayWednesday) {
                    assertFalse(ctx + ": Vayakhel+Pekudei must be split when RC Tevet is Tue+Wed",
                        combined);
                } else {
                    assertTrue(ctx + ": Vayakhel+Pekudei must be combined when RC Tevet is not Tue+Wed",
                        combined);
                }
            }
        }
    }

    /**
     * In every leap year, the four traditionally-combined pairs are always
     * read separately — never doubled.
     * From 5 klalim of R. Chaim Mordechai Brecher, printed in the back of Mikraot Gedolot.
     */
    @Test
    public void traditionalPairsAlwaysSplitInLeapYears() {
        EnumSet<ParshiotYearType> leapTypes = EnumSet.of(
            ParshiotYearType.F27, ParshiotYearType.S25, ParshiotYearType.N37,
            ParshiotYearType.F53, ParshiotYearType.S51,
            ParshiotYearType.F75, ParshiotYearType.S73);

        for (ParshiotYearType type : leapTypes) {
            for (boolean inIsrael : new boolean[]{false, true}) {
                String ctx = type + " " + (inIsrael ? "Israel" : "Diaspora");
                List<List<Parsha>> schedule = type.schedule(inIsrael);
                assertNotCombined(ctx, schedule, Parsha.VAYAKHEL,    Parsha.PEKUDEI);
                assertNotCombined(ctx, schedule, Parsha.TAZRIA,      Parsha.METZORA);
                assertNotCombined(ctx, schedule, Parsha.ACHAREI_MOT, Parsha.KEDOSHIM);
                assertNotCombined(ctx, schedule, Parsha.BEHAR,       Parsha.BECHUKOTAI);
            }
        }
    }

    private static void assertNotCombined(String ctx, List<List<Parsha>> schedule,
                                           Parsha a, Parsha b) {
        for (List<Parsha> slot : schedule) {
            assertFalse(ctx + ": " + a + "+" + b + " must be split in leap year",
                slot.contains(a) && slot.contains(b));
        }
    }

    @Test
    public void allParshotAppearInEveryYearType() {
        // VAYEILECH is legitimately absent from year types whose schedule starts with HAAZINU —
        // it was read the prior year combined with NITZAVIM. All other 52 parshiyot must appear.
        EnumSet<Parsha> mandatory = EnumSet.allOf(Parsha.class);
        mandatory.remove(Parsha.VAYEILECH);

        for (ParshiotYearType type : ParshiotYearType.values()) {
            for (boolean inIsrael : new boolean[]{false, true}) {
                String ctx = type + " " + (inIsrael ? "Israel" : "Diaspora");
                EnumSet<Parsha> covered = EnumSet.noneOf(Parsha.class);
                for (List<Parsha> slot : type.schedule(inIsrael)) {
                    covered.addAll(slot);
                }
                EnumSet<Parsha> missing = EnumSet.copyOf(mandatory);
                missing.removeAll(covered);
                assertTrue(ctx + ": missing parshiyot " + missing, missing.isEmpty());
            }
        }
    }

    /**
     * Every Shabbat on which getParsha returns an empty list must be a Yom Tov
     * or Chol Hamoed Shabbat — never an ordinary Shabbat with a missing reading.
     */
    @Test
    public void emptyShabbatotAreYomTovOrCholHamoed() {
        for (int y = 5750; y <= 5820; y++) {
            LocalDate firstShabbat = firstShabbatOfYear(y);
            int rosh = JEWISH.fromYMD(y, 7, 1).getDayOfWeek();
            int pesach = JEWISH.fromYMD(y, 1, 15).getDayOfWeek();
            YearCheshvanKislevType yearType = JewishCalendarImpl.INSTANCE.getYearType(y);
            for (boolean inIsrael : new boolean[]{false, true}) {
                // iterate exactly the schedule's own length — avoids false empty returns past schedule.size()
                int scheduleSize = ParshiotYearType.forYear(rosh, yearType, pesach).schedule(inIsrael).size();
                for (int week = 0; week < scheduleSize; week++) {
                    LocalDate shabbat = firstShabbat.plusWeeks(week);
                    IDate gregDate = GREG.fromYMD(shabbat.getYear(), shabbat.getMonthValue(), shabbat.getDayOfMonth());
                    if (!Parshiot.getParsha(gregDate, inIsrael).isEmpty()) continue;

                    IDate hDate = JEWISH.convert(gregDate);
                    boolean isHoliday = false;
                    for (JewishSpecialDay sd : JewishSpecialDay.values()) {
                        if (sd.applies(inIsrael) && sd.matches(hDate)
                                && (sd.isYomTov() || sd.isCholHamoed())) {
                            isHoliday = true;
                            break;
                        }
                    }
                    assertTrue("Year " + y + " at " + shabbat
                            + " (" + (inIsrael ? "Israel" : "Diaspora") + "): empty slot is not a holiday",
                            isHoliday);
                }
            }
        }
    }

    /**
     * In Diaspora, Matot-Masei is split if and only if it is a leap year satisfying
     * one of:
     *   (a) SHORT year (Kislev=29) → Rosh Chodesh Tevet is 1 day on Monday, or
     *   (b) non-SHORT year (Kislev=30) → Rosh Chodesh Tevet is 2 days (Kislev 30 + Tevet 1)
     *       falling on Tuesday-Wednesday.
     *
     * In Israel the rule is different for leap years with Saturday Pesach
     * (types F27, N37), so this test covers Diaspora only.
     * From 5 klalim of R. Chaim Mordechai Brecher, printed in the back of Mikraot Gedolot.
     */
    @Test
    public void matotMaseiSplitIffLeapWithSpecialRoshChodeshTevet() {
        for (int y = 5750; y <= 5820; y++) {
            boolean leap = JEWISH.isLeap(y);
            YearCheshvanKislevType yearType = JewishCalendarImpl.INSTANCE.getYearType(y);

            // (a) SHORT leap: Tevet 1 is Monday (dayOfWeek=2)
            boolean condA = leap && yearType == YearCheshvanKislevType.SHORT
                            && JEWISH.fromYMD(y, 10, 1).getDayOfWeek() == 2;
            // (b) non-SHORT leap: Kislev 30 is Tuesday (dayOfWeek=3), making RC Tevet Tue+Wed
            boolean condB = leap && yearType != YearCheshvanKislevType.SHORT
                            && JEWISH.fromYMD(y, 9, 30).getDayOfWeek() == 3;

            boolean splitExpected = condA || condB;

            int rosh   = JEWISH.fromYMD(y, 7,  1).getDayOfWeek();
            int pesach = JEWISH.fromYMD(y, 1, 15).getDayOfWeek();
            List<List<Parsha>> schedule =
                ParshiotYearType.forYear(rosh, yearType, pesach).schedule(false);
            boolean combined = schedule.stream()
                .anyMatch(slot -> slot.contains(Parsha.MATOT) && slot.contains(Parsha.MASEI));

            String ctx = "Year " + y + " Diaspora";
            if (splitExpected) {
                assertFalse(ctx + ": Matot+Masei must be split (condA=" + condA + " condB=" + condB + ")",
                    combined);
            } else {
                assertTrue(ctx + ": Matot+Masei must be combined", combined);
            }
        }
    }

    /**
     * Chukat and Balak are combined (read together) only in Diaspora, and only when
     * Rosh Chodesh Tammuz falls on Tuesday-Wednesday — i.e. Tammuz 1 is Tuesday
     * (getDayOfWeek()==3), which happens exactly when Pesach falls on Thursday.
     *
     * In Israel they are always read separately.
     * From 5 klalim of R. Chaim Mordechai Brecher, printed in the back of Mikraot Gedolot.
     */
    @Test
    public void chukatBalakCombinedOnlyInDiasporaWhenRoshChodeshTammuzIsTuesdayWednesday() {
        for (int y = 5750; y <= 5820; y++) {
            // Sivan always has 30 days, so RC Tammuz = Sivan 30 + Tammuz 1.
            // "Tuesday and Wednesday" refers to Tammuz 1 (Tue) and Tammuz 2 (Wed),
            // which occurs exactly when Pesach = Thursday.
            boolean rcTammuzTueWed = JEWISH.fromYMD(y, 4, 1).getDayOfWeek() == 3;

            int rosh   = JEWISH.fromYMD(y, 7,  1).getDayOfWeek();
            int pesach = JEWISH.fromYMD(y, 1, 15).getDayOfWeek();
            YearCheshvanKislevType yearType = JewishCalendarImpl.INSTANCE.getYearType(y);

            for (boolean inIsrael : new boolean[]{false, true}) {
                List<List<Parsha>> schedule =
                    ParshiotYearType.forYear(rosh, yearType, pesach).schedule(inIsrael);
                boolean combined = schedule.stream()
                    .anyMatch(slot -> slot.contains(Parsha.CHUKAT) && slot.contains(Parsha.BALAK));

                String ctx = "Year " + y + " " + (inIsrael ? "Israel" : "Diaspora");
                boolean expectCombined = !inIsrael && rcTammuzTueWed;

                if (expectCombined) {
                    assertTrue(ctx + ": Chukat+Balak must be combined when RC Tammuz is Tue+Wed", combined);
                } else {
                    assertFalse(ctx + ": Chukat+Balak must be split", combined);
                }
            }
        }
    }

    /**
     * Nitzavim is read alone (not combined with Vayeilech) if and only if the
     * following year's Rosh Hashana falls on Monday or Tuesday.
     * The rule holds for all year types, in both Israel and Diaspora.
     * From 5 klalim of R. Chaim Mordechai Brecher, printed in the back of Mikraot Gedolot.
     */
    @Test
    public void nitzavimIsStandaloneIffNextRoshHashanaIsMondayOrTuesday() {
        for (int y = 5750; y <= 5820; y++) {
            int nextRosh = JEWISH.fromYMD(y + 1, 7, 1).getDayOfWeek(); // 2=Mon, 3=Tue
            boolean expectStandalone = (nextRosh == 2 || nextRosh == 3);

            int rosh   = JEWISH.fromYMD(y, 7,  1).getDayOfWeek();
            int pesach = JEWISH.fromYMD(y, 1, 15).getDayOfWeek();
            YearCheshvanKislevType yearType = JewishCalendarImpl.INSTANCE.getYearType(y);

            for (boolean inIsrael : new boolean[]{false, true}) {
                List<List<Parsha>> schedule =
                    ParshiotYearType.forYear(rosh, yearType, pesach).schedule(inIsrael);
                boolean combined = schedule.stream()
                    .anyMatch(slot -> slot.contains(Parsha.NITZAVIM) && slot.contains(Parsha.VAYEILECH));

                String ctx = "Year " + y + " " + (inIsrael ? "Israel" : "Diaspora")
                    + " (next RH day=" + nextRosh + ")";
                if (expectStandalone) {
                    assertFalse(ctx + ": Nitzavim must be standalone", combined);
                } else {
                    assertTrue(ctx + ": Nitzavim must be combined with Vayeilech", combined);
                }
            }
        }
    }

    // ── exception predicates ──────────────────────────────────────────────────

    // Leap year with Thursday Rosh Hashana (rosh=5) → types K (5,2,3) and L (5,0,1)
    private static boolean isLeapThuRH(int y) {
        if (!JEWISH.isLeap(y)) return false;
        return JEWISH.fromYMD(y, 7, 1).getDayOfWeek() == 5;
    }

    // Leap year with Saturday Pesach (pesach=7) → types H (2,2,7) and J (3,1,7)
    private static boolean isLeapSatPesach(int y) {
        if (!JEWISH.isLeap(y)) return false;
        return JEWISH.fromYMD(y, 1, 15).getDayOfWeek() == 7;
    }
}
