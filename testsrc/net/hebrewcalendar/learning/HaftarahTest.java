package net.hebrewcalendar.learning;

import net.hebrewcalendar.data.Custom;
import net.hebrewcalendar.data.Haftarot;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

/**
 * These cases mirror the C library's test/test_haftarah.c one-for-one.
 * The two implementations of the precedence rules are deliberately
 * separate ports, so pinning both to the same expectations is what keeps
 * them from drifting.
 */
public class HaftarahTest {

    // ── helpers ──────────────────────────────────────────────────────

    private static void assertRef(Haftarot.Reference r, String book,
                                  int fromCh, int fromV, int toCh, int toV) {
        assertEquals(book, r.book);
        assertEquals(fromCh, r.fromCh);
        assertEquals(fromV,  r.fromV);
        assertEquals(toCh,   r.toCh);
        assertEquals(toV,    r.toV);
    }

    private static void assertDay(LocalDate d, Custom c, boolean inIsrael,
                                  Haftarah.Occasion occ, String book,
                                  int fromCh, int fromV, int toCh, int toV) {
        List<Haftarah.Result> rs = Haftarah.forDay(d, c, inIsrael);
        assertFalse("expected a reading on " + d, rs.isEmpty());
        assertEquals(occ, rs.get(0).occasion);
        assertRef(rs.get(0).refs.get(0), book, fromCh, fromV, toCh, toV);
    }

    // ── weekly parsha ────────────────────────────────────────────────

    @Test public void weekly_variesByCustom() {
        LocalDate bereishit = LocalDate.of(2025, 10, 18);
        assertDay(bereishit, Custom.ASHKENAZ, false, Haftarah.Occasion.WEEKLY, "Isaiah", 42, 5, 43, 10);
        assertDay(bereishit, Custom.SEFARD,   false, Haftarah.Occasion.WEEKLY, "Isaiah", 42, 5, 42, 21);
        assertDay(bereishit, Custom.TEIMAN,   false, Haftarah.Occasion.WEEKLY, "Isaiah", 42, 1, 42, 16);
    }

    @Test public void weekly_haazinu() {
        assertDay(LocalDate.of(2025, 10, 4), Custom.ASHKENAZ, false,
                  Haftarah.Occasion.WEEKLY, "II Samuel", 22, 1, 22, 51);
    }

    // ── special Shabbatot ────────────────────────────────────────────

    @Test public void arbaParshiyot_5786() {
        assertDay(LocalDate.of(2026, 2, 14), Custom.ASHKENAZ, false,
                  Haftarah.Occasion.PARSHAT_SHEKALIM,  "II Kings", 12,  1, 12, 17);
        assertDay(LocalDate.of(2026, 2, 28), Custom.ASHKENAZ, false,
                  Haftarah.Occasion.PARSHAT_ZACHOR,    "I Samuel", 15,  2, 15, 34);
        assertDay(LocalDate.of(2026, 3,  7), Custom.ASHKENAZ, false,
                  Haftarah.Occasion.PARSHAT_PARAH,     "Ezekiel",  36, 16, 36, 38);
        assertDay(LocalDate.of(2026, 3, 14), Custom.ASHKENAZ, false,
                  Haftarah.Occasion.PARSHAT_HACHODESH, "Ezekiel",  45, 16, 46, 18);
    }

    @Test public void shabbatHagadol_chabadKeepsWeekly() {
        LocalDate hagadol = LocalDate.of(2026, 3, 28);   // 10 Nisan, not Erev Pesach
        assertDay(hagadol, Custom.ASHKENAZ, false,
                  Haftarah.Occasion.SHABBAT_HAGADOL, "Malachi", 3, 4, 3, 24);
        assertDay(hagadol, Custom.CHABAD, false,
                  Haftarah.Occasion.WEEKLY, "Jeremiah", 7, 21, 7, 28);
    }

    // ── Rosh Chodesh / Machar Chodesh ────────────────────────────────

