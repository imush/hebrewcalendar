package net.hebrewcalendar;

import org.shredzone.commons.suncalc.SunTimes;

import java.time.LocalDate;
import java.time.ZonedDateTime;

/**
 * Computes halachic times (zmanim) for a given date and geographic location.
 *
 * <p>Angles used:
 * <ul>
 *   <li>Sunrise / Shkiah: visual horizon (accounts for refraction and solar disc radius)</li>
 *   <li>Dawn (Alot Hashachar): sun 16.1° below horizon before sunrise</li>
 *   <li>Tzait Hakochavim (nightfall): sun 6° below horizon after sunset</li>
 *   <li>End of Shabbat (Havdalah): sun 8.5° below horizon after sunset</li>
 * </ul>
 */
public class Zmanim {

    private static final double DAWN_ANGLE       = -16.1;
    private static final double NIGHTFALL_ANGLE  = -6.0;
    private static final double HAVDALAH_ANGLE   = -8.5;

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

    /** Alot Hashachar: sun 16.1° below horizon before sunrise. */
    public ZonedDateTime getDawn() { return rise(DAWN_ANGLE); }

    /** Netz Hachama: visible sunrise. */
    public ZonedDateTime getSunrise() { return rise(SunTimes.Twilight.VISUAL); }

    /** Shkiah: visible sunset. */
    public ZonedDateTime getSunset() { return set(SunTimes.Twilight.VISUAL); }

    /** Candle lighting: 18 minutes before shkiah. */
    public ZonedDateTime getCandleLighting() {
        ZonedDateTime s = getSunset();
        return s == null ? null : s.minusMinutes(18);
    }

    /** Tzait Hakochavim: sun 6° below horizon after sunset. */
    public ZonedDateTime getNightfall() { return set(NIGHTFALL_ANGLE); }

    /** End of Shabbat / Havdalah: sun 8.5° below horizon after sunset. */
    public ZonedDateTime getEndOfShabbat() { return set(HAVDALAH_ANGLE); }
}
