package net.hebrewcalendar.learning;

import net.hebrewcalendar.ICalendar;
import net.hebrewcalendar.IDate;
import net.hebrewcalendar.JewishCalendar;
import net.hebrewcalendar.JewishSpecialDay;
import net.hebrewcalendar.data.Custom;
import net.hebrewcalendar.data.Haftarot;
import net.hebrewcalendar.data.Parsha;
import net.hebrewcalendar.data.SpecialHaftarot;

import java.time.LocalDate;
import java.util.List;

/**
 * Resolves which haftarah to read on the upcoming Shabbat for a given
 * date, respecting the opentorah precedence rules encoded in
 * {@code SpecialReadings.scala}:
 *
 * <pre>
 *   Yom Tov  &gt;  Special Parsha  &gt;  Chanukah  &gt;  Rosh Chodesh  &gt;
 *   Machar Chodesh  &gt;  weekly parsha.
 * </pre>
 *
 * The C library carries an independent port of these same rules
 * ({@code hc_haftarah.c}) for the Flutter app; the two are kept in step
 * deliberately rather than one calling the other. If you change the
 * precedence here, change it there too.
 *
 * Known gap in both: opentorah's {@code shabbatAdditionalHaftarah} — the
 * extra verses Chabad and Fes append when a Rosh Chodesh or Machar
 * Chodesh haftarah is displaced by a higher-precedence reading — is not
 * applied.
 */
public final class Haftarah {

    private Haftarah() {}

    /** Occasion labels that mirror opentorah's SpecialReadings objects. */
    public enum Occasion {
        // Kept in the same order as the C hc_haftarah_occasion enum so the two
        // can be compared by ordinal as well as by reading.
        WEEKLY,
        PARSHAT_SHEKALIM,
        PARSHAT_ZACHOR,
        PARSHAT_PARAH,
        PARSHAT_HACHODESH,
        SHABBAT_HAGADOL,
        CHANUKAH_SHABBAT_1,
        CHANUKAH_SHABBAT_2,
        ROSH_CHODESH,
        MACHAR_CHODESH,
        ROSH_HASHANA,
        YOM_KIPPUR,
        YOM_KIPPUR_AFTERNOON,
        SUKKOT,
        SHMINI_ATZERET,
        SIMCHAT_TORAH,
        PESACH,
        SHAVUOT,
        CHOL_HAMOED_PESACH,
        CHOL_HAMOED_SUKKOT,
        TISHA_BAV,
        TISHA_BAV_AFTERNOON,
        FAST_AFTERNOON,
        SHABBAT_SHUVAH,
        SHABBAT_BEFORE_ROSH_HASHANA
    }

    /** Result: the occasion driving this haftarah + one or more references. */
    public static final class Result {
        public final Occasion occasion;
        public final List<Haftarot.Reference> refs;
        /**
         * The key this reading came from in the special-day data, or null for
         * a weekly haftarah. A caller wanting what is recorded against the
         * reading -- its sources, and any remark -- looks it up by this;
         * without it the mapping from occasion to key would have to be
         * written out a second time and kept in step.
         */
        public final String key;
        Result(Occasion occasion, List<Haftarot.Reference> refs) {
            this(occasion, refs, null);
        }
        Result(Occasion occasion, List<Haftarot.Reference> refs, String key) {
            this.occasion = occasion;
            this.refs = refs;
            this.key = key;
        }
    }

    /** Upcoming (or same-day) Shabbat for a Gregorian date. */
    public static LocalDate upcomingShabbat(LocalDate date) {
        IDate<?> jd = ICalendar.JEWISH.convert(
            ICalendar.GREGORIAN.fromYMD(date.getYear(), date.getMonthValue(), date.getDayOfMonth()));
        int dow = jd.getDayOfWeek();   // 1..7 (Sun..Sat)
        int add = (dow == 7) ? 0 : 7 - dow;
        return date.plusDays(add);
    }

