package net.hebrewcalendar.learning;

import java.time.LocalDate;
import java.util.Set;

import net.hebrewcalendar.data.DafYomi.Tractate;

/**
 * Worldwide Daf Yomi Bavli calculator. Tractate list + cycle constants
 * come from {@link net.hebrewcalendar.data.DafYomi}; only the algorithm
 * (day-in-cycle → tractate/daf) lives here.
 */
public final class DafYomi {

    private DafYomi() {}

    /** Tractates whose final daf occupies only amud A (front side). */
    private static final Set<String> LAST_AMUD_A_ONLY = Set.of(
        "Meilah", "Kinnim", "Tamid", "Menachot", "Bekhorot", "Makkot", "Niddah"
    );

    /** Immutable result: tractate name + daf number + cycle number. */
    public static final class Result {
        private final String tractate, tractateHe, tractateRu, tractateFr;
        private final int daf;
        private final int cycle;
        private final boolean amudA;
        Result(String tractate, String tractateHe, String tractateRu, String tractateFr,
               int daf, int cycle, boolean amudA) {
            this.tractate = tractate; this.tractateHe = tractateHe;
            this.tractateRu = tractateRu; this.tractateFr = tractateFr;
            this.daf = daf; this.cycle = cycle; this.amudA = amudA;
        }
        public String tractate()   { return tractate; }
        public String tractateHe() { return tractateHe; }
        public String tractateRu() { return tractateRu; }
        public String tractateFr() { return tractateFr; }
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
        public String labelRu() {
            return amudA ? tractateRu + " " + daf + "а" : tractateRu + " " + daf;
        }
        public String labelFr() {
            return amudA ? tractateFr + " " + daf + "a" : tractateFr + " " + daf;
        }
        public String label(String lang) {
            switch (lang) {
                case "he": return labelHe();
                case "ru": return labelRu();
                case "fr": return labelFr();
                default:   return label();
            }
        }
    }

    /**
     * Daf for the given Gregorian date.
     * @return the calculated daf, or {@code null} for dates before 11 Sep 1923.
     */
    public static Result forDate(LocalDate date) {
        long abs = date.toEpochDay();
        long oldStart = net.hebrewcalendar.data.DafYomi.OLD_START.toEpochDay();
        long newStart = net.hebrewcalendar.data.DafYomi.NEW_START.toEpochDay();
        int  oldCycleDays = net.hebrewcalendar.data.DafYomi.OLD_CYCLE_DAYS;
        int  newCycleDays = net.hebrewcalendar.data.DafYomi.NEW_CYCLE_DAYS;
        int  firstNewCycle = net.hebrewcalendar.data.DafYomi.FIRST_NEW_CYCLE;
        if (abs < oldStart) return null;

        int cycle;
        int dayInCycle;
        if (abs >= newStart) {
            long elapsed = abs - newStart;
            cycle = firstNewCycle + (int)(elapsed / newCycleDays);
            dayInCycle = (int)(elapsed % newCycleDays);
        } else {
            long elapsed = abs - oldStart;
            cycle = 1 + (int)(elapsed / oldCycleDays);
            dayInCycle = (int)(elapsed % oldCycleDays);
        }

        Tractate[] tractates = net.hebrewcalendar.data.DafYomi.TRACTATES;
        int daysSoFar = 0;
        for (Tractate t : tractates) {
            int lastDaf = (cycle < firstNewCycle) ? t.oldLastDaf : t.lastDaf;
            daysSoFar += lastDaf - 1;
            if (dayInCycle < daysSoFar) {
                int daf = lastDaf + 1 - (daysSoFar - dayInCycle) + t.dafOffset;
                boolean amudA = (daf == lastDaf + t.dafOffset)
                              && LAST_AMUD_A_ONLY.contains(t.en);
                return new Result(t.en, t.he, t.ru, t.fr, daf, cycle, amudA);
            }
        }
        throw new IllegalStateException("Daf Yomi lengths sum inconsistent");
    }
}