    @Test public void roshChodeshShabbat() {
        assertDay(LocalDate.of(2026, 4, 18), Custom.ASHKENAZ, false,
                  Haftarah.Occasion.ROSH_CHODESH, "Isaiah", 66, 1, 66, 24);
    }

    @Test public void macharChodeshShabbat() {
        assertDay(LocalDate.of(2026, 5, 16), Custom.ASHKENAZ, false,
                  Haftarah.Occasion.MACHAR_CHODESH, "I Samuel", 20, 18, 20, 42);
    }

    @Test public void roshChodeshNisan_yieldsToHachodesh() {
        // 1 Nisan 5782 = 2022-04-02, a Shabbat.
        assertDay(LocalDate.of(2022, 4, 2), Custom.ASHKENAZ, false,
                  Haftarah.Occasion.PARSHAT_HACHODESH, "Ezekiel", 45, 16, 46, 18);
    }

    // ── Rosh Chodesh / Machar Chodesh replace-vs-add ─────────────────

    /** Render a whole reading as "Book c:v-c:v;..." for exact comparison. */
    private static String render(LocalDate d, Custom c, boolean inIsrael) {
        Haftarah.Result r = Haftarah.forDate(d, c, inIsrael);
        if (r == null) return "";
        StringBuilder sb = new StringBuilder();
        for (Haftarot.Reference ref : r.refs)
            sb.append(ref.book).append(' ').append(ref.fromCh).append(':').append(ref.fromV)
              .append('-').append(ref.toCh).append(':').append(ref.toV).append(';');
        return sb.toString();
    }

    @Test public void roshChodeshElul_nechemtaHoldsExceptChabad() {
        // 2015-08-15 is Rosh Chodesh Elul (30 Av) on parshat Re'eh, and
        // 1 Elul falls the next day, so the Machar Chodesh addition applies too.
        LocalDate d = LocalDate.of(2015, 8, 15);
        assertEquals("Isaiah 54:11-55:5;", render(d, Custom.ASHKENAZ, false));
        assertEquals("Isaiah 66:1-66:24;Isaiah 66:23-66:23;"
                   + "I Samuel 20:18-20:18;I Samuel 20:42-20:42;",
                     render(d, Custom.CHABAD, false));
        // Fes never replaces for Machar Chodesh — it only ever appends.
        assertEquals("Isaiah 54:11-55:5;I Samuel 20:18-20:18;I Samuel 20:42-20:42;",
                     render(d, Custom.FES, false));
    }

    @Test public void specialShabbat_blocksReplace_chabadAppendsInstead() {
        // 2015-03-21 is Parshat Hachodesh falling on 1 Nisan.
        LocalDate d = LocalDate.of(2015, 3, 21);
        assertEquals("Ezekiel 45:16-46:18;", render(d, Custom.ASHKENAZ, false));
        assertEquals("Ezekiel 45:18-46:15;Isaiah 66:1-66:1;"
                   + "Isaiah 66:23-66:24;Isaiah 66:23-66:23;",
                     render(d, Custom.CHABAD, false));
    }

    @Test public void bothAdditionsCanApplyAtOnce() {
        // 30 Kislev 5776 (2015-12-12) is Shabbat Chanukah, Rosh Chodesh Tevet
        // and Erev Rosh Chodesh at once. Chanukah owns the reading; Tevet
        // blocks the Rosh Chodesh replace and being Rosh Chodesh blocks the
        // Machar Chodesh one, so Chabad appends both additions.
        LocalDate d = LocalDate.of(2015, 12, 12);
        assertEquals("Zechariah 2:14-4:7;"
                   + "Isaiah 66:1-66:1;Isaiah 66:23-66:24;Isaiah 66:23-66:23;"
                   + "I Samuel 20:18-20:18;I Samuel 20:42-20:42;",
                     render(d, Custom.CHABAD, false));
        assertEquals("Zechariah 2:14-4:7;", render(d, Custom.ASHKENAZ, false));
    }

    @Test public void macharChodeshElul_isSuppressed() {
        LocalDate d = LocalDate.of(2021, 8, 7);   // 29 Av
        assertEquals("Isaiah 54:11-55:5;", render(d, Custom.ASHKENAZ, false));
        assertEquals("Isaiah 54:11-55:5;I Samuel 20:18-20:18;I Samuel 20:42-20:42;",
                     render(d, Custom.CHABAD, false));
    }

