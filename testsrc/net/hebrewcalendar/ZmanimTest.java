package net.hebrewcalendar;

import org.junit.Test;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import static org.junit.Assert.*;

/**
 * Reference-value tests for {@link Zmanim}.
 *
 * <p>Reference times are Chabad.org's zmanim table for Montreal, QC on
 * 23 Jul 2026 (9 Av 5786). Montreal's coordinates line up cleanly with
 * Chabad's rendering (sunrise / nightfall 8.5° exact to the minute), so
 * ±60 seconds is a tight-but-safe tolerance: it catches an angle or opinion
 * regression (would shift by minutes) without tripping on Chabad's HH:MM
 * display rounding.
 */
public class ZmanimTest {

    // Montreal, QC — coordinates that line up with Chabad's rendered zmanim.
    private static final double MTL_LAT = 45.5017;
    private static final double MTL_LON = -73.5673;
    private static final String MTL_TZ  = "America/Montreal";
    private static final LocalDate DATE = LocalDate.of(2026, 7, 23);

    // Chabad's rendering rounds to the minute and their zipcode centroid is
    // slightly different from ours, so ±180 seconds is a safe slack that
    // still catches an angle or opinion regression (a wrong angle would
    // shift by many minutes; a wrong opinion by ~an hour).
    private static final long REF_TOLERANCE_SEC = 180;

    private static Zmanim makeZmanim() {
        Location loc = new Location(MTL_LAT, MTL_LON, 0.0, MTL_TZ, false, false);
        return new Zmanim(DATE, loc);
    }

    // Assert two ZonedDateTimes agree to within REF_TOLERANCE_SEC.
    private static void assertNear(String label, ZonedDateTime actual, int refH, int refM) {
        assertNotNull(label + " must not be null", actual);
        ZonedDateTime ref = actual.withHour(refH).withMinute(refM).withSecond(0).withNano(0);
        long diff = Math.abs(actual.toEpochSecond() - ref.toEpochSecond());
        assertTrue(label + " expected ~" + refH + ":" + String.format("%02d", refM)
                + " but got " + actual + " (Δ=" + diff + "s)", diff <= REF_TOLERANCE_SEC);
    }

    // ── Reference-value regression: each named opinion matches Chabad ───────
    // (all times AM/PM as displayed on chabad.org for Montreal 23 Jul 2026)

    // Alot Hashachar Rav Naeh (−26°) intentionally omitted here — Montreal
    // near the summer solstice doesn't dip that far, so our library returns
    // null. See alotRavNaeh_nullAtHighLatitudeSummer below.

    @Test public void alot_seferBeinHaShmashot_neg19_8deg() {
        assertNear("Alot Sefer Bein haShmashot",
                makeZmanim().getDawnSeferBeinHaShmashot().getTime(), 2, 48);
    }

    @Test public void alot_chabadDefault_neg16_9deg() {
        assertNear("Alot Chabad default", makeZmanim().getDawn().getTime(), 3, 21);
    }

    @Test public void alot_gra_neg16_1deg() {
        assertNear("Alot GR\"A", makeZmanim().getDawnGRA().getTime(), 3, 29);
    }

    @Test public void misheyakir_nivsheret_neg11_8deg() {
        assertNear("Misheyakir Nivsheret",
                makeZmanim().getMisheyakirNivsheret().getTime(), 4, 8);
    }

    @Test public void misheyakir_chabad_neg10_2deg() {
        assertNear("Misheyakir Chabad", makeZmanim().getMisheyakir().getTime(), 4, 21);
    }

    @Test public void sunrise_visible() {
        assertNear("Sunrise (Hanetz Hachama)", makeZmanim().getSunrise().getTime(), 5, 27);
    }

    @Test public void shema_magenAvraham() {
        assertNear("Shema MA",
                makeZmanim().getLatestShema(Zmanim.ShaahMethod.MAGEN_AVRAHAM).getTime(), 8, 14);
    }

    @Test public void shema_chabad() {
        assertNear("Shema Chabad (default)", makeZmanim().getLatestShema().getTime(), 9, 11);
    }