    /** All haftarah readings that fall ON this specific date (not the
     *  upcoming Shabbat). Empty list on days without a haftarah.
     *
     *  <ul>
     *    <li>Shabbat → one Result (weekly or a special-day override)</li>
     *    <li>Yom Tov weekday → one Result (festival haftarah)</li>
     *    <li>Yom Kippur → two Results (morning + afternoon)</li>
     *    <li>Tisha B'Av → two Results (morning + afternoon)</li>
     *    <li>Other public fasts → one Result (afternoon only)</li>
     *    <li>Chol HaMoed / regular weekday → empty</li>
     *  </ul>
     */
    public static List<Result> forDay(LocalDate date, Custom custom, boolean inIsrael) {
        IDate<JewishCalendar> h = ICalendar.JEWISH.convert(
            ICalendar.GREGORIAN.fromYMD(date.getYear(), date.getMonthValue(), date.getDayOfMonth()));
        int dow = h.getDayOfWeek();

        // Shabbat: same lookup as forDate (upcomingShabbat == today) — but that
        // answers for the morning only, and Yom Kippur is read again at Mincha
        // whichever day it falls on. Tisha BeAv needs no such case: when it
        // falls on Shabbat it is deferred to Sunday.
        if (dow == 7) {
            List<Result> shabbat = new java.util.ArrayList<>();
            Result r = forDate(date, custom, inIsrael);
            if (r != null) shabbat.add(r);
            if (JewishSpecialDay.YOM_KIPPUR.matches(h)) {
                addSpecial(shabbat, Occasion.YOM_KIPPUR_AFTERNOON, "YomKippur_AFTERNOON", custom);
            }
            return java.util.Collections.unmodifiableList(shabbat);
        }

        // Weekday: check for Yom Tov / Yom Kippur / fast days.
        List<Result> out = new java.util.ArrayList<>();
        boolean yomKippur = false, tishaBeAv = false;
        JewishSpecialDay festivalSd = null;
        boolean nonTishaBeAvFast = false;
        boolean tzomGedalia = false;
        boolean simchatTorah = false;

        for (JewishSpecialDay sd : JewishSpecialDay.values()) {
            // applies() is what keeps Israel-only days (Simchat Torah on 22
            // Tishrei) out of the Diaspora schedule and Diaspora-only second
            // days out of Israel's — matches() alone ignores location.
            if (!sd.matches(h) || !sd.applies(inIsrael)) continue;
            switch (sd) {
                case YOM_KIPPUR:   yomKippur = true; break;
                case FAST_AV_9:    tishaBeAv = true; break;
                case ROSH_HASHANA_1:
                case ROSH_HASHANA_2:
                case FIRST_DAY_SUKKOT:
                case SECOND_DAY_SUKKOT_C:
                case SHMINI_ATZERES_C:
                case FIRST_DAY_PESACH:
                case SECOND_DAY_PESACH_C:
                case SEVENTH_DAY_PESACH:
                case LAST_DAY_PESACH_C:
                case SHAVUOT:
                case SHAVUOT_2C:
                    if (festivalSd == null) festivalSd = sd;
                    break;
                case SIMCHAT_TORAH_I:
                case SIMCHAT_TORAH_C:
                    simchatTorah = true; break;
                case TZOM_GEDALIA:
                    tzomGedalia = true; nonTishaBeAvFast = true; break;
                default:
                    if (sd.isFast()) nonTishaBeAvFast = true;
            }
        }

        if (yomKippur) {
            addSpecial(out, Occasion.YOM_KIPPUR, "YomKippur_MAIN", custom);
            addSpecial(out, Occasion.YOM_KIPPUR_AFTERNOON, "YomKippur_AFTERNOON", custom);
            return out;
        }
        if (tishaBeAv) {
            addSpecial(out, Occasion.TISHA_BAV, "TishaBeAv_MAIN", custom);
            // Afternoon uses the default fast haftarah, sometimes with additions.
            addFastAfternoon(out, custom, JewishSpecialDay.FAST_AV_9, Occasion.TISHA_BAV_AFTERNOON);
            return out;
        }
        if (simchatTorah) {
            // Simchat Torah haftarah = Vezot HaBracha's haftarah.
            List<Haftarot.Reference> refs = Haftarot.forParsha(Parsha.VEZOT_HABRACHA, custom);
            if (refs != null) out.add(new Result(Occasion.SIMCHAT_TORAH, refs));
            return out;
        }
        if (festivalSd != null) {
            Result r = fromFestivalPublic(festivalSd, custom);
            if (r != null) out.add(r);
            return out;
        }
        if (nonTishaBeAvFast) {
            addFastAfternoon(out, custom, tzomGedalia ? JewishSpecialDay.TZOM_GEDALIA : null,
                             Occasion.FAST_AFTERNOON);
            return out;
        }

        return out;
    }

