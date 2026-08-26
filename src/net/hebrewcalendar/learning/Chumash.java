package net.hebrewcalendar.learning;

import net.hebrewcalendar.ICalendar;
import net.hebrewcalendar.IDate;
import net.hebrewcalendar.JewishCalendar;
import net.hebrewcalendar.data.ChumashAliyot;
import net.hebrewcalendar.data.Parsha;
import net.hebrewcalendar.impl.Parshiot;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Chumash Yomi — the day-of-week aliyah for the upcoming Shabbat's parsha.
 *
 * <p>Aliyah boundaries and book indexing come from
 * {@link net.hebrewcalendar.data.ChumashAliyot}; only the "which reading
 * on which day" algorithm (yom-tov skipping, Vezot HaBracha / Simchat
 * Torah special handling) lives here.
 */
public final class Chumash {

    private Chumash() {}

    /** Vezot HaBracha — the 54th parsha, read only on Simchat Torah. */
    private static final String VEZOT_ID = "VEZOT_HABRACHA";

    private static final String[] ALIYAH_EN = {
        "1st aliyah", "2nd aliyah", "3rd aliyah", "4th aliyah",
        "5th aliyah", "6th aliyah", "7th aliyah",
    };
    private static final String[] ALIYAH_HE = {
        "ראשון", "שני", "שלישי", "רביעי", "חמישי", "שישי", "שביעי",
    };

    /** One row of the display: a reading (single, double, or Vezot HaBracha) plus an aliyah range. */
    public static final class Portion {
        private final String readingId;
        private final int firstAliyah;
        private final int lastAliyah;
        Portion(String readingId, int first, int last) {
            this.readingId = readingId;
            this.firstAliyah = first;
            this.lastAliyah  = last;
        }

        /** Reading id (single parsha key, doubled JOINED_KEY, or VEZOT_HABRACHA). */
        public String readingId() { return readingId; }

        public List<String> parshaNames() {
            ChumashAliyot.Reading r = reading();
            if (r.displayEn != null) return List.of(r.displayEn);
            List<String> out = new ArrayList<>(r.parshiyot.size());
            for (String k : r.parshiyot) out.add(Parsha.valueOf(k).en);
            return Collections.unmodifiableList(out);
        }
        public List<String> parshaNamesHe() {
            ChumashAliyot.Reading r = reading();
            if (r.displayHe != null) return List.of(r.displayHe);
            List<String> out = new ArrayList<>(r.parshiyot.size());
            for (String k : r.parshiyot) out.add(Parsha.valueOf(k).he);
            return Collections.unmodifiableList(out);
        }
        public int firstAliyah() { return firstAliyah; }
        public int lastAliyah()  { return lastAliyah; }

        public String label()   { return String.join("-", parshaNames())   + " — " + aliyahRangeEn(); }
        public String labelHe() { return String.join("-", parshaNamesHe()) + " — " + aliyahRangeHe(); }

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

        /** Book name (Genesis / Exodus / …) containing this aliyah range. */
        public String book() { return ChumashAliyot.BOOKS[reading().book]; }
        public String startRef() { return reading().aliyot[firstAliyah - 1].split("-", 2)[0]; }
        public String endRef()   { return reading().aliyot[lastAliyah  - 1].split("-", 2)[1]; }

        private ChumashAliyot.Reading reading() {
            ChumashAliyot.Reading r = ChumashAliyot.READINGS.get(readingId);
            if (r == null) throw new IllegalStateException("no aliyah table for " + readingId);
            return r;
        }
    }

    /** Result: 1 portion normally, or 2 on Simchat Torah (Vezot + Bereishit). */
    public static final class Result {
        private final List<Portion> portions;
        Result(List<Portion> portions) {
            this.portions = Collections.unmodifiableList(portions);
        }
        public List<Portion> portions() { return portions; }
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

    public static Result forDate(LocalDate date) {
        return forDate(date, false);
    }

    public static Result forDate(LocalDate date, boolean inIsrael) {
        IDate<JewishCalendar> jd = ICalendar.JEWISH.convert(
            ICalendar.GREGORIAN.fromYMD(date.getYear(), date.getMonthValue(), date.getDayOfMonth()));
        return forHebrewDate(jd, inIsrael);
    }

    public static Result forHebrewDate(IDate<JewishCalendar> jd, boolean inIsrael) {
        int dow = jd.getDayOfWeek();  // 1=Sun..7=Sat

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
            int simchatTorahDay = inIsrael ? 22 : 23;
            IDate<JewishCalendar> simchatTorah =
                ICalendar.JEWISH.fromYMD(nextShabbat.getYear(), 7, simchatTorahDay);
            int cmp = compare(jd, simchatTorah);
            if (cmp < 0) {
                return new Result(List.of(new Portion(VEZOT_ID, dow, dow)));
            } else if (cmp == 0) {
                // Simchat Torah itself — Vezot [dow..7] + Bereshit [1..dow].
                List<Portion> ps = new ArrayList<>(2);
                ps.add(new Portion(VEZOT_ID, dow, 7));
                ps.add(new Portion(Parsha.BEREISHIT.key, 1, dow));
                return new Result(ps);
            }
        }
        // Normal case — day-of-week aliyah of the coming Shabbat's parsha.
        String readingId = nextParsha.size() == 1
                ? nextParsha.get(0).key
                : nextParsha.get(0).key + "_" + nextParsha.get(1).key;
        return new Result(List.of(new Portion(readingId, dow, dow)));
    }

    private static int compare(IDate<JewishCalendar> a, IDate<JewishCalendar> b) {
        int c = Integer.compare(a.getYear(), b.getYear());
        if (c != 0) return c;
        c = Integer.compare(a.getMonth(), b.getMonth());
        if (c != 0) return c;
        return Integer.compare(a.getDay(), b.getDay());
    }
}
