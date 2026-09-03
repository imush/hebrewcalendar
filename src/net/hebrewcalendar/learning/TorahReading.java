package net.hebrewcalendar.learning;

import net.hebrewcalendar.ICalendar;
import net.hebrewcalendar.IDate;
import net.hebrewcalendar.JewishCalendar;
import net.hebrewcalendar.JewishSpecialDay;
import net.hebrewcalendar.data.ChumashAliyot;
import net.hebrewcalendar.data.Custom;
import net.hebrewcalendar.data.Parsha;
import net.hebrewcalendar.data.SpecialTorah;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * What is read from the Torah on a given day.
 *
 * <p>Distinct from {@link Chumash}, which gives the daily portion a person
 * learns through the week. This is what is read from the scroll: the aliyot
 * and the maftir.
 *
 * <p>Verified against opentorah's own answers — see
 * {@code ReadingsAgainstOpentorahTest} and the fixture it reads.
 */
public final class TorahReading {

    private TorahReading() {}

    /** One span of Chumash: an aliyah, or the maftir. */
    public static final class Span {
        public final String book;
        public final int fromCh, fromV, toCh, toV;

        Span(String book, int fromCh, int fromV, int toCh, int toV) {
            this.book = book;
            this.fromCh = fromCh; this.fromV = fromV;
            this.toCh = toCh; this.toV = toV;
        }

        @Override public String toString() {
            return book + " " + fromCh + ":" + fromV + "-" + toCh + ":" + toV;
        }
    }

    /** When in the day a reading is read. */
    public enum Slot { MORNING, AFTERNOON }

    /** The day's reading: its aliyot in order, and the maftir if there is one. */
    public static final class Result {
        public final Slot slot;
        public final List<Span> aliyot;
        public final Span maftir;

        Result(Slot slot, List<Span> aliyot, Span maftir) {
            this.slot = slot;
            this.aliyot = Collections.unmodifiableList(aliyot);
            this.maftir = maftir;
        }
    }

    /** "1:1-1:5" in the named book. */
    private static Span parse(String book, String range) {
        int dash = range.indexOf('-');
        String from = range.substring(0, dash), to = range.substring(dash + 1);
        int fc = Integer.parseInt(from.substring(0, from.indexOf(':')));
        int fv = Integer.parseInt(from.substring(from.indexOf(':') + 1));
        int tc = Integer.parseInt(to.substring(0, to.indexOf(':')));
        int tv = Integer.parseInt(to.substring(to.indexOf(':') + 1));
        return new Span(book, fc, fv, tc, tv);
    }

    /**
     * The Shabbos morning Torah reading for this date, or null if the date is
     * not a Shabbos with a weekly parsha.
     *
     * <p>Its seven aliyot, with the maftir at its tail, and the special days
     * that displace one or both. A Shabbos whose reading replaces the parsha
     * outright -- a festival falling on Shabbos -- is not handled yet.
     */
    public static Result forDate(LocalDate date, Custom custom, boolean inIsrael) {
        for (Result result : forDay(date, custom, inIsrael))
            if (result.slot == Slot.MORNING) return result;
        return null;
    }

    /**
     * Every Torah reading that falls on this date, in the order they are read.
     *
     * <ul>
     *   <li>Shabbos morning -- the week's parsha in seven aliyot and a maftir
     *   <li>Shabbos Mincha, and Monday and Thursday morning -- the first three
     *       aliyot of the parsha of the Shabbos ahead
     * </ul>
     *
     * <p>The days whose reading is not the parsha's -- the festivals, the
     * fasts, Purim, the weekdays of Chanukah and of Rosh Chodesh -- are not
     * handled yet and contribute nothing.
     */
    public static List<Result> forDay(LocalDate date, Custom custom, boolean inIsrael) {
        IDate<JewishCalendar> h = ICalendar.JEWISH.convert(
                ICalendar.GREGORIAN.fromYMD(date.getYear(), date.getMonthValue(),
                                            date.getDayOfMonth()));
        int dayOfWeek = h.getDayOfWeek();   // 1..7, Sunday..Shabbos
        List<Result> out = new ArrayList<>();

        if (dayOfWeek == 7) {
            ChumashAliyot.Reading reading = readingOn(h, inIsrael);
            if (reading != null) {
                String book = ChumashAliyot.BOOKS[reading.book];
                List<Span> aliyot = new ArrayList<>();
                for (String range : reading.aliyotFor(custom)) aliyot.add(parse(book, range));
                Span maftir = reading.maftir == null ? null : parse(book, reading.maftir);
                out.add(withSpecialDays(h, aliyot, maftir));
            }
        }

        // Monday and Thursday morning, and Shabbos Mincha, all read the same
        // three aliyot: the opening of the parsha of the Shabbos ahead. On
        // Shabbos that is the week after this one, on a weekday this week's.
        if (dayOfWeek == 2 || dayOfWeek == 5 || dayOfWeek == 7) {
            ChumashAliyot.Reading next = nextWeeklyReading(h, inIsrael);
            if (next != null) {
                String book = ChumashAliyot.BOOKS[next.book];
                List<Span> aliyot = new ArrayList<>();
                for (String range : next.aliyotWeekday) aliyot.add(parse(book, range));
                out.add(new Result(dayOfWeek == 7 ? Slot.AFTERNOON : Slot.MORNING,
                                   aliyot, null));
            }
        }

        return out;
    }

