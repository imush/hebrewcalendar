package org.hebrewcalendar.impl;

enum HebrewMonth
{
    NISAN(1),
    IYAR(2),
    SIVAN(3),
    TAMUZ(4),
    AV(5),
    ELUL(6),
    TISHREI(7),
    CHESHVAN(8),
    KISLEV(9),
    TEVETH(10),
    SHVAT(11),
    ADAR(12),
    ADAR_2(13);

    private int _n;
    private HebrewMonth(int n) { _n = n; }

    public int getN() { return _n; }
}
