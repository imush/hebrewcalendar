package net.hebrewcalendar.learning;

import net.hebrewcalendar.ICalendar;
import net.hebrewcalendar.IDate;
import net.hebrewcalendar.JewishCalendar;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Pirkei Avot — read on Shabbat afternoons in the summer.
 *
 * <p><b>Base schedule (post-Pesach through pre-Shavuot).</b>
 * Starting on the Shabbat after Pesach, one chapter (perek) per week, in
 * order 1..6. In a year where the count of Shabbatot in that window is 7
 * (which happens in Eretz Israel, since Pesach is one day shorter), the
 * first Shabbat is skipped so the schedule stays aligned to Shavuot. If
 * only 5 Shabbatot fall in the window, the last one doubles (5-6).
 *
 * <p><b>Chabad extended schedule (optional).</b> Reading continues through
 * the summer up to the Shabbat before Rosh Hashanah, cycling chapters 1..6
 * on each successive Shabbat, skipping yom-tov Shabbatot. The <b>last three
 * Shabbatot before Rosh Hashanah</b> are always doubled — chapters 1-2,
 * 3-4, and 5-6 — so the year concludes with perek 6 immediately before
 * Rosh Hashanah.
 */
public final class PirkeiAvot {

    private PirkeiAvot() {}

    /** Chapter list (1..6) for a given Shabbat, or the null-object for non-Shabbat/off-season. */
    public static final class Result {
        private final List<Integer> chapters;   // 1..6, sorted; size 1 (normal) or 2 (doubled)
        Result(List<Integer> chapters) { this.chapters = List.copyOf(chapters); }
        public List<Integer> chapters() { return chapters; }
        /** Chapter numeral(s) only in Hebrew, e.g. {@code "ד׳"} or {@code "ג׳-ד׳"}. */
        public String chaptersHe() {
            if (chapters.size() == 1) return Gematria.of(chapters.get(0));
            return Gematria.of(chapters.get(0)) + "-" + Gematria.of(chapters.get(chapters.size() - 1));
        }
        /** Full Hebrew label, e.g. {@code "פרקי אבות ד׳"} or {@code "פרקי אבות ג׳-ד׳"}. */
        public String labelHe() { return "פרקי אבות " + chaptersHe(); }
    }

    /**
     * @param date        any Gregorian date (must be a Shabbat, else returns null)
     * @param inIsrael    true for Eretz Israel calendar (affects Pesach length + Shavuot skip)
     * @param extended    Chabad custom of continuing through the summer to Shabbat-before-RH
     * @return chapter list for this Shabbat, or {@code null} outside the season
     */
    public static Result forDate(LocalDate date, boolean inIsrael, boolean extended) {
        // Must be a Shabbat.
        if (date.getDayOfWeek().getValue() != 6) return null;   // 6 = SATURDAY in java.time
        IDate<JewishCalendar> jd = ICalendar.JEWISH.convert(
            ICalendar.GREGORIAN.fromYMD(date.getYear(), date.getMonthValue(), date.getDayOfMonth()));
        int year = jd.getYear();

        // Determine which Hebrew year's Avot-season this Shabbat belongs to.
        // The season runs from Shabbat-after-Pesach (spring) to either
        // Shabbat-before-Shavuot (base) or Shabbat-before-RH-of-next-year (extended).
        int seasonYear = year;
        // If this Shabbat is on or after 1 Tishrei, it belongs to the season
        // that ended on Shabbat-before-this-Rosh-Hashanah. Since the season
        // stops before RH, only Nisan..Elul dates matter.
        if (jd.getMonth() >= 7)  seasonYear = year;    // Tishrei..Adar → this year's Avot season
        // (For Tishrei/Cheshvan/Kislev/Tevet/Shvat/Adar dates, the season is
        // over — falls through to "null" below unless date is inside range.)

        List<LocalDate> shabbatot = seasonShabbatot(seasonYear, inIsrael, extended);
        if (shabbatot.isEmpty()) return null;

        int idx = shabbatot.indexOf(date);
        if (idx < 0) return null;

        return assignChapters(shabbatot, extended).get(idx);
    }

