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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
     * that displace one or both. For anything else -- a festival, a weekday,
     * the afternoon -- use {@link #forDay}.
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
     *   <li>Shabbos morning -- the week's parsha in seven aliyot and a maftir,
     *       or the reading of a festival that has taken the day
     *   <li>a festival or fast on a weekday -- its own reading
     *   <li>Rosh Chodesh, and the weekdays of Chanukah -- theirs
     *   <li>Shabbos Mincha, and Monday and Thursday morning where nothing else
     *       claims them -- the first three aliyot of the parsha of the Shabbos
     *       ahead
     *   <li>Yom Kippur and the fasts -- an afternoon reading as well
     * </ul>
     */
    public static List<Result> forDay(LocalDate date, Custom custom, boolean inIsrael) {
        IDate<JewishCalendar> h = ICalendar.JEWISH.convert(
                ICalendar.GREGORIAN.fromYMD(date.getYear(), date.getMonthValue(),
                                            date.getDayOfMonth()));
        int dayOfWeek = h.getDayOfWeek();   // 1..7, Sunday..Shabbos
        boolean shabbos = dayOfWeek == 7;
        Occasion occasion = scan(h, inIsrael);
        List<Result> out = new ArrayList<>();

        // A day that reads something of its own reads that instead of the
        // parsha; only where it reads nothing of its own does the parsha, or
        // Rosh Chodesh, get the morning.
        Result morning = festivalMorning(occasion, shabbos, inIsrael, custom,
                                         pesachOnThursday(h, inIsrael));
        if (morning == null && shabbos) {
            ChumashAliyot.Reading reading = readingOn(h, inIsrael);
            if (reading != null) {
                String book = ChumashAliyot.BOOKS[reading.book];
                List<Span> aliyot = new ArrayList<>();
                for (String range : reading.aliyotFor(custom)) aliyot.add(parse(book, range));
                Span maftir = reading.maftir == null ? null : parse(book, reading.maftir);
                morning = withSpecialDays(occasion, aliyot, maftir);
            }
        } else if (morning == null && occasion.roshChodesh) {
            morning = roshChodeshWeekday(custom);
        } else if (morning == null && (dayOfWeek == 2 || dayOfWeek == 5)) {
            // Monday and Thursday read the opening of the parsha of the
            // Shabbos ahead -- the same three aliyot as Shabbos Mincha.
            morning = weekdayParsha(h, inIsrael, Slot.MORNING);
        }
        if (morning != null) out.add(morning);

        if (occasion.yomKippur)
            out.add(new Result(Slot.AFTERNOON, torah("YomKippur_afternoonTorah"), null));
        else if (occasion.fast)
            out.add(new Result(Slot.AFTERNOON, fastTorah(), null));
        else if (shabbos) {
            Result mincha = weekdayParsha(h, inIsrael, Slot.AFTERNOON);
            if (mincha != null) out.add(mincha);
        }

        return out;
    }

    /** The opening three aliyot of the parsha of the Shabbos ahead. */
    private static Result weekdayParsha(IDate<JewishCalendar> h, boolean inIsrael, Slot slot) {
        ChumashAliyot.Reading next = nextWeeklyReading(h, inIsrael);
        if (next == null) return null;
        String book = ChumashAliyot.BOOKS[next.book];
        List<Span> aliyot = new ArrayList<>();
        for (String range : next.aliyotWeekday) aliyot.add(parse(book, range));
        return new Result(slot, aliyot, null);
    }

    /** Whether Pesach of this year begins on a Thursday, which shifts the
     *  readings of Chol HaMoed. */
    private static boolean pesachOnThursday(IDate<JewishCalendar> h, boolean inIsrael) {
        for (int back = 0; back < 30; back++) {
            IDate<JewishCalendar> d = ICalendar.JEWISH.addDays(h, -back);
            if (JewishSpecialDay.FIRST_DAY_PESACH.matches(d)) return d.getDayOfWeek() == 5;
        }
        return false;
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
    private static Result withSpecialDays(Occasion occasion, List<Span> aliyot, Span maftir) {
        boolean roshChodesh = occasion.roshChodesh, shushanPurim = occasion.shushanPurim;
        String parshaMaftir = occasion.parshaMaftir;
        int chanukahDay = occasion.chanukahDay;

        if (shushanPurim) {
            // Purim Meshulash: Purim's own reading falls on the Shabbos, and
            // is read as the maftir. Zachor was read the week before.
            maftir = joined(torah("Purim_torah"));
        } else if (parshaMaftir != null) {
            maftir = joined(torah(parshaMaftir + "_maftir"));
        } else if (chanukahDay != 0) {
            // Each day takes the two korbanot fragments of its nasi.
            List<Span> k = torah("Chanukah_korbanot");
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
        List<Span> f = torah("RoshChodesh_torah");
        return joined(f.subList(f.size() - 2, f.size()));
    }

    /** Consecutive fragments read as one span. */
    private static Span joined(List<Span> fragments) {
        return join(fragments.get(0), fragments.get(fragments.size() - 1));
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

    // ---- the days whose reading is not the week's parsha ----------------

    /** What the day is, as far as the Torah reading is concerned. */
    private static final class Occasion {
        JewishSpecialDay festival;      // the day whose reading replaces the parsha
        boolean roshChodesh, yomKippur, fast;
        int succosIntermediate;         // opentorah's intermediate day number, or 0
        int pesachDay;                  // the day of Pesach on chol hamoed, or 0
        int chanukahDay;                // 1..8, or 0
        String parshaMaftir;            // one of the four, or null
        boolean shushanPurim;
    }

    private static Occasion scan(IDate<JewishCalendar> h, boolean inIsrael) {
        Occasion o = new Occasion();
        for (JewishSpecialDay sd : JewishSpecialDay.values()) {
            // matches() is about the date alone; applies() is what makes the
            // eighth day of Pesach a festival outside Israel and an ordinary
            // day of Acharei within it.
            if (!sd.matches(h) || !sd.applies(inIsrael)) continue;
            switch (sd) {
                case ROSH_CHODESH:      o.roshChodesh = true; break;
                case SHUSHAN_PURIM:     o.shushanPurim = true; o.festival = sd; break;
                case SHABBAT_SHEKALIM:  o.parshaMaftir = "ParshasShekalim";  break;
                case SHABBAT_ZACHOR:    o.parshaMaftir = "ParshasZachor";    break;
                case SHABBAT_PARA:      o.parshaMaftir = "ParshasParah";     break;
                case SHABBAT_HACHODESH: o.parshaMaftir = "ParshasHachodesh"; break;

                case YOM_KIPPUR:        o.yomKippur = true; o.festival = sd; break;
                case TZOM_GEDALIA: case TENTH_TEVES: case TAANIT_ESTHER:
                case FAST_TAMUZ_17: case FAST_AV_9:
                    o.fast = true; o.festival = sd; break;

                // Chol HaMoed is numbered from the first intermediate day,
                // which is the second day of the festival in Israel and the
                // third outside it, so the two lands number the same day
                // differently and Hoshanah Rabbah is the last of them.
                case CHOL_HAMOED_SUKKOT_1I: case CHOL_HAMOED_SUKKOT_1C: o.succosIntermediate = 1; break;
                case CHOL_HAMOED_SUKKOT_2I: case CHOL_HAMOED_SUKKOT_2C: o.succosIntermediate = 2; break;
                case CHOL_HAMOED_SUKKOT_3I: case CHOL_HAMOED_SUKKOT_3C: o.succosIntermediate = 3; break;
                case CHOL_HAMOED_SUKKOT_4I: case CHOL_HAMOED_SUKKOT_4C: o.succosIntermediate = 4; break;
                case CHOL_HAMOED_SUKKOT_5I: o.succosIntermediate = 5; break;
                case HOSHANA_RABBA:         o.succosIntermediate = inIsrael ? 6 : 5; break;

                // Pesach's chol hamoed is keyed by the day of Pesach itself.
                case CHOL_HAMOED_PESACH_1I: o.pesachDay = 2; break;
                case CHOL_HAMOED_PESACH_2I: o.pesachDay = 3; break;
                case CHOL_HAMOED_PESACH_3I: o.pesachDay = 4; break;
                case CHOL_HAMOED_PESACH_4I: o.pesachDay = 5; break;
                case CHOL_HAMOED_PESACH_5I: o.pesachDay = 6; break;
                case CHOL_HAMOED_PESACH_1C: o.pesachDay = 3; break;
                case CHOL_HAMOED_PESACH_2C: o.pesachDay = 4; break;
                case CHOL_HAMOED_PESACH_3C: o.pesachDay = 5; break;
                case CHOL_HAMOED_PESACH_4C: o.pesachDay = 6; break;

                case ROSH_HASHANA_1: case ROSH_HASHANA_2:
                case FIRST_DAY_SUKKOT: case SECOND_DAY_SUKKOT_C:
                case SHMINI_ATZERES_C: case SIMCHAT_TORAH_C: case SIMCHAT_TORAH_I:
                case FIRST_DAY_PESACH: case SECOND_DAY_PESACH_C:
                case SEVENTH_DAY_PESACH: case LAST_DAY_PESACH_C:
                case SHAVUOT: case SHAVUOT_2C: case PURIM:
                    o.festival = sd; break;
                default:
                    if (sd.isChanukah()) o.chanukahDay = chanukahDayOf(sd);
                    break;
            }
        }
        if (o.succosIntermediate != 0 || o.pesachDay != 0) o.festival = null;
        return o;
    }

    /**
     * The morning reading of a day that reads something other than the parsha,
     * or null if this day reads the parsha after all.
     *
     * <p>On Shabbos the festivals read seven aliyot and on a weekday five --
     * six on Yom Kippur -- which is one division with aliyot merged, not two
     * divisions. `merge` does the merging, by the numbers opentorah gives.
     */
    private static Result festivalMorning(Occasion o, boolean shabbos, boolean inIsrael,
                                          Custom custom, boolean pesachOnThursday) {
        if (o.succosIntermediate != 0) return succosIntermediate(o, shabbos, inIsrael, custom);
        if (o.pesachDay != 0) return pesachIntermediate(o, shabbos, pesachOnThursday);
        if (o.chanukahDay != 0 && !shabbos) return chanukahWeekday(o, custom);
        if (o.festival == null) return null;

        switch (o.festival) {
            case ROSH_HASHANA_1:
                return festival(shabbos ? torah("RoshHashanah1_shabbosTorah")
                                        : merge(torah("RoshHashanah1_shabbosTorah"), 3, 5),
                                joined(torah("RoshHashanah1_maftir")));
            case ROSH_HASHANA_2:
                return festival(torah("RoshHashanah2_torah"), joined(torah("RoshHashanah1_maftir")));
            case YOM_KIPPUR:
                return festival(shabbos ? torah("YomKippur_shabbosTorah")
                                        : merge(torah("YomKippur_shabbosTorah"), 2),
                                joined(torah("YomKippur_maftir")));
            case FIRST_DAY_SUKKOT: case SECOND_DAY_SUKKOT_C:
                return festival(succos1Torah(shabbos), korbanot(0));
            case SHMINI_ATZERES_C:
                return festival(shabbos ? torah("FestivalEnd_shabbosTorah")
                                        : merge(torah("FestivalEnd_shabbosTorah"), 2, 3),
                                korbanot(7));
            case SIMCHAT_TORAH_C: case SIMCHAT_TORAH_I:
                return festival(simchasTorahTorah(), korbanot(7));
            case FIRST_DAY_PESACH:
                return festival(shabbos ? torah("Pesach1_shabbosTorah")
                                        : merge(torah("Pesach1_shabbosTorah"), 4, 7),
                                joined(torah("Pesach1_maftir")));
            case SECOND_DAY_PESACH_C:
                return festival(succos1Torah(false), joined(torah("Pesach1_maftir")));
            case SEVENTH_DAY_PESACH:
                return festival(shabbos ? torah("Pesach7_shabbosTorah")
                                        : merge(torah("Pesach7_shabbosTorah"), 2, 4),
                                joined(torah("PesachIntermediate_maftirEnd")));
            case LAST_DAY_PESACH_C:
                return festival(festivalEndTorah(shabbos),
                                joined(torah("PesachIntermediate_maftirEnd")));
            case SHAVUOT:
                return festival(torah("Shavuos1_torah"), joined(torah("Shavuos1_maftir")));
            case SHAVUOT_2C:
                return festival(festivalEndTorah(shabbos), joined(torah("Shavuos1_maftir")));
            case PURIM:
                return new Result(Slot.MORNING, torah("Purim_torah"), null);
            case SHUSHAN_PURIM:
                // Purim Meshulash: on Shabbos the parsha is still read, and
                // Purim's reading becomes its maftir. That is withSpecialDays.
                return shabbos ? null : new Result(Slot.MORNING, torah("Purim_torah"), null);
            case FAST_AV_9:
                return new Result(Slot.MORNING, torah("TishaBeAv_torah"), null);
            case TZOM_GEDALIA: case TENTH_TEVES: case TAANIT_ESTHER: case FAST_TAMUZ_17:
                return new Result(Slot.MORNING, fastTorah(), null);
            default:
                return null;
        }
    }

    private static Result festival(List<Span> aliyot, Span maftir) {
        return new Result(Slot.MORNING, aliyot, maftir);
    }

    /** Succos day 1 and 2, and Pesach day 2, share one division. */
    private static List<Span> succos1Torah(boolean shabbos) {
        List<Span> all = torah("Succos1_shabbosTorah");
        return shabbos ? all : merge(all, 2, 4);
    }

    /** The last day of a festival: Shemini Atzeres, Pesach 8, Shavuos 2. On a
     *  weekday the first two aliyot are not read at all, rather than merged. */
    private static List<Span> festivalEndTorah(boolean shabbos) {
        List<Span> all = torah("FestivalEnd_shabbosTorah");
        return shabbos ? all : all.subList(2, all.size());
    }

    /** Vezos Haberachah read to its end, with Bereishis begun after it. */
    private static List<Span> simchasTorahTorah() {
        ChumashAliyot.Reading vezosHaberachah = ChumashAliyot.READINGS.get("VEZOT_HABRACHA");
        String book = ChumashAliyot.BOOKS[vezosHaberachah.book];
        List<Span> out = new ArrayList<>();
        for (String range : vezosHaberachah.aliyot) out.add(parse(book, range));
        out = merge(out, 7);                                    // seven aliyot into six
        out.add(torah("SimchasTorah_chassanBereishis").get(0)); // and the seventh is Bereishis
        return out;
    }

    private static Result succosIntermediate(Occasion o, boolean shabbos, boolean inIsrael,
                                             Custom custom) {
        Span today = korbanotToday(o.succosIntermediate, inIsrael);
        if (shabbos)
            return new Result(Slot.MORNING, torah("IntermediateShabbos_torah"), today);

        // The korbanot run out before the days do, so from the fourth
        // intermediate day on the same three are read.
        int n = Math.min(o.succosIntermediate, 4);
        List<Span> aliyot = new ArrayList<>();
        if (readsSefard(custom)) {
            for (int i = 0; i < 4; i++) aliyot.add(today);
        } else {
            aliyot.add(korbanot(n));
            aliyot.add(korbanot(n + 1));
            aliyot.add(korbanot(n + 2));
            aliyot.add(today);
        }
        return new Result(Slot.MORNING, aliyot, null);
    }

    /** Outside Israel each day of Chol HaMoed reads the korbanot of both days
     *  it could be, because which one it is depends on the doubted day. */
    private static Span korbanotToday(int n, boolean inIsrael) {
        return inIsrael ? korbanot(n) : join(korbanot(n), korbanot(n + 1));
    }

    private static Result pesachIntermediate(Occasion o, boolean shabbos, boolean pesachOnThursday) {
        Span maftir = joined(torah("PesachIntermediate_maftirEnd"));
        if (shabbos)
            return new Result(Slot.MORNING, torah("IntermediateShabbos_torah"), maftir);

        // When Pesach begins on a Thursday the fourth and fifth days fall on
        // Shabbos and the day after, and the readings shift back by one.
        int day = pesachOnThursday && (o.pesachDay == 4 || o.pesachDay == 5)
                ? o.pesachDay - 1 : o.pesachDay;
        List<Span> first3;
        switch (day) {
            case 2:  first3 = merge(succos1Torah(false), 4, 5); break;
            case 3:  first3 = torah("PesachIntermediate_torah3"); break;
            case 4:  first3 = torah("PesachIntermediate_torah4"); break;
            case 5:  // the fourth, fifth and sixth of the Shabbos aliyot,
                     // with the middle two read as one
                     List<Span> all = torah("IntermediateShabbos_torah");
                     first3 = merge(all.subList(3, 7), 3).subList(0, 3); break;
            case 6:  first3 = torah("PesachIntermediate_torah6"); break;
            default: throw new IllegalStateException("no Chol HaMoed Pesach day " + day);
        }
        List<Span> aliyot = new ArrayList<>(first3);
        aliyot.add(maftir);   // read as the fourth aliyah, not as a maftir
        return new Result(Slot.MORNING, aliyot, null);
    }

    /**
     * A weekday of Chanukah. The first day opens with the priestly blessing
     * and the last carries the closing verses; between them each day reads its
     * own nasi and then looks ahead -- Ashkenaz to the next day's offering,
     * Sefard back over the same one.
     */
    private static Result chanukahWeekday(Occasion o, Custom custom) {
        int n = o.chanukahDay;
        List<Span> aliyot = new ArrayList<>();
        boolean sefard = readsSefard(custom);

        if (o.roshChodesh) {
            // Rosh Chodesh Teves: its own reading in three, and Chanukah after it
            aliyot.addAll(roshChodeshIn3());
            aliyot.add(chanukahFull(n));
        } else if (n == 1) {
            List<Span> cohen = torah("Chanukah_day1Cohen");
            aliyot.add(sefard ? joined(cohen) : cohen.get(1));
            aliyot.add(chanukahKorban(2 * (n - 1)));
            aliyot.add(chanukahKorban(2 * (n - 1) + 1));
        } else {
            aliyot.add(chanukahKorban(2 * (n - 1)));
            aliyot.add(chanukahKorban(2 * (n - 1) + 1));
            List<Span> korbanot = torah("Chanukah_korbanot");
            Span zos = korbanot.get(korbanot.size() - 1);
            if (n != 8) aliyot.add(sefard ? chanukahFull(n) : chanukahFull(n + 1));
            else aliyot.add(sefard ? join(chanukahFull(n), zos) : zos);
        }
        return new Result(Slot.MORNING, aliyot, null);
    }

    /** Rosh Chodesh on a weekday: four aliyot, and the Gra divides the middle
     *  of them differently. */
    private static Result roshChodeshWeekday(Custom custom) {
        List<Span> t = torah("RoshChodesh_torah");
        List<Span> aliyot = new ArrayList<>();
        aliyot.add(join(t.get(0), t.get(1)));
        aliyot.add(custom.isUnder(Custom.HAGRA)
                ? join(t.get(2), t.get(3))
                : join(t.get(1), t.get(2)));
        aliyot.add(join(t.get(3), t.get(4)));
        aliyot.add(t.get(5));
        return new Result(Slot.MORNING, aliyot, null);
    }

    /** The same reading pressed into three aliyot, to leave room for Chanukah. */
    private static List<Span> roshChodeshIn3() {
        List<Span> t = torah("RoshChodesh_torah");
        List<Span> out = new ArrayList<>();
        out.add(join(t.get(0), t.get(2)));
        out.add(join(t.get(3), t.get(4)));
        out.add(t.get(5));
        return out;
    }

    /** Vayechal: read on every public fast, morning and Mincha alike. */
    private static List<Span> fastTorah() {
        List<Span> intermediate = torah("IntermediateShabbos_torah");
        List<Span> out = new ArrayList<>();
        out.add(joined(torah("Fast_afternoonTorahPart1")));
        out.add(intermediate.get(3));
        out.add(intermediate.get(4));
        return out;
    }

    // ---- small helpers over the generated spans -------------------------

    /** The fragments of a generated reading, as spans of our own. */
    private static List<Span> torah(String key) {
        List<SpecialTorah.Span> found = SpecialTorah.forReading(key);
        if (found == null) throw new IllegalStateException("no special reading " + key);
        List<Span> out = new ArrayList<>();
        for (SpecialTorah.Span s : found)
            out.add(new Span(s.book, s.fromCh, s.fromV, s.toCh, s.toV));
        return out;
    }

    private static Span join(Span first, Span last) {
        return new Span(first.book, first.fromCh, first.fromV, last.toCh, last.toV);
    }

    private static Span korbanot(int i) {
        return torah("Succos_korbanot").get(i);
    }

    private static Span chanukahKorban(int i) {
        return torah("Chanukah_korbanot").get(i);
    }

    private static Span chanukahFull(int n) {
        return join(chanukahKorban(2 * (n - 1)), chanukahKorban(2 * (n - 1) + 1));
    }

    /**
     * The aliyot numbered here folded into the ones before them. Fewer aliyot
     * are read on a weekday than on a Shabbos, and this is how the same
     * division serves both. Numbers are 1-based and count the original aliyot.
     */
    private static List<Span> merge(List<Span> aliyot, int... into) {
        Set<Integer> drop = new HashSet<>();
        for (int n : into) drop.add(n);
        List<Span> out = new ArrayList<>();
        for (int i = 0; i < aliyot.size(); i++) {
            Span s = aliyot.get(i);
            if (drop.contains(i + 1) && !out.isEmpty()) out.add(join(out.remove(out.size() - 1), s));
            else out.add(s);
        }
        return out;
    }

    /** Whether this custom reads with Sefard where Sefard and Ashkenaz divide
     *  a reading differently. Chabad hangs off Sefard in the tree but goes with
     *  Ashkenaz in these places, so it is asked about first. */
    private static boolean readsSefard(Custom custom) {
        return !custom.isUnder(Custom.CHABAD) && custom.isUnder(Custom.SEFARD);
    }
}
