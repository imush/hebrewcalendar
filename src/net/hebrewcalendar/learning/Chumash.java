package net.hebrewcalendar.learning;

import net.hebrewcalendar.ICalendar;
import net.hebrewcalendar.IDate;
import net.hebrewcalendar.JewishCalendar;
import net.hebrewcalendar.Parsha;
import net.hebrewcalendar.impl.Parshiot;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Chumash Yomi — the day-of-week aliyah for the upcoming Shabbat's parsha.
 *
 * <p>Days are 1 = Sunday .. 7 = Shabbat; each day corresponds to that
 * numbered aliyah of the coming Shabbat's parsha.
 *
 * <p>Special-cases:
 * <ul>
 *   <li><b>Double parsha</b> (e.g. Tazria-Metzora) — both names are
 *       returned; the 7 aliyot span the combined reading.
 *   <li><b>Yom-tov Shabbat</b> — skipped; the "next parsha" is the next
 *       non-yom-tov Shabbat's reading, meaning the same parsha may be
 *       studied for two consecutive weeks.
 *   <li><b>Bereshit exception</b> — Vezot HaBracha is never read on
 *       Shabbat, only on Simchat Torah. During the week whose next Shabbat
 *       is Bereshit, days <i>before</i> Simchat Torah show Vezot HaBracha's
 *       aliyah for the day of week. On Simchat Torah itself (day x of
 *       week), aliyot x-7 of Vezot HaBracha <b>and</b> aliyot 1-x of
 *       Bereshit are shown together. Days <i>after</i> Simchat Torah (and
 *       through Bereshit Shabbat) revert to the normal Bereshit schedule.
 * </ul>
 *
 * <p>Verse boundaries are intentionally omitted — they vary by minhag.
 * Users can follow a Sefaria link (or their siddur/chumash) for the
 * exact verses.
 */
public final class Chumash {

    private Chumash() {}

    /** Vezot HaBracha — the 54th parsha, read only on Simchat Torah. */
    private static final String VEZOT_EN = "Vezot HaBracha";
    private static final String VEZOT_HE = "וזאת הברכה";

    /** Ordinal names for individual aliyot. */
    private static final String[] ALIYAH_EN = {
        "1st aliyah", "2nd aliyah", "3rd aliyah", "4th aliyah",
        "5th aliyah", "6th aliyah", "7th aliyah",
    };
    private static final String[] ALIYAH_HE = {
        "ראשון", "שני", "שלישי", "רביעי", "חמישי", "שישי", "שביעי",
    };

    /** Sefaria book names, indexed 1..5. */
    private static final String[] BOOKS = {
        null, "Genesis", "Exodus", "Leviticus", "Numbers", "Deuteronomy"
    };

