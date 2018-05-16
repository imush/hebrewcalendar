package net.hebrewcalendar;

import net.hebrewcalendar.impl.NoSuchHolidayException;
import org.junit.Test;

import static org.junit.Assert.*;

public class HUSHolidayTest
{
    @Test
    public void test2018() {
        HDate H20171231 = HCalendar.GREGORIAN.fromYMD(2017,12,31);
        try {
            assertEquals(HCalendar.GREGORIAN.fromYMD(2018,1,1),
                    HUSHoliday.NEW_YEAR.getNextOccurrence(H20171231, true));
            assertEquals(HCalendar.GREGORIAN.fromYMD(2018,1,15),
                    HUSHoliday.MLKJ_DAY.getNextOccurrence(H20171231, true));
            assertEquals(HCalendar.GREGORIAN.fromYMD(2018,2,19),
                    HUSHoliday.WASHINGTONS_BIRTHDAY.getNextOccurrence(H20171231, false));
            assertEquals(HCalendar.GREGORIAN.fromYMD(2018,5,28),
                    HUSHoliday.MEMORIAL_DAY.getNextOccurrence(H20171231, false));
            assertEquals(HCalendar.GREGORIAN.fromYMD(2018,7,4),
                    HUSHoliday.JULY_4.getNextOccurrence(H20171231, false));
            assertEquals(HCalendar.GREGORIAN.fromYMD(2018,9,3),
                    HUSHoliday.LABOR_DAY.getNextOccurrence(H20171231, false));
            assertEquals(HCalendar.GREGORIAN.fromYMD(2018,11,22),
                    HUSHoliday.THANKSGIVING.getNextOccurrence(H20171231, false));
            assertEquals(HCalendar.GREGORIAN.fromYMD(2018,12,25),
                    HUSHoliday.X_HOLIDAY.getNextOccurrence(H20171231, false));
        } catch (NoSuchHolidayException nshe) {
            fail(nshe.toString());
        }
    }
}