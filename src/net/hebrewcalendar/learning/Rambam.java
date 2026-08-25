package net.hebrewcalendar.learning;

import java.time.LocalDate;
import java.util.List;

/**
 * Rambam Yomi — Daily Mishneh Torah study, in both 1-chapter and
 * 3-chapter variants.
 *
 * <p>Instituted by the Lubavitcher Rebbe on 27 Nisan 5744 (Sunday,
 * 29 April 1984). The 1-chapter cycle runs 1017 days (~2 years 9
 * months); the 3-chapter cycle runs 339 days (~11 months). Both start
 * from the same date; the 3-chapter cycle diverges in two places from
 * the 1-chapter table to keep chapters grouped logically:
 * {@code "The Order of Prayer"} gets one extra chapter, and the final
 * two chapters of {@code "Leavened and Unleavened Bread"} are combined.
 *
 * <p>The first four "halachot" (Transmission of the Oral Law, Positive
 * Mitzvot, Negative Mitzvot, Overview of Mishneh Torah Contents) are
 * introductory sections; their {@code perek} field returns a verse-range
 * string ({@code "1-21"}, {@code "1:1-4:8"}, …) rather than a chapter
 * number.
 *
 * <p>Structural data and quirks ported from hebcal-learning (MIT).
 */
public final class Rambam {

    private Rambam() {}

    private static final long EPOCH = LocalDate.of(1984, 4, 29).toEpochDay();
    private static final int  CYCLE_1 = 1017;
    private static final int  CYCLE_3 = 339;

    /** One chapter (or introductory verse range) of the Mishneh Torah. */
    public static final class Reading {
        private final String name;
        private final String nameHe;
        private final String perek;
        Reading(String name, String nameHe, String perek) {
            this.name = name; this.nameHe = nameHe; this.perek = perek;
        }
        /** Halacha (section) name, e.g. {@code "Sabbath"}, {@code "Kings and Wars"}. */
        public String name()   { return name; }
        /** Hebrew halacha name, matching sefaria.org (e.g. {@code "הלכות שבת"}). */
        public String nameHe() { return nameHe; }
        /**
         * Chapter number as a string (usually numeric, but can be a range
         * like {@code "4-5"} or a verse span like {@code "1:1-4:8"} for
         * the four introductory sections).
         */
        public String perek() { return perek; }
        /** English label, {@code "Sabbath 5"} style. */
        public String label()   { return name   + " " + perek; }
        /** Hebrew label, {@code "הלכות שבת ה׳"} style, matching sefaria.org. */
        public String labelHe() { return nameHe + " " + Gematria.verseRange(perek); }

        /**
         * Deep-link to sefaria.org for this reading, e.g.
         * {@code https://www.sefaria.org/Mishneh_Torah,_Sabbath.5} or
         * {@code https://www.sefaria.org/Mishneh_Torah,_The_Order_of_Prayer.4-5}.
         */
        public String sefariaUrl() {
            return "https://www.sefaria.org/Mishneh_Torah,_"
                    + name.replace(' ', '_') + "." + perek;
        }
    }

    /**
     * chabad.org's daily Rambam page, e.g.
     * {@code https://www.chabad.org/dailystudy/rambam.asp?tdate=8/23/2026&rambamChapters=1}.
     * @param date       Gregorian date
     * @param chapters   {@code 1} for the 1-chapter cycle, {@code 3} for the 3-chapter cycle
     */
    public static String chabadUrl(LocalDate date, int chapters) {
        return chabadUrl(date, chapters, "en");
    }
    /** Locale-aware: {@code lang} = "he" / "ru" / "fr" swaps the
     *  chabad.org subdomain to the corresponding language site. */
    public static String chabadUrl(LocalDate date, int chapters, String lang) {
        if (chapters != 1 && chapters != 3)
            throw new IllegalArgumentException("chapters must be 1 or 3: " + chapters);
        return ChabadOrg.dailyStudyUrl("rambam.asp", date, "rambamChapters=" + chapters, lang);
    }

    /** {@link Reading} for the 1-chapter cycle, or {@code null} pre-1984-04-29. */
    public static Reading oneChapter(LocalDate date) {
        long abs = date.toEpochDay();
        if (abs < EPOCH) return null;
        int day = (int)((abs - EPOCH) % CYCLE_1);   // 0..1016
        Reading r = chapAt(day, MT_ONE);
        // chabad.org combines Order of Prayer chapters 4-5 in the 1-chapter cycle.
        if ("The Order of Prayer".equals(r.name) && "4".equals(r.perek)) {
            r = new Reading(r.name, r.nameHe, "4-5");
        }
        return r;
    }

