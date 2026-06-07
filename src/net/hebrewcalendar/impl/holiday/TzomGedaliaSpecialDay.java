package net.hebrewcalendar.impl.holiday;

import net.hebrewcalendar.ICalendar;
import net.hebrewcalendar.IDate;

/**
 * Tzom Gedalia (Fast of Gedaliah): 3 Tishrei, deferred to 4 Tishrei when 3 Tishrei is Shabbat.
 */
public final class TzomGedaliaSpecialDay extends AbstractRecurringSpecialDay {

    public TzomGedaliaSpecialDay(String name) {
        super(ICalendar.JEWISH, name);
    }

    @Override
    public boolean matches(IDate date) {
        IDate hDate = ICalendar.JEWISH.convert(date);
        if (hDate.getMonth() != 7) return false;
        int day = hDate.getDay();
        if (day == 3) return hDate.getDayOfWeek() != 7;           // 3 Tishrei, unless Shabbat
        if (day == 4) return hDate.addDays(-1).getDayOfWeek() == 7; // deferred from Shabbat
        return false;
    }
}
