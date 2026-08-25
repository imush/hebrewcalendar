package net.hebrewcalendar.learning;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class TehillimElulTest {

    @Test public void firstDay_isOneThroughThree() {
        // 1 Elul 5786 = 2026-08-14 (Fri).
        assertEquals("א׳-ג׳", Tehillim.elulSupplementHe(LocalDate.of(2026, 8, 14)));
    }

    @Test public void twentyNinthElul_isOneFortyThreeToOneFortyFive() {
        // 29 Elul 5786 = 2026-09-11 → chapters (29-1)*3+1 .. 29*3 = 85..87.
        assertEquals("פ״ה-פ״ז", Tehillim.elulSupplementHe(LocalDate.of(2026, 9, 11)));
    }

    @Test public void firstTishrei_continues_ninetyToNinetyTwo() {
        // 1 Tishrei 5787 = 2026-09-12 → day 30 of run → 88..90.
        assertEquals("פ״ח-צ׳", Tehillim.elulSupplementHe(LocalDate.of(2026, 9, 12)));
    }

    @Test public void erevYomKippur_lastRun() {
        // 9 Tishrei 5787 = 2026-09-20 → day 38 of run → 112..114.
        assertEquals("קי״ב-קי״ד", Tehillim.elulSupplementHe(LocalDate.of(2026, 9, 20)));
    }

    @Test public void yomKippur_noSupplement() {
        // 10 Tishrei 5787 = 2026-09-21 → after erev YK, no daily supplement.
        assertNull(Tehillim.elulSupplementHe(LocalDate.of(2026, 9, 21)));
    }

    @Test public void mid_Av_noSupplement() {
        assertNull(Tehillim.elulSupplementHe(LocalDate.of(2026, 7, 15)));
    }
}
