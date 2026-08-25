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

/** Cross-checks Tanya Yomi labels against sefaria.org display values. */
public class TanyaTest {

    @Test public void currentDay() {
        // 2026-08-23 → 10 Elul 5786
        Tanya.Result r = Tanya.forDate(LocalDate.of(2026, 8, 23));
        assertEquals("10 Elul", r.label());
        assertEquals("י׳ אלול", r.labelHe());
    }

    @Test public void regularAdar_nonLeapYear() {
        // 2026-03-15 → 26 Adar 5786 (5786 is not leap)
        Tanya.Result r = Tanya.forDate(LocalDate.of(2026, 3, 15));
        assertEquals("26 Adar", r.label());
        assertEquals("כ״ו אדר",  r.labelHe());
    }

    @Test public void adarI_leapYear() {
        // 2027-02-20 → 13 Adar I 5787 (5787 is leap)
        Tanya.Result r = Tanya.forDate(LocalDate.of(2027, 2, 20));
        assertEquals("13 Adar I", r.label());
        assertEquals("י״ג אדר-א", r.labelHe());
    }

    @Test public void adarII_leapYear() {
        // 2024-03-20 → 10 Adar II 5784
        Tanya.Result r = Tanya.forDate(LocalDate.of(2024, 3, 20));
        assertEquals("10 Adar II", r.label());
        assertEquals("י׳ אדר-ב",   r.labelHe());
    }

    @Test public void gershaim_multiLetterDays() {
        // 21 Elul 5786 = 2026-09-03 → "כ״א" not "כא"
        Tanya.Result r = Tanya.forDate(LocalDate.of(2026, 9, 3));
        assertEquals("21 Elul", r.label());
        assertEquals("כ״א אלול", r.labelHe());
    }

    @Test public void gershaim_fifteenSixteen() {
        // 15 Elul → ט״ו (not יה); 16 Elul → ט״ז (not יו)
        assertEquals("ט״ו אלול", Tanya.forDate(LocalDate.of(2026, 8, 28)).labelHe());
        assertEquals("ט״ז אלול", Tanya.forDate(LocalDate.of(2026, 8, 29)).labelHe());
    }

    @Test public void nissan_doubleS() {
        // 2026-03-30 → 12 Nissan 5786
        Tanya.Result r = Tanya.forDate(LocalDate.of(2026, 3, 30));
        assertEquals("12 Nissan", r.label());
    }

    // ── Perek + start/end (from baked Kehot schedule) ─────────────────

    @Test public void perek_currentDay() {
        // 2026-08-23 (10 Elul, non-leap 5786) → Iggeret HaKodesh 12
        Tanya.Result r = Tanya.forDate(LocalDate.of(2026, 8, 23));
        assertTrue(r.hasPortion());
        assertEquals("Iggeret HaKodesh 12", r.perek());
        assertEquals("אגרת הקודש י״ב",     r.perekHe());
        assertEquals("והנה מודעת זאת,",    r.startWords());
        assertEquals("מאה פעמים וכו׳״.",   r.endWords());
    }

    @Test public void fullLabelHe_shape() {
        Tanya.Result r = Tanya.forDate(LocalDate.of(2026, 8, 23));
        assertEquals("אגרת הקודש י״ב — והנה מודעת זאת, ... מאה פעמים וכו׳״.",
                r.fullLabelHe());
    }

    @Test public void nonLeapChagHaGeulah_hasFrontMatterPortion() {
        // 19 Kislev 5786 (non-leap, Chag HaGeulah) = 2025-12-09 — title page reading.
        Tanya.Result r = Tanya.forDate(LocalDate.of(2025, 12, 9));
        assertTrue(r.hasPortion());
        assertEquals("עמוד הכותרת", r.perekHe());
        assertEquals("ספר לקוטי אמרים", r.startWords());
    }