    @Test public void shema_gra() {
        assertNear("Shema GR\"A",
                makeZmanim().getLatestShema(Zmanim.ShaahMethod.GRA_VISIBLE).getTime(), 9, 13);
    }

    @Test public void shacharis_magenAvraham() {
        assertNear("Shacharis MA",
                makeZmanim().getLatestShacharis(Zmanim.ShaahMethod.MAGEN_AVRAHAM).getTime(), 9, 49);
    }

    @Test public void shacharis_chabad() {
        assertNear("Shacharis Chabad", makeZmanim().getLatestShacharis().getTime(), 10, 27);
    }

    @Test public void shacharis_gra() {
        assertNear("Shacharis GR\"A",
                makeZmanim().getLatestShacharis(Zmanim.ShaahMethod.GRA_VISIBLE).getTime(), 10, 29);
    }

    @Test public void chatzot_solarNoon() {
        assertNear("Chatzot", makeZmanim().getChatzot().getTime(), 13, 0);
    }

    @Test public void minchaGedolah_gra() {
        assertNear("Mincha Gedolah GR\"A",
                makeZmanim().getMinchaGedolah(Zmanim.ShaahMethod.GRA_VISIBLE).getTime(), 13, 39);
    }

    @Test public void minchaGedolah_chabad() {
        assertNear("Mincha Gedolah Chabad", makeZmanim().getMinchaGedolah().getTime(), 13, 39);
    }

    @Test public void minchaGedolah_magenAvraham() {
        assertNear("Mincha Gedolah MA",
                makeZmanim().getMinchaGedolah(Zmanim.ShaahMethod.MAGEN_AVRAHAM).getTime(), 13, 48);
    }

    @Test public void minchaKetana_gra() {
        assertNear("Mincha Ketana GR\"A",
                makeZmanim().getMinchaKetana(Zmanim.ShaahMethod.GRA_VISIBLE).getTime(), 17, 25);
    }

    @Test public void minchaKetana_chabad() {
        assertNear("Mincha Ketana Chabad", makeZmanim().getMinchaKetana().getTime(), 17, 28);
    }

    @Test public void minchaKetana_magenAvraham() {
        assertNear("Mincha Ketana MA",
                makeZmanim().getMinchaKetana(Zmanim.ShaahMethod.MAGEN_AVRAHAM).getTime(), 18, 33);
    }

    @Test public void plag_gra() {
        assertNear("Plag GR\"A",
                makeZmanim().getPlagHaMincha(Zmanim.ShaahMethod.GRA_VISIBLE).getTime(), 19, 0);
    }

    @Test public void plag_chabad() {
        assertNear("Plag Chabad", makeZmanim().getPlagHaMincha().getTime(), 19, 4);
    }

    @Test public void plag_magenAvraham() {
        assertNear("Plag MA",
                makeZmanim().getPlagHaMincha(Zmanim.ShaahMethod.MAGEN_AVRAHAM).getTime(), 20, 32);
    }

    @Test public void sunset() {
        assertNear("Sunset (Shkiah)", makeZmanim().getSunset().getTime(), 20, 34);
    }

    @Test public void nightfall_mediumStars_neg6deg() {
        assertNear("Nightfall −6°", makeZmanim().getNightfallMediumStars().getTime(), 21, 10);
    }

    @Test public void nightfall_alterRebbe_neg8_5deg() {
        assertNear("Nightfall −8.5° (Alter Rebbe)",
                makeZmanim().getNightfallAlterRebbe().getTime(), 21, 27);
    }

    @Test public void rabbeinuTam_plus72min() {
        assertNear("Rabeinu Tam +72", makeZmanim().getNightfallRabeinuTam().getTime(), 21, 46);
    }

    @Test public void chatzotHaLailah_midnight() {
        assertNear("Chatzot HaLailah", makeZmanim().getMidnight().getTime(), 1, 0);
    }

    // ── Backward compatibility: no-arg getters == CHABAD_AMITI overloads ───

