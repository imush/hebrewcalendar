package net.hebrewcalendar;

import org.shredzone.commons.suncalc.SunTimes;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZonedDateTime;

/**
 * Computes halachic times (zmanim) for a given date and geographic location.
 *
 * <p>All halachic-hour-based times follow the Alter Rebbe (Chabad) opinion:
 * a sha'ah zmanit is 1/12 of the period from hanetz amiti to shkiah amitis
 * (true sunrise / true sunset at solar-angle −1.583°, not the visible horizon).
 *
 * <p>Solar angles used:
 * <ul>
 *   <li>Sunrise / Shkiah: visual horizon (accounts for refraction and solar disc radius)</li>
 *   <li>Dawn (Alot Hashachar): sun 16.9° below horizon before sunrise</li>
 *   <li>Tzait Hakochavim (3 small stars): sun 6° below horizon after sunset</li>
 *   <li>Tzait (Alter Rebbe / end of Shabbat): sun 8.5° below horizon after sunset</li>
 * </ul>
 *
 * <p>All public methods return a {@link Zman}. Methods that cannot fall back to a reliable
 * alternative (e.g. simple solar angles) may return a {@code Zman} whose
 * {@link Zman#getTime()} is {@code null} under polar conditions.
 * Methods with explicit fallback logic (candle lighting, end-of-Shabbat) always return
 * a non-null time and set the appropriate {@link Flag}.
 */
public class Zmanim {

    /**
     * Flags describing how a {@link Zman} time was calculated.
     */
    public enum Flag {
        /** Jerusalem custom: candle lighting is 40 minutes before sunset instead of 18. */
        JERUSALEM_CANDLE_LIGHTING,
        /** Polar day: no sunset found; halachic midnight used as fallback. */
        NO_SUNSET,
        /** Polar summer: nightfall (or end-of-Shabbat) not found; halachic midnight used as fallback. */
        NO_NIGHTFALL,
        /** Today is a rest day and tomorrow is Yom Tov; candles are lit after nightfall. */
        CANDLES_AFTER_NIGHTFALL,
        /** Today is Yom Tov and tomorrow is Shabbat; candles are lit before sunset from existing flame. */
        CANDLES_BEFORE_SHABBAT
    }

    private static final double DAWN_ANGLE         = -16.9;  // Chabad: 72 min before hanetz amiti
    private static final double MISHEYAKIR_ANGLE   = -10.2;  // Chabad: recognise acquaintance at 4 cubits
    private static final double TRUE_HORIZON_ANGLE = -1.583; // Hanetz amiti / shkiah amitis
    private static final double NIGHTFALL_ANGLE    = -6.0;   // 3 small stars visible
    private static final double HAVDALAH_ANGLE     = -8.5;   // Alter Rebbe / end of Shabbat

    private final LocalDate  date;
    private final HLocation  location;

    /**
     * @param date     Gregorian date for which to compute zmanim
     * @param location geographic location with its halachic properties (Israel / Jerusalem flags)
     */
    public Zmanim(LocalDate date, HLocation location) {
        this.date     = date;
        this.location = location;
    }

    // ── Low-level solar helpers (private, may return null) ────────────────────

    private SunTimes.Parameters base() {
        return SunTimes.compute()
                .on(date).at(location.getLatitude(), location.getLongitude())
                .height(location.getElevationMeters()).timezone(location.getTimezone()).oneDay();
    }

    private ZonedDateTime set(SunTimes.Twilight twilight) { return base().twilight(twilight).execute().getSet(); }
    private ZonedDateTime set(double angle)               { return base().twilight(angle).execute().getSet(); }
    private ZonedDateTime rise(SunTimes.Twilight twilight){ return base().twilight(twilight).execute().getRise(); }
    private ZonedDateTime rise(double angle)              { return base().twilight(angle).execute().getRise(); }