    /**
     * Aliyah boundaries from hebcal-leyning (MIT). Format per row:
     *   {parshaKey}|{book}|1:1-2:3|2:4-2:19|…|5:25-6:8
     * where parshaKey is the label form ({@link Parsha#getEnglishName()} for
     * singles, {@code "First-Second"} for doubles, and {@code "Vezot HaBracha"}
     * for the Simchat Torah reading).
     */
    private static final String[] ALIYOT_TABLE = {
        "Bereishit|1|1:1-2:3|2:4-2:19|2:20-3:21|3:22-4:18|4:19-4:22|4:23-5:24|5:25-6:8",
        "Noach|1|6:9-6:22|7:1-7:16|7:17-8:14|8:15-9:7|9:8-9:17|9:18-10:32|11:1-11:32",
        "Lech Lecha|1|12:1-12:13|12:14-13:4|13:5-13:18|14:1-14:20|14:21-15:6|15:7-17:6|17:7-17:27",
        "Vayera|1|18:1-18:14|18:15-18:33|19:1-19:20|19:21-21:4|21:5-21:21|21:22-21:34|22:1-22:24",
        "Chayei Sarah|1|23:1-23:16|23:17-24:9|24:10-24:26|24:27-24:52|24:53-24:67|25:1-25:11|25:12-25:18",
        "Toldot|1|25:19-26:5|26:6-26:12|26:13-26:22|26:23-26:29|26:30-27:27|27:28-28:4|28:5-28:9",
        "Vayetze|1|28:10-28:22|29:1-29:17|29:18-30:13|30:14-30:27|30:28-31:16|31:17-31:42|31:43-32:3",
        "Vayishlach|1|32:4-32:13|32:14-32:30|32:31-33:5|33:6-33:20|34:1-35:11|35:12-36:19|36:20-36:43",
        "Vayeshev|1|37:1-37:11|37:12-37:22|37:23-37:36|38:1-38:30|39:1-39:6|39:7-39:23|40:1-40:23",
        "Miketz|1|41:1-41:14|41:15-41:38|41:39-41:52|41:53-42:18|42:19-43:15|43:16-43:29|43:30-44:17",
        "Vayigash|1|44:18-44:30|44:31-45:7|45:8-45:18|45:19-45:27|45:28-46:27|46:28-47:10|47:11-47:27",
        "Vayechi|1|47:28-48:9|48:10-48:16|48:17-48:22|49:1-49:18|49:19-49:26|49:27-50:20|50:21-50:26",
        "Shemot|2|1:1-1:17|1:18-2:10|2:11-2:25|3:1-3:15|3:16-4:17|4:18-4:31|5:1-6:1",
        "Vaera|2|6:2-6:13|6:14-6:28|6:29-7:7|7:8-8:6|8:7-8:18|8:19-9:16|9:17-9:35",
        "Bo|2|10:1-10:11|10:12-10:23|10:24-11:3|11:4-12:20|12:21-12:28|12:29-12:51|13:1-13:16",
        "Beshalach|2|13:17-14:8|14:9-14:14|14:15-14:25|14:26-15:26|15:27-16:10|16:11-16:36|17:1-17:16",
        "Yitro|2|18:1-18:12|18:13-18:23|18:24-18:27|19:1-19:6|19:7-19:19|19:20-20:14|20:15-20:23",
        "Mishpatim|2|21:1-21:19|21:20-22:3|22:4-22:26|22:27-23:5|23:6-23:19|23:20-23:25|23:26-24:18",
        "Terumah|2|25:1-25:16|25:17-25:40|26:1-26:14|26:15-26:30|26:31-26:37|27:1-27:8|27:9-27:19",
        "Tetzaveh|2|27:20-28:12|28:13-28:30|28:31-28:43|29:1-29:18|29:19-29:37|29:38-29:46|30:1-30:10",
        "Ki Tisa|2|30:11-31:17|31:18-33:11|33:12-33:16|33:17-33:23|34:1-34:9|34:10-34:26|34:27-34:35",
        "Vayakhel|2|35:1-35:20|35:21-35:29|35:30-36:7|36:8-36:19|36:20-37:16|37:17-37:29|38:1-38:20",
        "Pekudei|2|38:21-39:1|39:2-39:21|39:22-39:32|39:33-39:43|40:1-40:16|40:17-40:27|40:28-40:38",
        "Vayikra|3|1:1-1:13|1:14-2:6|2:7-2:16|3:1-3:17|4:1-4:26|4:27-5:10|5:11-5:26",
        "Tzav|3|6:1-6:11|6:12-7:10|7:11-7:38|8:1-8:13|8:14-8:21|8:22-8:29|8:30-8:36",
        "Shemini|3|9:1-9:16|9:17-9:23|9:24-10:11|10:12-10:15|10:16-10:20|11:1-11:32|11:33-11:47",
        "Tazria|3|12:1-13:5|13:6-13:17|13:18-13:23|13:24-13:28|13:29-13:39|13:40-13:54|13:55-13:59",
        "Metzora|3|14:1-14:12|14:13-14:20|14:21-14:32|14:33-14:53|14:54-15:15|15:16-15:28|15:29-15:33",
        "Acharei|3|16:1-16:17|16:18-16:24|16:25-16:34|17:1-17:7|17:8-18:5|18:6-18:21|18:22-18:30",
        "Kedoshim|3|19:1-19:14|19:15-19:22|19:23-19:32|19:33-19:37|20:1-20:7|20:8-20:22|20:23-20:27",
        "Emor|3|21:1-21:15|21:16-22:16|22:17-22:33|23:1-23:22|23:23-23:32|23:33-23:44|24:1-24:23",
        "Behar|3|25:1-25:13|25:14-25:18|25:19-25:24|25:25-25:28|25:29-25:38|25:39-25:46|25:47-26:2",
        "Bechukotai|3|26:3-26:5|26:6-26:9|26:10-26:46|27:1-27:15|27:16-27:21|27:22-27:28|27:29-27:34",
        "Bamidbar|4|1:1-1:19|1:20-1:54|2:1-2:34|3:1-3:13|3:14-3:39|3:40-3:51|4:1-4:20",
        "Naso|4|4:21-4:37|4:38-4:49|5:1-5:10|5:11-6:27|7:1-7:41|7:42-7:71|7:72-7:89",
        "Behaalotecha|4|8:1-8:14|8:15-8:26|9:1-9:14|9:15-10:10|10:11-10:34|10:35-11:29|11:30-12:16",
        "Shelach|4|13:1-13:20|13:21-14:7|14:8-14:25|14:26-15:7|15:8-15:16|15:17-15:26|15:27-15:41",
        "Korach|4|16:1-16:13|16:14-16:19|16:20-17:8|17:9-17:15|17:16-17:24|17:25-18:20|18:21-18:32",
        "Chukat|4|19:1-19:17|19:18-20:6|20:7-20:13|20:14-20:21|20:22-21:9|21:10-21:20|21:21-22:1",
        "Balak|4|22:2-22:12|22:13-22:20|22:21-22:38|22:39-23:12|23:13-23:26|23:27-24:13|24:14-25:9",
        "Pinchas|4|25:10-26:4|26:5-26:51|26:52-27:5|27:6-27:23|28:1-28:15|28:16-29:11|29:12-30:1",
        "Matot|4|30:2-30:17|31:1-31:12|31:13-31:24|31:25-31:41|31:42-31:54|32:1-32:19|32:20-32:42",
        "Masei|4|33:1-33:10|33:11-33:49|33:50-34:15|34:16-34:29|35:1-35:8|35:9-35:34|36:1-36:13",
        "Devarim|5|1:1-1:10|1:11-1:21|1:22-1:38|1:39-2:1|2:2-2:30|2:31-3:14|3:15-3:22",
        "Vaetchanan|5|3:23-4:4|4:5-4:40|4:41-4:49|5:1-5:18|5:19-6:3|6:4-6:25|7:1-7:11",
        "Eikev|5|7:12-8:10|8:11-9:3|9:4-9:29|10:1-10:11|10:12-11:9|11:10-11:21|11:22-11:25",
        "Reeh|5|11:26-12:10|12:11-12:28|12:29-13:19|14:1-14:21|14:22-14:29|15:1-15:18|15:19-16:17",
        "Shoftim|5|16:18-17:13|17:14-17:20|18:1-18:5|18:6-18:13|18:14-19:13|19:14-20:9|20:10-21:9",
        "Ki Teitzei|5|21:10-21:21|21:22-22:7|22:8-23:7|23:8-23:24|23:25-24:4|24:5-24:13|24:14-25:19",
        "Ki Tavo|5|26:1-26:11|26:12-26:15|26:16-26:19|27:1-27:10|27:11-28:6|28:7-28:69|29:1-29:8",
        "Nitzavim|5|29:9-29:11|29:12-29:14|29:15-29:28|30:1-30:6|30:7-30:10|30:11-30:14|30:15-30:20",
        "Vayeilech|5|31:1-31:3|31:4-31:6|31:7-31:9|31:10-31:13|31:14-31:19|31:20-31:24|31:25-31:30",
        "Haazinu|5|32:1-32:6|32:7-32:12|32:13-32:18|32:19-32:28|32:29-32:39|32:40-32:43|32:44-32:52",
        "Vayakhel-Pekudei|2|35:1-35:29|35:30-37:16|37:17-37:29|38:1-39:1|39:2-39:21|39:22-39:43|40:1-40:38",
        "Tazria-Metzora|3|12:1-13:23|13:24-13:39|13:40-13:54|13:55-14:20|14:21-14:32|14:33-15:15|15:16-15:33",
        "Acharei-Kedoshim|3|16:1-16:24|16:25-17:7|17:8-18:21|18:22-19:14|19:15-19:32|19:33-20:7|20:8-20:27",
        "Behar-Bechukotai|3|25:1-25:18|25:19-25:28|25:29-25:38|25:39-26:9|26:10-26:46|27:1-27:15|27:16-27:34",
        "Chukat-Balak|4|19:1-20:6|20:7-20:21|20:22-21:20|21:21-22:12|22:13-22:38|22:39-23:26|23:27-25:9",
        "Matot-Masei|4|30:2-31:12|31:13-31:54|32:1-32:19|32:20-33:49|33:50-34:15|34:16-35:8|35:9-36:13",
        "Nitzavim-Vayeilech|5|29:9-29:28|30:1-30:6|30:7-30:14|30:15-31:6|31:7-31:13|31:14-31:19|31:20-31:30",
        "Vezot HaBracha|5|33:1-33:7|33:8-33:12|33:13-33:17|33:18-33:21|33:22-33:26|33:27-33:29|34:1-34:12",
    };

