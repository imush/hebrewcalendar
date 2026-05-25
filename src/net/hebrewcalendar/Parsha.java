package net.hebrewcalendar;

/**
 * The 54 weekly Torah portions (parshiyot), each carrying an English and Hebrew name.
 * Double portions share a single reading on some Shabbatot; see {@link net.hebrewcalendar.impl.Parshiot}.
 */
public enum Parsha {
    BEREISHIT    ("Bereishit",    "בראשית"),
    NOACH        ("Noach",        "נח"),
    LECH_LECHA   ("Lech Lecha",   "לך לך"),
    VAYERA       ("Vayera",       "וירא"),
    CHAYEI_SARAH ("Chayei Sarah", "חיי שרה"),
    TOLDOT       ("Toldot",       "תולדות"),
    VAYETZE      ("Vayetze",      "ויצא"),
    VAYISHLACH   ("Vayishlach",   "וישלח"),
    VAYESHEV     ("Vayeshev",     "וישב"),
    MIKETZ       ("Miketz",       "מקץ"),
    VAYIGASH     ("Vayigash",     "ויגש"),
    VAYECHI      ("Vayechi",      "ויחי"),
    SHEMOT       ("Shemot",       "שמות"),
    VAERA        ("Vaera",        "וארא"),
    BO           ("Bo",           "בא"),
    BESHALACH    ("Beshalach",    "בשלח"),
    YITRO        ("Yitro",        "יתרו"),
    MISHPATIM    ("Mishpatim",    "משפטים"),
    TERUMAH      ("Terumah",      "תרומה"),
    TETZAVEH     ("Tetzaveh",     "תצוה"),
    KI_TISA      ("Ki Tisa",      "כי תשא"),
    VAYAKHEL     ("Vayakhel",     "ויקהל"),
    PEKUDEI      ("Pekudei",      "פקודי"),
    VAYIKRA      ("Vayikra",      "ויקרא"),
    TZAV         ("Tzav",         "צו"),
    SHEMINI      ("Shemini",      "שמיני"),
    TAZRIA       ("Tazria",       "תזריע"),
    METZORA      ("Metzora",      "מצורע"),
    ACHAREI_MOT  ("Acharei",      "אחרי"),
    KEDOSHIM     ("Kedoshim",     "קדושים"),
    EMOR         ("Emor",         "אמור"),
    BEHAR        ("Behar",        "בהר"),
    BECHUKOTAI   ("Bechukotai",   "בחקתי"),
    BAMIDBAR     ("Bamidbar",     "במדבר"),
    NASO         ("Naso",         "נשא"),
    BEHAALOTECHA ("Behaalotecha", "בהעלתך"),
    SHELACH      ("Shelach",      "שלח"),
    KORACH       ("Korach",       "קרח"),
    CHUKAT       ("Chukat",       "חקת"),
    BALAK        ("Balak",        "בלק"),
    PINCHAS      ("Pinchas",      "פינחס"),
    MATOT        ("Matot",        "מטות"),
    MASEI        ("Masei",        "מסעי"),
    DEVARIM      ("Devarim",      "דברים"),
    VAETCHANAN   ("Vaetchanan",   "ואתחנן"),
    EIKEV        ("Eikev",        "עקב"),
    REEH         ("Reeh",         "ראה"),
    SHOFTIM      ("Shoftim",      "שופטים"),
    KI_TEITZEI   ("Ki Teitzei",   "כי תצא"),
    KI_TAVO      ("Ki Tavo",      "כי תבוא"),
    NITZAVIM     ("Nitzavim",     "נצבים"),
    VAYEILECH    ("Vayeilech",    "וילך"),
    HAAZINU      ("Haazinu",      "האזינו");

    private final String englishName;
    private final String hebrewName;

    Parsha(String englishName, String hebrewName) {
        this.englishName = englishName;
        this.hebrewName  = hebrewName;
    }

    public String getEnglishName() { return englishName; }
    public String getHebrewName()  { return hebrewName;  }
}
