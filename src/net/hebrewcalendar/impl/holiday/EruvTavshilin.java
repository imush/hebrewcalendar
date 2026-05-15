package net.hebrewcalendar.impl.holiday;

import net.hebrewcalendar.HDate;
import net.hebrewcalendar.HHoliday;

/**
 * Eruv Tavshilin: required on the day before a Yom Tov that falls on Friday,
 * or two days before a Yom Tov that spans Thursday–Friday (i.e. Thursday is
 * Yom Tov and Friday is also Yom Tov).
 *
 * <p>Day-of-week convention (same as {@link net.hebrewcalendar.HDate#getDayOfWeek()}):
 * 1=Sun, 2=Mon, 3=Tue, 4=Wed, 5=Thu, 6=Fri, 7=Sat.</p>
 */
public final class EruvTavshilin extends AbstractHoliday {

    private final HHoliday[] _yomTovDays;

    /**
     * @param name       display name
     * @param yomTovDays the Yom Tov days relevant for this location (Israel or diaspora)
     */
    public EruvTavshilin(String name, HHoliday[] yomTovDays) {
        super(yomTovDays[0].getCalendar(), name);
        _yomTovDays = yomTovDays;
    }

    private boolean isYomTov(HDate date) {
        for (HHoliday h : _yomTovDays) {
            if (h.matches(date)) return true;
        }
        return false;
    }

    /**
     * Returns true when Eruv Tavshilin must be made on {@code date}:
     * <ul>
     *   <li>today is not itself Shabbat or Yom Tov,</li>
     *   <li>tomorrow is Yom Tov and falls on Friday, or</li>
     *   <li>tomorrow is Yom Tov on Thursday and the day after is also Yom Tov.</li>
     * </ul>
     */
    @Override
    public boolean matches(HDate date) {
        if (date.getDayOfWeek() == 7 || isYomTov(date)) return false; // already in rest day
        HDate tomorrow = date.addDays(1);
        if (!isYomTov(tomorrow)) return false;
        int dow = tomorrow.getDayOfWeek();
        if (dow == 6) return true;                                 // YT on Friday
        if (dow == 5 && isYomTov(date.addDays(2))) return true;   // YT Thu, Fri also YT
        return false;
    }
}
