package net.hebrewcalendar;

/**
 * Created by itz on 7/20/17.
 */
public interface HJewishCalendar
    extends HCalendar
{
    /**
     * Enum to represent the length of months Kislev and Cheshvan:
     * <br> SHORT - both months have length 29
     * <br> NORMAL - Cheshvan is 29 days long and Kislev 30 days long
     * <br> FULL - both months have length 30
     */
    enum YearType {
        SHORT, NORMAL, FULL
    }

    enum JewishMonth
    {
        NISAN(1), IYAR(2), SIVAN(3), TAMUZ(4), AV(5), ELUL(6), TISHREI(7), CHESHVAN(8),
        KISLEV(9), TEVETH(10), SHVAT(11), ADAR(12), ADAR_2(13);

        private final int _num;

        private static final JewishMonth[] _cache = new JewishMonth[13];
        static {
            for (JewishMonth jm : values())
                _cache[jm.getOrdinalNumber()-1] = jm;
        }

        JewishMonth(int n)
        {
            _num = n;
        }

        /**
         * Get the ordinal number
         * @return 1 for Nisan,... 12 for Adar, 13 for Adar_2
         */
        public int getOrdinalNumber() { return _num;}

        /**
         * Return the month represented by integer ordinal.
         * @param num the number
         * @return corresponding month
         */
        public static JewishMonth get(int num) {
            if (num < 1 || num > 13)
                throw new IllegalArgumentException("Month ordinal can only be between 1 and 13: " + num);
            return _cache[num-1];
        }
    }

    YearType getYearType(int year);

    /**
     * Get sefira count for the day; return 0 where not applicable
     * @param date day to test
     * @return sefira count (1-49), or 0 if it is not between Pesach and Shavuot
     */
    int getSefira(HDate date);
}
