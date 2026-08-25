package net.hebrewcalendar.learning;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class SefirahSotahTest {

    @Test public void firstDayOfSefirah_isDafBeit() {
        // 16 Nisan 5786 = 2026-04-03 (Fri) → daf 2.
        SefirahSotah.Result r = SefirahSotah.forDate(LocalDate.of(2026, 4, 3));
        assertNotNull(r);
        assertEquals(1, r.omerCount());
        assertEquals(2, r.daf());
        assertEquals("סוטה ב׳", r.labelHe());
    }

    @Test public void lastDayOfSotah_isDafFortyNine() {
        // 4 Sivan 5786 = 2026-05-20 (Wed) → count 48, daf 49 (last daf of Sotah).
        // On 5 Sivan we'd formally count 49 → daf 50, which doesn't exist,
        // so the schedule ends here and Chabad does the siyum on Shavuot morning.
        SefirahSotah.Result r = SefirahSotah.forDate(LocalDate.of(2026, 5, 20));
        assertNotNull(r);
        assertEquals(48, r.omerCount());
        assertEquals(49, r.daf());
        assertEquals("סוטה מ״ט", r.labelHe());
    }

    @Test public void erevShavuot_null_sotahComplete() {
        // 5 Sivan 5786 = 2026-05-21 — would be daf 50 (doesn't exist), null.
        assertNull(SefirahSotah.forDate(LocalDate.of(2026, 5, 21)));
    }

    @Test public void beforeSefirah_null() {
        // 15 Nisan (1st day Pesach) — sefirah hasn't started.
        assertNull(SefirahSotah.forDate(LocalDate.of(2026, 4, 2)));
    }

    @Test public void afterSefirah_null() {
        // 6 Sivan (Shavuot) — sefirah over.
        assertNull(SefirahSotah.forDate(LocalDate.of(2026, 5, 22)));
    }

    @Test public void midElul_null() {
        assertNull(SefirahSotah.forDate(LocalDate.of(2026, 8, 24)));
    }
}
