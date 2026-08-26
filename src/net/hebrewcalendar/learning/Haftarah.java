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
 * This first pass covers the most common overrides. Chabad-specific
 * fine points (RC in Elul, Shabbos Hagadol vs Erev Pesach, etc.) fall
 * through to the weekly haftarah in a few corner cases; consumers can
 * refine later without changing the public API.
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
        CHOL_HAMOED_SUKKOT
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

        for (JewishSpecialDay sd : JewishSpecialDay.values()) {
            if (!sd.matches(h)) continue;
            switch (sd) {
                case SHABBAT_SHEKALIM:  shabbatShekalim  = true; break;
                case SHABBAT_ZACHOR:    shabbatZachor    = true; break;
                case SHABBAT_PARA:      shabbatParah     = true; break;
                case SHABBAT_HACHODESH: shabbatHachodesh = true; break;
                case SHABBAT_HAGADOL:   shabbatHagadol   = true; break;
                case EREV_PESACH:       erevPesach       = true; break;
                case ROSH_HASHANA_1:
                case ROSH_HASHANA_2:
                case YOM_KIPPUR:
                case FIRST_DAY_SUKKOT:
                case SECOND_DAY_SUKKOT_C:
                case SHMINI_ATZERES_C:
                case FIRST_DAY_PESACH:
                case SEVENTH_DAY_PESACH:
                case LAST_DAY_PESACH_C:
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
        if (chanukah) {
            java.util.List<Parsha> parshas = ICalendar.JEWISH.getParsha(h, inIsrael);
            boolean isSecondShabbat = false;
            for (Parsha p : parshas) if (p == Parsha.MIKETZ) { isSecondShabbat = true; break; }
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
            case SEVENTH_DAY_PESACH:  key = "Pesach7_MAIN";        occ = Occasion.PESACH;          break;
            case LAST_DAY_PESACH_C:   key = "Pesach8_MAIN";        occ = Occasion.PESACH;          break;
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
