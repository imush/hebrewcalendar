package net.hebrewcalendar.learning;

import net.hebrewcalendar.ICalendar;
import net.hebrewcalendar.IDate;
import net.hebrewcalendar.JewishCalendar;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tanya Yomi — the daily Tanya portion in Chabad's cycle (Alter Rebbe's
 * takana). The whole Tanya is completed once per Hebrew year, with two
 * different partitions:
 * <ul>
 *   <li><b>Non-leap year</b> (12 months, ~354 days)
 *   <li><b>Leap year</b> (13 months, ~384 days — Adar I / II)
 * </ul>
 *
 * <p>Each daily portion is identified by its Hebrew date and augmented with
 * the section/perek it falls in and the first / last three words of the
 * reading — the compact form printed in the back of every Kehot Tanya.
 *
 * <p>Data source: Sefaria's Kehot text (CC-BY-NC), with inline day-boundary
 * markers extracted and baked into {@code tanya_schedule.json}.
 *
 * <p>Notes:
 * <ul>
 *   <li>Days without an assigned portion (e.g. yom tov: 6-7 Sivan;
 *       19-20 Kislev / Chag HaGeulah) return {@link Result#hasPortion()}
 *       = {@code false}; only the date is available.
 *   <li>30 Cheshvan / 30 Kislev, when the month is 29 days, is folded
 *       into 29 (start of 29 → end of 30) — matches Kehot's printed
 *       instruction "when there is no 30 read both together on 29".
 *   <li>Nissan is spelled with a double-s in English (matches Sefaria).
 *       Hebrew day uses geresh / gershayim: {@code "י׳ אלול"},
 *       {@code "י״א אלול"}.
 * </ul>
 */
public final class Tanya {

    private Tanya() {}

    private static final String[] MONTHS_EN = {
        "Nissan", "Iyar", "Sivan", "Tammuz", "Av", "Elul",
        "Tishrei", "Cheshvan", "Kislev", "Tevet", "Shevat",
        "Adar",         // month 12 — overridden to "Adar I" in leap years
        "Adar II",      // month 13 — leap year only
    };
    private static final String[] MONTHS_HE = {
        "ניסן", "אייר", "סיון", "תמוז", "אב", "אלול",
        "תשרי", "חשון", "כסלו", "טבת", "שבט",
        "אדר",          // → "אדר-א" in leap years
        "אדר-ב",
    };

    /** Section name mapping — English → Hebrew, matching Kehot's printed
     *  form (as used in the back-of-Tanya schedule table). */
    private static final Map<String, String> SECTION_HE = new HashMap<>();
    static {
        SECTION_HE.put("Title Page",                "עמוד הכותרת");
        SECTION_HE.put("Approbation",               "הסכמות הרבנים");
        SECTION_HE.put("Compiler's Foreword",       "הקדמת המלקט");
        SECTION_HE.put("Likkutei Amarim",           "לקוטי אמרים");
        SECTION_HE.put("Chinukh Katan",             "חינוך קטן");
        SECTION_HE.put("Shaar HaYichud VehaEmunah", "שער היחוד והאמונה");
        SECTION_HE.put("Iggeret HaTeshuvah",        "אגרת התשובה");
        SECTION_HE.put("Iggeret HaKodesh",          "אגרת הקודש");
        SECTION_HE.put("Kuntres Acharon",           "קונטרס אחרון");
    }

    // ── Loaded schedule ────────────────────────────────────────────────

    /** One daily-portion record from the baked table. */
    private static final class Portion {
        final String sectionEn;
        final int chapter;   // 0 = section has no numbered chapters (prefaces)
        final String start;  // first 3 words (nikud stripped)
        final String end;    // last  3 words (nikud stripped)
        Portion(String s, int c, String start, String end) {
            this.sectionEn = s; this.chapter = c; this.start = start; this.end = end;
        }
        String sectionHe() { return SECTION_HE.getOrDefault(sectionEn, sectionEn); }
    }

    /** Key = (leap ? 1000 : 0) + month*40 + day.  Compact and stable. */
    private static int key(boolean leap, int month, int day) {
        return (leap ? 1000 : 0) + month * 40 + day;
    }
    private static final Map<Integer, Portion> SCHEDULE = loadSchedule();

    private static Map<Integer, Portion> loadSchedule() {
        Map<Integer, Portion> m = new HashMap<>(800);
        try (InputStream in = Tanya.class.getResourceAsStream("tanya_schedule.json")) {
            if (in == null) return m;
            StringBuilder sb = new StringBuilder(50_000);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                String line; while ((line = br.readLine()) != null) sb.append(line).append('\n');
            }
            // Minimal JSON parse — each entry is {"leap": ..., "month": .., "day": .., ...}
            Pattern p = Pattern.compile(
                "\\{[^{}]*\"leap\"\\s*:\\s*(true|false)[^{}]*"
              + "\"month\"\\s*:\\s*(\\d+)[^{}]*"
              + "\"day\"\\s*:\\s*(\\d+)[^{}]*"
              + "\"section\"\\s*:\\s*\"([^\"]+)\"[^{}]*"
              + "\"chapter\"\\s*:\\s*(\\d+)[^{}]*"
              + "\"start\"\\s*:\\s*\"([^\"]*)\"[^{}]*"
              + "\"end\"\\s*:\\s*\"([^\"]*)\"[^{}]*\\}");
            Matcher mm = p.matcher(sb);
            while (mm.find()) {
                boolean leap = Boolean.parseBoolean(mm.group(1));
                int month = Integer.parseInt(mm.group(2));
                int day   = Integer.parseInt(mm.group(3));
                m.put(key(leap, month, day),
                        new Portion(mm.group(4), Integer.parseInt(mm.group(5)),
                                    mm.group(6), mm.group(7)));
            }
        } catch (Exception ignore) {}
        return m;
    }

    // ── Public API ─────────────────────────────────────────────────────

    public static final class Result {
        private final String dateLabel;    // "10 Elul"
        private final String dateLabelHe;  // "י׳ אלול"
        private final Portion portion;     // may be null (Chag HaGeulah / yom tov)
        private final Portion secondary;   // combined-day 30 portion, or null
        private final LocalDate date;

        Result(String label, String labelHe, Portion p, Portion secondary, LocalDate date) {
            this.dateLabel = label; this.dateLabelHe = labelHe;
            this.portion = p; this.secondary = secondary; this.date = date;
        }

        /** {@code true} if this date has an assigned daily portion; some
         *  days (Chag HaGeulah, yom tov) do not. */
        public boolean hasPortion() { return portion != null; }

        /** Bare English date, e.g. {@code "10 Elul"}. */
        public String label()   { return dateLabel; }
        /** Bare Hebrew date with gershaim, e.g. {@code "י׳ אלול"}. */
        public String labelHe() { return dateLabelHe; }

        /** Section + chapter (English), e.g. {@code "Iggeret HaKodesh 12"} or
         *  {@code null} if no portion for this date. */
        public String perek() {
            if (portion == null) return null;
            String s = portion.sectionEn;
            return portion.chapter > 0 ? s + " " + portion.chapter : s;
        }
        /** Section + chapter (Hebrew), e.g. {@code "אגרת הקודש י״ב"}. */
        public String perekHe() {
            if (portion == null) return null;
            String s = portion.sectionHe();
            return portion.chapter > 0 ? s + " " + Gematria.of(portion.chapter) : s;
        }
        /** First 3 words of the reading (nikud stripped). */
        public String startWords() { return portion == null ? null : portion.start; }
        /** Last  3 words of the reading. */
        public String endWords()   { return portion == null ? null : portion.end; }

        /**
         * Full Hebrew line as printed in the Kehot back-of-Tanya schedule,
         * e.g. {@code "אגרת הקודש י״ב — והנה מודעת זאת ... מאה פעמים וכו׳"}.
         * If this is a combined 29+30 day (short Cheshvan/Kislev), both
         * portions are joined by {@code " · "}.
         * Returns just the date if no portion is assigned.
         */
        public String fullLabelHe() {
            if (portion == null) return dateLabelHe;
            String part1 = portionHe(portion);
            if (secondary == null) return part1;
            return part1 + " · " + portionHe(secondary);
        }
        private static String portionHe(Portion p) {
            String perek = p.sectionHe();
            if (p.chapter > 0) perek += " " + Gematria.of(p.chapter);
            return perek + " — " + p.start + " ... " + p.end;
        }

        /**
         * chabad.org's daily Tanya page, which renders the passage assigned
         * to this Hebrew date in Kehot's printed Tanya.
         */
        public String chabadUrl() { return chabadUrl("en"); }
        /** Locale-aware: {@code lang} = "he" / "ru" / "fr" swaps the
         *  chabad.org subdomain to the corresponding language site. */
        public String chabadUrl(String lang) {
            return date == null ? null : ChabadOrg.dailyStudyUrl("tanya.asp", date, null, lang);
        }
    }

    public static Result forDate(LocalDate date) {
        IDate<JewishCalendar> jd = ICalendar.JEWISH.convert(
            ICalendar.GREGORIAN.fromYMD(date.getYear(), date.getMonthValue(), date.getDayOfMonth()));
        return build(jd, date);
    }

    public static Result forHebrewDate(IDate<JewishCalendar> jd) {
        IDate<?> g = ICalendar.GREGORIAN.convert(jd);
        LocalDate date = LocalDate.of(g.getYear(), g.getMonth(), g.getDay());
        return build(jd, date);
    }

    private static Result build(IDate<JewishCalendar> jd, LocalDate date) {
        int day   = jd.getDay();
        int month = jd.getMonth();
        int year  = jd.getYear();
        boolean leap = ICalendar.JEWISH.isLeap(year);
        String en = MONTHS_EN[month - 1];
        String he = MONTHS_HE[month - 1];
        if (month == 12 && leap) { en = "Adar I"; he = "אדר-א"; }
        String dateLabel   = day + " " + en;
        String dateLabelHe = Gematria.of(day) + " " + he;

        Portion p = SCHEDULE.get(key(leap, month, day));
        // Fold 30 Cheshvan / 30 Kislev into 29 when that month has only 29 days.
        Portion secondary = null;
        if (p != null && day == 29 && (month == 8 || month == 9)
                && ICalendar.JEWISH.monthLength(year, month) == 29) {
            secondary = SCHEDULE.get(key(leap, month, 30));
        }
        return new Result(dateLabel, dateLabelHe, p, secondary, date);
    }
}