    /** The weekly reading of this Shabbos, or null if it has none. */
    private static ChumashAliyot.Reading readingOn(IDate<JewishCalendar> shabbos, boolean inIsrael) {
        List<Parsha> parshas = ICalendar.JEWISH.getParsha(shabbos, inIsrael);
        if (parshas.isEmpty()) return null;

        // A combined week is read as one: the first parsha's aliyot run into
        // the second's, and opentorah keys that pairing as its own reading.
        String id = parshas.size() >= 2
                ? parshas.get(0).name() + "_" + parshas.get(1).name()
                : parshas.get(0).name();
        ChumashAliyot.Reading reading = ChumashAliyot.READINGS.get(id);
        if (reading == null && parshas.size() >= 2)
            reading = ChumashAliyot.READINGS.get(parshas.get(1).name());
        return reading;
    }

    /**
     * The next weekly reading after this day: the coming Shabbos's, or the
     * Shabbos after that when this day is itself Shabbos. A Shabbos taken by a
     * festival has no weekly reading, so the search steps over it.
     */
    private static ChumashAliyot.Reading nextWeeklyReading(IDate<JewishCalendar> day, boolean inIsrael) {
        int daysToShabbos = 7 - day.getDayOfWeek();   // 0 when the day is Shabbos
        IDate<JewishCalendar> shabbos = ICalendar.JEWISH.addDays(day, daysToShabbos == 0 ? 7 : daysToShabbos);
        // a year is enough: no run of festival Shabbosos comes close to it
        for (int week = 0; week < 54; week++) {
            ChumashAliyot.Reading reading = readingOn(shabbos, inIsrael);
            if (reading != null) return reading;
            shabbos = ICalendar.JEWISH.addDays(shabbos, 7);
        }
        return null;
    }

    /**
     * The special days that displace part of the parsha's reading.
     *
     * <p>Two things can happen, and they compose. A special day can take the
     * maftir -- one of the four parshiyos, a day of Chanukah, Rosh Chodesh --
     * and Rosh Chodesh, when something else has already taken the maftir,
     * instead becomes the seventh aliyah, the parsha's own seventh folding
     * back into the sixth to make room.
     */
    private static Result withSpecialDays(IDate<JewishCalendar> h,
                                          List<Span> aliyot, Span maftir) {
        boolean roshChodesh = false, shushanPurim = false;
        String parshaMaftir = null;
        int chanukahDay = 0;

        for (JewishSpecialDay sd : JewishSpecialDay.values()) {
            if (!sd.matches(h)) continue;
            switch (sd) {
                case ROSH_CHODESH:      roshChodesh  = true; break;
                case SHUSHAN_PURIM:     shushanPurim = true; break;
                case SHABBAT_SHEKALIM:  parshaMaftir = "ParshasShekalim";  break;
                case SHABBAT_ZACHOR:    parshaMaftir = "ParshasZachor";    break;
                case SHABBAT_PARA:      parshaMaftir = "ParshasParah";     break;
                case SHABBAT_HACHODESH: parshaMaftir = "ParshasHachodesh"; break;
                default:
                    if (sd.isChanukah()) chanukahDay = chanukahDayOf(sd);
                    break;
            }
        }

        if (shushanPurim) {
            // Purim Meshulash: Purim's own reading falls on the Shabbos, and
            // is read as the maftir. Zachor was read the week before.
            maftir = joined(SpecialTorah.forReading("Purim_torah"));
        } else if (parshaMaftir != null) {
            maftir = joined(SpecialTorah.forReading(parshaMaftir + "_maftir"));
        } else if (chanukahDay != 0) {
            // Each day takes the two korbanot fragments of its nasi.
            List<SpecialTorah.Span> k = SpecialTorah.forReading("Chanukah_korbanot");
            int i = 2 * (chanukahDay - 1);
            maftir = joined(k.subList(i, i + 2));
        } else if (roshChodesh) {
            maftir = roshChodeshSpan();
            roshChodesh = false;
        }

        if (roshChodesh) {
            // Something else has the maftir, so Rosh Chodesh is read as the
            // seventh aliyah and the parsha's seventh joins the sixth.
            int n = aliyot.size();
            Span sixth = aliyot.get(n - 2), seventh = aliyot.get(n - 1);
            aliyot.set(n - 2, new Span(sixth.book, sixth.fromCh, sixth.fromV,
                                       seventh.toCh, seventh.toV));
            aliyot.set(n - 1, roshChodeshSpan());
        }

        return new Result(Slot.MORNING, aliyot, maftir);
    }

    /** Numbers 28:9-15 -- the Shabbos of Rosh Chodesh, its last two fragments. */
    private static Span roshChodeshSpan() {
        List<SpecialTorah.Span> f = SpecialTorah.forReading("RoshChodesh_torah");
        return joined(f.subList(f.size() - 2, f.size()));
    }

    /** Consecutive fragments read as one span. */
    private static Span joined(List<SpecialTorah.Span> fragments) {
        SpecialTorah.Span first = fragments.get(0);
        SpecialTorah.Span last = fragments.get(fragments.size() - 1);
        return new Span(first.book, first.fromCh, first.fromV, last.toCh, last.toV);
    }

    private static int chanukahDayOf(JewishSpecialDay sd) {
        switch (sd) {
            case FIRST_DAY_CHANUKAH:   return 1;
            case SECOND_DAY_CHANUKAH:  return 2;
            case THIRD_DAY_CHANUKAH:   return 3;
            case FOURTH_DAY_CHANUKAH:  return 4;
            case FIFTH_DAY_CHANUKAH:   return 5;
            case SIXTH_DAY_CHANUKAH:   return 6;
            case SEVENTH_DAY_CHANUKAH: return 7;
            case EIGHTH_DAY_CHANUKAH:  return 8;
            default: return 0;
        }
    }
}
