package net.hebrewcalendar.learning;

import java.time.LocalDate;

/**
 * Worldwide Daf Yomi Bavli calculator.
 *
 * <p>The cycle began <b>11 September 1923</b> (1 Tishrei 5684). Cycles 1–7
 * ran 2702 days (Shekalim was learned as 13 blatt); from cycle 8 (starting
 * 24 June 1975) onwards each cycle runs 2711 days with Shekalim expanded
 * to 22 blatt.
 *
 * <p>Ported from Bob Newell's {@code daf.el} (public domain, 1998), by way
 * of hebcal's TypeScript implementation. Corrects Newell's original
 * off-by-one on Tamid / Middot day naming.
 *
 * <p>Names follow the transliteration used by sefaria.org so cross-checks
 * against Sefaria's {@code /api/calendars} endpoint match exactly.
 */
public final class DafYomi {

    private DafYomi() {}

    // ── Tractates in Daf Yomi order ─────────────────────────────────────────
    // Each tractate's page numbering starts at daf 2. lastDaf[i] is the final
    // daf, so tractate i occupies lastDaf[i] - 1 days.
    // Names are Sefaria's spellings.
    private static final String[] TRACTATES = {
        "Berakhot",     "Shabbat",      "Eruvin",       "Pesachim",
        "Shekalim",     "Yoma",         "Sukkah",       "Beitzah",
        "Rosh Hashanah","Taanit",       "Megillah",     "Moed Katan",
        "Chagigah",     "Yevamot",      "Ketubot",      "Nedarim",
        "Nazir",        "Sotah",        "Gittin",       "Kiddushin",
        "Bava Kamma",   "Bava Metzia",  "Bava Batra",   "Sanhedrin",
        "Makkot",       "Shevuot",      "Avodah Zarah", "Horayot",
        "Zevachim",     "Menachot",     "Chullin",      "Bekhorot",
        "Arakhin",      "Temurah",      "Keritot",      "Meilah",
        "Kinnim",       "Tamid",        "Middot",       "Niddah",
    };
    /** Hebrew tractate names in the same order, matching sefaria.org. */
    private static final String[] TRACTATES_HE = {
        "ברכות",        "שבת",          "עירובין",      "פסחים",
        "שקלים",        "יומא",         "סוכה",         "ביצה",
        "ראש השנה",     "תענית",        "מגילה",        "מועד קטן",
        "חגיגה",        "יבמות",        "כתובות",       "נדרים",
        "נזיר",         "סוטה",         "גיטין",        "קידושין",
        "בבא קמא",      "בבא מציעא",    "בבא בתרא",     "סנהדרין",
        "מכות",         "שבועות",       "עבודה זרה",    "הוריות",
        "זבחים",        "מנחות",        "חולין",        "בכורות",
        "ערכין",        "תמורה",        "כריתות",       "מעילה",
        "קינים",        "תמיד",         "מדות",         "נדה",
    };
    private static final int[] LAST_DAF = {
         64, 157, 105, 121,  22,  88,  56,  40,
         35,  31,  32,  29,  27, 122, 112,  91,
         66,  49,  90,  82, 119, 119, 176, 113,
         24,  49,  76,  14, 120, 110, 142,  61,
         34,  34,  28,  22,   4,   9,   5,  73,
    };

    /** Index of Shekalim (cycles 1–7 use 13 as its lastDaf instead). */
    private static final int SHEKALIM_INDEX = 4;
    private static final int SHEKALIM_OLD_LAST_DAF = 13;

    /**
     * Kinnim / Tamid / Middos are printed as continuations of the previous
     * masechta rather than starting at daf 2, so their daf numbering is
     * offset within Seder Kodashim volume 4 of the Vilna edition.
     */
    private static final int[] DAF_OFFSET = new int[TRACTATES.length];
    static {
        DAF_OFFSET[36] = 21; // Kinnim  starts at 23
        DAF_OFFSET[37] = 24; // Tamid   starts at 26
        DAF_OFFSET[38] = 32; // Middot  starts at 34
    }

    /**
     * Tractates whose final daf occupies only amud A (front side). Sefaria
     * renders these as e.g. "Menachot 110a" — we match.
     */
    private static final java.util.Set<String> LAST_AMUD_A_ONLY = java.util.Set.of(
        "Meilah", "Kinnim", "Tamid", "Menachot", "Bekhorot", "Makkot", "Niddah"
    );

    private static final long OLD_START = LocalDate.of(1923, 9, 11).toEpochDay();
    private static final long NEW_START = LocalDate.of(1975, 6, 24).toEpochDay();
    private static final int  OLD_CYCLE_DAYS = 2702;
    private static final int  NEW_CYCLE_DAYS = 2711;
    private static final int  FIRST_NEW_CYCLE = 8;

    /** Immutable result: tractate name + daf number + cycle number. */
    public static final class Result {
        private final String tractate;
        private final String tractateHe;
        private final int daf;
        private final int cycle;
        private final boolean amudA;
        Result(String tractate, String tractateHe, int daf, int cycle, boolean amudA) {
            this.tractate = tractate; this.tractateHe = tractateHe;
            this.daf = daf; this.cycle = cycle; this.amudA = amudA;
        }
        public String tractate()   { return tractate; }
        public String tractateHe() { return tractateHe; }
        public int    daf()        { return daf; }
        public int    cycle()      { return cycle; }
        public boolean amudA()     { return amudA; }
        /** English label, e.g. {@code "Chullin 115"} or {@code "Menachot 110a"}. */
        public String label() {
            return amudA ? tractate + " " + daf + "a" : tractate + " " + daf;
        }
        /** Hebrew label, e.g. {@code "חולין קט״ו"} or {@code "מנחות ק״י א"}. */
        public String labelHe() {
            String num = Gematria.of(daf);
            return amudA ? tractateHe + " " + num + " א" : tractateHe + " " + num;
        }
    }

    /**
     * Daf for the given Gregorian date.
     *
     * @return the calculated daf, or {@code null} for dates before 11 Sep 1923.
     */
    public static Result forDate(LocalDate date) {
        long abs = date.toEpochDay();
        if (abs < OLD_START) return null;

        int cycle;
        int dayInCycle;
        if (abs >= NEW_START) {
            long elapsed = abs - NEW_START;
            cycle = FIRST_NEW_CYCLE + (int)(elapsed / NEW_CYCLE_DAYS);
            dayInCycle = (int)(elapsed % NEW_CYCLE_DAYS);
        } else {
            long elapsed = abs - OLD_START;
            cycle = 1 + (int)(elapsed / OLD_CYCLE_DAYS);
            dayInCycle = (int)(elapsed % OLD_CYCLE_DAYS);
        }

        // Walk the tractates, accumulating days, until dayInCycle lands in one.
        int daysSoFar = 0;
        for (int i = 0; i < TRACTATES.length; i++) {
            int lastDaf = (i == SHEKALIM_INDEX && cycle < FIRST_NEW_CYCLE)
                    ? SHEKALIM_OLD_LAST_DAF : LAST_DAF[i];
            daysSoFar += lastDaf - 1;
            if (dayInCycle < daysSoFar) {
                int daf = lastDaf + 1 - (daysSoFar - dayInCycle) + DAF_OFFSET[i];
                boolean amudA = (daf == lastDaf + DAF_OFFSET[i])
                              && LAST_AMUD_A_ONLY.contains(TRACTATES[i]);
                return new Result(TRACTATES[i], TRACTATES_HE[i], daf, cycle, amudA);
            }
        }
        throw new IllegalStateException("Daf Yomi lengths sum inconsistent");
    }
}
