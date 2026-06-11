package net.hebrewcalendar.impl;

import net.hebrewcalendar.ICalendar;
import net.hebrewcalendar.IDate;
import net.hebrewcalendar.Parsha;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Collections;
import java.util.List;

/**
 * Determines the weekly Torah reading (parsha) for any Shabbat.
 *
 * Usage:
 *   List&lt;Parsha&gt; reading = Parshiot.getParsha(date, inIsrael);
 *   // empty list → Yom Tov or Chol Hamoed (no regular reading)
 *   // size 1 → single parsha
 *   // size 2 → double parsha (two sections combined)
 */
public class Parshiot {

    /**
     * @param date     any IDate (Hebrew or Gregorian) that falls on a Saturday
     * @param inIsrael true for Eretz Israel schedule, false for Diaspora
     * @return list of 1 or 2 Parshiot, or empty list if Yom Tov / Chol Hamoed Shabbat
     * @throws IllegalArgumentException if date is not a Saturday
     */
    public static List<Parsha> getParsha(IDate<net.hebrewcalendar.JewishCalendar> date, boolean inIsrael) {
        if (date.getDayOfWeek() != 7)
            throw new IllegalArgumentException("getParsha requires a Saturday; got day-of-week " + date.getDayOfWeek());

        int hebrewYear = date.getYear();

        LocalDate thisShabbat  = toLocalDate(date);
        LocalDate firstShabbat = firstShabbatOfYear(hebrewYear);

        long weekOffset = ChronoUnit.WEEKS.between(firstShabbat, thisShabbat);
        if (weekOffset < 0) {
            // Shabbat falls before this year's first Shabbat — use previous year
            hebrewYear--;
            firstShabbat = firstShabbatOfYear(hebrewYear);
            weekOffset   = ChronoUnit.WEEKS.between(firstShabbat, thisShabbat);
        }

        List<List<Parsha>> schedule = scheduleFor(hebrewYear, inIsrael);
        if (weekOffset < 0 || weekOffset >= schedule.size()) return Collections.emptyList();
        return schedule.get((int) weekOffset);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private static LocalDate firstShabbatOfYear(int hebrewYear) {
        IDate<net.hebrewcalendar.JewishCalendar> rosh = ICalendar.JEWISH.fromYMD(hebrewYear, 7, 1);
        return toLocalDate(rosh).with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
    }

    private static LocalDate toLocalDate(IDate<?> date) {
        IDate<?> g = ICalendar.GREGORIAN.convert(date);
        return LocalDate.of(g.getYear(), g.getMonth(), g.getDay());
    }

    private static List<List<Parsha>> scheduleFor(int year, boolean inIsrael) {
        IDate<net.hebrewcalendar.JewishCalendar> rosh   = ICalendar.JEWISH.fromYMD(year, 7, 1);
        IDate<net.hebrewcalendar.JewishCalendar> pesach = ICalendar.JEWISH.fromYMD(year, 1, 15);
        int r   = rosh.getDayOfWeek();
        int p   = pesach.getDayOfWeek();
        YearCheshvanKislevType yearType = JewishCalendarImpl.INSTANCE.getYearType(year);
        return ParshiotYearType.forYear(r, yearType, p).schedule(inIsrael);
    }
}
