package net.hebrewcalendar.learning;

import net.hebrewcalendar.ICalendar;
import net.hebrewcalendar.IDate;
import net.hebrewcalendar.JewishCalendar;

import java.time.LocalDate;

/**
 * Masechet Sotah daily-daf during Sefirat HaOmer (Chabad custom).
 *
 * <p>From 16 Nisan through 5 Sivan (both inclusive) — the 48 daytime periods
 * that each follow a night of counting omer — one daf of Bavli Sotah is
 * learned per day, matching the count of omer counted the previous night
 * plus one. So 16 Nisan reads daf 2, 17 Nisan daf 3, …, 5 Sivan
 * (49th count, erev Shavuot) reads daf 49 (the last daf).
 *
 * <p>Applies identically to Israel and Diaspora — sefirah counting starts
 * the same night in both.
 */
public final class SefirahSotah {

    private SefirahSotah() {}

    private static final String TRACTATE_HE = "סוטה";

    public static final class Result {
        private final int daf;
        private final int omerCount;   // count of omer counted the previous night
        Result(int daf, int omerCount) { this.daf = daf; this.omerCount = omerCount; }
        public int daf()       { return daf; }
        /** Number of days of omer counted the previous night (1..48). */
        public int omerCount() { return omerCount; }
        /** Daf numeral only in Hebrew, e.g. {@code "ב׳"}. */
        public String dafHe()   { return Gematria.of(daf); }
        /** Hebrew label, e.g. {@code "סוטה ב׳"}. */
        public String labelHe() { return TRACTATE_HE + " " + dafHe(); }
        /** English label, e.g. {@code "Sotah 2"}. */
        public String label()   { return "Sotah " + daf; }
    }

    /**
     * @return the day's Sotah reading, or {@code null} outside Sefirah.
     */
    public static Result forDate(LocalDate date) {
        IDate<JewishCalendar> jd = ICalendar.JEWISH.convert(
            ICalendar.GREGORIAN.fromYMD(date.getYear(), date.getMonthValue(), date.getDayOfMonth()));
        int year = jd.getYear();
        IDate<JewishCalendar> nisan15 = ICalendar.JEWISH.fromYMD(year, 1, 15);
        // Absolute day difference: 16 Nisan is 1 day after 15 Nisan.
        long diff = ICalendar.JEWISH.convert(jd) == jd
                ? diffDays(nisan15, jd)
                : 0;
        if (diff < 1 || diff > 48) return null;   // outside 16 Nisan .. 5 Sivan
        int omerCount = (int) diff;
        return new Result(omerCount + 1, omerCount);
    }

    private static long diffDays(IDate<JewishCalendar> a, IDate<JewishCalendar> b) {
        IDate<?> ga = ICalendar.GREGORIAN.convert(a);
        IDate<?> gb = ICalendar.GREGORIAN.convert(b);
        LocalDate la = LocalDate.of(ga.getYear(), ga.getMonth(), ga.getDay());
        LocalDate lb = LocalDate.of(gb.getYear(), gb.getMonth(), gb.getDay());
        return lb.toEpochDay() - la.toEpochDay();
    }
}
