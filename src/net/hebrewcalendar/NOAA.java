package net.hebrewcalendar;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * Solar position calculations using the NOAA algorithm.
 *
 * <p>All public methods return {@link ZonedDateTime} (or {@code null} for polar
 * conditions where the sun never crosses the requested angle).  Times outside the
 * calendar day (e.g. nightfall past midnight in polar summer) are returned as a
 * {@code ZonedDateTime} on the following calendar day — they are never clamped to
 * {@code null} as long as the sun physically crosses the angle.
 *
 * <p>The UTC offset supplied must be the fixed standard-time offset for the date
 * (callers are responsible for choosing the correct DST-aware offset).
 */
public final class NOAA {

    private NOAA() {}

    private static final double DEG2RAD = Math.PI / 180.0;
    private static final double RAD2DEG = 180.0 / Math.PI;

    // ── private helpers ───────────────────────────────────────────────────────

    /** Julian Day Number for a Gregorian date (noon UT). */
    private static double julianDay(int year, int month, final int day) {
        if (month <= 2) { year--; month += 12; }
        final int A = year / 100;
        final int B = 2 - A + A / 4;
        return (int)(365.25 * (year + 4716)) + (int)(30.6001 * (month + 1)) + day + B - 1524.5;
    }

    /**
     * Solar noon in minutes from local midnight; fills {@code declOut[0]} with
     * declination in radians.  Pass {@code null} for {@code declOut} to skip.
     */
    private static double solarNoonMinutes(final int year, final int month, final int day,
                                   final double lon, final double tzOffsetHours,
                                   final double[] declOut) {
        final double JD = julianDay(year, month, day);
        final double JC = (JD - 2451545.0) / 36525.0;

        final double L0   = mod360(280.46646 + JC * (36000.76983 + JC * 0.0003032));
        final double M    = 357.52911 + JC * (35999.05029 - 0.0001537 * JC);
        final double Mrad = M * DEG2RAD;
        final double C    = Math.sin(Mrad)   * (1.914602 - JC * (0.004817 + 0.000014 * JC))
                    + Math.sin(2*Mrad) * (0.019993 - 0.000101 * JC)
                    + Math.sin(3*Mrad) * 0.000289;
        final double sunLon = L0 + C;
        final double omega  = 125.04 - 1934.136 * JC;
        final double lambda = sunLon - 0.00569 - 0.00478 * Math.sin(omega * DEG2RAD);
        final double eps0   = 23.0 + (26.0 + (21.448 - JC * (46.8150 + JC * (0.00059 - JC * 0.001813))) / 60.0) / 60.0;
        final double eps    = eps0 + 0.00256 * Math.cos(omega * DEG2RAD);

        if (declOut != null)
            declOut[0] = Math.asin(Math.sin(eps * DEG2RAD) * Math.sin(lambda * DEG2RAD));

        double yVar = Math.tan(eps / 2.0 * DEG2RAD);
        yVar *= yVar;
        final double L0r = L0 * DEG2RAD;
        final double Mr  = M  * DEG2RAD;
        final double eqt = 4.0 * RAD2DEG * (
                  yVar * Math.sin(2*L0r)
                - 2.0 * 0.016708634 * Math.sin(Mr)
                + 4.0 * 0.016708634 * yVar * Math.sin(Mr) * Math.cos(2*L0r)
                - 0.5 * yVar * yVar * Math.sin(4*L0r)
                - 1.25 * 0.016708634 * 0.016708634 * Math.sin(2*Mr));

        return 720.0 - 4.0 * lon - eqt + tzOffsetHours * 60.0;
    }

    /**
     * Hour angle in degrees for the given elevation.
     * Returns {@link Double#NaN} if the sun never reaches that angle.
     */
    private static double hourAngleForElevation(final double latRad, final double declRad, final double elevDeg) {
        final double elevRad = elevDeg * DEG2RAD;
        final double cosHa   = (Math.sin(elevRad) - Math.sin(latRad) * Math.sin(declRad))
                       / (Math.cos(latRad) * Math.cos(declRad));
        if (cosHa < -1.0 || cosHa > 1.0) return Double.NaN;
        return Math.acos(cosHa) * RAD2DEG;
    }

    /** Convert minutes from local midnight to a {@link ZonedDateTime}. */
    private static ZonedDateTime minutesToZdt(final LocalDate date, final double minutes, final ZoneOffset tz) {
        final long secs = Math.round(minutes * 60.0);
        return date.atStartOfDay(tz).plusSeconds(secs);
    }

    private static double mod360(double x) {
        x = x % 360.0;
        return x < 0 ? x + 360.0 : x;
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Solar noon (transit) as a {@link ZonedDateTime}.
     * Solar noon depends only on longitude and date, not latitude.
     *
     * @param date  Gregorian date
     * @param lon   longitude in decimal degrees (east positive)
     * @param tz    UTC offset for this date (DST-aware if needed)
     */
    public static ZonedDateTime solarNoon(final LocalDate date, final double lon, final ZoneOffset tz) {
        final double tzH  = tz.getTotalSeconds() / 3600.0;
        final double noon = solarNoonMinutes(date.getYear(), date.getMonthValue(), date.getDayOfMonth(),
                                       lon, tzH, null);
        return minutesToZdt(date, noon, tz);
    }

    /**
     * Time when the sun crosses {@code elevDeg} on the rise or set side.
     *
     * <p>An observer above sea level sees a depressed horizon; the depression
     * δ = √(2h/R⊕) radians is subtracted from {@code elevDeg} so that all event
     * times shift accordingly (sunrise earlier, sunset later).
     * Pass {@code observerElevMeters = 0} to disable the correction.
     *
     * <p>Returns {@code null} only when the sun never reaches the effective angle
     * (polar day/night).  Times past local midnight are returned as a
     * {@link ZonedDateTime} on the next calendar day rather than {@code null}.
     *
     * @param date               Gregorian date
     * @param lat                latitude in decimal degrees (north positive)
     * @param lon                longitude in decimal degrees (east positive)
     * @param tz                 UTC offset for this date (DST-aware if needed)
     * @param elevDeg            target elevation in degrees; negative = below horizon
     *                           (e.g. {@code -0.8333} for visual sunrise/sunset)
     * @param isRise             {@code true} for sunrise side, {@code false} for sunset side
     * @param observerElevMeters observer elevation above sea level in metres (≥ 0)
     * @return the event time, or {@code null} for polar conditions
     */
    public static ZonedDateTime sunEvent(final LocalDate date, final double lat, final double lon,
                                         final ZoneOffset tz, final double elevDeg, final boolean isRise,
                                         final double observerElevMeters) {
        double effectiveElev = elevDeg;
        if (observerElevMeters > 0.0) {
            final double dip = RAD2DEG * Math.sqrt(2.0 * observerElevMeters / 6371000.0);
            effectiveElev -= dip;
        }
        final double tzH  = tz.getTotalSeconds() / 3600.0;
        final int y = date.getYear(), m = date.getMonthValue(), d = date.getDayOfMonth();
        final double[] decl = new double[1];
        final double noon = solarNoonMinutes(y, m, d, lon, tzH, decl);
        final double ha   = hourAngleForElevation(lat * DEG2RAD, decl[0], effectiveElev);
        if (Double.isNaN(ha)) return null;
        final double minutes = isRise ? noon - ha * 4.0 : noon + ha * 4.0;
        return minutesToZdt(date, minutes, tz);
    }
}
