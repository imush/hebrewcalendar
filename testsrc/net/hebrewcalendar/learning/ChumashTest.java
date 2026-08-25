package net.hebrewcalendar.learning;

import net.hebrewcalendar.ICalendar;
import net.hebrewcalendar.IDate;
import net.hebrewcalendar.JewishCalendar;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

public class ChumashTest {

    // ── Normal week ──────────────────────────────────────────────────────

    @Test public void sunday_showsFirstAliyah() {
        // 2026-08-23 is Sunday, 10 Elul 5786. Next Shabbat's parsha is Ki Tavo.
        Chumash.Result r = Chumash.forDate(LocalDate.of(2026, 8, 23));
        assertEquals(1, r.portions().size());
        Chumash.Portion p = r.portions().get(0);
        assertEquals(1, p.firstAliyah());
        assertEquals(1, p.lastAliyah());
        assertEquals(List.of("Ki Tavo"), p.parshaNames());
        assertEquals("Ki Tavo — 1st aliyah", r.label());
        assertEquals("כי תבוא — ראשון", r.labelHe());
    }

    @Test public void shabbatShowsSeventhAliyah() {
        // 2026-08-29 is Shabbat, and its own parsha is Ki Tavo.
        Chumash.Result r = Chumash.forDate(LocalDate.of(2026, 8, 29));
        assertEquals(7, r.portions().get(0).firstAliyah());
        assertEquals("Ki Tavo — 7th aliyah", r.label());
    }

    // ── Double parsha ────────────────────────────────────────────────────

    @Test public void doubleParshaListsBothNames() {
        // Sun 2026-05-03 → next Sat 2026-05-09 = Behar-Bechukotai (Diaspora 5786).
        Chumash.Result r = Chumash.forDate(LocalDate.of(2026, 5, 3));
        Chumash.Portion p = r.portions().get(0);
        assertEquals(List.of("Behar", "Bechukotai"), p.parshaNames());
        assertEquals(1, p.firstAliyah());
        assertEquals("Behar-Bechukotai — 1st aliyah", r.label());
    }

    // ── Yom-tov Shabbat skip ─────────────────────────────────────────────

    @Test public void yomTovShabbatSkip_sameParshaTwoWeeks() {
        // 2026-04-04 Sat = Pesach 8 (Diaspora yom tov, empty parsha).
        // Next parsha-Shabbat is 2026-04-11 = Shemini.
        // On Sun 2026-03-29 (before yom tov Shabbat) and Sun 2026-04-05
        // (after yom tov Shabbat), next parsha-Shabbat is the same Shemini
        // — so BOTH weeks read Shemini.
        Chumash.Result before = Chumash.forDate(LocalDate.of(2026, 3, 29));
        Chumash.Result after  = Chumash.forDate(LocalDate.of(2026, 4, 5));
        assertEquals(List.of("Shemini"), before.portions().get(0).parshaNames());
        assertEquals(List.of("Shemini"), after.portions().get(0).parshaNames());
    }

    // ── Bereshit exception: before Simchat Torah ─────────────────────────

    @Test public void beforeSimchatTorah_showsVezotHaBracha() {
        // 5787 Diaspora: Simchat Torah = 23 Tishrei 5787 = 2026-10-04 (Sun).
        // The day before (Sat 2026-10-03) is Shabbat Chol HaMoed Sukkot, no parsha.
        // On Fri 2026-10-02, next parsha-Shabbat = 2026-10-10 = Bereshit,
        // and Fri < Simchat Torah, so show Vezot HaBracha day-of-week aliyah.
        Chumash.Result r = Chumash.forDate(LocalDate.of(2026, 10, 2));  // Fri (dow=6)
        Chumash.Portion p = r.portions().get(0);
        assertEquals(List.of("Vezot HaBracha"), p.parshaNames());
        assertEquals(6, p.firstAliyah());
        assertEquals(6, p.lastAliyah());
        assertEquals("Vezot HaBracha — 6th aliyah", r.label());
    }