    /**
     * Collapse adjacent {@link Reading}s that share a {@code name} into
     * range labels. E.g. {@code ["Sabbath 5", "Sabbath 6", "Sabbath 7"]}
     * → {@code ["Sabbath 5-7"]}; a day that crosses halachot yields
     * multiple entries, e.g.
     * {@code ["Defilement by a Corpse 24-25", "Red Heifer 1"]}. This
     * matches sefaria.org's {@code Daily Rambam (3 Chapters)} rendering.
     */
    public static List<String> collapse(List<Reading> readings) {
        java.util.List<String> out = new java.util.ArrayList<>();
        int i = 0;
        while (i < readings.size()) {
            Reading start = readings.get(i);
            int j = i;
            while (j + 1 < readings.size() && readings.get(j + 1).name.equals(start.name)) j++;
            if (j == i) {
                out.add(start.label());
            } else {
                // Extract first token of start.perek and last token of end.perek.
                // Handles both plain "5" and "1-83"-style ranges: joining
                // ["1-83", "84-166", "167-248"] must yield "1-248", not
                // "1-83-167-248".
                String first = firstToken(start.perek);
                String last  = lastToken(readings.get(j).perek);
                out.add(start.name + " " + first + "-" + last);
            }
            i = j + 1;
        }
        return out;
    }

    /**
     * Hebrew-side analogue of {@link #collapse}: groups consecutive readings
     * that share the same {@code nameHe} into a single {@code "הלכה א׳-ג׳"}
     * range, matching Kehot's printed schedule. Chapters and ranges are
     * gematria-formatted via {@link Gematria#verseRange}.
     */
    public static List<String> collapseHe(List<Reading> readings) {
        java.util.List<String> out = new java.util.ArrayList<>();
        int i = 0;
        while (i < readings.size()) {
            Reading start = readings.get(i);
            int j = i;
            while (j + 1 < readings.size() && readings.get(j + 1).nameHe.equals(start.nameHe)) j++;
            if (j == i) {
                out.add(start.labelHe());
            } else {
                String first = firstToken(start.perek);
                String last  = lastToken(readings.get(j).perek);
                out.add(start.nameHe + " " + Gematria.of(Integer.parseInt(first))
                        + "-" + Gematria.of(Integer.parseInt(last)));
            }
            i = j + 1;
        }
        return out;
    }

    private static String firstToken(String perek) {
        int dash = perek.indexOf('-');
        return dash < 0 ? perek : perek.substring(0, dash);
    }
    private static String lastToken(String perek) {
        int dash = perek.lastIndexOf('-');
        return dash < 0 ? perek : perek.substring(dash + 1);
    }

    /** Three {@link Reading}s for the 3-chapter cycle, or {@code null} pre-1984-04-29. */
    public static List<Reading> threeChapters(LocalDate date) {
        long abs = date.toEpochDay();
        if (abs < EPOCH) return null;
        int day = (int)((abs - EPOCH) % CYCLE_3);   // 0..338
        int base = day * 3;
        Reading r1 = chapAt(base,     MT_THREE);
        // In the 3-chapter cycle, chabad.org combines the final 8+9 of
        // "Leavened and Unleavened Bread" when it lands as r1.
        if ("Leavened and Unleavened Bread".equals(r1.name) && "8".equals(r1.perek)) {
            r1 = new Reading(r1.name, r1.nameHe, "8-9");
        }
        Reading r2 = chapAt(base + 1, MT_THREE);
        Reading r3 = chapAt(base + 2, MT_THREE);
        return List.of(r1, r2, r3);
    }

    // ── Structure table ────────────────────────────────────────────────────

    private static final String[][] FIRST_FOUR_VERSES = {
        { "1-21", "22-33", "34-45" },       // Transmission of the Oral Law
        { "1-83", "84-166", "167-248" },    // Positive Mitzvot
        { "1-122", "123-245", "246-365" },  // Negative Mitzvot
        { "1:1-4:8", "5:1-9:9", "10:1-14:10" }, // Overview of Mishneh Torah Contents
    };

