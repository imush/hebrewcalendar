package net.hebrewcalendar.impl;

/**
 * Tekufa (Jewish solar season) calculator.
 *
 * <p>Two opinions are provided as public singletons:
 * <ul>
 *   <li>{@link #RAV_ADA} — year = 235/19 months; season = 91d 7h 519p 31r</li>
 *   <li>{@link #SHMUEL}  — year = 365.25 days;  season = 91d 7h 540p</li>
 * </ul>
 *
 * <p>Epoch derivation: both opinions subtract their {@code nisanOffset} Duration from
 * firstMoladNisan(year 1) = JewishTime.Moment(179, 9, 642) to obtain the first Tekufat Nisan
 * of Shnat Tohu (שנת תהו, Hebrew year 1, the Year of Tohu; Bereishit 1:2).
 */
public final class TekufaCalc {

    /** Tekufa according to Rav Ada bar Ahavah (year = 235/19 months). */
    public static final TekufaCalc RAV_ADA = new TekufaCalc(
            new JewishTime.Duration(0, 9, 642),
            new JewishTime.Duration(91, 7, 519, 31));

    /**
     * Tekufa according to Shmuel (year = 365.25 Julian days).
     *
     * <p>Verification: year 5769 → Wednesday April 8, 2009 (Birkat HaChama);
     * year 5786 → Wednesday April 8, 2026 (887 weeks later).
     */
    public static final TekufaCalc SHMUEL = new TekufaCalc(
            new JewishTime.Duration(7, 9, 642),
            new JewishTime.Duration(91, 7, 540));

    private final JewishTime.Duration nisanOffset;
    private final JewishTime.Duration seasonLength;

    private TekufaCalc(final JewishTime.Duration nisanOffset, final JewishTime.Duration seasonLength) {
        this.nisanOffset  = nisanOffset;
        this.seasonLength = seasonLength;
    }

    /**
     * Returns the JewishTime of the tekufa for the given Hebrew year and season index.
     *
     * @param opinion     {@link #RAV_ADA} or {@link #SHMUEL}
     * @param hebrewYear  Hebrew year (1 = Shnat Tohu)
     * @param seasonIndex 0=Nisan, 1=Tammuz, 2=Tishrei, 3=Tevet
     */
    public static JewishTime.Moment getTekufaTime(final TekufaCalc opinion, final int hebrewYear, final int seasonIndex) {
        final JewishTime.Moment firstMoladNisan  = JewishCalendarImpl.INSTANCE.molad(1, 1);
        final JewishTime.Moment firstTekufaNisan = firstMoladNisan.subtract(opinion.nisanOffset);
        final int n = (hebrewYear - 1) * 4 + seasonIndex;
        return firstTekufaNisan.add(opinion.seasonLength.times(n));
    }
}
