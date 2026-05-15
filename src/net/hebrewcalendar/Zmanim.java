package net.hebrewcalendar;

import org.shredzone.commons.suncalc.SunTimes;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Computes halachic times (zmanim) for a given date and geographic location.
 *
 * <p>All halachic-hour-based times follow the Alter Rebbe / Vilna Gaon (Gra) opinion:
 * a sha'ah zmanit is 1/12 of the period from visual sunrise to visual sunset.
 *
 * <p>Solar angles used:
 * <ul>
 *   <li>Sunrise / Shkiah: visual horizon (accounts for refraction and solar disc radius)</li>
 *   <li>Dawn (Alot Hashachar): sun 16.1° below horizon before sunrise</li>
 *   <li>Tzait Hakochavim (3 small stars): sun 6° below horizon after sunset</li>
 *   <li>Tzait (Alter Rebbe / end of Shabbat): sun 8.5° below horizon after sunset</li>
 * </ul>
 */
public class Zmanim {

    /**
     * Flags describing how a {@link Zman} time was calculated.
     */
    public enum Flag {
        /** Jerusalem custom: candle lighting is 40 minutes before sunset instead of 18. */
        JERUSALEM_CANDLE_LIGHTING,
        /** Polar day: no sunset found; halachic midnight used for candle lighting. */
        NO_SUNSET,
        /** Polar summer: nightfall (or end-of-Shabbat) not found; halachic midnight used. */
        NO_NIGHTFALL
    }

    private static final double DAWN_ANGLE        = -16.9;  // Chabad: 16.9° = 72 min before hanetz amiti
    private static final double MISHEYAKIR_ANGLE  = -10.2;  // Chabad: can recognise acquaintance at 4 cubits
    private static final double TRUE_HORIZON_ANGLE = -1.583; // Hanetz amiti / shkiah amitis: mountains of EY elevation
    private static final double NIGHTFALL_ANGLE   = -6.0;   // 3 small stars visible
    private static final double HAVDALAH_ANGLE    = -8.5;   // Alter Rebbe / end of Shabbat

    private final LocalDate date;
    private final double latitude;
    private final double longitude;
    private final double elevationMeters;
    private final String timezone;

    public Zmanim(LocalDate date, double latitude, double longitude, double elevationMeters, String timezone) {
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
        this.elevationMeters = elevationMeters;
        this.timezone = timezone;
    }

    private SunTimes.Parameters base() {
        return SunTimes.compute()
                .on(date)
                .at(latitude, longitude)
                .height(elevationMeters)
                .timezone(timezone)
                .oneDay();
    }

    private ZonedDateTime set(SunTimes.Twilight twilight) {
        return base().twilight(twilight).execute().getSet();
    }

    private ZonedDateTime set(double angle) {
        return base().twilight(angle).execute().getSet();
    }

    private ZonedDateTime rise(SunTimes.Twilight twilight) {
        return base().twilight(twilight).execute().getRise();
    }

    private ZonedDateTime rise(double angle) {
        return base().twilight(angle).execute().getRise();
    }

    /** Alot Hashachar: sun 16.9° below horizon — equivalent to 72 minutes before true sunrise (Chabad). */
    public ZonedDateTime getDawn() { return rise(DAWN_ANGLE); }

    /** Netz Hachama: visible sunrise. */
    public ZonedDateTime getSunrise() { return rise(SunTimes.Twilight.VISUAL); }

    /** Shkiah: visible sunset. */
    public ZonedDateTime getSunset() { return set(SunTimes.Twilight.VISUAL); }

    /**
     * Hanetz Amiti: true sunrise — sun at 1.583° below the horizon, corresponding to
     * visibility at the elevation of the mountains of Eretz Yisrael.
     * Used as the start of the halachic day for all sha'ah-zmanit calculations (Chabad).
     */
    public ZonedDateTime getHanetzAmiti() { return rise(TRUE_HORIZON_ANGLE); }

    /**
     * Shkiah Amitis: true sunset — sun at 1.583° below the horizon.
     * Used as the end of the halachic day for all sha'ah-zmanit calculations (Chabad).
     */
    public ZonedDateTime getShkiahAmitis() { return set(TRUE_HORIZON_ANGLE); }

    /** Candle lighting: 18 minutes before shkiah. */
    public ZonedDateTime getCandleLighting() {
        ZonedDateTime s = getSunset();
        return s == null ? null : s.minusMinutes(18);
    }

    /** Tzait Hakochavim: sun 6° below horizon after sunset. */
    public ZonedDateTime getNightfall() { return set(NIGHTFALL_ANGLE); }