    private static void addSpecial(List<Result> out, Occasion occ, String key, Custom custom) {
        List<Haftarot.Reference> refs = SpecialHaftarot.forOccasion(key, custom);
        if (refs != null) out.add(new Result(occ, refs, key));
    }

    /** Fast-day afternoon haftarah.
     *
     *  Tzom Gedalya overrides the default for Morocco and Fes (opentorah:
     *  {@code FastOfGedalia.afternoonHaftarahExceptions}), so the exception
     *  table is consulted first.
     *
     *  Some customs (Sefard, Teiman and their descendants) have no fast-day
     *  haftarah at all in opentorah's data — for those nothing is added. */
    private static void addFastAfternoon(List<Result> out, Custom custom,
                                         JewishSpecialDay which, Occasion occ) {
        // Tisha BeAv Mincha is its own table, not the one the other fasts
        // share: there Teiman and most of Sefard read nothing, and here
        // everyone reads. Falling through to the shared table left more than
        // half the tree with no haftarah.
        if (which == JewishSpecialDay.FAST_AV_9) {
            List<Haftarot.Reference> own =
                SpecialHaftarot.forOccasion("TishaBeAv_AFTERNOON", custom);
            if (own != null) out.add(new Result(occ, own, "TishaBeAv_AFTERNOON"));
            return;
        }
        if (which == JewishSpecialDay.TZOM_GEDALIA) {
            List<Haftarot.Reference> exc =
                SpecialHaftarot.forOccasion("FastOfGedalia_AFTERNOON_EXCEPTIONS", custom);
            if (exc != null) { out.add(new Result(occ, exc, "FastOfGedalia_AFTERNOON_EXCEPTIONS")); return; }
        }
        List<Haftarot.Reference> base = SpecialHaftarot.forOccasion("Fast_AFTERNOON_DEFAULT", custom);
        // A custom with no entry reads nothing at Mincha -- which is a fact
        // about the day, not a gap: it is what several of the sources are
        // attesting. Say so with an empty reading rather than by returning
        // nothing at all, so a caller can still reach what is recorded here.
        out.add(base != null ? new Result(occ, base, "Fast_AFTERNOON_DEFAULT")
                             : new Result(occ, java.util.List.of(), "Fast_AFTERNOON_DEFAULT"));
    }

    private static Result fromFestivalPublic(JewishSpecialDay sd, Custom custom) {
        // Weekday Yom Tov haftarot use the same SpecialHaftarot entries as
        // their Shabbat variants (the haftarah is fixed to the yom tov).
        return fromFestival(sd, custom);
    }

    /** Main entry point: haftarah for the upcoming/current Shabbat. */
    /** Is this day on or after 17 Tammuz, so inside the three weeks? */
    private static boolean afterSeventeenTammuz(IDate<JewishCalendar> h) {
        int month = h.getMonth(), day = h.getDay();
        // Tammuz is month 4 counting from Nisan, Av is 5
        return (month == 4 && day >= 17) || month == 5;
    }

