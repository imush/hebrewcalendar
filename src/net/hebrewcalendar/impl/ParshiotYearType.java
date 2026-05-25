package net.hebrewcalendar.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import net.hebrewcalendar.Parsha;
import static net.hebrewcalendar.Parsha.*;
import static net.hebrewcalendar.impl.YearCheshvanKislevType.*;

/**
 * The 14 canonical Hebrew year types, each carrying its Shabbat reading schedule.
 *
 * Constructor params: rosh (day of Rosh Hashana, 1=Sun…7=Sat), yearType (Cheshvan/Kislev length),
 * pesach (day of Pesach, 1=Sun…7=Sat).
 */
public enum ParshiotYearType {

    // ── regular years ─────────────────────────────────────────────────────────
    F25(2, FULL,   5, ei -> ei ? scheduleF25_I() : scheduleF25_D()),   // Mon/Full/Thu
    S23(2, SHORT,  3, ei -> scheduleF25_I()),                           // Mon/Short/Tue
    N35(3, NORMAL, 5, ei -> ei ? scheduleF25_I() : scheduleF25_D()),   // Tue/Normal/Thu
    F51(5, FULL,   1, ei -> scheduleF51()),                             // Thu/Full/Sun
    N57(5, NORMAL, 7, ei -> ei ? scheduleN57_I() : scheduleN57_D()),   // Thu/Normal/Sat
    F73(7, FULL,   3, ei -> scheduleF73()),                             // Sat/Full/Tue
    S71(7, SHORT,  1, ei -> scheduleS71()),                             // Sat/Short/Sun

    // ── leap years ────────────────────────────────────────────────────────────
    F27(2, FULL,   7, ei -> ei ? scheduleF27_I() : scheduleF27_D()),   // Mon/Full/Sat
    S25(2, SHORT,  5, ei -> ei ? scheduleS25_I() : scheduleS25_D()),   // Mon/Short/Thu
    N37(3, NORMAL, 7, ei -> ei ? scheduleN37_I() : scheduleN37_D()),   // Tue/Normal/Sat
    F53(5, FULL,   3, ei -> scheduleF53()),                             // Thu/Full/Tue
    S51(5, SHORT,  1, ei -> scheduleS51()),                             // Thu/Short/Sun
    F75(7, FULL,   5, ei -> ei ? scheduleF75_I() : scheduleF75_D()),   // Sat/Full/Thu
    S73(7, SHORT,  3, ei -> scheduleS73());                             // Sat/Short/Tue

    final int rosh;
    final YearCheshvanKislevType yearType;
    final int pesach;
    private final Function<Boolean, List<List<Parsha>>> schedule;

    ParshiotYearType(int rosh, YearCheshvanKislevType yearType, int pesach,
                      Function<Boolean, List<List<Parsha>>> schedule) {
        this.rosh     = rosh;
        this.yearType = yearType;
        this.pesach   = pesach;
        this.schedule = schedule;
    }

    public List<List<Parsha>> schedule(boolean inIsrael) {
        return schedule.apply(inIsrael);
    }