    /** End of Shabbat / Havdalah: sun 8.5° below horizon after sunset. */
    public ZonedDateTime getEndOfShabbat() { return set(HAVDALAH_ANGLE); }

    /** Halachic midnight: solar nadir, 12 hours after solar noon. */
    public ZonedDateTime getMidnight() {
        ZonedDateTime noon = base().execute().getNoon();
        if (noon != null) return noon.plusHours(12);
        return date.plusDays(1).atStartOfDay(ZoneId.of(timezone));
    }

    /** True when these coordinates fall within the city of Jerusalem. */
    private boolean isJerusalem() {
        return latitude >= 31.71 && latitude <= 31.85
            && longitude >= 35.13 && longitude <= 35.28;
    }

    private int candleMinutes() {
        return isJerusalem() ? 40 : 18;
    }

    /**
     * Returns candle-lighting time as a {@link Zman}, including any applicable {@link Flag}s.
     *
     * <p>Rules applied:
     * <ul>
     *   <li>When today is a rest day and tomorrow is Yom Tov, candles must wait until
     *       nightfall (Tzait Hakochavim). If nightfall is unavailable, halachic midnight
     *       is used and {@link Flag#NO_NIGHTFALL} is set.</li>
     *   <li>Otherwise: {@link Flag#JERUSALEM_CANDLE_LIGHTING} triggers 40-minute rule;
     *       standard is 18 minutes before sunset. If sunset is unavailable,
     *       halachic midnight minus the candle-lighting offset is used and
     *       {@link Flag#NO_SUNSET} is set.</li>
     * </ul>
     */
    public Zman getCandleLightingZman(boolean todayIsRestDay, boolean tomorrowIsYomTov) {
        if (todayIsRestDay && tomorrowIsYomTov) {
            ZonedDateTime t = getNightfall();
            return t != null
                ? new Zman(t)
                : new Zman(getMidnight(), Flag.NO_NIGHTFALL);
        }
        int minutes = candleMinutes();
        ZonedDateTime s = getSunset();
        if (s != null) {
            return isJerusalem()
                ? new Zman(s.minusMinutes(minutes), Flag.JERUSALEM_CANDLE_LIGHTING)
                : new Zman(s.minusMinutes(minutes));
        }
        // Polar day: no sunset — fall back to halachic midnight
        return isJerusalem()
            ? new Zman(getMidnight().minusMinutes(minutes), Flag.NO_SUNSET, Flag.JERUSALEM_CANDLE_LIGHTING)
            : new Zman(getMidnight().minusMinutes(minutes), Flag.NO_SUNSET);
    }

    /**
     * Returns end-of-Shabbat time as a {@link Zman}.
     * Falls back to halachic midnight with {@link Flag#NO_NIGHTFALL} when unavailable.
     */
    public Zman getEndOfShabbatZman() {
        ZonedDateTime t = getEndOfShabbat();
        return t != null ? new Zman(t) : new Zman(getMidnight(), Flag.NO_NIGHTFALL);
    }

    // ── Sha'ah zmanit ────────────────────────────────────────────────────────

    /**
     * Returns the length of one sha'ah zmanit (halachic hour) in seconds.
     * Per Chabad: 1/12 of the period from hanetz amiti to shkiah amitis (both at 1.583°).
     * Returns 0 when either endpoint is unavailable (polar conditions).
     */
    public long shaahZmanitSeconds() {
        ZonedDateTime r = getHanetzAmiti();
        ZonedDateTime s = getShkiahAmitis();
        if (r == null || s == null) return 0;
        return Duration.between(r, s).getSeconds() / 12;
    }

    // ── Morning zmanim ────────────────────────────────────────────────────────

    /**
     * Misheyakir: earliest time to don tefillin and recite the Shema.
     * The moment one can recognise a casual acquaintance from a distance of four cubits
     * (and can distinguish the blue tchelet from white tzitzit threads).
     * Calculated when the sun is 10.2° below the horizon — approximately 45 minutes
     * before sunrise in Jerusalem at the equinox (per Chabad.org).
     * Returns null when the sun does not reach this angle (polar conditions).
     */
    public ZonedDateTime getMisheyakir() {
        return rise(MISHEYAKIR_ANGLE);
    }

    /**
     * Sof Zman Krias Shema: latest time to recite the morning Shema.
     * Per Chabad: 3 sha'ot zmaniot after hanetz amiti.
     * Returns null under polar conditions.
     */
    public ZonedDateTime getLatestShema() {
        ZonedDateTime r = getHanetzAmiti();
        long sha = shaahZmanitSeconds();
        return (r == null || sha == 0) ? null : r.plusSeconds(sha * 3);
    }