    /**
     * All Shabbatot in the Avot season for the given Hebrew year, in order.
     * Yom-tov Shabbatot (Shavuot 2nd day in Diaspora) are excluded.
     * In Eretz Israel, if the pre-Shavuot window contains 7 Shabbatot, the
     * first is dropped so the base schedule aligns to Shavuot.
     */
    private static List<LocalDate> seasonShabbatot(int hebrewYear, boolean inIsrael, boolean extended) {
        // Bounds: Shabbat-after-Pesach ... Shabbat-before-(Shavuot | RH).
        int pesachLastDay = inIsrael ? 21 : 22;   // Nisan
        LocalDate pesachEnd = toGreg(ICalendar.JEWISH.fromYMD(hebrewYear, 1, pesachLastDay));
        LocalDate firstShabbat = nextShabbat(pesachEnd.plusDays(1));

        LocalDate lastShabbat;
        if (!extended) {
            LocalDate shavuot = toGreg(ICalendar.JEWISH.fromYMD(hebrewYear, 3, 6));
            lastShabbat = prevShabbat(shavuot.minusDays(1));
        } else {
            LocalDate roshHashanah = toGreg(ICalendar.JEWISH.fromYMD(hebrewYear + 1, 7, 1));
            lastShabbat = prevShabbat(roshHashanah.minusDays(1));
        }

        List<LocalDate> raw = new ArrayList<>();
        for (LocalDate s = firstShabbat; !s.isAfter(lastShabbat); s = s.plusDays(7))
            raw.add(s);

        // Skip yom-tov Shabbatot — only Shavuot 2 (7 Sivan) in Diaspora falls
        // within our range. Israel has 1-day Shavuot so nothing to skip.
        List<LocalDate> filtered = new ArrayList<>(raw.size());
        LocalDate shavuot2Diaspora = inIsrael ? null
                : toGreg(ICalendar.JEWISH.fromYMD(hebrewYear, 3, 7));
        for (LocalDate s : raw) {
            if (shavuot2Diaspora != null && s.equals(shavuot2Diaspora)) continue;
            filtered.add(s);
        }

        // Israel-only pre-Shavuot alignment: if the pre-Shavuot count is 7,
        // drop the first so the base cycle lines up to 6 chapters.
        if (inIsrael) {
            LocalDate shavuot = toGreg(ICalendar.JEWISH.fromYMD(hebrewYear, 3, 6));
            long preShavuotCount = filtered.stream().filter(s -> s.isBefore(shavuot)).count();
            if (preShavuotCount == 7) filtered.remove(0);
        }

        return filtered;
    }

    /**
     * Assign 1..6 chapter values to each Shabbat. Base: linear 1..6, doubling
     * the last if only 5 Shabbatot. Extended: cycle 1..6 for all but the last
     * three; the final three are always {1-2, 3-4, 5-6}.
     */
    private static List<Result> assignChapters(List<LocalDate> shabbatot, boolean extended) {
        int n = shabbatot.size();
        List<Result> out = new ArrayList<>(n);

        if (!extended) {
            // Base: 1..n (or 5 Shabbatot → 1,2,3,4,5-6).
            if (n == 5) {
                for (int i = 1; i <= 4; i++) out.add(new Result(List.of(i)));
                out.add(new Result(List.of(5, 6)));
            } else {
                for (int i = 0; i < n; i++) {
                    int chap = (i % 6) + 1;
                    out.add(new Result(List.of(chap)));
                }
            }
            return out;
        }

        // Extended (Chabad summer): all but the last 3 Shabbatot get a single
        // cycling chapter; the last 3 get doubled chapters (1-2, 3-4, 5-6).
        int singleCount = Math.max(0, n - 3);
        for (int i = 0; i < singleCount; i++) {
            int chap = (i % 6) + 1;
            out.add(new Result(List.of(chap)));
        }
        int doubledStart = singleCount;
        int[][] doubles = { {1, 2}, {3, 4}, {5, 6} };
        for (int i = doubledStart; i < n; i++) {
            int di = i - doubledStart;
            if (di < doubles.length) out.add(new Result(List.of(doubles[di][0], doubles[di][1])));
            else out.add(new Result(List.of(6)));   // safety fallback
        }
        return out;
    }

    // ── helpers ────────────────────────────────────────────────────

    private static LocalDate toGreg(IDate<JewishCalendar> jd) {
        IDate<?> g = ICalendar.GREGORIAN.convert(jd);
        return LocalDate.of(g.getYear(), g.getMonth(), g.getDay());
    }
    private static LocalDate nextShabbat(LocalDate d) {
        while (d.getDayOfWeek().getValue() != 6) d = d.plusDays(1);
        return d;
    }
    private static LocalDate prevShabbat(LocalDate d) {
        while (d.getDayOfWeek().getValue() != 6) d = d.minusDays(1);
        return d;
    }
}