    public static Result forDate(LocalDate date, Custom custom, boolean inIsrael) {
        LocalDate shabbat  = upcomingShabbat(date);
        IDate<JewishCalendar> h = ICalendar.JEWISH.convert(
            ICalendar.GREGORIAN.fromYMD(shabbat.getYear(), shabbat.getMonthValue(), shabbat.getDayOfMonth()));
        LocalDate nextDay = shabbat.plusDays(1);
        IDate<JewishCalendar> hNext = ICalendar.JEWISH.convert(
            ICalendar.GREGORIAN.fromYMD(nextDay.getYear(), nextDay.getMonthValue(), nextDay.getDayOfMonth()));

        // Which JewishSpecialDays land on this Shabbat?
        boolean chanukah = false;
        boolean shabbatShekalim = false, shabbatZachor = false, shabbatParah = false, shabbatHachodesh = false;
        boolean shabbatHagadol = false, shabbatShuvah = false, erevPesach = false;
        JewishSpecialDay festivalSd = null;
        boolean cholHamoedPesach = false, cholHamoedSukkot = false;
        boolean simchatTorah = false;

        for (JewishSpecialDay sd : JewishSpecialDay.values()) {
            // applies() is what keeps Israel-only days (Simchat Torah on 22
            // Tishrei) out of the Diaspora schedule and Diaspora-only second
            // days out of Israel's — matches() alone ignores location.
            if (!sd.matches(h) || !sd.applies(inIsrael)) continue;
            switch (sd) {
                case SHABBAT_SHEKALIM:  shabbatShekalim  = true; break;
                case SHABBAT_ZACHOR:    shabbatZachor    = true; break;
                case SHABBAT_PARA:      shabbatParah     = true; break;
                case SHABBAT_HACHODESH: shabbatHachodesh = true; break;
                case SHABBAT_HAGADOL:   shabbatHagadol   = true; break;
                case SHABBAT_SHUVAH:    shabbatShuvah    = true; break;
                // Purim Meshulash: when Shushan Purim falls on Shabbat, that
                // Shabbat takes Parshas Zachor's maftir and haftarah. hamichlol
                // keys the two together — "שבת זכור ופורים משולש".
                case SHUSHAN_PURIM:     shabbatZachor    = true; break;
                case EREV_PESACH:       erevPesach       = true; break;
                case SIMCHAT_TORAH_I:
                case SIMCHAT_TORAH_C:
                    simchatTorah = true; break;
                case ROSH_HASHANA_1:
                case ROSH_HASHANA_2:
                case YOM_KIPPUR:
                case FIRST_DAY_SUKKOT:
                case SECOND_DAY_SUKKOT_C:
                case SHMINI_ATZERES_C:
                case FIRST_DAY_PESACH:
                case SECOND_DAY_PESACH_C:
                case SEVENTH_DAY_PESACH:
                case LAST_DAY_PESACH_C:
                case SHAVUOT:
                case SHAVUOT_2C:
                    if (festivalSd == null) festivalSd = sd;
                    break;
                default:
                    if (sd.isChanukah()) chanukah = true;
                    else if (sd.isCholHamoed()) {
                        String n = sd.getName();
                        if (n.contains("Pesach")) cholHamoedPesach = true;
                        else if (n.contains("Sukkot")) cholHamoedSukkot = true;
                    }
            }
        }

        // opentorah's SpecialShabbos: the four parshiyot plus Shabbat
        // Hagadol. This is a property of the *day*, not of which branch
        // below fires — Chabad keeps the weekly haftarah on Shabbat Hagadol,
        // but the day is still a special Shabbat for the Rosh Chodesh rules.
        boolean isSpecialShabbat = shabbatShekalim || shabbatZachor || shabbatParah
                                || shabbatHachodesh || shabbatHagadol;

        // ── Base reading ─────────────────────────────────────────────
        Result result = null;

        // Simchat Torah reads Vezot HaBracha, so it takes that haftarah
        // rather than a SpecialHaftarot entry.
        if (simchatTorah) {
            List<Haftarot.Reference> refs = Haftarot.forParsha(Parsha.VEZOT_HABRACHA, custom);
            if (refs != null) result = new Result(Occasion.SIMCHAT_TORAH, refs);
        }
        if (result == null && festivalSd != null)   result = fromFestival(festivalSd, custom);
        if (result == null && cholHamoedPesach)
            result = special(Occasion.CHOL_HAMOED_PESACH, "PesachIntermediate_SHABBAT", custom);
        if (result == null && cholHamoedSukkot)
            result = special(Occasion.CHOL_HAMOED_SUKKOT, "SuccosIntermediate_SHABBAT", custom);

        // Special Parshiot
        if (result == null && shabbatShekalim)
            result = special(Occasion.PARSHAT_SHEKALIM,  "ParshasShekalim_MAIN",  custom);
        if (result == null && shabbatZachor)
            result = special(Occasion.PARSHAT_ZACHOR,    "ParshasZachor_MAIN",    custom);
        if (result == null && shabbatParah)
            result = special(Occasion.PARSHAT_PARAH,     "ParshasParah_MAIN",     custom);
        if (result == null && shabbatHachodesh)
            result = special(Occasion.PARSHAT_HACHODESH, "ParshasHachodesh_MAIN", custom);

        // Shabbat Hagadol (Chabad keeps weekly unless it IS erev Pesach)
        if (result == null && shabbatHagadol && (custom != Custom.CHABAD || erevPesach))
            result = special(Occasion.SHABBAT_HAGADOL, "ShabbosHagodol_MAIN", custom);

        // Chanukah. opentorah splits on the day number, not the parsha:
        //   `if dayNumber < 8 then shabbat1Haftarah else shabbat2Haftarah`.
        // The eighth day is only ever a Shabbat when 25 Kislev was itself a
        // Shabbat — exactly the years that have two Chanukah Shabbatot.
        if (result == null && chanukah) {
            boolean second = JewishSpecialDay.EIGHTH_DAY_CHANUKAH.matches(h);
            result = special(second ? Occasion.CHANUKAH_SHABBAT_2 : Occasion.CHANUKAH_SHABBAT_1,
                             second ? "Chanukah_SHABBAT_2" : "Chanukah_SHABBAT_1", custom);
        }

        // ── Shabbat Shuvah and the Shabbat before Rosh Hashanah ──────
        //
        // upstream stores the Shabbat Shuvah haftarah in Vayeilech's weekly
        // slot, with only an XML comment to say so. That holds only when
        // Vayeilech falls on Shabbat Shuvah; in the other ~60% of years it is
        // folded into Nitzavim-Vayeilech and read *before* Rosh Hashanah, so
        // the reading lands a week early and Shabbat Shuvah falls through to
        // Haazinu's own haftarah — which belongs to it only when Haazinu
        // falls after Yom Kippur. Both weeks are named explicitly here; the
        // readings are unchanged, only which Shabbat they attach to.
        java.util.List<Parsha> parshas = ICalendar.JEWISH.getParsha(h, inIsrael);

        if (result == null && shabbatShuvah) {
            List<Haftarot.Reference> refs = Haftarot.forParsha(Parsha.VAYEILECH, custom);
            if (refs != null) result = new Result(Occasion.SHABBAT_SHUVAH, refs);
        }
        // Nitzavim is always the Shabbat before Rosh Hashanah, alone or paired
        // with Vayeilech. The combined-week rule below would take Vayeilech's
        // (i.e. Shabbat Shuvah's) haftarah, so name this week explicitly.
        if (result == null && parshas.contains(Parsha.NITZAVIM)) {
            List<Haftarot.Reference> refs = Haftarot.forParsha(Parsha.NITZAVIM, custom);
            if (refs != null) result = new Result(Occasion.SHABBAT_BEFORE_ROSH_HASHANA, refs);
        }

        // Pinchas read after 17 Tammuz is inside the three weeks, and takes the
        // first haftarah of rebuke — which is Matot's — instead of its own.
        // opentorah calls this correctPinchas. In a year where Matot and Masei
        // combine, Pinchas falls before the fast and keeps its own.
        if (result == null && parshas.size() == 1 && parshas.get(0) == Parsha.PINCHAS
                && afterSeventeenTammuz(h)) {
            List<Haftarot.Reference> refs = Haftarot.forParsha(Parsha.MATOT, custom);
            if (refs != null) result = new Result(Occasion.WEEKLY, refs);
        }

        // Weekly parsha. A combined week follows its second parsha, except for
        // the customs the first parsha claims back — Chabad reads Acharei's
        // haftarah when Acharei and Kedoshim combine, and Nitzavim claims every
        // custom against Vayeilech. opentorah says so with
        // precedenceWhenCombined; Haftarot carries it.
        if (result == null) {
            if (parshas.isEmpty()) return null;
            Parsha target = parshas.get(0);
            if (parshas.size() >= 2
                    && !Haftarot.takesPrecedenceWhenCombined(parshas.get(0), custom)) {
                target = parshas.get(1);
            }
            List<Haftarot.Reference> refs = Haftarot.forParsha(target, custom);
            if (refs != null) result = new Result(Occasion.WEEKLY, refs);
        }
        if (result == null) return null;

        // ── Rosh Chodesh / Machar Chodesh corrections ────────────────
        // Post-corrections on whatever was resolved above, mirroring
        // opentorah's RoshChodesh.correct / ErevRoshChodesh.correct. Each is
        // a replace-or-add decision: where the Rosh Chodesh haftarah may
        // displace the base reading it replaces it outright; where it may
        // not, Chabad (and Fes, for Machar Chodesh) still append a few of
        // its verses to whatever is read instead.
        int rc = roshChodeshOf(h);
        int mc = roshChodeshOf(hNext);

        // Rosh Chodesh Tishrei is Rosh Hashana — never mentioned as Rosh Chodesh.
        if (rc > 0 && rc != TISHREI) {
            // Tevet is always Chanukah and Av is always the Three Weeks, so in
            // both the day's own haftarah outranks Rosh Chodesh.
            boolean allowReplace = !isSpecialShabbat && rc != TEVET && rc != AV;
            // In Elul the Shiva d'Nechemta hold their ground — except for
            // Chabad, who read the Rosh Chodesh haftarah.
            if (allowReplace && (rc != ELUL || custom == Custom.CHABAD)) {
                Result r = special(Occasion.ROSH_CHODESH, "RoshChodesh_SHABBAT", custom);
                if (r != null) result = r;
            } else {
                result = append(result, "RoshChodesh_SHABBAT_ADDITION", custom);
            }
        }

        if (mc > 0 && mc != TISHREI) {
            // A Shabbat that is itself Rosh Chodesh reads the Rosh Chodesh
            // haftarah, not Machar Chodesh.
            boolean allowReplace = !isSpecialShabbat && rc <= 0
                                && mc != TEVET && mc != AV && mc != ELUL;
            // Fes never replaces — it always takes the addition instead.
            if (allowReplace && custom != Custom.FES) {
                Result r = special(Occasion.MACHAR_CHODESH, "ErevRoshChodesh_SHABBAT", custom);
                if (r != null) result = r;
            } else {
                result = append(result, "ErevRoshChodesh_SHABBAT_ADDITION", custom);
            }
        }

        return result;
    }