    @Test public void beforeSimchatTorah_evenOnShabbatCholHaMoed_showsVezot() {
        // Sat 2026-10-03 = Shabbat Chol HaMoed Sukkot (dow=7).
        // Next parsha-Shabbat is 2026-10-10 = Bereshit.
        // 2026-10-03 < Simchat Torah (2026-10-04), so exception applies.
        Chumash.Result r = Chumash.forDate(LocalDate.of(2026, 10, 3));
        assertEquals(List.of("Vezot HaBracha"), r.portions().get(0).parshaNames());
        assertEquals(7, r.portions().get(0).firstAliyah());
    }

    // ── Bereshit exception: Simchat Torah day ────────────────────────────

    @Test public void simchatTorahDay_combinesVezotAndBereshit() {
        // 5787 Diaspora Simchat Torah = 2026-10-04 (Sun, dow=1).
        Chumash.Result r = Chumash.forDate(LocalDate.of(2026, 10, 4));
        assertEquals("two portions on Simchat Torah", 2, r.portions().size());
        Chumash.Portion vez = r.portions().get(0);
        Chumash.Portion ber = r.portions().get(1);
        assertEquals(List.of("Vezot HaBracha"), vez.parshaNames());
        assertEquals(1, vez.firstAliyah());   // x = 1 (Sunday)
        assertEquals(7, vez.lastAliyah());
        assertEquals(List.of("Bereishit"),    ber.parshaNames());
        assertEquals(1, ber.firstAliyah());
        assertEquals(1, ber.lastAliyah());    // 1..x = 1..1
        assertTrue(r.label().contains("Vezot HaBracha"));
        assertTrue(r.label().contains("Bereishit"));
    }

    // ── Bereshit exception: after Simchat Torah ──────────────────────────

    @Test public void afterSimchatTorah_showsBereshit() {
        // 5787 Diaspora: day after Simchat Torah is Mon 2026-10-05 (dow=2).
        // Next parsha-Shabbat = 2026-10-10 = Bereshit. After Simchat Torah,
        // so normal Bereshit case.
        Chumash.Result r = Chumash.forDate(LocalDate.of(2026, 10, 5));
        Chumash.Portion p = r.portions().get(0);
        assertEquals(List.of("Bereishit"), p.parshaNames());
        assertEquals(2, p.firstAliyah());
        assertEquals("Bereishit — 2nd aliyah", r.label());
    }

    @Test public void bereshitShabbat_showsBereshit7thAliyah() {
        // Shabbat 2026-10-10 (Bereshit).
        Chumash.Result r = Chumash.forDate(LocalDate.of(2026, 10, 10));
        assertEquals(List.of("Bereishit"), r.portions().get(0).parshaNames());
        assertEquals(7, r.portions().get(0).firstAliyah());
    }

    // ── Simchat Torah on a Tue/Wed/Fri (Diaspora possibilities) ─────────

    @Test public void simchatTorah_fridayCase() {
        // 5789 Diaspora Simchat Torah = 23 Tishrei 5789 = 2028-10-13 (Fri, dow=6).
        Chumash.Result r = Chumash.forDate(LocalDate.of(2028, 10, 13));
        assertEquals(2, r.portions().size());
        Chumash.Portion vez = r.portions().get(0);
        Chumash.Portion ber = r.portions().get(1);
        assertEquals(6, vez.firstAliyah());  // x = 6
        assertEquals(7, vez.lastAliyah());
        assertEquals(1, ber.firstAliyah());
        assertEquals(6, ber.lastAliyah());   // 1..x
    }

    @Test public void simchatTorah_fridayCase_labelIsRange() {
        Chumash.Result r = Chumash.forDate(LocalDate.of(2028, 10, 13));
        assertTrue("expected 'aliyot 6-7' in label, got: " + r.label(),
                r.label().contains("Vezot HaBracha — aliyot 6-7"));
        assertTrue(r.label().contains("Bereishit — aliyot 1-6"));
    }

    // ── Eretz Israel: Simchat Torah on Shabbat (22 Tishrei = Shemini Atzeret) ──