    public static ParshiotYearType forYear(int rosh, YearCheshvanKislevType yearType, int pesach) {
        for (ParshiotYearType t : values())
            if (t.rosh == rosh && t.yearType == yearType && t.pesach == pesach) return t;
        throw new IllegalArgumentException(
            "Unknown Hebrew year type: rosh=" + rosh + " yearType=" + yearType + " pesach=" + pesach);
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private static List<Parsha> s(Parsha a)           { return Collections.singletonList(a); }
    private static List<Parsha> d(Parsha a, Parsha b) { return Arrays.asList(a, b); }
    private static List<Parsha> e()                    { return Collections.emptyList(); }

    // ── schedule definitions ──────────────────────────────────────────────────
    // empty list entry = Shabbat with Yom Tov / Chol Hamoed reading (no regular parsha)

    // ── A : Mon/Full/Thu  Israel (50 Shabbatot) — also B and C Israel ─────────
    private static List<List<Parsha>> scheduleF25_I() {
        return Arrays.asList(
            s(VAYEILECH), s(HAAZINU), e(),
            s(BEREISHIT), s(NOACH), s(LECH_LECHA), s(VAYERA), s(CHAYEI_SARAH),
            s(TOLDOT), s(VAYETZE), s(VAYISHLACH), s(VAYESHEV), s(MIKETZ),
            s(VAYIGASH), s(VAYECHI), s(SHEMOT), s(VAERA), s(BO), s(BESHALACH),
            s(YITRO), s(MISHPATIM), s(TERUMAH), s(TETZAVEH), s(KI_TISA),
            d(VAYAKHEL,PEKUDEI), s(VAYIKRA), s(TZAV), e(),
            s(SHEMINI), d(TAZRIA,METZORA), d(ACHAREI_MOT,KEDOSHIM), s(EMOR),
            d(BEHAR,BECHUKOTAI), s(BAMIDBAR), s(NASO), s(BEHAALOTECHA),
            s(SHELACH), s(KORACH), s(CHUKAT), s(BALAK), s(PINCHAS),
            d(MATOT,MASEI), s(DEVARIM), s(VAETCHANAN), s(EIKEV), s(REEH),
            s(SHOFTIM), s(KI_TEITZEI), s(KI_TAVO), d(NITZAVIM,VAYEILECH)
        );
    }

    // ── A : Mon/Full/Thu  Diaspora (50 Shabbatot) — also C Diaspora ──────────
    private static List<List<Parsha>> scheduleF25_D() {
        return Arrays.asList(
            s(VAYEILECH), s(HAAZINU), e(),
            s(BEREISHIT), s(NOACH), s(LECH_LECHA), s(VAYERA), s(CHAYEI_SARAH),
            s(TOLDOT), s(VAYETZE), s(VAYISHLACH), s(VAYESHEV), s(MIKETZ),
            s(VAYIGASH), s(VAYECHI), s(SHEMOT), s(VAERA), s(BO), s(BESHALACH),
            s(YITRO), s(MISHPATIM), s(TERUMAH), s(TETZAVEH), s(KI_TISA),
            d(VAYAKHEL,PEKUDEI), s(VAYIKRA), s(TZAV), e(),
            s(SHEMINI), d(TAZRIA,METZORA), d(ACHAREI_MOT,KEDOSHIM), s(EMOR),
            d(BEHAR,BECHUKOTAI), s(BAMIDBAR), e(),
            s(NASO), s(BEHAALOTECHA), s(SHELACH), s(KORACH), d(CHUKAT,BALAK),
            s(PINCHAS), d(MATOT,MASEI), s(DEVARIM), s(VAETCHANAN), s(EIKEV),
            s(REEH), s(SHOFTIM), s(KI_TEITZEI), s(KI_TAVO), d(NITZAVIM,VAYEILECH)
        );
    }

    // ── D : Thu/Full/Sun  (51 Shabbatot) ─────────────────────────────────────
    private static List<List<Parsha>> scheduleF51() {
        return Arrays.asList(
            s(HAAZINU), e(), e(),
            s(BEREISHIT), s(NOACH), s(LECH_LECHA), s(VAYERA), s(CHAYEI_SARAH),
            s(TOLDOT), s(VAYETZE), s(VAYISHLACH), s(VAYESHEV), s(MIKETZ),
            s(VAYIGASH), s(VAYECHI), s(SHEMOT), s(VAERA), s(BO), s(BESHALACH),
            s(YITRO), s(MISHPATIM), s(TERUMAH), s(TETZAVEH), s(KI_TISA),
            s(VAYAKHEL), s(PEKUDEI), s(VAYIKRA), s(TZAV), e(),
            s(SHEMINI), d(TAZRIA,METZORA), d(ACHAREI_MOT,KEDOSHIM), s(EMOR),
            d(BEHAR,BECHUKOTAI), s(BAMIDBAR), s(NASO), s(BEHAALOTECHA),
            s(SHELACH), s(KORACH), s(CHUKAT), s(BALAK), s(PINCHAS),
            d(MATOT,MASEI), s(DEVARIM), s(VAETCHANAN), s(EIKEV), s(REEH),
            s(SHOFTIM), s(KI_TEITZEI), s(KI_TAVO), s(NITZAVIM)
        );
    }

    // ── E : Thu/Normal/Sat  Israel (51 Shabbatot) ────────────────────────────
    private static List<List<Parsha>> scheduleN57_I() {
        return Arrays.asList(
            s(HAAZINU), e(), e(),
            s(BEREISHIT), s(NOACH), s(LECH_LECHA), s(VAYERA), s(CHAYEI_SARAH),
            s(TOLDOT), s(VAYETZE), s(VAYISHLACH), s(VAYESHEV), s(MIKETZ),
            s(VAYIGASH), s(VAYECHI), s(SHEMOT), s(VAERA), s(BO), s(BESHALACH),
            s(YITRO), s(MISHPATIM), s(TERUMAH), s(TETZAVEH), s(KI_TISA),
            d(VAYAKHEL,PEKUDEI), s(VAYIKRA), s(TZAV), e(),
            s(SHEMINI), d(TAZRIA,METZORA), d(ACHAREI_MOT,KEDOSHIM), s(EMOR),
            s(BEHAR), s(BECHUKOTAI), s(BAMIDBAR), s(NASO), s(BEHAALOTECHA),
            s(SHELACH), s(KORACH), s(CHUKAT), s(BALAK), s(PINCHAS),
            d(MATOT,MASEI), s(DEVARIM), s(VAETCHANAN), s(EIKEV), s(REEH),
            s(SHOFTIM), s(KI_TEITZEI), s(KI_TAVO), s(NITZAVIM)
        );
    }

    // ── E : Thu/Normal/Sat  Diaspora (51 Shabbatot) ──────────────────────────
    private static List<List<Parsha>> scheduleN57_D() {
        return Arrays.asList(
            s(HAAZINU), e(), e(),
            s(BEREISHIT), s(NOACH), s(LECH_LECHA), s(VAYERA), s(CHAYEI_SARAH),
            s(TOLDOT), s(VAYETZE), s(VAYISHLACH), s(VAYESHEV), s(MIKETZ),
            s(VAYIGASH), s(VAYECHI), s(SHEMOT), s(VAERA), s(BO), s(BESHALACH),
            s(YITRO), s(MISHPATIM), s(TERUMAH), s(TETZAVEH), s(KI_TISA),
            d(VAYAKHEL,PEKUDEI), s(VAYIKRA), s(TZAV), e(), e(),
            s(SHEMINI), d(TAZRIA,METZORA), d(ACHAREI_MOT,KEDOSHIM), s(EMOR),
            d(BEHAR,BECHUKOTAI), s(BAMIDBAR),
            s(NASO), s(BEHAALOTECHA), s(SHELACH), s(KORACH), s(CHUKAT), s(BALAK),
            s(PINCHAS), d(MATOT,MASEI), s(DEVARIM), s(VAETCHANAN), s(EIKEV),
            s(REEH), s(SHOFTIM), s(KI_TEITZEI), s(KI_TAVO), s(NITZAVIM)
        );
    }

    // ── F : Sat/Full/Tue  (51 Shabbatot) ─────────────────────────────────────
    private static List<List<Parsha>> scheduleF73() {
        return Arrays.asList(
            e(), s(HAAZINU), e(), e(),
            s(BEREISHIT), s(NOACH), s(LECH_LECHA), s(VAYERA), s(CHAYEI_SARAH),
            s(TOLDOT), s(VAYETZE), s(VAYISHLACH), s(VAYESHEV), s(MIKETZ),
            s(VAYIGASH), s(VAYECHI), s(SHEMOT), s(VAERA), s(BO), s(BESHALACH),
            s(YITRO), s(MISHPATIM), s(TERUMAH), s(TETZAVEH), s(KI_TISA),
            d(VAYAKHEL,PEKUDEI), s(VAYIKRA), s(TZAV), e(),
            s(SHEMINI), d(TAZRIA,METZORA), d(ACHAREI_MOT,KEDOSHIM), s(EMOR),
            d(BEHAR,BECHUKOTAI), s(BAMIDBAR), s(NASO), s(BEHAALOTECHA),
            s(SHELACH), s(KORACH), s(CHUKAT), s(BALAK), s(PINCHAS),
            d(MATOT,MASEI), s(DEVARIM), s(VAETCHANAN), s(EIKEV), s(REEH),
            s(SHOFTIM), s(KI_TEITZEI), s(KI_TAVO), d(NITZAVIM,VAYEILECH)
        );
    }


    // ── G : Sat/Short/Sun  (51 Shabbatot) ────────────────────────────────────
    private static List<List<Parsha>> scheduleS71() {
        return Arrays.asList(
            e(), s(HAAZINU), e(), e(),
            s(BEREISHIT), s(NOACH), s(LECH_LECHA), s(VAYERA), s(CHAYEI_SARAH),
            s(TOLDOT), s(VAYETZE), s(VAYISHLACH), s(VAYESHEV), s(MIKETZ),
            s(VAYIGASH), s(VAYECHI), s(SHEMOT), s(VAERA), s(BO), s(BESHALACH),
            s(YITRO), s(MISHPATIM), s(TERUMAH), s(TETZAVEH), s(KI_TISA),
            d(VAYAKHEL,PEKUDEI), s(VAYIKRA), s(TZAV), e(),
            s(SHEMINI), d(TAZRIA,METZORA), d(ACHAREI_MOT,KEDOSHIM), s(EMOR),
            d(BEHAR,BECHUKOTAI), s(BAMIDBAR), s(NASO), s(BEHAALOTECHA),
            s(SHELACH), s(KORACH), s(CHUKAT), s(BALAK), s(PINCHAS),
            d(MATOT,MASEI), s(DEVARIM), s(VAETCHANAN), s(EIKEV), s(REEH),
            s(SHOFTIM), s(KI_TEITZEI), s(KI_TAVO), s(NITZAVIM)
        );
    }

    // ── H : Mon/Full/Sat  Israel (55 Shabbatot) ──────────────────────────────
    private static List<List<Parsha>> scheduleF27_I() {
        return Arrays.asList(
            s(VAYEILECH), s(HAAZINU), e(),
            s(BEREISHIT), s(NOACH), s(LECH_LECHA), s(VAYERA), s(CHAYEI_SARAH),
            s(TOLDOT), s(VAYETZE), s(VAYISHLACH), s(VAYESHEV), s(MIKETZ),
            s(VAYIGASH), s(VAYECHI), s(SHEMOT), s(VAERA), s(BO), s(BESHALACH),
            s(YITRO), s(MISHPATIM), s(TERUMAH), s(TETZAVEH), s(KI_TISA),
            s(VAYAKHEL), s(PEKUDEI), s(VAYIKRA), s(TZAV),
            s(SHEMINI), s(TAZRIA), s(METZORA), e(),
            s(ACHAREI_MOT), s(KEDOSHIM), s(EMOR), s(BEHAR), s(BECHUKOTAI),
            s(BAMIDBAR), s(NASO), s(BEHAALOTECHA), s(SHELACH), s(KORACH),
            s(CHUKAT), s(BALAK), s(PINCHAS), s(MATOT), s(MASEI),
            s(DEVARIM), s(VAETCHANAN), s(EIKEV), s(REEH),
            s(SHOFTIM), s(KI_TEITZEI), s(KI_TAVO), s(NITZAVIM)
        );
    }

    // ── H : Mon/Full/Sat  Diaspora (55 Shabbatot) ────────────────────────────
    private static List<List<Parsha>> scheduleF27_D() {
        return Arrays.asList(
            s(VAYEILECH), s(HAAZINU), e(),
            s(BEREISHIT), s(NOACH), s(LECH_LECHA), s(VAYERA), s(CHAYEI_SARAH),
            s(TOLDOT), s(VAYETZE), s(VAYISHLACH), s(VAYESHEV), s(MIKETZ),
            s(VAYIGASH), s(VAYECHI), s(SHEMOT), s(VAERA), s(BO), s(BESHALACH),
            s(YITRO), s(MISHPATIM), s(TERUMAH), s(TETZAVEH), s(KI_TISA),
            s(VAYAKHEL), s(PEKUDEI), s(VAYIKRA), s(TZAV),
            s(SHEMINI), s(TAZRIA), s(METZORA), e(), e(),
            s(ACHAREI_MOT), s(KEDOSHIM), s(EMOR), s(BEHAR), s(BECHUKOTAI),
            s(BAMIDBAR), s(NASO), s(BEHAALOTECHA), s(SHELACH), s(KORACH),
            s(CHUKAT), s(BALAK), s(PINCHAS), d(MATOT,MASEI),
            s(DEVARIM), s(VAETCHANAN), s(EIKEV), s(REEH),
            s(SHOFTIM), s(KI_TEITZEI), s(KI_TAVO), s(NITZAVIM)
        );
    }

    // ── I : Mon/Short/Thu  Israel (54 Shabbatot) ─────────────────────────────
    private static List<List<Parsha>> scheduleS25_I() {
        return Arrays.asList(
            s(VAYEILECH), s(HAAZINU), e(),
            s(BEREISHIT), s(NOACH), s(LECH_LECHA), s(VAYERA), s(CHAYEI_SARAH),
            s(TOLDOT), s(VAYETZE), s(VAYISHLACH), s(VAYESHEV), s(MIKETZ),
            s(VAYIGASH), s(VAYECHI), s(SHEMOT), s(VAERA), s(BO), s(BESHALACH),
            s(YITRO), s(MISHPATIM), s(TERUMAH), s(TETZAVEH), s(KI_TISA),
            s(VAYAKHEL), s(PEKUDEI), s(VAYIKRA), s(TZAV),
            s(SHEMINI), s(TAZRIA), s(METZORA), e(),
            s(ACHAREI_MOT), s(KEDOSHIM), s(EMOR), s(BEHAR), s(BECHUKOTAI),
            s(BAMIDBAR), s(NASO), s(BEHAALOTECHA), s(SHELACH), s(KORACH),
            s(CHUKAT), s(BALAK), s(PINCHAS), d(MATOT,MASEI),
            s(DEVARIM), s(VAETCHANAN), s(EIKEV), s(REEH),
            s(SHOFTIM), s(KI_TEITZEI), s(KI_TAVO), d(NITZAVIM,VAYEILECH)
        );
    }

    // ── I : Mon/Short/Thu  Diaspora (54 Shabbatot) ───────────────────────────
    private static List<List<Parsha>> scheduleS25_D() {
        return Arrays.asList(
            s(VAYEILECH), s(HAAZINU), e(),
            s(BEREISHIT), s(NOACH), s(LECH_LECHA), s(VAYERA), s(CHAYEI_SARAH),
            s(TOLDOT), s(VAYETZE), s(VAYISHLACH), s(VAYESHEV), s(MIKETZ),
            s(VAYIGASH), s(VAYECHI), s(SHEMOT), s(VAERA), s(BO), s(BESHALACH),
            s(YITRO), s(MISHPATIM), s(TERUMAH), s(TETZAVEH), s(KI_TISA),
            s(VAYAKHEL), s(PEKUDEI), s(VAYIKRA), s(TZAV),
            s(SHEMINI), s(TAZRIA), s(METZORA), e(),
            s(ACHAREI_MOT), s(KEDOSHIM), s(EMOR), s(BEHAR), s(BECHUKOTAI),
            s(BAMIDBAR), e(),
            s(NASO), s(BEHAALOTECHA), s(SHELACH), s(KORACH), d(CHUKAT,BALAK),
            s(PINCHAS), d(MATOT,MASEI), s(DEVARIM), s(VAETCHANAN), s(EIKEV),
            s(REEH), s(SHOFTIM), s(KI_TEITZEI), s(KI_TAVO), d(NITZAVIM,VAYEILECH)
        );
    }

    // ── J : Tue/Normal/Sat  Israel (55 Shabbatot) ────────────────────────────
    private static List<List<Parsha>> scheduleN37_I() {
        return Arrays.asList(
            s(VAYEILECH), s(HAAZINU), e(),
            s(BEREISHIT), s(NOACH), s(LECH_LECHA), s(VAYERA), s(CHAYEI_SARAH),
            s(TOLDOT), s(VAYETZE), s(VAYISHLACH), s(VAYESHEV), s(MIKETZ),
            s(VAYIGASH), s(VAYECHI), s(SHEMOT), s(VAERA), s(BO), s(BESHALACH),
            s(YITRO), s(MISHPATIM), s(TERUMAH), s(TETZAVEH), s(KI_TISA),
            s(VAYAKHEL), s(PEKUDEI), s(VAYIKRA), s(TZAV),
            s(SHEMINI), s(TAZRIA), s(METZORA), e(),
            s(ACHAREI_MOT), s(KEDOSHIM), s(EMOR), s(BEHAR), s(BECHUKOTAI),
            s(BAMIDBAR), s(NASO), s(BEHAALOTECHA), s(SHELACH), s(KORACH),
            s(CHUKAT), s(BALAK), s(PINCHAS), s(MATOT), s(MASEI),
            s(DEVARIM), s(VAETCHANAN), s(EIKEV), s(REEH),
            s(SHOFTIM), s(KI_TEITZEI), s(KI_TAVO), s(NITZAVIM)
        );
    }

    // ── J : Tue/Normal/Sat  Diaspora (55 Shabbatot) ──────────────────────────
    // Shavuot falls on Sunday (Pesach=Sat + 50d ≡ Sun mod 7), so no Shabbat
    // Yom Tov in Sivan — same post-Pesach structure as scheduleF27_D.
    private static List<List<Parsha>> scheduleN37_D() {
        return Arrays.asList(
            s(VAYEILECH), s(HAAZINU), e(),
            s(BEREISHIT), s(NOACH), s(LECH_LECHA), s(VAYERA), s(CHAYEI_SARAH),
            s(TOLDOT), s(VAYETZE), s(VAYISHLACH), s(VAYESHEV), s(MIKETZ),
            s(VAYIGASH), s(VAYECHI), s(SHEMOT), s(VAERA), s(BO), s(BESHALACH),
            s(YITRO), s(MISHPATIM), s(TERUMAH), s(TETZAVEH), s(KI_TISA),
            s(VAYAKHEL), s(PEKUDEI), s(VAYIKRA), s(TZAV),
            s(SHEMINI), s(TAZRIA), s(METZORA), e(), e(),
            s(ACHAREI_MOT), s(KEDOSHIM), s(EMOR), s(BEHAR), s(BECHUKOTAI),
            s(BAMIDBAR), s(NASO), s(BEHAALOTECHA), s(SHELACH), s(KORACH),
            s(CHUKAT), s(BALAK), s(PINCHAS), d(MATOT,MASEI),
            s(DEVARIM), s(VAETCHANAN), s(EIKEV), s(REEH),
            s(SHOFTIM), s(KI_TEITZEI), s(KI_TAVO), s(NITZAVIM)
        );
    }

    // ── K : Thu/Full/Tue  (55 Shabbatot) ─────────────────────────────────────
    private static List<List<Parsha>> scheduleF53() {
        return Arrays.asList(
            s(HAAZINU), e(), e(),
            s(BEREISHIT), s(NOACH), s(LECH_LECHA), s(VAYERA), s(CHAYEI_SARAH),
            s(TOLDOT), s(VAYETZE), s(VAYISHLACH), s(VAYESHEV), s(MIKETZ),
            s(VAYIGASH), s(VAYECHI), s(SHEMOT), s(VAERA), s(BO), s(BESHALACH),
            s(YITRO), s(MISHPATIM), s(TERUMAH), s(TETZAVEH), s(KI_TISA),
            s(VAYAKHEL), s(PEKUDEI), s(VAYIKRA), s(TZAV),
            s(SHEMINI), s(TAZRIA), s(METZORA), s(ACHAREI_MOT), e(),
            s(KEDOSHIM), s(EMOR), s(BEHAR), s(BECHUKOTAI),
            s(BAMIDBAR), s(NASO), s(BEHAALOTECHA), s(SHELACH), s(KORACH),
            s(CHUKAT), s(BALAK), s(PINCHAS), s(MATOT), s(MASEI),
            s(DEVARIM), s(VAETCHANAN), s(EIKEV), s(REEH),
            s(SHOFTIM), s(KI_TEITZEI), s(KI_TAVO), d(NITZAVIM,VAYEILECH)
        );
    }

    // ── L : Thu/Short/Sun  (55 Shabbatot) ────────────────────────────────────
    private static List<List<Parsha>> scheduleS51() {
        return Arrays.asList(
            s(HAAZINU), e(), e(),
            s(BEREISHIT), s(NOACH), s(LECH_LECHA), s(VAYERA), s(CHAYEI_SARAH),
            s(TOLDOT), s(VAYETZE), s(VAYISHLACH), s(VAYESHEV), s(MIKETZ),
            s(VAYIGASH), s(VAYECHI), s(SHEMOT), s(VAERA), s(BO), s(BESHALACH),
            s(YITRO), s(MISHPATIM), s(TERUMAH), s(TETZAVEH), s(KI_TISA),
            s(VAYAKHEL), s(PEKUDEI), s(VAYIKRA), s(TZAV),
            s(SHEMINI), s(TAZRIA), s(METZORA), s(ACHAREI_MOT), e(),
            s(KEDOSHIM), s(EMOR), s(BEHAR), s(BECHUKOTAI),
            s(BAMIDBAR), s(NASO), s(BEHAALOTECHA), s(SHELACH), s(KORACH),
            s(CHUKAT), s(BALAK), s(PINCHAS), s(MATOT), s(MASEI),
            s(DEVARIM), s(VAETCHANAN), s(EIKEV), s(REEH),
            s(SHOFTIM), s(KI_TEITZEI), s(KI_TAVO), s(NITZAVIM)
        );
    }

    // ── M : Sat/Full/Thu  Israel (55 Shabbatot) ──────────────────────────────
    private static List<List<Parsha>> scheduleF75_I() {
        return Arrays.asList(
            e(), s(HAAZINU), e(), e(),
            s(BEREISHIT), s(NOACH), s(LECH_LECHA), s(VAYERA), s(CHAYEI_SARAH),
            s(TOLDOT), s(VAYETZE), s(VAYISHLACH), s(VAYESHEV), s(MIKETZ),
            s(VAYIGASH), s(VAYECHI), s(SHEMOT), s(VAERA), s(BO), s(BESHALACH),
            s(YITRO), s(MISHPATIM), s(TERUMAH), s(TETZAVEH), s(KI_TISA),
            s(VAYAKHEL), s(PEKUDEI), s(VAYIKRA), s(TZAV),
            s(SHEMINI), s(TAZRIA), s(METZORA), e(),
            s(ACHAREI_MOT), s(KEDOSHIM), s(EMOR), s(BEHAR), s(BECHUKOTAI),
            s(BAMIDBAR), s(NASO), s(BEHAALOTECHA), s(SHELACH), s(KORACH),
            s(CHUKAT), s(BALAK), s(PINCHAS), d(MATOT,MASEI),
            s(DEVARIM), s(VAETCHANAN), s(EIKEV), s(REEH),
            s(SHOFTIM), s(KI_TEITZEI), s(KI_TAVO), d(NITZAVIM,VAYEILECH)
        );
    }

    // ── M : Sat/Full/Thu  Diaspora (55 Shabbatot) ────────────────────────────
    private static List<List<Parsha>> scheduleF75_D() {
        return Arrays.asList(
            e(), s(HAAZINU), e(), e(),
            s(BEREISHIT), s(NOACH), s(LECH_LECHA), s(VAYERA), s(CHAYEI_SARAH),
            s(TOLDOT), s(VAYETZE), s(VAYISHLACH), s(VAYESHEV), s(MIKETZ),
            s(VAYIGASH), s(VAYECHI), s(SHEMOT), s(VAERA), s(BO), s(BESHALACH),
            s(YITRO), s(MISHPATIM), s(TERUMAH), s(TETZAVEH), s(KI_TISA),
            s(VAYAKHEL), s(PEKUDEI), s(VAYIKRA), s(TZAV),
            s(SHEMINI), s(TAZRIA), s(METZORA), e(),
            s(ACHAREI_MOT), s(KEDOSHIM), s(EMOR), s(BEHAR), s(BECHUKOTAI),
            s(BAMIDBAR), e(),
            s(NASO), s(BEHAALOTECHA), s(SHELACH), s(KORACH), d(CHUKAT,BALAK),
            s(PINCHAS), d(MATOT,MASEI), s(DEVARIM), s(VAETCHANAN), s(EIKEV),
            s(REEH), s(SHOFTIM), s(KI_TEITZEI), s(KI_TAVO), d(NITZAVIM,VAYEILECH)
        );
    }

    // ── N : Sat/Short/Tue  (55 Shabbatot) ────────────────────────────────────
    private static List<List<Parsha>> scheduleS73() {
        return Arrays.asList(
            e(), s(HAAZINU), e(), e(),
            s(BEREISHIT), s(NOACH), s(LECH_LECHA), s(VAYERA), s(CHAYEI_SARAH),
            s(TOLDOT), s(VAYETZE), s(VAYISHLACH), s(VAYESHEV), s(MIKETZ),
            s(VAYIGASH), s(VAYECHI), s(SHEMOT), s(VAERA), s(BO), s(BESHALACH),
            s(YITRO), s(MISHPATIM), s(TERUMAH), s(TETZAVEH), s(KI_TISA),
            s(VAYAKHEL), s(PEKUDEI), s(VAYIKRA), s(TZAV),
            s(SHEMINI), s(TAZRIA), s(METZORA), e(),
            s(ACHAREI_MOT), s(KEDOSHIM), s(EMOR), s(BEHAR), s(BECHUKOTAI),
            s(BAMIDBAR), s(NASO), s(BEHAALOTECHA), s(SHELACH), s(KORACH),
            s(CHUKAT), s(BALAK), s(PINCHAS), d(MATOT,MASEI),
            s(DEVARIM), s(VAETCHANAN), s(EIKEV), s(REEH),
            s(SHOFTIM), s(KI_TEITZEI), s(KI_TAVO), d(NITZAVIM,VAYEILECH)
        );
    }
}