    private ZonedDateTime sunsetOrNull()       { return set(SunTimes.Twilight.VISUAL); }
    private ZonedDateTime hanetzAmitiOrNull()  { return rise(TRUE_HORIZON_ANGLE); }
    private ZonedDateTime shkiahAmitisOrNull() { return set(TRUE_HORIZON_ANGLE); }
    private ZonedDateTime nightfallOrNull()    { return set(NIGHTFALL_ANGLE); }
    private ZonedDateTime endOfShabbatOrNull() { return set(HAVDALAH_ANGLE); }

    private ZonedDateTime midnight() {
        return base().execute().getNoon().plusHours(12);
    }

    private int candleMinutesBeforeSunset() { return location.isInJerusalem() ? 40 : 18; }

    // ── Sha'ah zmanit ─────────────────────────────────────────────────────────

    /**
     * Length of one sha'ah zmanit (halachic hour) in seconds.
     * Per Chabad: 1/12 of the period from hanetz amiti to shkiah amitis.
     * Returns 0 under polar conditions.
     */
    public long shaahZmanitSeconds() {
        ZonedDateTime r = hanetzAmitiOrNull();
        ZonedDateTime s = shkiahAmitisOrNull();
        if (r == null || s == null) return 0;
        return Duration.between(r, s).getSeconds() / 12;
    }

    // ── Morning zmanim ────────────────────────────────────────────────────────

    /** Alot Hashachar: sun 16.9° below horizon (72 min before hanetz amiti per Chabad). */
    public Zman getDawn() { return new Zman(rise(DAWN_ANGLE)); }

    /** Netz Hachama: visible sunrise. */
    public Zman getSunrise() { return new Zman(rise(SunTimes.Twilight.VISUAL)); }

    /**
     * Hanetz Amiti: true sunrise — sun 1.583° below horizon.
     * Start of the halachic day for sha'ah-zmanit calculations (Chabad).
     */
    public Zman getHanetzAmiti() { return new Zman(hanetzAmitiOrNull()); }

    /**
     * Misheyakir: earliest time to don tallis and tefillin.
     * Sun 10.2° below horizon (~45 min before sunrise in Jerusalem at equinox, per Chabad).
     */
    public Zman getMisheyakir() { return new Zman(rise(MISHEYAKIR_ANGLE)); }

    /**
     * Sof Zman Krias Shema: 3 sha'ot zmaniot after hanetz amiti (per Chabad).
     * Returns a {@link Zman} with null time under polar conditions.
     */
    public Zman getLatestShema() {
        ZonedDateTime r = hanetzAmitiOrNull();
        long sha = shaahZmanitSeconds();
        return new Zman((r == null || sha == 0) ? null : r.plusSeconds(sha * 3));
    }

    /**
     * Sof Zman Tefilla: latest time for morning Shacharit.
     * 4 sha'ot zmaniot after hanetz amiti (per Chabad).
     * Returns a {@link Zman} with null time under polar conditions.
     */
    public Zman getLatestShacharis() {
        ZonedDateTime r = hanetzAmitiOrNull();
        long sha = shaahZmanitSeconds();
        return new Zman((r == null || sha == 0) ? null : r.plusSeconds(sha * 4));
    }

    /**
     * Sof Zman Biur Chametz: latest time to burn chametz on Erev Pesach.
     * 5 sha'ot zmaniot after hanetz amiti.
     * Returns a {@link Zman} with null time under polar conditions.
     */
    public Zman getBurningChometz() {
        ZonedDateTime r = hanetzAmitiOrNull();
        long sha = shaahZmanitSeconds();
        return new Zman((r == null || sha == 0) ? null : r.plusSeconds(sha * 5));
    }

    // ── Midday and afternoon zmanim ───────────────────────────────────────────