    @Test public void israel_simchatTorahOnShabbat_combines() {
        // 22 Tishrei 5787 = 2026-10-03 (Sat). In Eretz Israel this IS Simchat Torah.
        Chumash.Result r = Chumash.forDate(LocalDate.of(2026, 10, 3), true);
        assertEquals("two portions on Simchat Torah", 2, r.portions().size());
        Chumash.Portion vez = r.portions().get(0);
        Chumash.Portion ber = r.portions().get(1);
        assertEquals(List.of("Vezot HaBracha"), vez.parshaNames());
        assertEquals(7, vez.firstAliyah());  // x = 7 (Shabbat)
        assertEquals(7, vez.lastAliyah());   // aliyah 7 only
        assertEquals(List.of("Bereishit"),    ber.parshaNames());
        assertEquals(1, ber.firstAliyah());
        assertEquals(7, ber.lastAliyah());   // aliyot 1..x = 1..7
    }

    @Test public void israel_dayBeforeSimchatTorah_showsVezot() {
        // Fri 2026-10-02, inIsrael=true. Next parsha-Shabbat skips 22 Tishrei
        // (Israel Simchat Torah, yom tov) to 29 Tishrei = Bereshit. Fri < ST,
        // so show Vezot aliyah 6.
        Chumash.Result r = Chumash.forDate(LocalDate.of(2026, 10, 2), true);
        Chumash.Portion p = r.portions().get(0);
        assertEquals(List.of("Vezot HaBracha"), p.parshaNames());
        assertEquals(6, p.firstAliyah());
    }

    @Test public void israel_dayAfterSimchatTorah_showsBereshit() {
        // Sun 2026-10-04, inIsrael=true. After 22 Tishrei Israel Simchat Torah.
        // Next parsha-Shabbat = 2026-10-10 = Bereshit. Normal case.
        Chumash.Result r = Chumash.forDate(LocalDate.of(2026, 10, 4), true);
        Chumash.Portion p = r.portions().get(0);
        assertEquals(List.of("Bereishit"), p.parshaNames());
        assertEquals(1, p.firstAliyah());
    }

    // ── Israel vs Diaspora parsha divergence ─────────────────────────────

    @Test public void israelDiasporaDivergence_afterShavuot() {
        // 2027-06-12 Sat: Diaspora = yom tov (Shavuot 2), no parsha.
        //                 Israel   = Naso (already regular parsha).
        // On Sun 2027-06-06 (before that Sat), the "next parsha" differs:
        //   Diaspora → skip 06-12 (yom tov), next parsha-Sat = 06-19 = Naso.
        //   Israel   → 06-12 has parsha = Naso; next parsha-Sat = 06-12 = Naso.
        // Both should return "Naso" — but Diaspora is Naso "the week later".
        Chumash.Result diaspora = Chumash.forDate(LocalDate.of(2027, 6, 6), false);
        Chumash.Result israel   = Chumash.forDate(LocalDate.of(2027, 6, 6), true);
        assertEquals(List.of("Naso"), diaspora.portions().get(0).parshaNames());
        assertEquals(List.of("Naso"), israel.portions().get(0).parshaNames());

        // The following Sunday shows the divergence directly:
        //   Diaspora: 2027-06-13 Sun → next Sat 2027-06-19 = Naso.
        //   Israel:   2027-06-13 Sun → next Sat 2027-06-19 = Behaalotecha.
        Chumash.Result diaspora2 = Chumash.forDate(LocalDate.of(2027, 6, 13), false);
        Chumash.Result israel2   = Chumash.forDate(LocalDate.of(2027, 6, 13), true);
        assertEquals(List.of("Naso"),         diaspora2.portions().get(0).parshaNames());
        assertEquals(List.of("Behaalotecha"), israel2.portions().get(0).parshaNames());
    }

    // ── Sefaria links ────────────────────────────────────────────────────

    @Test public void sefariaUrl_singleChapter_shortForm() {
        // Sun 2026-08-23 → Ki Tavo 1st aliyah = Deut 26:1-11 (same chapter → short form).
        Chumash.Portion p = Chumash.forDate(LocalDate.of(2026, 8, 23)).portions().get(0);
        assertEquals("https://www.sefaria.org/Deuteronomy.26.1-11", p.sefariaUrl());
    }