    @Test public void nonLeapSivan6_hasChinukhKatanEnd() {
        // 6 Sivan 5786 = 2026-05-22 — end of Chinukh Katan (Shaar HaYichud's intro).
        Tanya.Result r = Tanya.forDate(LocalDate.of(2026, 5, 22));
        assertTrue(r.hasPortion());
        assertEquals("חינוך קטן", r.perekHe());
        assertEquals("אך הנה ידוע", r.startWords());
    }

    @Test public void leapChagHaGeulah_matchesChabad() {
        // 19 Kislev 5787 (leap year) = 2026-11-29 — title page reading.
        Tanya.Result r = Tanya.forDate(LocalDate.of(2026, 11, 29));
        assertTrue(r.hasPortion());
        assertEquals("עמוד הכותרת", r.perekHe());
        assertEquals("ספר לקוטי אמרים", r.startWords());
    }

    @Test public void leapKislev24_isPerek1() {
        // 24 Kislev 5787 (leap) = 2026-12-04 — Likkutei Amarim Perek 1 start.
        Tanya.Result r = Tanya.forDate(LocalDate.of(2026, 12, 4));
        assertTrue(r.hasPortion());
        assertEquals("לקוטי אמרים א׳", r.perekHe());
        assertEquals("תניא פרק ג׳", r.startWords());
    }

    @Test public void leapYear_partition() {
        // 15 Adar I 5787 (leap year) = 2027-02-22 — uses leap-year table.
        Tanya.Result r = Tanya.forDate(LocalDate.of(2027, 2, 22));
        assertTrue(r.hasPortion());
        assertEquals("15 Adar I", r.label());
        assertNotNull(r.perekHe());
    }

    // ── Bulk cross-check ─────────────────────────────────────────────────
    // English labels match Sefaria's "Tanya Yomi" display exactly; Hebrew
    // labels intentionally diverge — we use traditional gershaim
    // formatting ("י׳ אלול") rather than Sefaria's plain letters
    // ("י אלול"). English fixture is still authoritative.

    @Test public void matchesSefariaFixture_englishOnly() throws Exception {
        List<Ref> refs = loadFixture();
        assertTrue("fixture loaded", refs.size() >= 100);
        int miss = 0;
        StringBuilder err = new StringBuilder();
        for (Ref ref : refs) {
            if (ref.en == null) continue;
            Tanya.Result r = Tanya.forDate(ref.date);
            if (!ref.en.equals(r.label())) {
                miss++;
                if (miss <= 10)
                    err.append("  ").append(ref.date)
                       .append(": exp ").append(ref.en)
                       .append(", got ").append(r.label()).append('\n');
            }
        }
        assertEquals("Tanya EN mismatches:\n" + err, 0, miss);
    }

    private static final class Ref {
        final LocalDate date; final String en; final String he;
        Ref(LocalDate d, String en, String he) { this.date=d; this.en=en; this.he=he; }
    }

    private static List<Ref> loadFixture() throws Exception {
        StringBuilder sb = new StringBuilder();
        try (InputStream in = TanyaTest.class.getResourceAsStream("tanya_refs.json")) {
            assertNotNull("tanya_refs.json on classpath", in);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                String line; while ((line = br.readLine()) != null) sb.append(line).append('\n');
            }
        }
        List<Ref> refs = new ArrayList<>();
        Pattern obj  = Pattern.compile("\\{[^{}]*\\}");
        Pattern datP = Pattern.compile("\"date\"\\s*:\\s*\"(\\d{4}-\\d{2}-\\d{2})\"");
        Pattern enP  = Pattern.compile("\"en\"\\s*:\\s*\"([^\"]+)\"");
        Pattern heP  = Pattern.compile("\"he\"\\s*:\\s*\"([^\"]+)\"");
        Matcher om = obj.matcher(sb);
        while (om.find()) {
            String o = om.group();
            Matcher dm = datP.matcher(o); if (!dm.find()) continue;
            Matcher em = enP.matcher(o); Matcher hm = heP.matcher(o);
            refs.add(new Ref(LocalDate.parse(dm.group(1)),
                em.find() ? em.group(1) : null,
                hm.find() ? hm.group(1) : null));
        }
        return refs;
    }
}