    private static final Map<String, ParshaAliyot> ALIYOT = parseTable();
    private static Map<String, ParshaAliyot> parseTable() {
        Map<String, ParshaAliyot> m = new HashMap<>(ALIYOT_TABLE.length);
        for (String row : ALIYOT_TABLE) {
            String[] parts = row.split("\\|");
            String[][] aliyot = new String[7][];
            for (int i = 0; i < 7; i++) aliyot[i] = parts[2 + i].split("-", 2);
            m.put(parts[0], new ParshaAliyot(Integer.parseInt(parts[1]), aliyot));
        }
        return m;
    }
    private static final class ParshaAliyot {
        final int book;
        final String[][] aliyot;  // [7][2] = start, end (each "chap:verse")
        ParshaAliyot(int book, String[][] aliyot) { this.book = book; this.aliyot = aliyot; }
    }

    /** One row of the display: a parsha (or double parsha) plus an aliyah range. */
    public static final class Portion {
        private final List<String> parshaNames;
        private final List<String> parshaNamesHe;
        private final int firstAliyah;
        private final int lastAliyah;
        Portion(List<String> en, List<String> he, int first, int last) {
            this.parshaNames = Collections.unmodifiableList(en);
            this.parshaNamesHe = Collections.unmodifiableList(he);
            this.firstAliyah = first;
            this.lastAliyah  = last;
        }
        public List<String> parshaNames()   { return parshaNames; }
        public List<String> parshaNamesHe() { return parshaNamesHe; }
        public int firstAliyah()            { return firstAliyah; }
        public int lastAliyah()             { return lastAliyah; }

