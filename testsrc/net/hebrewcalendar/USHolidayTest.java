package net.hebrewcalendar;

import net.hebrewcalendar.impl.GregorianCalendar;
import net.hebrewcalendar.impl.NoSuchHolidayException;
import org.junit.Test;

import static org.junit.Assert.*;

public class USHolidayTest
{
    @Test
    public void test2018() {
        IDate<GregorianCalendar> H20171231 = ICalendar.GREGORIAN.fromYMD(2017,12,31);
        try {
            assertEquals(ICalendar.GREGORIAN.fromYMD(2018,1,1),
                    USHoliday.NEW_YEAR.getNextOccurrence(H20171231, true));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2018,1,15),
                    USHoliday.MLKJ_DAY.getNextOccurrence(H20171231, true));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2018,2,19),
                    USHoliday.WASHINGTONS_BIRTHDAY.getNextOccurrence(H20171231, false));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2018,5,28),
                    USHoliday.MEMORIAL_DAY.getNextOccurrence(H20171231, false));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2018,7,4),
                    USHoliday.JULY_4.getNextOccurrence(H20171231, false));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2018,9,3),
                    USHoliday.LABOR_DAY.getNextOccurrence(H20171231, false));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2018,11,22),
                    USHoliday.THANKSGIVING.getNextOccurrence(H20171231, false));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2018,12,25),
                    USHoliday.X_HOLIDAY.getNextOccurrence(H20171231, false));
        } catch (NoSuchHolidayException nshe) {
            fail(nshe.toString());
        }
    }
}