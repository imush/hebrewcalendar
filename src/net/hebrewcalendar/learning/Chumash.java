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

    private static final String[] ALIYAH_EN = {
        "1st aliyah", "2nd aliyah", "3rd aliyah", "4th aliyah",
        "5th aliyah", "6th aliyah", "7th aliyah",
    };
    private static final String[] ALIYAH_HE = {
        "ראשון", "שני", "שלישי", "רביעי", "חמישי", "שישי", "שביעי",
    };
    private static final String[] ALIYAH_RU = {
        "1-я алия", "2-я алия", "3-я алия", "4-я алия",
        "5-я алия", "6-я алия", "7-я алия",
    };
    private static final String[] ALIYAH_FR = {
        "1ʳᵉ aliya", "2ᵉ aliya", "3ᵉ aliya", "4ᵉ aliya",
        "5ᵉ aliya", "6ᵉ aliya", "7ᵉ aliya",
    };

    /** One row of the display: a reading (single or double parsha) plus an aliyah range. */
    public static final class Portion {
        private final String readingId;
        private final int firstAliyah;
        private final int lastAliyah;
        Portion(String readingId, int first, int last) {
            this.readingId = readingId;
            this.firstAliyah = first;
            this.lastAliyah  = last;
        }

        /** Reading id (single parsha key, or doubled JOINED_KEY). */
        public String readingId() { return readingId; }

        public List<String> parshaNames()   { return parshaNames("en"); }
        public List<String> parshaNamesHe() { return parshaNames("he"); }
        private List<String> parshaNames(String lang) {
            ChumashAliyot.Reading r = reading();
            List<String> out = new ArrayList<>(r.parshiyot.size());
            for (String k : r.parshiyot) {
                Parsha p = Parsha.valueOf(k);
                switch (lang) {
                    case "he": out.add(p.he); break;
                    case "ru": out.add(p.ru); break;
                    case "fr": out.add(p.fr); break;
                    default:   out.add(p.en);
                }
            }
            return Collections.unmodifiableList(out);
        }
        public int firstAliyah() { return firstAliyah; }
        public int lastAliyah()  { return lastAliyah; }

        public String label()   { return labelFor("en"); }
        public String labelHe() { return labelFor("he"); }
        public String labelRu() { return labelFor("ru"); }
        public String labelFr() { return labelFor("fr"); }
        public String label(String lang) { return labelFor(lang); }
        private String labelFor(String lang) {
            return String.join("-", parshaNames(lang)) + " — " + aliyahRange(lang);
        }

        private String aliyahRange(String lang) {
            String[] names;
            switch (lang) {
                case "he": names = ALIYAH_HE; break;
                case "ru": names = ALIYAH_RU; break;
                case "fr": names = ALIYAH_FR; break;
                default:   names = ALIYAH_EN;
            }
            if (firstAliyah == lastAliyah) return names[firstAliyah - 1];
            if ("he".equals(lang))
                return names[firstAliyah - 1] + "-" + names[lastAliyah - 1];
            if ("ru".equals(lang))
                return "алийот " + firstAliyah + "-" + lastAliyah;
            return "aliyot " + firstAliyah + "-" + lastAliyah;
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
        public String label()           { return label("en"); }
        public String labelHe()         { return label("he"); }
        public String label(String lang) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < portions.size(); i++) {
                if (i > 0) sb.append("; ");
                sb.append(portions.get(i).label(lang));
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
                return new Result(List.of(new Portion(Parsha.VEZOT_HABRACHA.key, dow, dow)));
            } else if (cmp == 0) {
                // Simchat Torah itself — Vezot [dow..7] + Bereshit [1..dow].
                List<Portion> ps = new ArrayList<>(2);
                ps.add(new Portion(Parsha.VEZOT_HABRACHA.key, dow, 7));
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