    // Hebrew month numbers used by the Rosh Chodesh rules.
    private static final int AV = 5, ELUL = 6, TISHREI = 7, TEVET = 10;

    /** Month whose Rosh Chodesh falls on this Hebrew date, or -1. Day 30 is
     *  the first of a two-day Rosh Chodesh and belongs to the next month;
     *  month+1 is always in range because Elul and both Adars are 29 days. */
    private static int roshChodeshOf(IDate<JewishCalendar> h) {
        if (h.getDay() == 1)  return h.getMonth();
        if (h.getDay() == 30) return h.getMonth() + 1;
        return -1;
    }

    /** Append an "addition" entry, leaving the occasion alone — the extra
     *  verses don't change what the reading is. Most customs have no
     *  addition defined, in which case the reading is returned unchanged. */
    private static Result append(Result base, String key, Custom custom) {
        List<Haftarot.Reference> extra = SpecialHaftarot.forOccasion(key, custom);
        if (extra == null || extra.isEmpty()) return base;
        List<Haftarot.Reference> combined = new java.util.ArrayList<>(base.refs);
        combined.addAll(extra);
        return new Result(base.occasion, java.util.Collections.unmodifiableList(combined));
    }

    private static Result fromFestival(JewishSpecialDay sd, Custom custom) {
        String key;
        Occasion occ;
        switch (sd) {
            case ROSH_HASHANA_1:      key = "RoshHashanah1_MAIN";  occ = Occasion.ROSH_HASHANA;    break;
            case ROSH_HASHANA_2:      key = "RoshHashanah2_MAIN";  occ = Occasion.ROSH_HASHANA;    break;
            case YOM_KIPPUR:          key = "YomKippur_MAIN";      occ = Occasion.YOM_KIPPUR;      break;
            case FIRST_DAY_SUKKOT:    key = "Succos1_MAIN";        occ = Occasion.SUKKOT;          break;
            case SECOND_DAY_SUKKOT_C: key = "Succos2_MAIN";        occ = Occasion.SUKKOT;          break;
            case SHMINI_ATZERES_C:    key = "SheminiAtzeres_MAIN"; occ = Occasion.SHMINI_ATZERET;  break;
            case FIRST_DAY_PESACH:    key = "Pesach1_MAIN";        occ = Occasion.PESACH;          break;
            case SECOND_DAY_PESACH_C: key = "Pesach2_MAIN";        occ = Occasion.PESACH;          break;
            case SEVENTH_DAY_PESACH:  key = "Pesach7_MAIN";        occ = Occasion.PESACH;          break;
            case LAST_DAY_PESACH_C:   key = "Pesach8_MAIN";        occ = Occasion.PESACH;          break;
            case SHAVUOT:             key = "Shavuos1_MAIN";       occ = Occasion.SHAVUOT;         break;
            case SHAVUOT_2C:          key = "Shavuos2_MAIN";       occ = Occasion.SHAVUOT;         break;
            default: return null;
        }
        List<Haftarot.Reference> refs = SpecialHaftarot.forOccasion(key, custom);
        return refs == null ? null : new Result(occ, refs, key);
    }

    private static Result special(Occasion occ, String key, Custom custom) {
        List<Haftarot.Reference> refs = SpecialHaftarot.forOccasion(key, custom);
        return refs == null ? null : new Result(occ, refs, key);
    }
}
