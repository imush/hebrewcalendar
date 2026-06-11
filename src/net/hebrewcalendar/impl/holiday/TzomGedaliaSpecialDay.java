package net.hebrewcalendar.impl.holiday;

import net.hebrewcalendar.ICalendar;
import net.hebrewcalendar.IDate;
import net.hebrewcalendar.JewishCalendar;

/**
 * Tzom Gedalia (Fast of Gedaliah): 3 Tishrei, deferred to 4 Tishrei when 3 Tishrei is Shabbat.
 */
public final class TzomGedaliaSpecialDay extends AbstractRecurringSpecialDay<JewishCalendar> {

    public TzomGedaliaSpecialDay(String name) {
        super(ICalendar.JEWISH, name);
    }

    @Override
    public boolean matches(IDate<JewishCalendar> date) {
        if (date.getMonth() != 7) return false;
        int day = date.getDay();
        if (day == 3) return date.getDayOfWeek() != 7;           // 3 Tishrei, unless Shabbat
        if (day == 4) return date.addDays(-1).getDayOfWeek() == 7; // deferred from Shabbat
        return false;
    }
}