    /**
     * Chatzot (halachic noon): midpoint between hanetz amiti and shkiah amitis.
     * Falls back to solar transit under polar conditions; always returns a non-null time.
     */
    public Zman getChatzot() {
        ZonedDateTime r = hanetzAmitiOrNull();
        ZonedDateTime s = shkiahAmitisOrNull();
        if (r == null || s == null) return new Zman(base().execute().getNoon());
        return new Zman(r.plusSeconds(Duration.between(r, s).getSeconds() / 2));
    }

    /**
     * Shkiah Amitis: true sunset — sun 1.583° below horizon.
     * End of the halachic day for sha'ah-zmanit calculations (Chabad).
     */
    public Zman getShkiahAmitis() { return new Zman(shkiahAmitisOrNull()); }

    /**
     * Mincha Gedolah: earliest time for Mincha.
     * 6.5 sha'ot zmaniot after hanetz amiti.
     * Returns a {@link Zman} with null time under polar conditions.
     */
    public Zman getMinchaGedolah() {
        ZonedDateTime r = hanetzAmitiOrNull();
        long sha = shaahZmanitSeconds();
        return new Zman((r == null || sha == 0) ? null : r.plusSeconds((long)(sha * 6.5)));
    }

    /**
     * Mincha Ketana: optimal time for Mincha.
     * 9.5 sha'ot zmaniot after hanetz amiti.
     * Returns a {@link Zman} with null time under polar conditions.
     */
    public Zman getMinchaKetana() {
        ZonedDateTime r = hanetzAmitiOrNull();
        long sha = shaahZmanitSeconds();
        return new Zman((r == null || sha == 0) ? null : r.plusSeconds((long)(sha * 9.5)));
    }

    /**
     * Plag HaMincha: 10.75 sha'ot zmaniot after hanetz amiti (1.25 sha'ot before shkiah amitis).
     * Returns a {@link Zman} with null time under polar conditions.
     */
    public Zman getPlagHaMincha() {
        ZonedDateTime r = hanetzAmitiOrNull();
        long sha = shaahZmanitSeconds();
        return new Zman((r == null || sha == 0) ? null : r.plusSeconds((long)(sha * 10.75)));
    }

    // ── Evening zmanim ────────────────────────────────────────────────────────

    /** Shkiah: visible sunset. */
    public Zman getSunset() { return new Zman(sunsetOrNull()); }

    /**
     * Tzait Hakochavim (Alter Rebbe / Vilna Gaon): sun 8.5° below horizon.
     * Used as the end of Shabbat and Yom Tov. For the fallback-aware version that
     * substitutes halachic midnight under polar conditions, use {@link #getEndOfShabbatZman()}.
     */
    public Zman getNightfallAlterRebbe() { return new Zman(endOfShabbatOrNull()); }

    /**
     * Tzait Hakochavim per Rabbeinu Tam: 72 minutes after sunset.
     * Widely used as the end of fast days.
     * Returns a {@link Zman} with null time when sunset is unavailable.
     */
    public Zman getNightfallRabeinuTam() {
        ZonedDateTime s = sunsetOrNull();
        return new Zman(s == null ? null : s.plusMinutes(72));
    }

    /**
     * Halachic midnight: 12 hours after solar noon.
     * Solar noon (transit) is always computable, so this never returns a null time.
     */
    public Zman getMidnight() { return new Zman(midnight()); }

    // ── Shabbat / Yom Tov times ───────────────────────────────────────────────

    /**
     * End of Shabbat / Havdalah: sun 8.5° below horizon (Alter Rebbe).
     * Falls back to halachic midnight with {@link Flag#NO_NIGHTFALL} when unavailable.
     * Always returns a non-null time.
     */
    public Zman getEndOfShabbatZman() {
        ZonedDateTime t = endOfShabbatOrNull();
        return t != null ? new Zman(t) : new Zman(midnight(), Flag.NO_NIGHTFALL);
    }

