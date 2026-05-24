package net.hebrewcalendar;

/**
 * A read-only view of an absolute moment in the Jewish calendar system.
 *
 * <p>Units: 1 day = 24 hours; 1 hour = 1080 chalakim (parts); 1 chelek = 76 regaim.
 * Regaim are only used for Rav Ada tekufa calculations; for molad computations
 * {@link #getRega()} returns 0.
 *
 * <p>Instances are returned by {@link JewishCalendar#getTekufatRavAda} and
 * {@link JewishCalendar#getTekufatShmuel} for tekufa times and are accepted
 * by {@link JewishCalendar#fromMoment} to convert to a calendar date.
 */
public interface JewishMoment
{
    /** Days elapsed since the epoch. */
    long getDay();

    /** Hour within the day (0–23). */
    int getHour();

    /** Chalakim (parts) within the hour (0–1079). */
    int getPart();

    /** Regaim within the chelek (0–75); 0 for all non-Rav-Ada calculations. */
    int getRega();
}
