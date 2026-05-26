package net.hebrewcalendar;

import java.util.ArrayList;
import java.util.List;

/**
 * Extension of {@link ICalendar} with Hebrew-calendar-specific operations
 * such as sefira counting, tekufa (solar season) calculations, and Torah reading schedule.
 */
public interface JewishCalendar
    extends ICalendar
{
    /**
     * The four Jewish solar seasons (tekufot), in calendar order starting from spring.
     */
    enum Season {
        /** Tekufat Nisan — vernal equinox (spring). */
        NISAN(0),
        /** Tekufat Tammuz — summer solstice. */
        TAMMUZ(1),
        /** Tekufat Tishrei — autumnal equinox (fall). */
        TISHREI(2),
        /** Tekufat Tevet — winter solstice. */
        TEVET(3);

        private final int index;
        Season(int index) { this.index = index; }

        /** Returns the 0-based ordinal used in tekufa offset arithmetic. */
        public int getIndex() { return index; }
    }

    /**
     * Get sefira count for the day; return 0 where not applicable
     * @param date day to test
     * @return sefira count (1-49), or 0 if it is not between Pesach and Shavuot
     */
    int getSefira(IDate date);

    /**
     * Returns the molad (lunar conjunction) time for the given Hebrew year and month.
     *
     * <p>The returned {@link JewishMoment} uses Jewish calendar time units:
     * day count from the epoch, hours (0–23 from 6 pm), chalakim (0–1079), regaim (0–75).
     * Convert to civil time by adding 18 hours (6 pm offset) to the hour component.
     *
     * @param year  Hebrew year
     * @param month Hebrew month number (see {@link JewishMonth})
     * @return the molad time as an {@link JewishMoment}
     */
    JewishMoment moladOfMonth(int year, int month);

    /**
     * Returns the Hebrew date whose day component equals that of the given moment.
     *
     * @param moment a {@link JewishMoment} such as a molad or tekufa time
     * @return the corresponding Hebrew {@link IDate}
     */
    IDate fromMoment(JewishMoment moment);

    /**
     * Returns the time of the requested tekufa according to Rav Ada bar Ahavah.
     *
     * <p>Rav Ada's solar year equals 235/19 lunar months. The epoch (first Tekufat Nisan,
     * year 1) is derived by subtracting 9h 642p from the first Molad Nisan.
     *
     * @param hebrewYear Hebrew year number
     * @param season     one of the four {@link Season} values
     * @return the {@link JewishMoment} of that tekufa
     */
    JewishMoment getTekufatRavAda(int hebrewYear, Season season);

    /**
     * Returns the time of the requested tekufa according to Shmuel.
     *
     * <p>Shmuel's solar year is exactly 365 days and 6 hours (the Julian year).
     * One season = 91d 7h 540p. Governs the 28-year Birkat HaChama cycle
     * and the start of Tal u'Matar in the diaspora.
     *
     * @param hebrewYear Hebrew year number
     * @param season     one of the four {@link Season} values
     * @return the {@link JewishMoment} of that tekufa
     */
    JewishMoment getTekufatShmuel(int hebrewYear, Season season);

    /**
     * Returns {@code true} if the given Hebrew year is a Birkat HaChama year.
     *
     * <p>Birkat HaChama is recited once every 28 years, when Tekufat Nisan according
     * to Shmuel falls on Wednesday at the same hour as at Creation. Years satisfying
     * {@code hebrewYear % 28 == 1} are Birkat HaChama years.
     * The most recent occurrence was year 5769 (April 8, 2009).
     *
     * @param hebrewYear Hebrew year number
     * @return {@code true} if Birkat HaChama is recited this year
     */
    default boolean isBirkatHaChamaYear(int hebrewYear) {
        return hebrewYear % 28 == 1;
    }

    /**
     * Returns the weekly Torah reading (parsha) for the given Shabbat date.
     *
     * @param date     any IDate that falls on a Saturday
     * @param inIsrael true for Eretz Israel schedule, false for Diaspora
     * @return list of 1 {@link Parsha} (regular week) or 2 (double portion);
     *         empty list on a Shabbat that falls on Yom Tov or Chol Hamoed
     */
    List<Parsha> getParsha(IDate date, boolean inIsrael);

    /**
     * Returns the special maftir Torah readings for the given Shabbat date.
     *
     * <p>Returns an empty list if the date is not Shabbat. Otherwise, returns the
     * names of any applicable special readings: Rosh Chodesh, Chanukah, Yom Tov,
     * Chol Hamoed, or one of the four Arba Parshiyot (Shekalim, Zachor, Para, Hachodesh).
     *
     * @param date     date to check (any calendar)
     * @param inIsrael true for Eretz Israel schedule, false for Diaspora
     * @return list of special maftir names; empty if not Shabbat or no special reading
     */
    default List<String> specialMaftir(IDate date, boolean inIsrael) {
        if (date.getDayOfWeek() != 7) return List.of();
        List<String> result = new ArrayList<>();
        boolean chanukahAdded = false;
        for (JewishSpecialDay sd : JewishSpecialDay.values()) {
            if (!sd.applies(inIsrael) || !sd.matches(date)) continue;
            if (sd == JewishSpecialDay.ROSH_CHODESH) {
                result.add(sd.getName());
            } else if (sd.isChanukah()) {
                if (!chanukahAdded) { result.add("Chanukah"); chanukahAdded = true; }
            } else if (sd.isYomTov()) {
                result.add(sd.getName());
            } else if (sd.isCholHamoed()) {
                result.add(sd.getName());
            } else if (sd.isArbaParshiyot()) {
                result.add(sd.getName());
            }
        }
        return result;
    }
}
