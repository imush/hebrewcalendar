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
 * Known gap in both: opentorah's {@code shabbosAdditionalHaftarah} — the
 * extra verses Chabad and Fes append when a Rosh Chodesh or Machar
 * Chodesh haftarah is displaced by a higher-precedence reading — is not
 * applied.
 */
public final class Haftarah {

    private Haftarah() {}

    /** Occasion labels that mirror opentorah's SpecialReadings objects. */
    public enum Occasion {
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
        SUKKOT,
        SHMINI_ATZERET,
        PESACH,
        SHAVUOT,
        CHOL_HAMOED_PESACH,
        CHOL_HAMOED_SUKKOT,
        SIMCHAT_TORAH,
        YOM_KIPPUR_AFTERNOON,
        TISHA_BAV,
        TISHA_BAV_AFTERNOON,
        FAST_AFTERNOON
    }

    /** Result: the occasion driving this haftarah + one or more references. */
    public static final class Result {
        public final Occasion occasion;
        public final List<Haftarot.Reference> refs;
        Result(Occasion occasion, List<Haftarot.Reference> refs) {
            this.occasion = occasion;
            this.refs = refs;
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

        // Shabbat: same lookup as forDate (upcomingShabbat == today).
        if (dow == 7) {
            Result r = forDate(date, custom, inIsrael);
            return r == null ? java.util.Collections.emptyList() : java.util.Collections.singletonList(r);
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
        if (refs != null) out.add(new Result(occ, refs));
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
        if (which == JewishSpecialDay.TZOM_GEDALIA) {
            List<Haftarot.Reference> exc =
                SpecialHaftarot.forOccasion("FastOfGedalia_AFTERNOON_EXCEPTIONS", custom);
            if (exc != null) { out.add(new Result(occ, exc)); return; }
        }
        List<Haftarot.Reference> base = SpecialHaftarot.forOccasion("Fast_AFTERNOON_DEFAULT", custom);
        if (base != null) out.add(new Result(occ, base));
    }

    private static Result fromFestivalPublic(JewishSpecialDay sd, Custom custom) {
        // Weekday Yom Tov haftarot use the same SpecialHaftarot entries as
        // their Shabbat variants (the haftarah is fixed to the yom tov).
        return fromFestival(sd, custom);
    }

    /** Main entry point: haftarah for the upcoming/current Shabbat. */
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
        boolean shabbatHagadol = false, erevPesach = false;
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

        // ── Yom Tov Shabbat ──────────────────────────────────────────
        // Simchat Torah reads Vezot HaBracha, so it takes that haftarah
        // rather than a SpecialHaftarot entry.
        if (simchatTorah) {
            List<Haftarot.Reference> refs = Haftarot.forParsha(Parsha.VEZOT_HABRACHA, custom);
            if (refs != null) return new Result(Occasion.SIMCHAT_TORAH, refs);
        }
        if (festivalSd != null) {
            Result r = fromFestival(festivalSd, custom);
            if (r != null) return r;
        }
        if (cholHamoedPesach) {
            List<Haftarot.Reference> refs = SpecialHaftarot.forOccasion("PesachIntermediate_SHABBAT", custom);
            if (refs != null) return new Result(Occasion.CHOL_HAMOED_PESACH, refs);
        }
        if (cholHamoedSukkot) {
            List<Haftarot.Reference> refs = SpecialHaftarot.forOccasion("SuccosIntermediate_SHABBAT", custom);
            if (refs != null) return new Result(Occasion.CHOL_HAMOED_SUKKOT, refs);
        }

        // ── Special Parshiot ─────────────────────────────────────────
        if (shabbatShekalim)  return special(Occasion.PARSHAT_SHEKALIM,  "ParshasShekalim_MAIN",  custom);
        if (shabbatZachor)    return special(Occasion.PARSHAT_ZACHOR,    "ParshasZachor_MAIN",    custom);
        if (shabbatParah)     return special(Occasion.PARSHAT_PARAH,     "ParshasParah_MAIN",     custom);
        if (shabbatHachodesh) return special(Occasion.PARSHAT_HACHODESH, "ParshasHachodesh_MAIN", custom);

        // ── Shabbos Hagadol (Chabad keeps weekly unless it IS erev Pesach) ─
        if (shabbatHagadol && (custom != Custom.CHABAD || erevPesach)) {
            return special(Occasion.SHABBAT_HAGADOL, "ShabbosHagodol_MAIN", custom);
        }

        // ── Chanukah Shabbat ─────────────────────────────────────────
        // opentorah splits on the day number, not the parsha:
        //   `if dayNumber < 8 then shabbos1Haftarah else shabbos2Haftarah`.
        // The eighth day is only ever a Shabbat when 25 Kislev was itself a
        // Shabbat — exactly the years that have two Chanukah Shabbatot.
        if (chanukah) {
            boolean isSecondShabbat = JewishSpecialDay.EIGHTH_DAY_CHANUKAH.matches(h);
            Occasion occ = isSecondShabbat ? Occasion.CHANUKAH_SHABBAT_2 : Occasion.CHANUKAH_SHABBAT_1;
            String key = isSecondShabbat ? "Chanukah_SHABBAT_2" : "Chanukah_SHABBAT_1";
            return special(occ, key, custom);
        }

        // ── Rosh Chodesh Shabbat (skip Nisan/Av/Tishrei — handled above) ─
        int roshChodeshMonth = -1;
        if (h.getDay() == 1) roshChodeshMonth = h.getMonth();
        else if (h.getDay() == 30) roshChodeshMonth = h.getMonth() + 1;
        if (roshChodeshMonth > 0 && !isSkippedRoshChodeshMonth(roshChodeshMonth)) {
            return special(Occasion.ROSH_CHODESH, "RoshChodesh_SHABBAT", custom);
        }

        // ── Machar Chodesh Shabbat (tomorrow is Rosh Chodesh) ────────
        int nextMonth = -1;
        if (hNext.getDay() == 1) nextMonth = hNext.getMonth();
        else if (hNext.getDay() == 30) nextMonth = hNext.getMonth() + 1;
        if (nextMonth > 0 && !isSkippedRoshChodeshMonth(nextMonth)) {
            return special(Occasion.MACHAR_CHODESH, "ErevRoshChodesh_SHABBAT", custom);
        }

        // ── Weekly parsha ────────────────────────────────────────────
        java.util.List<Parsha> parshas = ICalendar.JEWISH.getParsha(h, inIsrael);
        if (parshas.isEmpty()) return null;
        // Combined week: follow the second parsha's haftarah.
        Parsha target = parshas.size() >= 2 ? parshas.get(1) : parshas.get(0);
        List<Haftarot.Reference> refs = Haftarot.forParsha(target, custom);
        return refs == null ? null : new Result(Occasion.WEEKLY, refs);
    }

    private static boolean isSkippedRoshChodeshMonth(int m) {
        // Nisan (1) — Pesach 1 haftarah trumps; Av (5) — Chazon/Nachamu rules
        // handled elsewhere; Tishrei (7) — Rosh Hashana.
        return m == 1 || m == 5 || m == 7;
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
        return refs == null ? null : new Result(occ, refs);
    }

    private static Result special(Occasion occ, String key, Custom custom) {
        List<Haftarot.Reference> refs = SpecialHaftarot.forOccasion(key, custom);
        return refs == null ? null : new Result(occ, refs);
    }
}
