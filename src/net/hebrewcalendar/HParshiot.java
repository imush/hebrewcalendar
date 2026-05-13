package net.hebrewcalendar;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

/**
 * Determines the weekly Torah reading (parsha) for any Shabbat.
 *
 * Usage:
 *   Parsha[] reading = HParshiot.getParsha(date, inIsrael);
 *   // null  → Yom Tov or Chol Hamoed (no regular reading)
 *   // length 1 → single parsha
 *   // length 2 → double parsha (two sections combined)
 */
public class HParshiot {

    /**
     * @param date     any HDate (Hebrew or Gregorian) that falls on a Saturday
     * @param inIsrael true for Eretz Israel schedule, false for Diaspora
     * @return array of 1 or 2 Parsha, or null if Yom Tov / Chol Hamoed Shabbat
     */
    public static Parsha[] getParsha(HDate date, boolean inIsrael) {
        HDate hDate  = HCalendar.HEBREW.convert(date);
        int hebrewYear = hDate.getYear();

        LocalDate thisShabbat  = toLocalDate(date);
        LocalDate firstShabbat = firstShabbatOfYear(hebrewYear);

        long weekOffset = ChronoUnit.WEEKS.between(firstShabbat, thisShabbat);
        if (weekOffset < 0) {
            // Shabbat falls before this year's first Shabbat — use previous year
            hebrewYear--;
            firstShabbat = firstShabbatOfYear(hebrewYear);
            weekOffset   = ChronoUnit.WEEKS.between(firstShabbat, thisShabbat);
        }

        List<Parsha[]> schedule = scheduleFor(hebrewYear, inIsrael);
        if (weekOffset < 0 || weekOffset >= schedule.size()) return null;
        return schedule.get((int) weekOffset);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private static LocalDate firstShabbatOfYear(int hebrewYear) {
        HDate rosh = HCalendar.HEBREW.fromYMD(hebrewYear, 7, 1);
        return toLocalDate(rosh).with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
    }

    private static LocalDate toLocalDate(HDate date) {
        HDate g = HCalendar.GREGORIAN.convert(date);
        return LocalDate.of(g.getYear(), g.getMonth(), g.getDay());
    }

    private static List<Parsha[]> scheduleFor(int year, boolean inIsrael) {
        HDate rosh   = HCalendar.HEBREW.fromYMD(year, 7, 1);
        HDate pesach = HCalendar.HEBREW.fromYMD(year, 1, 15);
        int r   = rosh.getDayOfWeek();
        int p   = pesach.getDayOfWeek();
        YearCheshvanKislevType yearType = HCalendar.HEBREW.getYearType(year);
        return HParshiotYearType.forYear(r, yearType, p).schedule(inIsrael);
    }
}