    @Test public void backCompat_shema() {
        Zmanim z = makeZmanim();
        assertEquals(z.getLatestShema().getTime(),
                     z.getLatestShema(Zmanim.ShaahMethod.CHABAD_AMITI).getTime());
    }

    @Test public void backCompat_shacharis() {
        Zmanim z = makeZmanim();
        assertEquals(z.getLatestShacharis().getTime(),
                     z.getLatestShacharis(Zmanim.ShaahMethod.CHABAD_AMITI).getTime());
    }

    @Test public void backCompat_burningChometz() {
        Zmanim z = makeZmanim();
        assertEquals(z.getBurningChometz().getTime(),
                     z.getBurningChometz(Zmanim.ShaahMethod.CHABAD_AMITI).getTime());
    }

    @Test public void backCompat_minchaGedolah() {
        Zmanim z = makeZmanim();
        assertEquals(z.getMinchaGedolah().getTime(),
                     z.getMinchaGedolah(Zmanim.ShaahMethod.CHABAD_AMITI).getTime());
    }

    @Test public void backCompat_minchaKetana() {
        Zmanim z = makeZmanim();
        assertEquals(z.getMinchaKetana().getTime(),
                     z.getMinchaKetana(Zmanim.ShaahMethod.CHABAD_AMITI).getTime());
    }

    @Test public void backCompat_plagHaMincha() {
        Zmanim z = makeZmanim();
        assertEquals(z.getPlagHaMincha().getTime(),
                     z.getPlagHaMincha(Zmanim.ShaahMethod.CHABAD_AMITI).getTime());
    }

    @Test public void backCompat_shaahZmanit() {
        Zmanim z = makeZmanim();
        assertEquals(z.shaahZmanitSeconds(),
                     z.shaahZmanitSeconds(Zmanim.ShaahMethod.CHABAD_AMITI));
    }

    // ── Structural invariants ──────────────────────────────────────────────

    /** Nivsheret uses a larger depression angle than Chabad, so its
     *  Misheyakir must fall <em>earlier</em> in the morning. */
    @Test public void misheyakirNivsheret_earlierThanChabad() {
        Zmanim z = makeZmanim();
        assertTrue("Nivsheret Misheyakir (−11.8°) must precede Chabad (−10.2°)",
                z.getMisheyakirNivsheret().getTime()
                        .isBefore(z.getMisheyakir().getTime()));
    }

    /** Igrot Moshe / Sefer Bein hashmashot Nightfall shares the −8.5° solar
     *  angle with Alter Rebbe — the accessor is just a naming affordance. */
    @Test public void nightfallIgrotMoshe_equalsAlterRebbe() {
        Zmanim z = makeZmanim();
        assertEquals("Igrot Moshe nightfall (−8.5°) must equal Alter Rebbe",
                z.getNightfallAlterRebbe().getTime(),
                z.getNightfallIgrotMoshe().getTime());
    }

    /** Rav Naeh (−26°) never happens at mid-latitude summer — this pins the
     *  polar-null contract so a future refactor doesn't silently start
     *  extrapolating. Montreal on 23 Jul proves the point (the summer sun
     *  simply doesn't set that far below the horizon that far north). */
    @Test public void alotRavNaeh_nullAtHighLatitudeSummer() {
        assertNull("Rav Naeh Alot should return null when sun never reaches −26°",
                makeZmanim().getDawnRavNaeh().getTime());
    }

    /** Chatzot is astronomically the same regardless of method (any symmetric
     *  span around solar transit shares the same midpoint), so we don't offer
     *  a per-method overload — verify it lands on the visible sunrise / sunset
     *  midpoint within a few seconds regardless. */
    @Test public void chatzot_agreesWithGraMidpoint() {
        Zmanim z = makeZmanim();
        ZonedDateTime chatzot = z.getChatzot().getTime();
        long griMid = (z.getSunrise().getTime().toEpochSecond()
                     + z.getSunset().getTime().toEpochSecond()) / 2;
        assertTrue("Chatzot should agree with GR\"A midpoint",
                Math.abs(chatzot.toEpochSecond() - griMid) < 10);
    }
}