    private static final class Halacha {
        final String name; final String nameHe; final int chapters;
        Halacha(String name, String nameHe, int chapters) {
            this.name = name; this.nameHe = nameHe; this.chapters = chapters;
        }
    }
    // Overload kept for readability of the 88-line table below.
    private static Halacha e(String name, String nameHe, int ch) {
        return new Halacha(name, nameHe, ch);
    }

    // Mishneh Torah — 88 halachot in order, chapter counts sum to 1017.
    // Verbatim from hebcal/mishnehTorah.json (MIT).
    private static final Halacha[] MT_ONE = new Halacha[] {
        e("Transmission of the Oral Law", "מסירת תורה שבעל פה", 3),
        e("Positive Mitzvot", "מצוות עשה", 3),
        e("Negative Mitzvot", "מצוות לא תעשה", 3),
        e("Overview of Mishneh Torah Contents", "תוכן החיבור", 3),
        e("Foundations of the Torah", "הלכות יסודי התורה", 10),
        e("Human Dispositions", "הלכות דעות", 7),
        e("Torah Study", "הלכות תלמוד תורה", 7),
        e("Foreign Worship and Customs of the Nations", "הלכות עבודה זרה וחוקות הגויים", 12),
        e("Repentance", "הלכות תשובה", 10),
        e("Reading the Shema", "הלכות קריאת שמע", 4),
        e("Prayer and the Priestly Blessing", "הלכות תפילה וברכת כהנים", 15),
        e("Tefillin, Mezuzah and the Torah Scroll", "הלכות תפילין ומזוזה וספר תורה", 10),
        e("Fringes", "הלכות ציצית", 3),
        e("Blessings", "הלכות ברכות", 11),
        e("Circumcision", "הלכות מילה", 3),
        e("The Order of Prayer", "סדר התפילה", 4),
        e("Sabbath", "הלכות שבת", 30),
        e("Eruvin", "הלכות עירובין", 8),
        e("Rest on the Tenth of Tishrei", "הלכות שביתת עשור", 3),
        e("Rest on a Holiday", "הלכות שביתת יום טוב", 8),
        e("Leavened and Unleavened Bread", "הלכות חמץ ומצה", 9),
        e("Shofar, Sukkah and Lulav", "הלכות שופר וסוכה ולולב", 8),
        e("Sheqel Dues", "הלכות שקלים", 4),
        e("Sanctification of the New Month", "הלכות קידוש החודש", 19),
        e("Fasts", "הלכות תעניות", 5),
        e("Scroll of Esther and Hanukkah", "הלכות מגילה וחנוכה", 4),
        e("Marriage", "הלכות אישות", 25),
        e("Divorce", "הלכות גירושין", 13),
        e("Levirate Marriage and Release", "הלכות יבום וחליצה", 8),
        e("Virgin Maiden", "הלכות נערה בתולה", 3),
        e("Woman Suspected of Infidelity", "הלכות סוטה", 4),
        e("Forbidden Intercourse", "הלכות איסורי ביאה", 22),
        e("Forbidden Foods", "הלכות מאכלות אסורות", 17),
        e("Ritual Slaughter", "הלכות שחיטה", 14),
        e("Oaths", "הלכות שבועות", 12),
        e("Vows", "הלכות נדרים", 13),
        e("Nazariteship", "הלכות נזירות", 10),
        e("Appraisals and Devoted Property", "הלכות ערכים וחרמין", 8),
        e("Diverse Species", "הלכות כלאים", 10),
        e("Gifts to the Poor", "הלכות מתנות עניים", 10),
        e("Heave Offerings", "הלכות תרומות", 15),
        e("Tithes", "הלכות מעשרות", 14),
        e("Second Tithes and Fourth Year's Fruit", "הלכות מעשר שני ונטע רבעי", 11),
        e("First Fruits and other Gifts to Priests Outside the Sanctuary", "הלכות ביכורים ושאר מתנות כהונה שבגבולין", 12),
        e("Sabbatical Year and the Jubilee", "הלכות שמיטה ויובל", 13),
        e("The Chosen Temple", "הלכות בית הבחירה", 8),
        e("Vessels of the Sanctuary and Those Who Serve Therein", "הלכות כלי המקדש והעובדין בו", 10),
        e("Admission into the Sanctuary", "הלכות ביאת מקדש", 9),
        e("Things Forbidden on the Altar", "הלכות איסורי המזבח", 7),
        e("Sacrificial Procedure", "הלכות מעשה הקרבנות", 19),
        e("Daily Offerings and Additional Offerings", "הלכות תמידים ומוספין", 10),
        e("Sacrifices Rendered Unfit", "הלכות פסולי המוקדשין", 19),
        e("Service on the Day of Atonement", "הלכות עבודת יום הכפורים", 5),
        e("Trespass", "הלכות מעילה", 8),
        e("Paschal Offering", "הלכות קרבן פסח", 10),
        e("Festival Offering", "הלכות חגיגה", 3),
        e("Firstlings", "הלכות בכורות", 8),
        e("Offerings for Unintentional Transgressions", "הלכות שגגות", 15),
        e("Offerings for Those with Incomplete Atonement", "הלכות מחוסרי כפרה", 5),
        e("Substitution", "הלכות תמורה", 4),
        e("Defilement by a Corpse", "הלכות טומאת מת", 25),
        e("Red Heifer", "הלכות פרה אדומה", 15),
        e("Defilement by Leprosy", "הלכות טומאת צרעת", 16),
        e("Those Who Defile Bed or Seat", "הלכות מטמאי משכב ומושב", 13),
        e("Other Sources of Defilement", "הלכות שאר אבות הטומאות", 20),
        e("Defilement of Foods", "הלכות טומאת אוכלים", 16),
        e("Vessels", "הלכות כלים", 28),
        e("Immersion Pools", "הלכות מקואות", 11),
        e("Damages to Property", "הלכות נזקי ממון", 14),
        e("Theft", "הלכות גניבה", 9),
        e("Robbery and Lost Property", "הלכות גזילה ואבידה", 18),
        e("One Who Injures a Person or Property", "הלכות חובל ומזיק", 8),
        e("Murderer and the Preservation of Life", "הלכות רוצח ושמירת נפש", 13),
        e("Sales", "הלכות מכירה", 30),
        e("Ownerless Property and Gifts", "הלכות זכייה ומתנה", 12),
        e("Neighbors", "הלכות שכנים", 14),
        e("Agents and Partners", "הלכות שלוחין ושותפין", 10),
        e("Slaves", "הלכות עבדים", 9),
        e("Hiring", "הלכות שכירות", 13),
        e("Borrowing and Deposit", "הלכות שאלה ופיקדון", 8),
        e("Creditor and Debtor", "הלכות מלווה ולווה", 27),
        e("Plaintiff and Defendant", "הלכות טוען ונטען", 16),
        e("Inheritances", "הלכות נחלות", 11),
        e("The Sanhedrin and the Penalties within Their Jurisdiction", "הלכות סנהדרין והעונשין המסורין להם", 26),
        e("Testimony", "הלכות עדות", 22),
        e("Rebels", "הלכות ממרים", 7),
        e("Mourning", "הלכות אבל", 14),
        e("Kings and Wars", "הלכות מלכים ומלחמות", 12),
    };