    /**
     * Sof Zman Tefilla: latest time for the morning Shacharit prayer.
     * 4 sha'ot zmaniot after hanetz amiti (per Magen Avraham / Chabad).
     * Returns null under polar conditions.
     */
    public ZonedDateTime getLatestShacharis() {
        ZonedDateTime r = getHanetzAmiti();
        long sha = shaahZmanitSeconds();
        return (r == null || sha == 0) ? null : r.plusSeconds(sha * 4);
    }

    /**
     * Sof Zman Biur Chametz: latest time to burn chametz on Erev Pesach.
     * 5 sha'ot zmaniot after hanetz amiti.
     * Returns null under polar conditions.
     */
    public ZonedDateTime getBurningChometz() {
        ZonedDateTime r = getHanetzAmiti();
        long sha = shaahZmanitSeconds();
        return (r == null || sha == 0) ? null : r.plusSeconds(sha * 5);
    }

    // ── Midday and afternoon zmanim ───────────────────────────────────────────

    /**
     * Chatzot (halachic noon): midpoint between hanetz amiti and shkiah amitis.
     * Latest time for Shacharit; earliest time for Mincha Gedolah (plus half a sha'ah).
     * Returns null under polar conditions.
     */
    public ZonedDateTime getChatzot() {
        ZonedDateTime r = getHanetzAmiti();
        ZonedDateTime s = getShkiahAmitis();
        if (r == null || s == null) return null;
        return r.plusSeconds(Duration.between(r, s).getSeconds() / 2);
    }

    /**
     * Mincha Gedolah: earliest permissible time for the afternoon Mincha prayer.
     * 6.5 sha'ot zmaniot after hanetz amiti (= Chatzot + half a sha'ah zmanit).
     * Per Shulchan Aruch, Orach Chayim 233:1.
     * Returns null under polar conditions.
     */
    public ZonedDateTime getMinchaGedolah() {
        ZonedDateTime r = getHanetzAmiti();
        long sha = shaahZmanitSeconds();
        return (r == null || sha == 0) ? null : r.plusSeconds((long)(sha * 6.5));
    }

    /**
     * Mincha Ketana: the preferred/optimal time for Mincha.
     * 9.5 sha'ot zmaniot after hanetz amiti (= 2.5 sha'ot before shkiah amitis).
     * Per Shulchan Aruch, Orach Chayim 233:1.
     * Returns null under polar conditions.
     */
    public ZonedDateTime getMinchaKetana() {
        ZonedDateTime r = getHanetzAmiti();
        long sha = shaahZmanitSeconds();
        return (r == null || sha == 0) ? null : r.plusSeconds((long)(sha * 9.5));
    }

    /**
     * Plag HaMincha: 1.25 sha'ot zmaniot before shkiah amitis (10.75 sha'ot after hanetz amiti).
     * Used for early Kabbalat Shabbat — accepting Shabbat before sunset on Friday.
     * Per Shulchan Aruch, Orach Chayim 267:2.
     * Returns null under polar conditions.
     */
    public ZonedDateTime getPlagHaMincha() {
        ZonedDateTime r = getHanetzAmiti();
        long sha = shaahZmanitSeconds();
        return (r == null || sha == 0) ? null : r.plusSeconds((long)(sha * 10.75));
    }

    // ── Evening zmanim ────────────────────────────────────────────────────────

    /**
     * Tzait Hakochavim per Rabbeinu Tam: 72 minutes after sunset.
     * Rabbeinu Tam holds that halachic nightfall (tzeit) is 72 minutes after
     * sunset — the time required for the sun to descend a full 15° below the
     * horizon in the Talmudic latitudes.  Widely used as the end of fast days.
     * Returns null when sunset is unavailable.
     */
    public ZonedDateTime getNightfallRabeinuTam() {
        ZonedDateTime s = getSunset();
        return s == null ? null : s.plusMinutes(72);
    }

    // ── Legacy convenience methods ────────────────────────────────────────────

    /** @deprecated Use {@link #getCandleLightingZman} for flag-aware results. */
    public ZonedDateTime getCandleLightingTime(boolean todayIsRestDay, boolean tomorrowIsYomTov) {
        return getCandleLightingZman(todayIsRestDay, tomorrowIsYomTov).getTime();
    }

    /** @deprecated Use {@link #getEndOfShabbatZman} for flag-aware results. */
    public ZonedDateTime getEndOfShabbatOrMidnight() {
        return getEndOfShabbatZman().getTime();
    }
}
