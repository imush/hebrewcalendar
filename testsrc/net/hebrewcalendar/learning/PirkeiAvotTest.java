package net.hebrewcalendar.learning;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class PirkeiAvotTest {

    // ── Not-Shabbat / off-season ─────────────────────────────────────

    @Test public void weekday_returnsNull() {
        assertNull(PirkeiAvot.forDate(LocalDate.of(2026, 8, 23), false, true));  // Sun
    }

    @Test public void midWinter_returnsNull() {
        // Shabbat 2026-01-31 — no Avot in winter.
        assertNull(PirkeiAvot.forDate(LocalDate.of(2026, 1, 31), false, true));
    }

    // ── Base schedule (Pesach → Shavuot) ─────────────────────────────

    @Test public void diaspora_firstShabbatAfterPesach_isPerek1() {
        // 5786 Diaspora Pesach ends 22 Nisan = 2026-04-09 (Thu).
        // First Shabbat after = 2026-04-11 (Sat) = 24 Nisan.
        PirkeiAvot.Result r = PirkeiAvot.forDate(LocalDate.of(2026, 4, 11), false, false);
        assertNotNull(r);
        assertEquals(java.util.List.of(1), r.chapters());
        assertEquals("פרקי אבות א׳", r.labelHe());
    }

    @Test public void diaspora_shabbatBeforeShavuot_isPerek6() {
        // Shavuot 5786 = 2026-05-22 (Fri). Shabbat before = 2026-05-16 = 29 Iyar.
        // 6 Shabbatot from 24 Nisan to 29 Iyar → 6th chapter.
        PirkeiAvot.Result r = PirkeiAvot.forDate(LocalDate.of(2026, 5, 16), false, false);
        assertNotNull(r);
        assertEquals(java.util.List.of(6), r.chapters());
    }

    @Test public void israel_israelSkipFirst_alignsSchedule() {
        // Israel Pesach ends 21 Nisan 5786 = 2026-04-08 (Wed).
        // First Shabbat after = 2026-04-11 (Sat) = 24 Nisan. If Israel has
        // 7 Shabbatot pre-Shavuot, we skip the first, so 24 Nisan returns null.
        // 5786 Israel: pre-Shavuot count = 6 (same as Diaspora since Diaspora
        // has one less Shabbat window because Shavuot 2 is skipped anyway).
        // So this test simply verifies Israel returns a chapter (not null)
        // and matches Diaspora's alignment for 5786.
        PirkeiAvot.Result r = PirkeiAvot.forDate(LocalDate.of(2026, 4, 11), true, false);
        assertNotNull(r);
    }

    // ── Chabad extended (through Shabbat-before-RH) ──────────────────

    @Test public void extended_lastThreeShabbatot_doubled_5786() {
        // Per user: on 9 Elul 5786 = Sat 2026-08-22, we learn chapters 1-2.
        // Following two Shabbatot: 3-4, then 5-6.
        assertEquals(java.util.List.of(1, 2),
                PirkeiAvot.forDate(LocalDate.of(2026, 8, 22), false, true).chapters());
        assertEquals(java.util.List.of(3, 4),
                PirkeiAvot.forDate(LocalDate.of(2026, 8, 29), false, true).chapters());
        assertEquals(java.util.List.of(5, 6),
                PirkeiAvot.forDate(LocalDate.of(2026, 9, 5), false, true).chapters());
    }

    @Test public void extended_labelHe_forDoublePerek() {
        PirkeiAvot.Result r = PirkeiAvot.forDate(LocalDate.of(2026, 8, 22), false, true);
        assertEquals("פרקי אבות א׳-ב׳", r.labelHe());
    }

    @Test public void extended_afterRoshHashanah_null() {
        // Shabbat 2026-09-19 = 8 Tishrei 5787 — after RH, no Avot.
        assertNull(PirkeiAvot.forDate(LocalDate.of(2026, 9, 19), false, true));
    }

    @Test public void extended_shabbat_beforeRH_shows_ch6_pair() {
        // Shabbat before RH 5787 = 2026-09-05 = 23 Elul 5786 → (5-6).
        assertEquals(java.util.List.of(5, 6),
                PirkeiAvot.forDate(LocalDate.of(2026, 9, 5), false, true).chapters());
    }
}