    @Test public void roshChodeshTishrei_isRoshHashana_noAddition() {
        Haftarah.Result r = Haftarah.forDate(LocalDate.of(2026, 9, 12), Custom.CHABAD, false);
        assertNotNull(r);
        assertEquals(Haftarah.Occasion.ROSH_HASHANA, r.occasion);
        assertEquals(1, r.refs.size());
    }

    // ── Chanukah ─────────────────────────────────────────────────────

    @Test public void chanukah_singleShabbat_isFirst() {
        assertDay(LocalDate.of(2025, 12, 20), Custom.ASHKENAZ, false,
                  Haftarah.Occasion.CHANUKAH_SHABBAT_1, "Zechariah", 2, 14, 4, 7);
    }

    @Test public void chanukah_splitsOnDayNumberNotParsha() {
        // opentorah: `if dayNumber < 8 then shabbos1Haftarah else shabbos2Haftarah`.
        // Two Chanukah Shabbatot happen exactly when 25 Kislev is a Shabbat;
        // day 8 is then also a Shabbat and reads I Kings 7. In 5787,
        // 25 Kislev = 2026-12-05 and the eighth day = 2026-12-12.
        assertDay(LocalDate.of(2026, 12, 5), Custom.ASHKENAZ, false,
                  Haftarah.Occasion.CHANUKAH_SHABBAT_1, "Zechariah", 2, 14, 4, 7);
        assertDay(LocalDate.of(2026, 12, 12), Custom.ASHKENAZ, false,
                  Haftarah.Occasion.CHANUKAH_SHABBAT_2, "I Kings", 7, 40, 7, 50);
    }

    // ── Yom Tov, fasts, empty days ───────────────────────────────────

    @Test public void yomKippur_hasMorningAndAfternoon() {
        List<Haftarah.Result> rs = Haftarah.forDay(LocalDate.of(2026, 9, 21), Custom.ASHKENAZ, false);
        assertEquals(2, rs.size());
        assertEquals(Haftarah.Occasion.YOM_KIPPUR, rs.get(0).occasion);
        assertRef(rs.get(0).refs.get(0), "Isaiah", 57, 14, 58, 14);
        assertEquals(Haftarah.Occasion.YOM_KIPPUR_AFTERNOON, rs.get(1).occasion);
        assertRef(rs.get(1).refs.get(0), "Jonah", 1, 1, 4, 11);
    }

    @Test public void tishaBeAv_hasMorningAndAfternoon() {
        List<Haftarah.Result> rs = Haftarah.forDay(LocalDate.of(2026, 7, 23), Custom.ASHKENAZ, false);
        assertEquals(2, rs.size());
        assertEquals(Haftarah.Occasion.TISHA_BAV, rs.get(0).occasion);
        assertRef(rs.get(0).refs.get(0), "Jeremiah", 8, 13, 9, 23);
        assertEquals(Haftarah.Occasion.TISHA_BAV_AFTERNOON, rs.get(1).occasion);
    }

    @Test public void simchatTorah_readsVezotHabracha() {
        // Diaspora 23 Tishrei; Israel 22 Tishrei (= Shmini Atzeret there).
        assertDay(LocalDate.of(2026, 10, 4), Custom.ASHKENAZ, false,
                  Haftarah.Occasion.SIMCHAT_TORAH, "Joshua", 1, 1, 1, 18);
        assertDay(LocalDate.of(2026, 10, 3), Custom.ASHKENAZ, true,
                  Haftarah.Occasion.SIMCHAT_TORAH, "Joshua", 1, 1, 1, 18);
    }

    @Test public void sheminiAtzeret_isDiasporaOnly() {
        // Regression: JewishSpecialDay.matches() ignores location, so without
        // an applies(inIsrael) filter the Israel-only Simchat Torah day was
        // hijacking 22 Tishrei in the Diaspora too.
        assertDay(LocalDate.of(2026, 10, 3), Custom.ASHKENAZ, false,
                  Haftarah.Occasion.SHMINI_ATZERET, "I Kings", 8, 54, 8, 66);
    }

