package net.hebrewcalendar.learning;

import net.hebrewcalendar.ICalendar;
import net.hebrewcalendar.IDate;
import net.hebrewcalendar.JewishCalendar;

import java.time.LocalDate;

/**
 * Tanya Yomi — the daily Tanya portion in Chabad's cycle. Schedule data
 * (740 entries: non-leap + leap partitions) and the section EN↔HE names
 * come from {@link net.hebrewcalendar.data.Tanya}; only the date-labeling
 * algorithm lives here.
 */
public final class Tanya {

    private Tanya() {}

    private static final String[] MONTHS_EN = {
        "Nissan", "Iyar", "Sivan", "Tammuz", "Av", "Elul",
        "Tishrei", "Cheshvan", "Kislev", "Tevet", "Shevat",
        "Adar", "Adar II",
    };
    private static final String[] MONTHS_HE = {
        "ניסן", "אייר", "סיון", "תמוז", "אב", "אלול",
        "תשרי", "חשון", "כסלו", "טבת", "שבט",
        "אדר", "אדר-ב",
    };

    public static final class Result {
        private final String dateLabel;
        private final String dateLabelHe;
        private final net.hebrewcalendar.data.Tanya.Portion portion;
        private final net.hebrewcalendar.data.Tanya.Portion secondary;

        Result(String label, String labelHe,
               net.hebrewcalendar.data.Tanya.Portion p,
               net.hebrewcalendar.data.Tanya.Portion secondary) {
            this.dateLabel = label; this.dateLabelHe = labelHe;
            this.portion = p; this.secondary = secondary;
        }

        public boolean hasPortion() { return portion != null; }
        public String label()   { return dateLabel; }
        public String labelHe() { return dateLabelHe; }

        public String perek() {
            if (portion == null) return null;
            String s = portion.section.en;
            return portion.chapter > 0 ? s + " " + portion.chapter : s;
        }
        public String perekHe() {
            if (portion == null) return null;
            String s = portion.section.he;
            return portion.chapter > 0 ? s + " " + Gematria.of(portion.chapter) : s;
        }
        public String startWords() { return portion == null ? null : portion.start; }
        public String endWords()   { return portion == null ? null : portion.end; }

        public String fullLabelHe() { return fullLabel("he"); }

        /** English-side counterpart to {@link #fullLabelHe}. The start/end
         *  quotations remain Hebrew because they cite the Tanya text. */
        public String fullLabel() { return fullLabel("en"); }
        public String fullLabel(String lang) {
            if (portion == null) return "he".equals(lang) ? dateLabelHe : dateLabel;
            String part1 = portionFor(portion, lang);
            if (secondary == null) return part1;
            return part1 + " · " + portionFor(secondary, lang);
        }
        private static String portionFor(net.hebrewcalendar.data.Tanya.Portion p, String lang) {
            String perek;
            switch (lang) {
                case "he": perek = p.section.he; break;
                case "ru": perek = p.section.ru; break;
                case "fr": perek = p.section.fr; break;
                default:   perek = p.section.en;
            }
            if (p.chapter > 0) {
                perek += " " + ("he".equals(lang) ? Gematria.of(p.chapter) : Integer.toString(p.chapter));
            }
            return perek + " — " + p.start + " ... " + p.end;
        }
    }

    public static Result forDate(LocalDate date) {
        IDate<JewishCalendar> jd = ICalendar.JEWISH.convert(
            ICalendar.GREGORIAN.fromYMD(date.getYear(), date.getMonthValue(), date.getDayOfMonth()));
        return forHebrewDate(jd);
    }

    public static Result forHebrewDate(IDate<JewishCalendar> jd) {
        int day   = jd.getDay();
        int month = jd.getMonth();
        int year  = jd.getYear();
        boolean leap = ICalendar.JEWISH.isLeap(year);
        String en = MONTHS_EN[month - 1];
        String he = MONTHS_HE[month - 1];
        if (month == 12 && leap) { en = "Adar I"; he = "אדר-א"; }
        String dateLabel   = day + " " + en;
        String dateLabelHe = Gematria.of(day) + " " + he;

        net.hebrewcalendar.data.Tanya.Portion p =
                net.hebrewcalendar.data.Tanya.SCHEDULE.get(net.hebrewcalendar.data.Tanya.key(leap, month, day));
        // Fold 30 Cheshvan / 30 Kislev into 29 when that month has only 29 days.
        net.hebrewcalendar.data.Tanya.Portion secondary = null;
        if (p != null && day == 29 && (month == 8 || month == 9)
                && ICalendar.JEWISH.monthLength(year, month) == 29) {
            secondary = net.hebrewcalendar.data.Tanya.SCHEDULE.get(net.hebrewcalendar.data.Tanya.key(leap, month, 30));
        }
        return new Result(dateLabel, dateLabelHe, p, secondary);
    }
}