    private Zman getCandleLightingZmanInternal(boolean todayIsRestDay, boolean tomorrowIsYomTov) {
        if (todayIsRestDay && tomorrowIsYomTov) {
            ZonedDateTime t = nightfallOrNull();
            return t != null
                ? new Zman(t, Flag.CANDLES_AFTER_NIGHTFALL)
                : new Zman(midnight(), Flag.CANDLES_AFTER_NIGHTFALL, Flag.NO_NIGHTFALL);
        }
        int minutes = candleMinutesBeforeSunset();
        ZonedDateTime s = sunsetOrNull();
        if (s != null) {
            if (location.isInJerusalem())
                return todayIsRestDay
                    ? new Zman(s.minusMinutes(minutes), Flag.CANDLES_BEFORE_SHABBAT, Flag.JERUSALEM_CANDLE_LIGHTING)
                    : new Zman(s.minusMinutes(minutes), Flag.JERUSALEM_CANDLE_LIGHTING);
            else
                return todayIsRestDay
                    ? new Zman(s.minusMinutes(minutes), Flag.CANDLES_BEFORE_SHABBAT)
                    : new Zman(s.minusMinutes(minutes));
        }
        // Polar day: no sunset — fall back to halachic midnight
        if (location.isInJerusalem())
            return todayIsRestDay
                ? new Zman(midnight().minusMinutes(minutes), Flag.CANDLES_BEFORE_SHABBAT, Flag.NO_SUNSET, Flag.JERUSALEM_CANDLE_LIGHTING)
                : new Zman(midnight().minusMinutes(minutes), Flag.NO_SUNSET, Flag.JERUSALEM_CANDLE_LIGHTING);
        else
            return todayIsRestDay
                ? new Zman(midnight().minusMinutes(minutes), Flag.CANDLES_BEFORE_SHABBAT, Flag.NO_SUNSET)
                : new Zman(midnight().minusMinutes(minutes), Flag.NO_SUNSET);
    }

    /**
     * Candle-lighting time for this date, with applicable {@link Flag}s.
     *
     * <ul>
     *   <li>Today is rest day + tomorrow is Yom Tov: nightfall (Tzait at 6°);
     *       {@link Flag#CANDLES_AFTER_NIGHTFALL} set. Falls back to halachic midnight
     *       with {@link Flag#NO_NIGHTFALL}.</li>
     *   <li>Today is Yom Tov + tomorrow is Shabbat: before-sunset lighting from existing flame;
     *       {@link Flag#CANDLES_BEFORE_SHABBAT} set.</li>
     *   <li>Jerusalem: 40 minutes before sunset; {@link Flag#JERUSALEM_CANDLE_LIGHTING} set.</li>
     *   <li>Polar day (no sunset): halachic midnight used; {@link Flag#NO_SUNSET} set.</li>
     * </ul>
     *
     * <p>Rest-day and Yom Tov status are determined using the {@code inIsrael} flag
     * supplied to the {@link HLocation} at construction time.
     *
     * @return a {@link Zman} with appropriate flags, or {@code null} if tomorrow is not a rest day
     */
    public Zman getCandleLightingZman() {
        HDate todayGreg    = HCalendar.GREGORIAN.fromYMD(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
        HDate tomorrowGreg = todayGreg.addDays(1);
        if (!isRestDay(tomorrowGreg, location.isInIsrael())) return null;
        boolean todayIsRest      = isRestDay(todayGreg, location.isInIsrael());
        boolean tomorrowIsYomTov = tomorrowGreg.getDayOfWeek() != 7; // Saturday = 7 in HDate
        return getCandleLightingZmanInternal(todayIsRest, tomorrowIsYomTov);
    }

    /** Saturday (7 in HDate) or any Yom Tov applicable at the given location. */
    private static boolean isRestDay(HDate date, boolean inIsrael) {
        if (date.getDayOfWeek() == 7) return true;
        for (HJewishHoliday h : HJewishHoliday.values()) {
            if (h.isYomTov() && h.applies(inIsrael) && h.matches(date)) return true;
        }
        return false;
    }
}
