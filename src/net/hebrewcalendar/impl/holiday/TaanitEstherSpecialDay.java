package net.hebrewcalendar.impl.holiday;

import net.hebrewcalendar.ICalendar;
import net.hebrewcalendar.IDate;
import net.hebrewcalendar.JewishCalendar;

/**
 * Fast of Esther (Taanit Esther): the day before Purim (13 Adar/Adar II),
 * except when Purim falls on Sunday — in which case the fast is moved back to
 * Thursday (11 Adar/Adar II) to avoid Shabbat.
 */
public final class TaanitEstherSpecialDay extends AbstractRecurringSpecialDay<JewishCalendar> {

    public TaanitEstherSpecialDay(final String name) {
        super(ICalendar.JEWISH, name);
    }

    @Override
    public boolean matches(final IDate<JewishCalendar> date) {
        final int year = date.getYear();
        // Last month = Adar in regular years, Adar II in leap years
        final int lastMonth = ICalendar.JEWISH.monthsInYear(year);

        if (date.getMonth() != lastMonth) return false;

        // Find Purim (day 14) in this month to determine its day of week
        final IDate<JewishCalendar> purim = date.addDays(14 - date.getDay());
        // Sunday=1 per IDate.getDayOfWeek() contract
        final int taanitDay = (purim.getDayOfWeek() == 1) ? 11 : 13;
        return date.getDay() == taanitDay;
    }
}