    // 3-chapter variant differs in two places to keep chapters grouped:
    // index 15 (The Order of Prayer)     : 4 → 5
    // index 20 (Leavened & Unleavened B.): 9 → 8
    private static final Halacha[] MT_THREE;
    static {
        MT_THREE = MT_ONE.clone();
        // Verify the indices we're about to override.
        if (!"The Order of Prayer".equals(MT_THREE[15].name))
            throw new AssertionError("MT index 15 changed: " + MT_THREE[15].name);
        if (!"Leavened and Unleavened Bread".equals(MT_THREE[20].name))
            throw new AssertionError("MT index 20 changed: " + MT_THREE[20].name);
        MT_THREE[15] = e(MT_THREE[15].name, MT_THREE[15].nameHe, 5);
        MT_THREE[20] = e(MT_THREE[20].name, MT_THREE[20].nameHe, 8);
    }

    /** Look up the (halacha, perek) for a 0-based chapter index into the given MT table. */
    private static Reading chapAt(int idx, Halacha[] mt) {
        int rem = idx;
        for (int i = 0; i < mt.length; i++) {
            if (rem < mt[i].chapters) {
                int chapNum = rem + 1;
                String perek = i < 4 ? FIRST_FOUR_VERSES[i][chapNum - 1]
                                     : Integer.toString(chapNum);
                return new Reading(mt[i].name, mt[i].nameHe, perek);
            }
            rem -= mt[i].chapters;
        }
        throw new IllegalStateException("Mishneh Torah chapter table inconsistent");
    }
}
