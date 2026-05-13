package net.hebrewcalendar;

public enum HebrewMonth {
    NISAN(1), IYAR(2), SIVAN(3), TAMUZ(4), AV(5), ELUL(6), TISHREI(7), CHESHVAN(8),
    KISLEV(9), TEVETH(10), SHVAT(11), ADAR(12), ADAR_2(13);

    private final int _num;

    private static final HebrewMonth[] _cache = new HebrewMonth[13];
    static {
        for (HebrewMonth m : values())
            _cache[m.getOrdinalNumber() - 1] = m;
    }

    HebrewMonth(int n) { _num = n; }

    public int getOrdinalNumber() { return _num; }

    public static HebrewMonth get(int num) {
        if (num < 1 || num > 13)
            throw new IllegalArgumentException("Month ordinal can only be between 1 and 13: " + num);
        return _cache[num - 1];
    }
}
