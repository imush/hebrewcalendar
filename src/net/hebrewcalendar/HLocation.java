package net.hebrewcalendar;

/**
 * Immutable geographic location used to compute halachic times.
 * Construct once per request and pass to {@link Zmanim}.
 */
public class HLocation {

    private final double latitude;
    private final double longitude;
    private final double elevationMeters;
    private final String timezone;
    private final boolean inIsrael;
    private final boolean inJerusalem;

    /**
     * @param latitude        geographic latitude in decimal degrees (negative = south)
     * @param longitude       geographic longitude in decimal degrees (negative = west)
     * @param elevationMeters elevation above sea level in metres; most halachic opinions
     *                        ignore elevation and callers should pass {@code 0.0} unless they
     *                        specifically intend elevation-adjusted times — it is passed through
     *                        to the solar calculator and will shift all computed times
     * @param timezone        IANA timezone identifier (e.g. {@code "America/New_York"})
     * @param inIsrael        true for Eretz Israel — determines which holidays apply
     * @param inJerusalem     true when candle lighting should follow the Jerusalem custom (40 min before sunset)
     */
    public HLocation(double latitude, double longitude, double elevationMeters, String timezone,
                     boolean inIsrael, boolean inJerusalem) {
        this.latitude       = latitude;
        this.longitude      = longitude;
        this.elevationMeters = elevationMeters;
        this.timezone       = timezone;
        this.inIsrael       = inIsrael;
        this.inJerusalem    = inJerusalem;
    }

    public double  getLatitude()        { return latitude; }
    public double  getLongitude()       { return longitude; }
    public double  getElevationMeters() { return elevationMeters; }
    public String  getTimezone()        { return timezone; }
    public boolean isInIsrael()         { return inIsrael; }
    public boolean isInJerusalem()      { return inJerusalem; }

    /**
     * Returns true when the given coordinates fall within the city of Jerusalem.
     * Callers may use this to set the {@code inJerusalem} constructor argument.
     */
    public static boolean isJerusalem(double latitude, double longitude) {
        return latitude >= 31.71 && latitude <= 31.85
            && longitude >= 35.13 && longitude <= 35.28;
    }
}