        /** e.g. {@code "Bereishit — 3rd aliyah"} or {@code "Vezot HaBracha — aliyot 4-7"}. */
        public String label() {
            return String.join("-", parshaNames) + " — " + aliyahRangeEn();
        }
        public String labelHe() {
            return String.join("-", parshaNamesHe) + " — " + aliyahRangeHe();
        }
        private String aliyahRangeEn() {
            return firstAliyah == lastAliyah
                    ? ALIYAH_EN[firstAliyah - 1]
                    : "aliyot " + firstAliyah + "-" + lastAliyah;
        }
        private String aliyahRangeHe() {
            return firstAliyah == lastAliyah
                    ? ALIYAH_HE[firstAliyah - 1]
                    : ALIYAH_HE[firstAliyah - 1] + "-" + ALIYAH_HE[lastAliyah - 1];
        }

        /**
         * Deep-link to sefaria.org spanning the exact aliyah range of this
         * portion, e.g. {@code https://www.sefaria.org/Genesis.1.1-2.3} for
         * Bereishit's 1st aliyah, or
         * {@code https://www.sefaria.org/Deuteronomy.33.18-33.29} for
         * Vezot HaBracha aliyot 4-6.
         */
        public String sefariaUrl() {
            String key = String.join("-", parshaNames);
            ParshaAliyot pa = ALIYOT.get(key);
            if (pa == null) throw new IllegalStateException("no aliyah table for " + key);
            String[] start = pa.aliyot[firstAliyah - 1].clone();  // [startRef, endRef]
            String[] last  = pa.aliyot[lastAliyah  - 1];
            String[] s = start[0].split(":", 2);   // [startChap, startVerse]
            String[] e = last [1].split(":", 2);   // [endChap,   endVerse  ]
            String tail = s[0].equals(e[0])
                    ? s[0] + "." + s[1] + "-" + e[1]                 // same chapter
                    : s[0] + "." + s[1] + "-" + e[0] + "." + e[1];  // spans chapters
            return "https://www.sefaria.org/" + BOOKS[pa.book] + "." + tail;
        }
    }

    public static final class Result {
        private final List<Portion> portions;
        private final LocalDate date;
        Result(List<Portion> portions, LocalDate date) {
            this.portions = Collections.unmodifiableList(portions);
            this.date = date;
        }
        public List<Portion> portions() { return portions; }

