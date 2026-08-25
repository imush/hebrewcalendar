package net.hebrewcalendar.learning;

import org.junit.Test;

import static org.junit.Assert.*;

/** Gematria — number → Hebrew numeral, formatted to sefaria.org conventions. */
public class GematriaTest {

    // ── Basic units ──────────────────────────────────────────────────────

    @Test public void singleLetterUnits() {
        assertEquals("א׳", Gematria.of(1));
        assertEquals("ה׳", Gematria.of(5));
        assertEquals("ט׳", Gematria.of(9));
    }

    @Test public void tens() {
        assertEquals("י׳", Gematria.of(10));
        assertEquals("כ׳", Gematria.of(20));
        assertEquals("ל׳", Gematria.of(30));
        assertEquals("צ׳", Gematria.of(90));
    }

    // ── 15/16 special-case, avoiding יה / יו ──────────────────────────

    @Test public void fifteenAndSixteen() {
        assertEquals("ט״ו", Gematria.of(15));
        assertEquals("ט״ז", Gematria.of(16));
    }

    @Test public void fifteenSixteenAlsoInHundreds() {
        // 115 = ק + טו (not קי״ה); Sefaria: קט״ו
        assertEquals("קט״ו", Gematria.of(115));
        // 216 = ר + טז → רט״ז
        assertEquals("רט״ז", Gematria.of(216));
    }

    // ── Sefaria-observed values ─────────────────────────────────────────

    @Test public void chullin115_koph_tet_vav() { assertEquals("קט״ו", Gematria.of(115)); }
    @Test public void twentyFour()               { assertEquals("כ״ד",   Gematria.of(24)); }
    @Test public void twentyFive()               { assertEquals("כ״ה",   Gematria.of(25)); }
    @Test public void fiftySix()                 { assertEquals("נ״ו",   Gematria.of(56)); }
    @Test public void hundred()                  { assertEquals("ק׳",    Gematria.of(100)); }
    @Test public void hundredSeven()             { assertEquals("ק״ז",   Gematria.of(107)); }
    @Test public void twoFortyEight()            { assertEquals("רמ״ח", Gematria.of(248)); }
    @Test public void hundredThirtyEight()       { assertEquals("קל״ח", Gematria.of(138)); }

    // ── Ranges / verse strings ──────────────────────────────────────────

    @Test public void verseRange_range() {
        assertEquals("כ״ד-כ״ה", Gematria.verseRange("24-25"));
        assertEquals("א׳-רמ״ח", Gematria.verseRange("1-248"));
    }

    @Test public void verseRange_verseSpan() {
        assertEquals("א׳:א׳-ד׳:ח׳",   Gematria.verseRange("1:1-4:8"));
        assertEquals("י׳:א׳-י״ד:י׳", Gematria.verseRange("10:1-14:10"));
    }

    @Test public void verseRange_single() {
        assertEquals("ה׳", Gematria.verseRange("5"));
    }

    // ── Domain errors ──────────────────────────────────────────────────

    @Test(expected = IllegalArgumentException.class)
    public void zeroThrows() { Gematria.of(0); }

    @Test(expected = IllegalArgumentException.class)
    public void negativeThrows() { Gematria.of(-1); }
}
