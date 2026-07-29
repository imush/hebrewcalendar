package net.hebrewcalendar;

import net.hebrewcalendar.impl.GregorianCalendar;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
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

    /**
     * Method for computing the sha'ah zmanit (halachic hour) used by portion-of-day
     * zmanim (Shema, Tefilla, Mincha, Plag, Biur Chametz).
     */
    public enum ShaahMethod {
        /**
         * Chabad / Alter Rebbe: sha'ah = (hanetz amiti → shkiah amitis) / 12,
         * i.e. from sun at −1.583° rising to sun at −1.583° setting. Portion-of-
         * day zmanim are counted from hanetz amiti. This is what the no-arg
         * getters use.
         */
        CHABAD_AMITI,
        /**
         * GR"A: sha'ah = (visible sunrise → visible sunset) / 12. Portion-of-
         * day zmanim are counted from visible sunrise.
         */
        GRA_VISIBLE,
        /**
         * Magen Avraham: sha'ah = (Alot at −16.1° → Tzeit at −16.1°) / 12.
         * Portion-of-day zmanim are counted from Alot at −16.1° (GR"A's Alot).
         * The −16.1° Tzeit here is purely a computational anchor for the MA
         * day-length; it is not used as a standalone nightfall.
         */
        MAGEN_AVRAHAM
    }

    private static final double DAWN_ANGLE                = -16.9;  // Chabad: 72 min before hanetz amiti
    private static final double DAWN_RAV_NAEH_ANGLE       = -26.0;  // Rav Avrohom Chaim Naeh
    private static final double DAWN_SEFER_BEIN_HASHMASHOT_ANGLE = -19.8; // Sefer Bein haShmashot
    private static final double DAWN_GRA_ANGLE            = -16.1;  // GR"A; also anchors MA sha'ah
    private static final double MISHEYAKIR_ANGLE          = -10.2;  // Chabad / Zmanei Halacha Lemaaseh
    private static final double MISHEYAKIR_SBH_ANGLE      = -11.5;  // Sefer Bein haShmashot
    private static final double MISHEYAKIR_NIVSHERET_ANGLE = -11.8; // Nivsheret
    private static final double TRUE_HORIZON_ANGLE        = -1.583; // Hanetz amiti / shkiah amitis
    private static final double NIGHTFALL_ANGLE           = -6.0;   // 3 small stars visible
    private static final double NIGHTFALL_MELAMED_ANGLE  = -7.083; // Melamed Lehoil
    private static final double HAVDALAH_ANGLE            = -8.5;   // Alter Rebbe / Igrot Moshe / Sefer Bein hashmashot

    private final LocalDate  date;
    private final Location  location;

    /**
     * @param date     Gregorian date for which to compute zmanim
     * @param location geographic location with its halachic properties (Israel / Jerusalem flags)
     */
    public Zmanim(final LocalDate date, final Location location) {
        this.date     = date;
        this.location = location;
    }

    // ── Low-level solar helpers (private, may return null) ────────────────────

    private static final double VISUAL_ANGLE = -0.8333;

    private ZoneOffset tzOffset() {
        return date.atStartOfDay(ZoneId.of(location.getTimezone())).getOffset();
    }

    private ZonedDateTime rise(double angle) {
        return NOAA.sunEvent(date, location.getLatitude(), location.getLongitude(), tzOffset(), angle, true, 0.0);
    }

    private ZonedDateTime set(double angle) {
        return NOAA.sunEvent(date, location.getLatitude(), location.getLongitude(), tzOffset(), angle, false, 0.0);
    }

    private ZonedDateTime sunsetOrNull()       { return set(VISUAL_ANGLE); }
    private ZonedDateTime hanetzAmitiOrNull()  { return rise(TRUE_HORIZON_ANGLE); }
    private ZonedDateTime shkiahAmitisOrNull() { return set(TRUE_HORIZON_ANGLE); }
    private ZonedDateTime nightfallOrNull()    { return set(NIGHTFALL_ANGLE); }
    private ZonedDateTime endOfShabbatOrNull() { return set(HAVDALAH_ANGLE); }

    private ZonedDateTime midnight() {
        return NOAA.solarNoon(date, location.getLongitude(), tzOffset()).plusHours(12);
    }

    private int candleMinutesBeforeSunset() { return location.isInJerusalem() ? 40 : 18; }

    // ── Sha'ah zmanit ─────────────────────────────────────────────────────────

    /**
     * Length of one sha'ah zmanit (halachic hour) in seconds.
     * Per Chabad: 1/12 of the period from hanetz amiti to shkiah amitis.
     * Returns 0 under polar conditions.
     */
    public long shaahZmanitSeconds() { return shaahZmanitSeconds(ShaahMethod.CHABAD_AMITI); }

    /**
     * Length of one sha'ah zmanit for the given method (see {@link ShaahMethod}).
     * Returns 0 under polar conditions.
     */
    public long shaahZmanitSeconds(final ShaahMethod method) {
        final ZonedDateTime r = shaahStart(method);
        final ZonedDateTime s = shaahEnd(method);
        if (r == null || s == null) return 0;
        return Duration.between(r, s).getSeconds() / 12;
    }

    // Halachic midnight at the start of today's halachic day (= 12h before
    // the "chatzot halaila" that closes today's daytime). Always computable
    // since it's a fixed offset from solar noon.
    private ZonedDateTime chatzotHaLailahStart() {
        return NOAA.solarNoon(date, location.getLongitude(), tzOffset()).minusHours(12);
    }

    // Start / end anchors for the sha'ah-zmanit span, per method.
    private ZonedDateTime shaahStart(final ShaahMethod method) {
        switch (method) {
            case CHABAD_AMITI:  return hanetzAmitiOrNull();
            case GRA_VISIBLE:   return rise(VISUAL_ANGLE);
            case MAGEN_AVRAHAM: {
                final ZonedDateTime alot = rise(DAWN_GRA_ANGLE);
                // Polar fallback: when the sun never dips to −16.1°, treat the
                // full night-to-night span (Chatzot HaLailah → Chatzot HaLailah)
                // as the halachic day, so sha'ah = 24h / 12 = 2h and portion-
                // of-day zmanim count from Chatzot HaLailah.
                return alot != null ? alot : chatzotHaLailahStart();
            }
            default:            throw new IllegalArgumentException("Unknown ShaahMethod: " + method);
        }
    }

    private ZonedDateTime shaahEnd(final ShaahMethod method) {
        switch (method) {
            case CHABAD_AMITI:  return shkiahAmitisOrNull();
            case GRA_VISIBLE:   return set(VISUAL_ANGLE);
            case MAGEN_AVRAHAM: {
                final ZonedDateTime tzet = set(DAWN_GRA_ANGLE);
                return tzet != null ? tzet : chatzotHaLailahStart().plusHours(24);
            }
            default:            throw new IllegalArgumentException("Unknown ShaahMethod: " + method);
        }
    }

    // Portion-of-day zmanim built by adding `count` sha'ot to the method's start
    // anchor. Returns a Zman with null time under polar conditions.
    private Zman portionOfDay(final ShaahMethod method, final double count) {
        final ZonedDateTime start = shaahStart(method);
        final long sha = shaahZmanitSeconds(method);
        return new Zman((start == null || sha == 0) ? null
                : start.plusSeconds((long)(sha * count)));
    }

    // ── Morning zmanim ────────────────────────────────────────────────────────

    /** Alot Hashachar: sun 16.9° below horizon (72 min before hanetz amiti per Chabad). */
    public Zman getDawn() { return new Zman(rise(DAWN_ANGLE)); }

    /** Alot Hashachar per Rav Avrohom Chaim Naeh: sun 26° below horizon. */
    public Zman getDawnRavNaeh() { return new Zman(rise(DAWN_RAV_NAEH_ANGLE)); }

    /** Alot Hashachar per Sefer Bein haShmashot: sun 19.8° below horizon. */
    public Zman getDawnSeferBeinHaShmashot() { return new Zman(rise(DAWN_SEFER_BEIN_HASHMASHOT_ANGLE)); }

    /** Alot Hashachar per GR"A: sun 16.1° below horizon. Also anchors the MA sha'ah zmanit. */
    public Zman getDawnGRA() { return new Zman(rise(DAWN_GRA_ANGLE)); }

    /** Netz Hachama: visible sunrise. */
    public Zman getSunrise() { return new Zman(rise(VISUAL_ANGLE)); }

    /**
     * Hanetz Amiti: true sunrise — sun 1.583° below horizon.
     * Start of the halachic day for sha'ah-zmanit calculations (Chabad).
     */
    public Zman getHanetzAmiti() { return new Zman(hanetzAmitiOrNull()); }

    /**
     * Misheyakir: earliest time to don tallis and tefillin.
     * Sun 10.2° below horizon (~45 min before sunrise in Jerusalem at equinox,
     * per Chabad / Zmanei Halacha Lemaaseh).
     */
    public Zman getMisheyakir() { return new Zman(rise(MISHEYAKIR_ANGLE)); }

    /** Misheyakir per Sefer Bein haShmashot: sun 11.5° below horizon. */
    public Zman getMisheyakirSeferBeinHaShmashot() { return new Zman(rise(MISHEYAKIR_SBH_ANGLE)); }

    /** Misheyakir per Nivsheret: sun 11.8° below horizon. */
    public Zman getMisheyakirNivsheret() { return new Zman(rise(MISHEYAKIR_NIVSHERET_ANGLE)); }

    /**
     * Sof Zman Krias Shema: 3 sha'ot zmaniot after hanetz amiti (per Chabad).
     * Returns a {@link Zman} with null time under polar conditions.
     */
    public Zman getLatestShema() { return getLatestShema(ShaahMethod.CHABAD_AMITI); }

    /** Sof Zman Krias Shema per the given {@link ShaahMethod}. */
    public Zman getLatestShema(final ShaahMethod method) { return portionOfDay(method, 3); }

    /**
     * Sof Zman Tefilla: latest time for morning Shacharit.
     * 4 sha'ot zmaniot after hanetz amiti (per Chabad).
     * Returns a {@link Zman} with null time under polar conditions.
     */
    public Zman getLatestShacharis() { return getLatestShacharis(ShaahMethod.CHABAD_AMITI); }

    /** Sof Zman Tefilla per the given {@link ShaahMethod}. */
    public Zman getLatestShacharis(final ShaahMethod method) { return portionOfDay(method, 4); }

    /**
     * Sof Zman Biur Chametz: latest time to burn chametz on Erev Pesach.
     * 5 sha'ot zmaniot after hanetz amiti.
     * Returns a {@link Zman} with null time under polar conditions.
     */
    public Zman getBurningChometz() { return getBurningChometz(ShaahMethod.CHABAD_AMITI); }

    /** Sof Zman Biur Chametz per the given {@link ShaahMethod}. */
    public Zman getBurningChometz(final ShaahMethod method) { return portionOfDay(method, 5); }

    // ── Midday and afternoon zmanim ───────────────────────────────────────────

    /**
     * Chatzot (halachic noon): midpoint between hanetz amiti and shkiah amitis.
     * Falls back to solar transit under polar conditions; always returns a non-null time.
     */
    public Zman getChatzot() {
        final ZonedDateTime r = hanetzAmitiOrNull();
        final ZonedDateTime s = shkiahAmitisOrNull();
        if (r == null || s == null) return new Zman(NOAA.solarNoon(date, location.getLongitude(), tzOffset()));
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
    public Zman getMinchaGedolah() { return getMinchaGedolah(ShaahMethod.CHABAD_AMITI); }

    /** Mincha Gedolah per the given {@link ShaahMethod}. */
    public Zman getMinchaGedolah(final ShaahMethod method) { return portionOfDay(method, 6.5); }

    /**
     * Mincha Ketana: optimal time for Mincha.
     * 9.5 sha'ot zmaniot after hanetz amiti.
     * Returns a {@link Zman} with null time under polar conditions.
     */
    public Zman getMinchaKetana() { return getMinchaKetana(ShaahMethod.CHABAD_AMITI); }

    /** Mincha Ketana per the given {@link ShaahMethod}. */
    public Zman getMinchaKetana(final ShaahMethod method) { return portionOfDay(method, 9.5); }

    /**
     * Plag HaMincha: 10.75 sha'ot zmaniot after hanetz amiti (1.25 sha'ot before shkiah amitis).
     * Returns a {@link Zman} with null time under polar conditions.
     */
    public Zman getPlagHaMincha() { return getPlagHaMincha(ShaahMethod.CHABAD_AMITI); }

    /** Plag HaMincha per the given {@link ShaahMethod}. */
    public Zman getPlagHaMincha(final ShaahMethod method) { return portionOfDay(method, 10.75); }

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
     * Tzait Hakochavim per Igrot Moshe / Sefer Bein hashmashot: sun 8.5°
     * below horizon (same solar angle as {@link #getNightfallAlterRebbe()};
     * exposed under a distinct name for clarity when citing the source opinion).
     */
    public Zman getNightfallIgrotMoshe() { return new Zman(endOfShabbatOrNull()); }

    /**
     * Tzait Hakochavim per Melamed Lehoil (מלמד להועיל): sun 7.083°
     * below horizon (3 medium stars, ~30 minutes as degrees after shkiah).
     */
    public Zman getNightfallMelamedLehoil() { return new Zman(set(NIGHTFALL_MELAMED_ANGLE)); }

    /**
     * Tzait Hakochavim with 3 medium stars: sun 6° below horizon after sunset.
     * Typically used as the end of fasts other than Yom Kippur.
     * Returns a {@link Zman} with null time under polar conditions.
     */
    public Zman getNightfallMediumStars() { return new Zman(nightfallOrNull()); }

    /**
     * Tzait Hakochavim per Rabbeinu Tam: 72 minutes after sunset.
     * Widely used as the end of fast days.
     * Returns a {@link Zman} with null time when sunset is unavailable.
     */
    public Zman getNightfallRabeinuTam() {
        final ZonedDateTime s = sunsetOrNull();
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
        final ZonedDateTime t = endOfShabbatOrNull();
        return t != null ? new Zman(t) : new Zman(midnight(), Flag.NO_NIGHTFALL);
    }

    private Zman getCandleLightingZmanInternal(final boolean todayIsRestDay, final boolean tomorrowIsYomTov) {
        if (todayIsRestDay && tomorrowIsYomTov) {
            // Alter Rebbe / Igrot Moshe tzait (−8.5°, three small stars) — matches
            // End of Shabbat / YT elsewhere in the API. Was previously the three-
            // medium-stars nightfall (−6°); that time is available separately via
            // getNightfallMediumStars().
            final ZonedDateTime t = endOfShabbatOrNull();
            return t != null
                ? new Zman(t, Flag.CANDLES_AFTER_NIGHTFALL)
                : new Zman(midnight(), Flag.CANDLES_AFTER_NIGHTFALL, Flag.NO_NIGHTFALL);
        }
        final int minutes = candleMinutesBeforeSunset();
        final ZonedDateTime s = sunsetOrNull();
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
     *   <li>Today is rest day + tomorrow is Yom Tov: Alter Rebbe tzait (−8.5°, three small stars);
     *       {@link Flag#CANDLES_AFTER_NIGHTFALL} set. Falls back to halachic midnight
     *       with {@link Flag#NO_NIGHTFALL}.</li>
     *   <li>Today is Yom Tov + tomorrow is Shabbat: before-sunset lighting from existing flame;
     *       {@link Flag#CANDLES_BEFORE_SHABBAT} set.</li>
     *   <li>Jerusalem: 40 minutes before sunset; {@link Flag#JERUSALEM_CANDLE_LIGHTING} set.</li>
     *   <li>Polar day (no sunset): halachic midnight used; {@link Flag#NO_SUNSET} set.</li>
     * </ul>
     *
     * <p>Rest-day and Yom Tov status are determined using the {@code inIsrael} flag
     * supplied to the {@link Location} at construction time.
     *
     * @return a {@link Zman} with appropriate flags, or {@code null} if tomorrow is not a rest day
     */
    public Zman getCandleLightingZman() {
        final IDate<GregorianCalendar> todayGreg    = ICalendar.GREGORIAN.fromYMD(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
        final IDate<GregorianCalendar> tomorrowGreg = todayGreg.addDays(1);
        final IDate<JewishCalendar>    todayHeb     = ICalendar.JEWISH.convert(todayGreg);
        final IDate<JewishCalendar>    tomorrowHeb  = ICalendar.JEWISH.convert(tomorrowGreg);
        if (!isRestDay(tomorrowHeb, location.isInIsrael())) return null;
        final boolean todayIsRest      = isRestDay(todayHeb, location.isInIsrael());
        final boolean tomorrowIsYomTov = tomorrowHeb.getDayOfWeek() != 7; // Saturday = 7 in IDate
        return getCandleLightingZmanInternal(todayIsRest, tomorrowIsYomTov);
    }

    /** Saturday (7 in IDate) or any Yom Tov applicable at the given location. */
    private static boolean isRestDay(final IDate<JewishCalendar> date, final boolean inIsrael) {
        if (date.getDayOfWeek() == 7) return true;
        for (JewishSpecialDay h : JewishSpecialDay.values()) {
            if (h.isYomTov() && h.applies(inIsrael) && h.matches(date)) return true;
        }
        return false;
    }
}
