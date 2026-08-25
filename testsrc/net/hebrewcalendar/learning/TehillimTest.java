package net.hebrewcalendar.learning;

import net.hebrewcalendar.ICalendar;
import net.hebrewcalendar.IDate;
import net.hebrewcalendar.JewishCalendar;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class TehillimTest {

    @Test public void day1_isPsalms1to9() {
        Tehillim.Result r = Tehillim.forDayOfMonth(1, 30);
        assertEquals("1-9",         r.portion());
        assertEquals("Psalms 1-9",  r.label());
        assertEquals("תהלים א׳-ט׳", r.labelHe());
    }

    @Test public void day15_isTwoChapters() {
        assertEquals("77-78", Tehillim.forDayOfMonth(15, 30).portion());
    }

    @Test public void day25_isFirstHalf_of119() {
        Tehillim.Result r = Tehillim.forDayOfMonth(25, 30);
        assertEquals("119:1-96",             r.portion());
        assertEquals("תהלים קי״ט:א׳-צ״ו",    r.labelHe());
    }

    @Test public void day26_isSecondHalf_of119() {
        assertEquals("119:97-176", Tehillim.forDayOfMonth(26, 30).portion());
    }

    @Test public void day30_of30dayMonth_isPsalms145to150() {
        assertEquals("145-150", Tehillim.forDayOfMonth(30, 30).portion());
    }

    @Test public void day29_of29dayMonth_combines29and30() {
        Tehillim.Result r = Tehillim.forDayOfMonth(29, 29);
        assertEquals("140-150",              r.portion());
        assertEquals("Psalms 140-150",       r.label());
        assertEquals("תהלים ק״מ-ק״נ",        r.labelHe());
    }

    @Test public void day29_of30dayMonth_isPsalms140to144() {
        assertEquals("140-144", Tehillim.forDayOfMonth(29, 30).portion());
    }

    @Test public void dayOutOfRange_throws() {
        try { Tehillim.forDayOfMonth(0,  30); fail(); } catch (IllegalArgumentException e) {}
        try { Tehillim.forDayOfMonth(31, 30); fail(); } catch (IllegalArgumentException e) {}
        try { Tehillim.forDayOfMonth(30, 29); fail(); } catch (IllegalArgumentException e) {}
    }

    // ── Integration with Hebrew-date lookup ────────────────────────────

    @Test public void forDate_usesHebrewDayOfMonth() {
        // 2026-08-23 is 10 Elul 5786 (Elul is always 29 days).
        Tehillim.Result r = Tehillim.forDate(LocalDate.of(2026, 8, 23));
        IDate<JewishCalendar> jd = ICalendar.JEWISH.convert(
            ICalendar.GREGORIAN.fromYMD(2026, 8, 23));
        assertEquals(6,  jd.getMonth());  // Elul
        assertEquals(10, jd.getDay());
        assertEquals("55-59", r.portion());
    }

    @Test public void forDate_combinesOnLastDayOfShortMonth() {
        // 29 Iyar 5786 = 2026-05-16. Iyar is always 29 days.
        Tehillim.Result r = Tehillim.forDate(LocalDate.of(2026, 5, 16));
        IDate<JewishCalendar> jd = ICalendar.JEWISH.convert(
            ICalendar.GREGORIAN.fromYMD(2026, 5, 16));
        assertEquals(2,  jd.getMonth());  // Iyar
        assertEquals(29, jd.getDay());
        assertEquals("140-150", r.portion());
    }

    @Test public void forDate_dayThirty_onLongMonth() {
        // 30 Tishrei 5787 = 2026-10-11 (Tishrei is always 30 days).
        Tehillim.Result r = Tehillim.forDate(LocalDate.of(2026, 10, 11));
        IDate<JewishCalendar> jd = ICalendar.JEWISH.convert(
            ICalendar.GREGORIAN.fromYMD(2026, 10, 11));
        assertEquals(7,  jd.getMonth());  // Tishrei
        assertEquals(30, jd.getDay());
        assertEquals("145-150", r.portion());
    }
}
