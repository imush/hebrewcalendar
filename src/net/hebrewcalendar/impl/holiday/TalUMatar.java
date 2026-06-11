package net.hebrewcalendar.impl.holiday;

import net.hebrewcalendar.ICalendar;
import net.hebrewcalendar.IDate;
import net.hebrewcalendar.JewishCalendar;
import net.hebrewcalendar.JewishMoment;
import net.hebrewcalendar.impl.NoSuchHolidayException;

/**
 * Tal U'Matar Livracha — prayer for dew and rain.
 *
 * <p>In Israel: begins on 7 Cheshvan (month 8, day 7) each year.
 * In the Diaspora: begins on the 60th day after Tekufat Tishrei according to Shmuel
 * (i.e. addDays(59) from the day of the tekufa, counting the tekufa day as day 1).
 *
 * <p>If the computed date falls on Shabbat (day 7), it is postponed to the following day.
 */
public class TalUMatar extends AbstractRecurringSpecialDay<JewishCalendar> {

    private static final JewishCalendar JEWISH = ICalendar.JEWISH;
    private final boolean inIsrael;

    public TalUMatar(boolean inIsrael, String name) {
        super(ICalendar.JEWISH, name);
        this.inIsrael = inIsrael;
    }

    @Override
    public boolean matches(IDate<JewishCalendar> date) {
        return date.compareTo(computeDate(date.getYear())) == 0;
    }

    @Override
    public IDate<JewishCalendar> getNextOccurrence(IDate<JewishCalendar> date, boolean strict) throws NoSuchHolidayException {
        IDate<JewishCalendar> start = strict ? date.addDays(1) : date;
        int y = start.getYear();
        IDate<JewishCalendar> candidate = computeDate(y);
        if (candidate.before(start)) candidate = computeDate(y + 1);
        return candidate;
    }

    @Override
    public IDate<JewishCalendar> getPrevOccurrence(IDate<JewishCalendar> date, boolean strict) throws NoSuchHolidayException {
        IDate<JewishCalendar> end = strict ? date.addDays(-1) : date;
        int y = end.getYear();
        IDate<JewishCalendar> candidate = computeDate(y);
        if (candidate.after(end)) candidate = computeDate(y - 1);
        return candidate;
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private IDate<JewishCalendar> computeDate(int hebrewYear) {
        IDate<JewishCalendar> base;
        if (inIsrael) {
            base = ICalendar.JEWISH.fromYMD(hebrewYear, 8, 7);
        } else {
            // getTekufatShmuel(Y, TISHREI) gives the tekufa in the fall of the same Gregorian year
            // as Nisan of year Y — i.e. the tekufa at the END of year Y, not the start.
            // For year Y we need the tekufa at the START of year Y, which is getTekufatShmuel(Y-1, TISHREI).
            JewishMoment tekufa = JEWISH.getTekufatShmuel(hebrewYear - 1, JewishCalendar.Season.TISHREI);
            base = JEWISH.fromMoment(tekufa).addDays(59);
        }
        if (base.getDayOfWeek() == 7) base = base.addDays(1);
        return base;
    }
}
