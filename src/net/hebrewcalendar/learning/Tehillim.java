package net.hebrewcalendar.learning;

import net.hebrewcalendar.ICalendar;
import net.hebrewcalendar.IDate;
import net.hebrewcalendar.JewishCalendar;

import java.time.LocalDate;

/**
 * Monthly Tehillim reading (Psalms by day-of-Hebrew-month).
 *
 * <p>The 150 psalms are divided into 30 daily portions read across the
 * Hebrew month. In a 29-day month, the last two portions (140-144 and
 * 145-150) are read together on the 29th.
 */
public final class Tehillim {

    private Tehillim() {}

    /** Standard 30-portion monthly division. Verse-range notation is chapters,
     *  except day 25/26 which split chapter 119 by verse. */
    private static final String[] PORTIONS = {
        "1-9",       "10-17",     "18-22",     "23-28",     "29-34",     "35-38",
        "39-43",     "44-48",     "49-54",     "55-59",     "60-65",     "66-68",
        "69-71",     "72-76",     "77-78",     "79-82",     "83-87",     "88-89",
        "90-96",     "97-103",    "104-105",   "106-107",   "108-112",   "113-118",
        "119:1-96",  "119:97-176","120-134",   "135-139",   "140-144",   "145-150",
    };

    /** Combined 29+30 for short months: "140-150". */
    private static final String COMBINED = "140-150";

    public static final class Result {
        private final int day;
        private final String portion;
        private final String portionHe;
        private final LocalDate date;  // null when built via forDayOfMonth(); links unavailable
        Result(int day, String portion, String portionHe, LocalDate date) {
            this.day = day; this.portion = portion; this.portionHe = portionHe; this.date = date;
        }
        public int    day()       { return day; }
        public String portion()   { return portion; }
        public String portionHe() { return portionHe; }
        /** English label, e.g. {@code "Psalms 1-9"} or {@code "Psalms 140-150"}. */
        public String label()   { return "Psalms " + portion; }
        /** Hebrew label, e.g. {@code "תהלים א׳-ט׳"}. */
        public String labelHe() { return "תהלים " + portionHe; }

        /**
         * Deep-link to sefaria.org for the day's psalms, e.g.
         * {@code https://www.sefaria.org/Psalms.1-9} or
         * {@code https://www.sefaria.org/Psalms.119.1-96} for the 119-split days.
         */
        public String sefariaUrl() {
            // portion is one of: "1-9", "119:1-96", "140-150", etc.
            return "https://www.sefaria.org/Psalms." + portion.replace(':', '.');
        }

        /**
         * chabad.org's daily Tehillim page for this date, if built via
         * {@link #forDate(LocalDate)}; {@code null} if built via
         * {@link #forDayOfMonth(int, int)}.
         */
        public String chabadUrl() { return chabadUrl("en"); }
        /** Locale-aware: {@code lang} = "he" / "ru" / "fr" swaps the
         *  chabad.org subdomain to the corresponding language site. */
        public String chabadUrl(String lang) {
            return date == null ? null : ChabadOrg.dailyStudyUrl("tehillim.asp", date, null, lang);
        }
    }

    /** Reading for the given Gregorian date. */
    public static Result forDate(LocalDate date) {
        IDate<JewishCalendar> jd = ICalendar.JEWISH.convert(
            ICalendar.GREGORIAN.fromYMD(date.getYear(), date.getMonthValue(), date.getDayOfMonth()));
        int mlen = ICalendar.JEWISH.monthLength(jd.getYear(), jd.getMonth());
        return build(jd.getDay(), mlen, date);
    }

    /** Reading for the given Hebrew date. Chabad link will be unavailable. */
    public static Result forHebrewDate(IDate<JewishCalendar> jd) {
        int mlen = ICalendar.JEWISH.monthLength(jd.getYear(), jd.getMonth());
        IDate<?> g = ICalendar.GREGORIAN.convert(jd);
        LocalDate date = LocalDate.of(g.getYear(), g.getMonth(), g.getDay());
        return build(jd.getDay(), mlen, date);
    }

    /** Direct lookup for testing without constructing a full Hebrew date;
     *  {@link Result#chabadUrl()} will be {@code null}. */
    public static Result forDayOfMonth(int day, int monthLength) {
        return build(day, monthLength, null);
    }

    /**
     * Chabad supplement of 3 extra chapters per day from 1 Elul through
     * 9 Tishrei (erev Yom Kippur) inclusive:
     * 1 Elul → chapters 1-3, 2 Elul → 4-6, …, 9 Tishrei → 112-114. On
     * Yom Kippur the remaining 115-150 are read in shul (not covered here).
     *
     * @return Hebrew range like {@code "א׳-ג׳"}, or {@code null} outside season.
     */
    public static String elulSupplementHe(LocalDate date) {
        IDate<JewishCalendar> jd = ICalendar.JEWISH.convert(
            ICalendar.GREGORIAN.fromYMD(date.getYear(), date.getMonthValue(), date.getDayOfMonth()));
        int month = jd.getMonth();
        int day   = jd.getDay();
        int dayInRun;
        if (month == 6) dayInRun = day;                // Elul: 1..29
        else if (month == 7 && day <= 9) dayInRun = 29 + day;  // Tishrei 1..9 → 30..38
        else return null;
        int start = (dayInRun - 1) * 3 + 1;
        int end   = start + 2;
        return Gematria.of(start) + "-" + Gematria.of(end);
    }

    private static Result build(int day, int monthLength, LocalDate date) {
        if (day < 1 || day > monthLength)
            throw new IllegalArgumentException("day " + day + " out of range for month of " + monthLength);
        if (day == 29 && monthLength == 29) {
            return new Result(29, COMBINED, Gematria.verseRange(COMBINED), date);
        }
        String p = PORTIONS[day - 1];
        return new Result(day, p, Gematria.verseRange(p), date);
    }
}
