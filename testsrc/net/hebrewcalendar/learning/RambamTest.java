package net.hebrewcalendar.learning;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * Rambam Yomi is validated against a fixture of 66 dates whose expected
 * 1-chapter and 3-chapter readings were sampled from sefaria.org's
 * {@code /api/calendars} endpoint, spanning 2020-01-01 through 2027-06-23.
 *
 * The 3-chapter output goes through {@link Rambam#collapse}, matching
 * Sefaria's convention (adjacent same-halacha readings joined as a range).
 */
public class RambamTest {

    // ── Boundary / structural sanity ─────────────────────────────────────

    @Test public void beforeEpoch_returnsNull() {
        assertNull(Rambam.oneChapter(LocalDate.of(1984, 4, 28)));
        assertNull(Rambam.threeChapters(LocalDate.of(1984, 4, 28)));
    }

    @Test public void day1_isTransmissionOfOralLaw() {
        Rambam.Reading r = Rambam.oneChapter(LocalDate.of(1984, 4, 29));
        assertEquals("Transmission of the Oral Law", r.name());
        assertEquals("1-21", r.perek());  // introductory-verse range
    }

    @Test public void threeChapters_dayOne_returnsExactlyThree() {
        List<Rambam.Reading> rs = Rambam.threeChapters(LocalDate.of(1984, 4, 29));
        assertEquals(3, rs.size());
        assertEquals("Transmission of the Oral Law", rs.get(0).name());
        assertEquals("1-21", rs.get(0).perek());
    }

    @Test public void oneChapterCycleRollover() {
        // day 1017 (0-indexed 1017) == day 0 (0-indexed 0)
        LocalDate epoch = LocalDate.of(1984, 4, 29);
        Rambam.Reading a = Rambam.oneChapter(epoch);
        Rambam.Reading b = Rambam.oneChapter(epoch.plusDays(1017));
        assertEquals(a.name(),  b.name());
        assertEquals(a.perek(), b.perek());
    }

    @Test public void threeChapterCycleRollover() {
        LocalDate epoch = LocalDate.of(1984, 4, 29);
        List<Rambam.Reading> a = Rambam.threeChapters(epoch);
        List<Rambam.Reading> b = Rambam.threeChapters(epoch.plusDays(339));
        assertEquals(a.get(0).name(),  b.get(0).name());
        assertEquals(a.get(2).perek(), b.get(2).perek());
    }

    @Test public void collapseGroupsAdjacent() {
        List<Rambam.Reading> in = List.of(
                new Rambam.Reading("Sabbath", "הלכות שבת", "5"),
                new Rambam.Reading("Sabbath", "הלכות שבת", "6"),
                new Rambam.Reading("Sabbath", "הלכות שבת", "7"));
        assertEquals(List.of("Sabbath 5-7"), Rambam.collapse(in));
    }

    @Test public void collapseSplitsAtHalachaBoundary() {
        List<Rambam.Reading> in = List.of(
                new Rambam.Reading("A", "א", "3"),
                new Rambam.Reading("A", "א", "4"),
                new Rambam.Reading("B", "ב", "1"));
        assertEquals(List.of("A 3-4", "B 1"), Rambam.collapse(in));
    }

    // ── Bulk cross-check against Sefaria ─────────────────────────────────

    @Test public void matchesSefaria_oneChapter() throws Exception {
        List<Ref> refs = loadFixture();
        int mismatches = 0;
        StringBuilder err = new StringBuilder();
        for (Ref ref : refs) {
            if (ref.r1 == null) continue;
            String got = Rambam.oneChapter(ref.date).label();
            if (!got.equals(ref.r1)) {
                mismatches++;
                err.append("  ").append(ref.date).append(": exp ").append(ref.r1)
                   .append(", got ").append(got).append('\n');
            }
        }
        assertEquals("1-chapter mismatches:\n" + err, 0, mismatches);
    }

    @Test public void hebrewLabels_matchSefaria() throws Exception {
        // Cross-check Hebrew labels for both 1-chapter and 3-chapter rows.
        List<HeRef> refs = loadHebrewFixture();
        int miss1 = 0, miss3 = 0;
        StringBuilder err = new StringBuilder();
        for (HeRef ref : refs) {
            if (ref.r1_he != null) {
                String got = Rambam.oneChapter(ref.date).labelHe();
                if (!got.equals(ref.r1_he)) {
                    miss1++;
                    if (miss1 <= 8) err.append("  1ch ").append(ref.date)
                        .append(": exp ").append(ref.r1_he).append(", got ").append(got).append('\n');
                }
            }
            if (ref.r3_he != null && !ref.r3_he.isEmpty()) {
                // Collapse in Hebrew: same shape as English collapse.
                List<Rambam.Reading> rs = Rambam.threeChapters(ref.date);
                List<String> got = collapseHe(rs);
                if (!got.equals(ref.r3_he)) {
                    miss3++;
                    if (miss3 <= 8) err.append("  3ch ").append(ref.date)
                        .append(": exp ").append(ref.r3_he)
                        .append(", got ").append(got).append('\n');
                }
            }
        }
        assertEquals("Hebrew Rambam mismatches:\n" + err, 0, miss1 + miss3);
    }

    /** Hebrew-side analogue of {@link Rambam#collapse}. */
    private static List<String> collapseHe(List<Rambam.Reading> rs) {
        List<String> out = new ArrayList<>();
        int i = 0;
        while (i < rs.size()) {
            Rambam.Reading start = rs.get(i);
            int j = i;
            while (j + 1 < rs.size() && rs.get(j + 1).name().equals(start.name())) j++;
            if (j == i) {
                out.add(start.labelHe());
            } else {
                String startPerek = firstToken(start.perek());
                String endPerek   = lastToken(rs.get(j).perek());
                out.add(start.nameHe() + " "
                        + net.hebrewcalendar.learning.Gematria.of(Integer.parseInt(startPerek))
                        + "-"
                        + net.hebrewcalendar.learning.Gematria.of(Integer.parseInt(endPerek)));
            }
            i = j + 1;
        }
        return out;
    }
    private static String firstToken(String s) { int d=s.indexOf('-'); return d<0?s:s.substring(0,d); }
    private static String lastToken(String s)  { int d=s.lastIndexOf('-'); return d<0?s:s.substring(d+1); }

    private static final class HeRef {
        final LocalDate date; final String r1_he; final List<String> r3_he;
        HeRef(LocalDate d, String r1_he, List<String> r3_he) {
            this.date = d; this.r1_he = r1_he; this.r3_he = r3_he;
        }
    }

    private static List<HeRef> loadHebrewFixture() throws Exception {
        StringBuilder sb = new StringBuilder();
        try (InputStream in = RambamTest.class.getResourceAsStream("learning_refs_he.json")) {
            assertNotNull("learning_refs_he.json on classpath", in);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                String line; while ((line = br.readLine()) != null) sb.append(line).append('\n');
            }
        }
        // Parse — each object may contain r1_he and/or r3_he:[...] arrays.
        List<HeRef> refs = new ArrayList<>();
        Pattern obj = Pattern.compile("\\{[^{}]*(?:\\[[^\\]]*\\])?[^{}]*(?:\\[[^\\]]*\\])?[^{}]*\\}");
        Pattern dp  = Pattern.compile("\"date\"\\s*:\\s*\"(\\d{4}-\\d{2}-\\d{2})\"");
        Pattern r1p = Pattern.compile("\"r1_he\"\\s*:\\s*\"([^\"]+)\"");
        Pattern r3p = Pattern.compile("\"r3_he\"\\s*:\\s*\\[([^\\]]*)\\]");
        Pattern strP= Pattern.compile("\"([^\"]+)\"");
        Matcher om = obj.matcher(sb);
        while (om.find()) {
            String o = om.group();
            Matcher dm = dp.matcher(o); if (!dm.find()) continue;
            LocalDate d = LocalDate.parse(dm.group(1));
            Matcher r1m = r1p.matcher(o); String r1 = r1m.find() ? r1m.group(1) : null;
            Matcher r3m = r3p.matcher(o); List<String> r3 = null;
            if (r3m.find()) {
                r3 = new ArrayList<>();
                Matcher s = strP.matcher(r3m.group(1));
                while (s.find()) r3.add(s.group(1));
            }
            refs.add(new HeRef(d, r1, r3));
        }
        return refs;
    }

    @Test public void matchesSefaria_threeChapters() throws Exception {
        List<Ref> refs = loadFixture();
        int mismatches = 0;
        StringBuilder err = new StringBuilder();
        for (Ref ref : refs) {
            if (ref.r3 == null || ref.r3.isEmpty()) continue;
            List<String> got = Rambam.collapse(Rambam.threeChapters(ref.date));
            if (!got.equals(ref.r3)) {
                mismatches++;
                err.append("  ").append(ref.date).append(":\n    exp ").append(ref.r3)
                   .append("\n    got ").append(got).append('\n');
            }
        }
        assertEquals("3-chapter mismatches:\n" + err, 0, mismatches);
    }

    // ── fixture helpers ─────────────────────────────────────────────────

    private static final class Ref {
        final LocalDate date; final String r1; final List<String> r3;
        Ref(LocalDate d, String r1, List<String> r3) { this.date=d; this.r1=r1; this.r3=r3; }
    }

    private static List<Ref> loadFixture() throws Exception {
        StringBuilder sb = new StringBuilder();
        try (InputStream in = RambamTest.class.getResourceAsStream("rambam_refs.json")) {
            assertNotNull("rambam_refs.json on classpath", in);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                String line; while ((line = br.readLine()) != null) sb.append(line).append('\n');
            }
        }
        // Very small JSON parser: each entry has "date", optional "r1", optional "r3":[...].
        // We match the outermost {...} groups then parse fields within.
        List<Ref> refs = new ArrayList<>();
        Pattern obj = Pattern.compile("\\{[^{}]*(?:\\[[^\\]]*\\])?[^{}]*\\}");
        Matcher om = obj.matcher(sb);
        Pattern datP = Pattern.compile("\"date\"\\s*:\\s*\"(\\d{4}-\\d{2}-\\d{2})\"");
        Pattern r1P  = Pattern.compile("\"r1\"\\s*:\\s*\"([^\"]+)\"");
        Pattern r3P  = Pattern.compile("\"r3\"\\s*:\\s*\\[([^\\]]*)\\]");
        Pattern strP = Pattern.compile("\"([^\"]+)\"");
        while (om.find()) {
            String o = om.group();
            Matcher dm = datP.matcher(o); if (!dm.find()) continue;
            LocalDate d = LocalDate.parse(dm.group(1));
            Matcher r1m = r1P.matcher(o);  String r1 = r1m.find() ? r1m.group(1) : null;
            Matcher r3m = r3P.matcher(o);  List<String> r3 = null;
            if (r3m.find()) {
                r3 = new ArrayList<>();
                Matcher s = strP.matcher(r3m.group(1));
                while (s.find()) r3.add(s.group(1));
            }
            refs.add(new Ref(d, r1, r3));
        }
        return refs;
    }
}