        /**
         * chabad.org's daily Torah Reading page for this date, which
         * renders the day's aliyah using the Chabad minhag's verse
         * boundaries. Same URL for both portions on Simchat Torah — the
         * page shows the combined reading.
         */
        public String chabadUrl() {
            return ChabadOrg.dailyStudyUrl("torahreading.asp", date, null);
        }
        /** e.g. {@code "Bereishit — 3rd aliyah"} or on Simchat Torah
         *  {@code "Vezot HaBracha — aliyot 4-7; Bereishit — aliyot 1-4"}. */
        public String label() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < portions.size(); i++) {
                if (i > 0) sb.append("; ");
                sb.append(portions.get(i).label());
            }
            return sb.toString();
        }
        public String labelHe() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < portions.size(); i++) {
                if (i > 0) sb.append("; ");
                sb.append(portions.get(i).labelHe());
            }
            return sb.toString();
        }
    }

    /** Diaspora default. */
    public static Result forDate(LocalDate date) {
        return forDate(date, false);
    }

    public static Result forDate(LocalDate date, boolean inIsrael) {
        IDate<JewishCalendar> jd = ICalendar.JEWISH.convert(
            ICalendar.GREGORIAN.fromYMD(date.getYear(), date.getMonthValue(), date.getDayOfMonth()));
        return forHebrewDate(jd, inIsrael);
    }

    public static Result forHebrewDate(IDate<JewishCalendar> jd, boolean inIsrael) {
        LocalDate date = toGreg(jd);
        int dow = jd.getDayOfWeek();  // 1=Sun..7=Sat

        // Find the next Shabbat that has a regular parsha (skip yom-tov Shabbatot).
        IDate<JewishCalendar> nextShabbat = jd;
        if (dow != 7) nextShabbat = ICalendar.JEWISH.addDays(jd, 7 - dow);
        List<Parsha> nextParsha;
        while (true) {
            nextParsha = Parshiot.getParsha(nextShabbat, inIsrael);
            if (!nextParsha.isEmpty()) break;
            nextShabbat = ICalendar.JEWISH.addDays(nextShabbat, 7);
        }

        boolean isBereshit = nextParsha.size() == 1 && nextParsha.get(0) == Parsha.BEREISHIT;
        if (isBereshit) {
            // Simchat Torah = 22 Tishrei (Israel) or 23 Tishrei (Diaspora), same
            // Hebrew year as the Bereshit Shabbat we're pointing at.
            int simchatTorahDay = inIsrael ? 22 : 23;
            IDate<JewishCalendar> simchatTorah =
                ICalendar.JEWISH.fromYMD(nextShabbat.getYear(), 7, simchatTorahDay);
            int cmp = compare(jd, simchatTorah);
            if (cmp < 0) {
                // Before Simchat Torah — day-of-week aliyah of Vezot HaBracha.
                return single(VEZOT_EN, VEZOT_HE, dow, date);
            } else if (cmp == 0) {
                // Simchat Torah itself — Vezot [dow..7] + Bereshit [1..dow].
                List<Portion> ps = new ArrayList<>(2);
                ps.add(new Portion(List.of(VEZOT_EN), List.of(VEZOT_HE), dow, 7));
                ps.add(new Portion(
                        List.of(Parsha.BEREISHIT.getEnglishName()),
                        List.of(Parsha.BEREISHIT.getHebrewName()),
                        1, dow));
                return new Result(ps, date);
            }
            // else: after Simchat Torah, fall through to normal case (Bereshit).
        }
        // Normal case — day-of-week aliyah of the coming Shabbat's parsha.
        List<String> en = new ArrayList<>(nextParsha.size());
        List<String> he = new ArrayList<>(nextParsha.size());
        for (Parsha p : nextParsha) {
            en.add(p.getEnglishName());
            he.add(p.getHebrewName());
        }
        return new Result(List.of(new Portion(en, he, dow, dow)), date);
    }

    private static Result single(String en, String he, int aliyah, LocalDate date) {
        return new Result(List.of(new Portion(List.of(en), List.of(he), aliyah, aliyah)), date);
    }

    private static LocalDate toGreg(IDate<JewishCalendar> jd) {
        IDate<?> g = ICalendar.GREGORIAN.convert(jd);
        return LocalDate.of(g.getYear(), g.getMonth(), g.getDay());
    }

    private static int compare(IDate<JewishCalendar> a, IDate<JewishCalendar> b) {
        int c = Integer.compare(a.getYear(), b.getYear());
        if (c != 0) return c;
        c = Integer.compare(a.getMonth(), b.getMonth());
        if (c != 0) return c;
        return Integer.compare(a.getDay(), b.getDay());
    }
}
