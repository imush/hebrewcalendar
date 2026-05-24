package net.hebrewcalendar.impl.holiday;

import net.hebrewcalendar.ICalendar;
import net.hebrewcalendar.IDate;

/**
 * Fast of Esther (Taanit Esther): the day before Purim (13 Adar/Adar II),
 * except when Purim falls on Sunday — in which case the fast is moved back to
 * Thursday (11 Adar/Adar II) to avoid Shabbat.
 */
public final class TaanitEstherSpecialDay extends AbstractRecurringSpecialDay {

    public TaanitEstherSpecialDay(String name) {
        super(ICalendar.JEWISH, name);
    }

    @Override
    public boolean matches(IDate date) {
        IDate hDate = ICalendar.JEWISH.convert(date);
        int year = hDate.getYear();
        // Last month = Adar in regular years, Adar II in leap years
        int lastMonth = ICalendar.JEWISH.monthsInYear(year);

        if (hDate.getMonth() != lastMonth) return false;

        // Find Purim (day 14) in this month to determine its day of week
        IDate purim = ICalendar.JEWISH.convert(hDate.addDays(14 - hDate.getDay()));
        // Sunday=1 per IDate.getDayOfWeek() contract
        int taanitDay = (purim.getDayOfWeek() == 1) ? 11 : 13;
        return hDate.getDay() == taanitDay;
    }
}