    @Test public void sefariaUrl_singleAliyahSpanningChapters() {
        // Sun 2026-10-11 → Noach 1st aliyah = Gen 6:9-6:22.
        // Pick Bereishit 1st for chapter-spanning: aliyah 1 = 1:1-2:3.
        // Actually 2026-10-05 (Mon after Simchat Torah) shows Bereishit 2nd aliyah = 2:4-2:19.
        // Use Bereishit Shabbat 2026-10-10 (7th aliyah = 5:25-6:8).
        Chumash.Portion p = Chumash.forDate(LocalDate.of(2026, 10, 10)).portions().get(0);
        assertEquals("https://www.sefaria.org/Genesis.5.25-6.8", p.sefariaUrl());
    }

    @Test public void sefariaUrl_doubleParsha() {
        // Sun 2026-05-03 → Behar-Bechukotai 1st aliyah = Lev 25:1-18 (same chapter).
        Chumash.Portion p = Chumash.forDate(LocalDate.of(2026, 5, 3)).portions().get(0);
        assertEquals("https://www.sefaria.org/Leviticus.25.1-18", p.sefariaUrl());
    }

    @Test public void sefariaUrl_vezotAliyahRange_onSimchatTorah() {
        // Fri 2028-10-13 = Simchat Torah, Vezot HaBracha aliyot 6-7 = Deut 33:27-34:12.
        Chumash.Result r = Chumash.forDate(LocalDate.of(2028, 10, 13));
        Chumash.Portion vez = r.portions().get(0);
        Chumash.Portion ber = r.portions().get(1);
        assertEquals("https://www.sefaria.org/Deuteronomy.33.27-34.12", vez.sefariaUrl());
        // Bereishit aliyot 1-6 = Gen 1:1-5:24
        assertEquals("https://www.sefaria.org/Genesis.1.1-5.24", ber.sefariaUrl());
    }

    @Test public void sefariaUrl_beforeSimchatTorah_vezot() {
        // Fri 2026-10-02 → Vezot HaBracha 6th aliyah = Deut 33:27-29 (same chapter).
        Chumash.Portion p = Chumash.forDate(LocalDate.of(2026, 10, 2)).portions().get(0);
        assertEquals("https://www.sefaria.org/Deuteronomy.33.27-29", p.sefariaUrl());
    }

    // ── chabad.org links (date-based; no verse boundaries needed) ────────

    @Test public void chabadUrl_normalDay() {
        Chumash.Result r = Chumash.forDate(LocalDate.of(2026, 8, 23));
        assertEquals("https://www.chabad.org/dailystudy/torahreading.asp?tdate=8/23/2026",
                r.chabadUrl());
    }

    @Test public void chabadUrl_simchatTorah_sameUrlForBothPortions() {
        // On Simchat Torah, chabad.org's single page shows both Vezot & Bereshit.
        // Result.chabadUrl() is per-date, so the URL is identical regardless of
        // which portion the caller renders.
        Chumash.Result r = Chumash.forDate(LocalDate.of(2026, 10, 4));
        assertEquals("https://www.chabad.org/dailystudy/torahreading.asp?tdate=10/4/2026",
                r.chabadUrl());
        assertEquals(2, r.portions().size());
    }

    @Test public void chabadUrl_padding_noLeadingZeros() {
        // Sanity: chabad.org accepts "1/5/2027", not "01/05/2027". Verify format.
        Chumash.Result r = Chumash.forDate(LocalDate.of(2027, 1, 5));
        assertEquals("https://www.chabad.org/dailystudy/torahreading.asp?tdate=1/5/2027",
                r.chabadUrl());
    }

    @Test public void diaspora_sameDate_showsShabbatCholHaMoed_vezot() {
        // Cross-check: same Sat 2026-10-03 as above but Diaspora → not yet
        // Simchat Torah (which is Sun 2026-10-04 in Diaspora). Show Vezot
        // aliyah 7 (single-portion "before Simchat Torah" case).
        Chumash.Result r = Chumash.forDate(LocalDate.of(2026, 10, 3), false);
        assertEquals(1, r.portions().size());
        assertEquals(List.of("Vezot HaBracha"), r.portions().get(0).parshaNames());
        assertEquals(7, r.portions().get(0).firstAliyah());
    }
}
