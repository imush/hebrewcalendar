package net.hebrewcalendar.impl.holiday;

import net.hebrewcalendar.ICalendar;
import net.hebrewcalendar.IDate;
import net.hebrewcalendar.JewishCalendar;
import net.hebrewcalendar.JewishMoment;
import net.hebrewcalendar.impl.NoSuchHolidayException;

/**
 * Birkat HaChama (ברכת החמה) — the Blessing of the Sun, recited once every 28 years
 * when Tekufat Nisan according to Shmuel falls at the same hour as at Creation (Tuesday 6pm).
 *
 * <p>Occurs in Hebrew years where {@code year % 28 == 1}.
 * The most recent occurrence was 14 Nisan 5769 (April 8, 2009).
 *
 * <p>{@link #getNextOccurrence} and {@link #getPrevOccurrence} jump directly to the relevant
 * 28-year cycle rather than iterating day by day.
 */
public class BirkatHachama extends AbstractRecurringSpecialDay {

    private static final JewishCalendar JEWISH = (JewishCalendar) ICalendar.JEWISH;

    public BirkatHachama(String name) {
        super(ICalendar.JEWISH, name);
    }

    @Override
    public boolean matches(IDate date) {
        IDate hDate = ICalendar.JEWISH.convert(date);
        int year = hDate.getYear();
        if (!JEWISH.isBirkatHaChamaYear(year)) return false;
        return hDate.compareTo(tekufaDateForYear(year)) == 0;
    }

    @Override
    public IDate getNextOccurrence(IDate date, boolean strict) throws NoSuchHolidayException {
        IDate start = strict ? date.addDays(1) : date;
        int y = ICalendar.JEWISH.convert(start).getYear();

        int bhYear = nearestAtOrAfter(y);
        IDate tekufaDate = tekufaDateForYear(bhYear);

        if (tekufaDate.before(start)) {
            bhYear += 28;
            tekufaDate = tekufaDateForYear(bhYear);
        }

        return tekufaDate;
    }

    @Override
    public IDate getPrevOccurrence(IDate date, boolean strict) throws NoSuchHolidayException {
        IDate end = strict ? date.addDays(-1) : date;
        int y = ICalendar.JEWISH.convert(end).getYear();

        int bhYear = nearestAtOrBefore(y);
        IDate tekufaDate = tekufaDateForYear(bhYear);

        if (tekufaDate.after(end)) {
            bhYear -= 28;
            tekufaDate = tekufaDateForYear(bhYear);
        }

        return tekufaDate;
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private static IDate tekufaDateForYear(int year) {
        JewishMoment tekufa = JEWISH.getTekufatShmuel(year, JewishCalendar.Season.NISAN);
        return JEWISH.fromMoment(tekufa);
    }

    /** Nearest BH year (y % 28 == 1) that is >= y. */
    private static int nearestAtOrAfter(int y) {
        return y + ((1 - y % 28 + 28) % 28);
    }

    /** Nearest BH year (y % 28 == 1) that is <= y. */
    private static int nearestAtOrBefore(int y) {
        return y - ((y % 28 - 1 + 28) % 28);
    }
}
