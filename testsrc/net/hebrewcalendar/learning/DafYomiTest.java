package net.hebrewcalendar.learning;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * DafYomi is validated against a fixture of 38 dates whose expected
 * (tractate, daf) values were sampled from sefaria.org's public
 * calendars API in the current cycle (Cycle 14, 2020–2027).
 *
 * The algorithm itself is a public-domain port of Bob Newell's 1998
 * daf.el (also used by hebcal) with the corrected Tamid/Middot naming;
 * pre-1975 cycles are inherited from that port and not independently
 * regressed here since sefaria.org rate-limits bulk historical queries.
 */
public class DafYomiTest {

    // ── Boundary / cycle tests ──────────────────────────────────────────────

    @Test public void cycle14_day1_is_berakhot2() {
        DafYomi.Result r = DafYomi.forDate(LocalDate.of(2020, 1, 5));
        assertEquals("Berakhot", r.tractate());
        assertEquals(2, r.daf());
        assertEquals(14, r.cycle());
    }

    @Test public void cycle13_last_day_is_niddah73a() {
        DafYomi.Result r = DafYomi.forDate(LocalDate.of(2020, 1, 4));
        assertEquals("Niddah", r.tractate());
        assertEquals(73, r.daf());
        assertEquals(13, r.cycle());
        assertTrue("Niddah last daf is amud A only", r.amudA());
        assertEquals("Niddah 73a", r.label());
    }

    @Test public void before_cycle1_returns_null() {
        assertNull(DafYomi.forDate(LocalDate.of(1923, 9, 10)));
    }

    @Test public void cycle1_day1_is_berakhot2() {
        DafYomi.Result r = DafYomi.forDate(LocalDate.of(1923, 9, 11));
        assertEquals("Berakhot", r.tractate());
        assertEquals(2, r.daf());
        assertEquals(1, r.cycle());
    }

    @Test public void cycle8_boundary_new_style() {
        // 24 June 1975 is day 1 of cycle 8 (new-style: Shekalim as 22 blatt)
        DafYomi.Result r = DafYomi.forDate(LocalDate.of(1975, 6, 24));
        assertEquals("Berakhot", r.tractate());
        assertEquals(2, r.daf());
        assertEquals(8, r.cycle());
    }

    @Test public void menachot_ends_amud_a() {
        // From the reference fixture: 2026-05-01 → Menachot 110a
        DafYomi.Result r = DafYomi.forDate(LocalDate.of(2026, 5, 1));
        assertEquals("Menachot 110a", r.label());
        assertTrue(r.amudA());
    }

    // ── Hebrew labels ────────────────────────────────────────────────────

    @Test public void hebrewLabel_currentDay() {
        DafYomi.Result r = DafYomi.forDate(LocalDate.of(2026, 8, 23));
        assertEquals("Chullin 115", r.label());
        assertEquals("חולין קט״ו",  r.labelHe());
    }

    @Test public void hebrewLabel_matchesSefaria() throws Exception {
        // Bulk cross-check Hebrew labels against sefaria.org display values
        // captured in learning_refs_he.json.
        java.util.List<HebrewRef> refs = loadHebrewFixture();
        assertTrue("Hebrew fixture loaded", refs.size() >= 100);
        int mismatches = 0;
        StringBuilder err = new StringBuilder();
        for (HebrewRef ref : refs) {
            if (ref.daf_he == null) continue;
            DafYomi.Result r = DafYomi.forDate(ref.date);
            String got = r == null ? "(null)" : r.labelHe();
            if (!got.equals(ref.daf_he)) {
                mismatches++;
                if (mismatches <= 10)
                    err.append("  ").append(ref.date)
                       .append(": exp ").append(ref.daf_he)
                       .append(", got ").append(got).append('\n');
            }
        }
        assertEquals("Hebrew Daf Yomi mismatches:\n" + err, 0, mismatches);
    }

    private static final class HebrewRef {
        final LocalDate date; final String daf_he;
        HebrewRef(LocalDate d, String daf_he) { this.date = d; this.daf_he = daf_he; }
    }

    private static java.util.List<HebrewRef> loadHebrewFixture() throws Exception {
        StringBuilder sb = new StringBuilder();
        try (var in = DafYomiTest.class.getResourceAsStream("learning_refs_he.json")) {
            assertNotNull("learning_refs_he.json on classpath", in);
            try (var br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                String line; while ((line = br.readLine()) != null) sb.append(line).append('\n');
            }
        }
        // Match "date":"YYYY-MM-DD"  and optional  "daf_he":"..."  within each object.
        Pattern p = Pattern.compile(
            "\"date\"\\s*:\\s*\"(\\d{4}-\\d{2}-\\d{2})\"[\\s\\S]*?"
          + "(?:\"daf_he\"\\s*:\\s*\"([^\"]+)\")?[\\s\\S]*?\\}");
        Matcher m = p.matcher(sb);
        java.util.List<HebrewRef> out = new java.util.ArrayList<>();
        while (m.find()) {
            out.add(new HebrewRef(LocalDate.parse(m.group(1)), m.group(2)));
        }
        return out;
    }

    // ── Bulk cross-check against Sefaria fixture ────────────────────────────

    @Test public void matches_sefaria_fixture() throws Exception {
        List<Ref> refs = loadFixture();
        assertTrue("fixture loaded", refs.size() >= 30);
        int checked = 0, mismatches = 0;
        StringBuilder err = new StringBuilder();
        for (Ref ref : refs) {
            DafYomi.Result r = DafYomi.forDate(ref.date);
            String actual = r == null ? "(null)" : r.label();
            if (!actual.equals(ref.expected)) {
                mismatches++;
                err.append(String.format("  %s: expected %s, got %s%n",
                        ref.date, ref.expected, actual));
            }
            checked++;
        }
        assertEquals("Daf Yomi mismatches:\n" + err, 0, mismatches);
        // Sanity: fixture actually got exercised.
        assertTrue(checked >= 30);
    }

    // ── fixture helpers ─────────────────────────────────────────────────────

    private static final class Ref {
        final LocalDate date; final String expected;
        Ref(LocalDate date, String expected) { this.date = date; this.expected = expected; }
    }

    /** Minimal JSON reader — the fixture is a flat array of `{date,daf}` pairs. */
    private static List<Ref> loadFixture() throws Exception {
        StringBuilder sb = new StringBuilder();
        try (java.io.InputStream in = DafYomiTest.class.getResourceAsStream("dafyomi_refs.json")) {
            assertNotNull("dafyomi_refs.json on classpath", in);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                String line; while ((line = br.readLine()) != null) sb.append(line).append('\n');
            }
        }
        // Extract every "date":"YYYY-MM-DD" ... "daf":"..."  pair.
        Pattern p = Pattern.compile(
            "\\{\\s*\"date\"\\s*:\\s*\"(\\d{4}-\\d{2}-\\d{2})\"\\s*,\\s*"
          + "\"daf\"\\s*:\\s*\"([^\"]+)\"\\s*\\}");
        Matcher m = p.matcher(sb);
        List<Ref> out = new ArrayList<>();
        while (m.find()) {
            out.add(new Ref(LocalDate.parse(m.group(1)), m.group(2)));
        }
        return out;
    }
}
