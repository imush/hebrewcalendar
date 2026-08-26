package net.hebrewcalendar.learning;

import java.time.LocalDate;
import java.util.List;

import net.hebrewcalendar.data.RambamHalacha;

/**
 * Rambam Yomi — Daily Mishneh Torah study, in both 1-chapter and
 * 3-chapter variants. Halacha table + cycle constants come from
 * {@link net.hebrewcalendar.data.RambamHalacha}; only the algorithm
 * + collapse helpers live here.
 */
public final class Rambam {

    private Rambam() {}

    /** One chapter (or introductory verse range) of the Mishneh Torah. */
    public static final class Reading {
        private final String name;
        private final String nameHe;
        private final String perek;
        Reading(String name, String nameHe, String perek) {
            this.name = name; this.nameHe = nameHe; this.perek = perek;
        }
        public String name()   { return name; }
        public String nameHe() { return nameHe; }
        public String perek()  { return perek; }
        public String label()   { return name   + " " + perek; }
        public String labelHe() { return nameHe + " " + Gematria.verseRange(perek); }
    }

    /** {@link Reading} for the 1-chapter cycle, or {@code null} pre-1984-04-29. */
    public static Reading oneChapter(LocalDate date) {
        long abs = date.toEpochDay();
        long epoch = RambamHalacha.EPOCH.toEpochDay();
        if (abs < epoch) return null;
        int day = (int)((abs - epoch) % RambamHalacha.ONE_CHAPTER_CYCLE_DAYS);
        Reading r = chapAt(day, false);
        if (r.name.equals(RambamHalacha.THE_ORDER_OF_PRAYER.en) && "4".equals(r.perek)) {
            r = new Reading(r.name, r.nameHe, "4-5");
        }
        return r;
    }

    /** Three {@link Reading}s for the 3-chapter cycle, or {@code null} pre-1984-04-29. */
    public static List<Reading> threeChapters(LocalDate date) {
        long abs = date.toEpochDay();
        long epoch = RambamHalacha.EPOCH.toEpochDay();
        if (abs < epoch) return null;
        int day = (int)((abs - epoch) % RambamHalacha.THREE_CHAPTER_CYCLE_DAYS);
        int base = day * 3;
        Reading r1 = chapAt(base,     true);
        if (r1.name.equals(RambamHalacha.LEAVENED_AND_UNLEAVENED_BREAD.en) && "8".equals(r1.perek)) {
            r1 = new Reading(r1.name, r1.nameHe, "8-9");
        }
        Reading r2 = chapAt(base + 1, true);
        Reading r3 = chapAt(base + 2, true);
        return List.of(r1, r2, r3);
    }

    /** Look up the (halacha, perek) for a 0-based chapter index. */
    private static Reading chapAt(int idx, boolean useThreeChapterVariant) {
        int rem = idx;
        RambamHalacha[] halachot = RambamHalacha.values();
        for (int i = 0; i < halachot.length; i++) {
            RambamHalacha h = halachot[i];
            int chapters = useThreeChapterVariant ? h.chapters3 : h.chapters;
            if (rem < chapters) {
                int chapNum = rem + 1;
                String perek = i < 4 ? RambamHalacha.FIRST_FOUR_VERSES[i][chapNum - 1]
                                     : Integer.toString(chapNum);
                return new Reading(h.en, h.he, perek);
            }
            rem -= chapters;
        }
        throw new IllegalStateException("Mishneh Torah chapter table inconsistent");
    }

    /** Collapse adjacent readings that share a name into range labels. */
    public static List<String> collapse(List<Reading> readings) {
        return collapseImpl(readings, false);
    }

    /** Hebrew-side analogue of {@link #collapse}. */
    public static List<String> collapseHe(List<Reading> readings) {
        return collapseImpl(readings, true);
    }

    private static List<String> collapseImpl(List<Reading> readings, boolean he) {
        java.util.List<String> out = new java.util.ArrayList<>();
        int i = 0;
        while (i < readings.size()) {
            Reading start = readings.get(i);
            String startName = he ? start.nameHe : start.name;
            int j = i;
            while (j + 1 < readings.size()
                    && (he ? readings.get(j + 1).nameHe : readings.get(j + 1).name).equals(startName)) {
                j++;
            }
            if (j == i) {
                out.add(he ? start.labelHe() : start.label());
            } else {
                String first = firstToken(start.perek);
                String last  = lastToken(readings.get(j).perek);
                if (he) {
                    out.add(startName + " " + Gematria.of(Integer.parseInt(first))
                            + "-" + Gematria.of(Integer.parseInt(last)));
                } else {
                    out.add(startName + " " + first + "-" + last);
                }
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
}
