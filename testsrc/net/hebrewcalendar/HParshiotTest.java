package net.hebrewcalendar;

import org.junit.Test;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.*;

import static org.junit.Assert.*;

public class HParshiotTest {

    private static final HCalendar       GREG   = HCalendar.GREGORIAN;
    private static final HJewishCalendar HEBREW = HCalendar.HEBREW;

    // ── year-type key ─────────────────────────────────────────────────────────

    private static String yearTypeKey(int hebrewYear) {
        HDate rosh   = HEBREW.fromYMD(hebrewYear, 7, 1);
        HDate pesach = HEBREW.fromYMD(hebrewYear, 1, 15);
        int r   = rosh.getDayOfWeek();
        int len = HEBREW.getYearType(hebrewYear).ordinal();
        int p   = pesach.getDayOfWeek();
        return r + "," + len + "," + p;
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private static LocalDate shabbatBeforeShavuot(int hebrewYear) {
        HDate shavuot = HEBREW.fromYMD(hebrewYear, 3, 6);
        HDate g = GREG.convert(shavuot);
        LocalDate ld = LocalDate.of(g.getYear(), g.getMonth(), g.getDay());
        LocalDate shabbat = ld.minusDays(1);
        while (shabbat.getDayOfWeek() != DayOfWeek.SATURDAY) shabbat = shabbat.minusDays(1);
        return shabbat;
    }

    private static Parsha[] getParsha(LocalDate shabbat, boolean inIsrael) {
        HDate d = GREG.fromYMD(shabbat.getYear(), shabbat.getMonthValue(), shabbat.getDayOfMonth());
        return HParshiot.getParsha(d, inIsrael);
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
            HDate simchatTorah = HEBREW.fromYMD(y, 7, 22);
            HDate g = GREG.convert(simchatTorah);
            LocalDate ld = LocalDate.of(g.getYear(), g.getMonth(), g.getDay());
            LocalDate shabbat = ld.plusDays(1);
            while (shabbat.getDayOfWeek() != DayOfWeek.SATURDAY) shabbat = shabbat.plusDays(1);

            Parsha[] parsha = getParsha(shabbat, false);
            assertNotNull("Year " + y + ": expected Bereishit but got null (Yom Tov)", parsha);
            assertEquals("Year " + y + ": first Shabbat after Simchat Torah",
                Parsha.BEREISHIT, parsha[0]);
        }
    }

    @Test
    public void shabbatBeforePesachIsTzavOrMetzora() {
        for (int y = 5750; y <= 5820; y++) {
            HDate pesach = HEBREW.fromYMD(y, 1, 15);
            HDate g = GREG.convert(pesach);
            LocalDate ld = LocalDate.of(g.getYear(), g.getMonth(), g.getDay());
            LocalDate shabbat = ld.minusDays(1);
            while (shabbat.getDayOfWeek() != DayOfWeek.SATURDAY) shabbat = shabbat.minusDays(1);

            Parsha[] parsha = getParsha(shabbat, false);
            assertNotNull("Year " + y + ": Shabbat before Pesach should not be Yom Tov", parsha);

            boolean leap = HEBREW.isLeap(y);
            Parsha expected = !leap       ? Parsha.TZAV
                            : isLeapThuRH(y) ? Parsha.ACHAREI_MOT
                            : Parsha.METZORA;
            assertEquals("Year " + y + " (leap=" + leap + "): Shabbat before Pesach",
                expected, parsha[0]);
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
            Parsha[] parsha = getParsha(shabbat, false);
            assertNotNull("Year " + y + ": Shabbat before Shavuot should not be Yom Tov", parsha);
            assertEquals("Year " + y + ": Shabbat before Shavuot", Parsha.BAMIDBAR, parsha[0]);
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

            Parsha[] diaspora = getParsha(shabbat, false);
            Parsha[] israel   = getParsha(shabbat, true);
            assertNotNull("Year " + y + ": diaspora should not be null", diaspora);
            assertNotNull("Year " + y + ": Israel should not be null", israel);
            assertEquals("Year " + y + " diaspora: Shabbat before Shavuot", Parsha.NASO, diaspora[0]);
            assertEquals("Year " + y + " Israel:   Shabbat before Shavuot", Parsha.NASO, israel[0]);
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

            Parsha[] diaspora = getParsha(shabbat, false);
            Parsha[] israel   = getParsha(shabbat, true);
            assertNotNull("Year " + y + ": diaspora should not be null", diaspora);
            assertNotNull("Year " + y + ": Israel should not be null", israel);
            assertEquals("Year " + y + " diaspora: Shabbat before Shavuot", Parsha.BAMIDBAR, diaspora[0]);
            assertEquals("Year " + y + " Israel:   Shabbat before Shavuot", Parsha.NASO, israel[0]);
        }
        assertTrue("No leap-Saturday-Pesach years found in range", foundAtLeastOne);
    }

    @Test
    public void may30_2026() {
        HDate date = GREG.fromYMD(2026, 5, 30);
        assertEquals("May 30 2026 should be Saturday", 7, date.getDayOfWeek());

        Parsha[] diaspora = HParshiot.getParsha(date, false);
        assertNotNull("Diaspora: expected a parsha on May 30 2026", diaspora);
        assertEquals("Diaspora: May 30 2026 should be Naso", Parsha.NASO, diaspora[0]);

        Parsha[] israel = HParshiot.getParsha(date, true);
        assertNotNull("Israel: expected a parsha on May 30 2026", israel);
        assertEquals("Israel: May 30 2026 should be Behaalotecha", Parsha.BEHAALOTECHA, israel[0]);
    }

    // ── exception predicates ──────────────────────────────────────────────────

    // Leap year with Thursday Rosh Hashana (rosh=5) → types K (5,2,3) and L (5,0,1)
    private static boolean isLeapThuRH(int y) {
        if (!HEBREW.isLeap(y)) return false;
        return HEBREW.fromYMD(y, 7, 1).getDayOfWeek() == 5;
    }

    // Leap year with Saturday Pesach (pesach=7) → types H (2,2,7) and J (3,1,7)
    private static boolean isLeapSatPesach(int y) {
        if (!HEBREW.isLeap(y)) return false;
        return HEBREW.fromYMD(y, 1, 15).getDayOfWeek() == 7;
    }
}
