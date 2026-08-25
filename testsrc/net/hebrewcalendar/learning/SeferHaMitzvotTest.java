package net.hebrewcalendar.learning;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

/**
 * SeferHaMitzvot is validated by:
 *
 *  1. Boundary behavior around the 29 Apr 1984 epoch.
 *  2. Day-13 spot check documented in the source of
 *     {@code hebcal-learning/SeferHaMitzvotEvent.ts}: 29 Adar II 5784 =
 *     8 Apr 2024 = day 13 = {@code "N10, N47, ..., N23, N24"}.
 *  3. Cycle-rollover invariant (day 1 = day 340 = day 679).
 *  4. Label-expansion logic (raw compact form ↔ human phrase).
 *
 * The 339-entry cycle table itself is verbatim from hebcal-learning
 * (MIT-licensed), which is the canonical machine-readable source for
 * Chabad's Sefer HaMitzvot Yomi schedule.
 */
public class SeferHaMitzvotTest {

    // ── Boundary ────────────────────────────────────────────────────────────

    @Test public void beforeEpoch_returnsNull() {
        assertNull(SeferHaMitzvot.forDate(LocalDate.of(1984, 4, 28)));
    }

    @Test public void day1_isRambamIntroduction() {
        SeferHaMitzvot.Result r = SeferHaMitzvot.forDate(LocalDate.of(1984, 4, 29));
        assertEquals(1, r.dayInCycle());
        assertTrue("day 1 should be the Introduction — got: " + r.raw(),
                   r.raw().startsWith("Maimonides"));
        // Prose entries pass through unchanged.
        assertEquals(r.raw(), r.label());
    }

    // ── Documented example ─────────────────────────────────────────────────

    @Test public void day13_matchesHebcalDocExample() {
        // 29 Adar II 5784 = 8 Apr 2024 (from hebcal's SeferHaMitzvotEvent.ts).
        SeferHaMitzvot.Result r = SeferHaMitzvot.forDate(LocalDate.of(2024, 4, 8));
        assertEquals(13, r.dayInCycle());
        assertEquals("N10, N47, N60, N6, N5, N2, N3, N4, N15, P186, N23, N24",
                     r.raw());
    }

    // ── Cycle invariants ────────────────────────────────────────────────────

    @Test public void cycleRolloverIsExact() {
        // day 340 == day 1; day 679 == day 1.
        LocalDate epoch = LocalDate.of(1984, 4, 29);
        SeferHaMitzvot.Result r0 = SeferHaMitzvot.forDate(epoch);
        SeferHaMitzvot.Result r1 = SeferHaMitzvot.forDate(epoch.plusDays(339));
        SeferHaMitzvot.Result r2 = SeferHaMitzvot.forDate(epoch.plusDays(2 * 339));
        assertEquals(r0.raw(), r1.raw());
        assertEquals(r0.raw(), r2.raw());
        assertEquals(1, r0.dayInCycle());
        assertEquals(1, r1.dayInCycle());
        assertEquals(1, r2.dayInCycle());
    }

    @Test public void everyCycleDayIsReachable() {
        // Sweep one full cycle and confirm dayInCycle covers 1..339 exactly once.
        LocalDate epoch = LocalDate.of(1984, 4, 29);
        java.util.BitSet seen = new java.util.BitSet(340);
        for (int i = 0; i < 339; i++) {
            SeferHaMitzvot.Result r = SeferHaMitzvot.forDate(epoch.plusDays(i));
            assertNotNull(r);
            assertFalse("day " + r.dayInCycle() + " repeated", seen.get(r.dayInCycle()));
            seen.set(r.dayInCycle());
        }
        assertEquals(339, seen.cardinality());
    }

    // ── Label formatting ───────────────────────────────────────────────────

    @Test public void label_groupsPositiveThenNegative() {
        // Synthesize a Result to isolate the label logic.
        // Day 336: "P173, N362, N364, N363, N365"
        SeferHaMitzvot.Result r = SeferHaMitzvot.forDate(
                LocalDate.of(1984, 4, 29).plusDays(335));
        assertEquals("P173, N362, N364, N363, N365", r.raw());
        assertEquals("Positive Commandment 173; "
                   + "Negative Commandments 362, 364, 363, 365", r.label());
    }

    @Test public void label_singleCommandment() {
        // Day 20: "P73, P10"
        SeferHaMitzvot.Result r = SeferHaMitzvot.forDate(
                LocalDate.of(1984, 4, 29).plusDays(19));
        assertEquals("P73, P10", r.raw());
        assertEquals("Positive Commandments 73, 10", r.label());
    }

    @Test public void label_prosePassesThrough() {
        // Day 1 = "Maimonides' Introduction..." — pure prose.
        SeferHaMitzvot.Result r = SeferHaMitzvot.forDate(LocalDate.of(1984, 4, 29));
        assertEquals(r.raw(), r.label());
        // Day 35 mixes prose and codes ("P215, Nusach HaTefila"); return raw.
        SeferHaMitzvot.Result r2 = SeferHaMitzvot.forDate(
                LocalDate.of(1984, 4, 29).plusDays(34));
        assertTrue(r2.raw().contains("Nusach HaTefila"));
        assertEquals(r2.raw(), r2.label());
    }

    // ── Hebrew labels ─────────────────────────────────────────────────────

    @Test public void labelHe_groupsMixed() {
        // Day 336: "P173, N362, N364, N363, N365"
        SeferHaMitzvot.Result r = SeferHaMitzvot.forDate(
                LocalDate.of(1984, 4, 29).plusDays(335));
        assertEquals("מצות עשה קע״ג; מצות לא תעשה שס״ב, שס״ד, שס״ג, שס״ה",
                r.labelHe());
    }

    @Test public void labelHe_singleGroup() {
        // Day 20: "P73, P10" → "מצות עשה ע״ג, י׳"
        SeferHaMitzvot.Result r = SeferHaMitzvot.forDate(
                LocalDate.of(1984, 4, 29).plusDays(19));
        assertEquals("מצות עשה ע״ג, י׳", r.labelHe());
    }

    @Test public void labelHe_prosePassesThrough() {
        SeferHaMitzvot.Result r = SeferHaMitzvot.forDate(LocalDate.of(1984, 4, 29));
        assertEquals(r.raw(), r.labelHe());
    }

}