    @Test public void tzomGedalia_marrakeshHasItsOwnAfternoonHaftarah() {
        // Marrakesh is upstream now, and R. Asulin settles what it reads: Shuva
        // on the Fast of Gedalia, where the rest of Morocco reads Dirshu, and
        // Shuva with Micah on the other fasts, where most of Morocco reads
        // nothing at all. The local narrowing this test was written for is gone.
        LocalDate gedalia = LocalDate.of(2026, 9, 14);
        assertDay(gedalia, Custom.ASHKENAZ, false,
                  Haftarah.Occasion.FAST_AFTERNOON, "Isaiah", 55, 6, 56, 8);
        assertDay(gedalia, Custom.MOROCCO, false,
                  Haftarah.Occasion.FAST_AFTERNOON, "Isaiah", 55, 6, 56, 8);
        assertDay(gedalia, Custom.FES, false,
                  Haftarah.Occasion.FAST_AFTERNOON, "Isaiah", 55, 6, 56, 8);
        assertDay(gedalia, Custom.MARRAKESH, false,
                  Haftarah.Occasion.FAST_AFTERNOON, "Hosea", 14, 2, 14, 10);
        // On the other fasts Marrakesh reads Shuva with the Micah ending, and
        // Morocco reads nothing.
        assertDay(LocalDate.of(2026, 7, 2), Custom.MARRAKESH, false,
                  Haftarah.Occasion.FAST_AFTERNOON, "Hosea", 14, 2, 14, 10);
        assertReadsNothing(LocalDate.of(2026, 7, 2), Custom.MOROCCO);
    }

    @Test public void fastDay_someCustomsHaveNoHaftarah() {
        // opentorah leaves Fast.defaultAfternoonHaftarah undefined for the
        // Sefard and Teiman branches — they read no haftarah at mincha. The
        // occasion still applies, and says so with an empty reading: that a
        // custom reads nothing here is what several of the sources attest, and
        // a caller showing them needs to be able to reach the entry.
        assertReadsNothing(LocalDate.of(2026, 7, 2), Custom.SEFARD);
        assertReadsNothing(LocalDate.of(2026, 7, 2), Custom.TEIMAN);
    }

    /** The fast's Mincha occasion applies, and this custom reads nothing at it. */
    private static void assertReadsNothing(LocalDate date, Custom custom) {
        List<Haftarah.Result> results = Haftarah.forDay(date, custom, false);
        for (Haftarah.Result r : results)
            if (r.occasion == Haftarah.Occasion.FAST_AFTERNOON) {
                assertTrue(custom + " should read nothing at mincha", r.refs.isEmpty());
                return;
            }
        fail(custom + ": expected a FAST_AFTERNOON occasion recording that nothing is read");
    }

    @Test public void ordinaryWeekdayAndCholHamoed_haveNone() {
        assertTrue(Haftarah.forDay(LocalDate.of(2026, 4, 15), Custom.ASHKENAZ, false).isEmpty());
        assertTrue(Haftarah.forDay(LocalDate.of(2026, 6, 10), Custom.ASHKENAZ, false).isEmpty());
    }

    // ── exhaustive scan ──────────────────────────────────────────────

    @Test public void everyShabbatResolves() {
        LocalDate d = Haftarah.upcomingShabbat(LocalDate.of(2020, 1, 1));
        int checked = 0;
        for (int week = 0; week < 40 * 52; week++, d = d.plusDays(7)) {
            for (boolean israel : new boolean[]{false, true}) {
                for (Custom c : Custom.values()) {
                    Haftarah.Result r = Haftarah.forDate(d, c, israel);
                    assertNotNull("no haftarah: " + d + " " + c + " israel=" + israel, r);
                    assertFalse("empty refs: " + d + " " + c, r.refs.isEmpty());
                    checked++;
                }
            }
        }
        assertTrue(checked > 70000);
    }
}
