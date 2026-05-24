package net.hebrewcalendar.impl;

import net.hebrewcalendar.ICalendar;
import net.hebrewcalendar.IDate;
import net.hebrewcalendar.JewishCalendar;
import net.hebrewcalendar.Parsha;
import org.junit.Test;

import java.time.LocalDate;
import java.time.DayOfWeek;
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
