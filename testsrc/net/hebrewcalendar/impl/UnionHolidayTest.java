package net.hebrewcalendar.impl;

import net.hebrewcalendar.HCalendar;
import net.hebrewcalendar.HDate;
import net.hebrewcalendar.HHoliday;
import net.hebrewcalendar.impl.holiday.MonthDayHoliday;
import net.hebrewcalendar.impl.holiday.UnionHoliday;
import org.junit.Test;

import static org.junit.Assert.*;

public class UnionHolidayTest
{
    private static final MonthDayHoliday roshChodesh0 =
            new MonthDayHoliday(HCalendar.HEBREW, "Rosh Chodesh", 0, 30);
    private static final MonthDayHoliday roshChodesh1 =
            new MonthDayHoliday(HCalendar.HEBREW, "Rosh Chodesh", 0, 1);
    private static final HHoliday roshChodesh =
            new UnionHoliday("Rosh Chodesh", new HHoliday[] {roshChodesh0, roshChodesh1});

    @Test
    public void getNextOccurrenceOnOrAfter()
            throws Exception
    {
        HDate cheshvan11 = HCalendar.HEBREW.fromYMD(5777, 8, 11);
        HDate kislev1 = HCalendar.HEBREW.fromYMD(5777, 9, 1);
        HDate shvat11 = HCalendar.HEBREW.fromYMD(5777, 11, 11);
        HDate shvat30 = HCalendar.HEBREW.fromYMD(5777, 11, 30);
        HDate kislev2 = HCalendar.HEBREW.fromYMD(5778, 9, 2);
        HDate kislev30 = HCalendar.HEBREW.fromYMD(5778, 9, 30);
        HDate teveth1 = HCalendar.HEBREW.fromYMD(5778, 10, 1);

        assertEquals(kislev1, roshChodesh.getNextOccurrence(cheshvan11, false));
        assertEquals(shvat30, roshChodesh.getNextOccurrence(shvat11, false));
        assertEquals(kislev30, roshChodesh.getNextOccurrence(kislev2, true));
        assertEquals(teveth1, roshChodesh.getNextOccurrence(teveth1, false));
        assertEquals(teveth1, roshChodesh.getPrevOccurrence(teveth1, false));
        assertEquals(kislev30, roshChodesh.getPrevOccurrence(teveth1, true));

    }

    @Test
    public void matches()
            throws Exception
    {
        HDate shvat11 = HCalendar.HEBREW.fromYMD(5777, 11, 11);
        HDate adar1 = HCalendar.HEBREW.fromYMD(5777, 12, 1);
        HDate kislev1 = HCalendar.HEBREW.fromYMD(5777, 9, 1);
        HDate kislev30 = HCalendar.HEBREW.fromYMD(5778, 9, 30);
        HDate teveth1 = HCalendar.HEBREW.fromYMD(5778, 10, 1);

        assertTrue(roshChodesh.matches(adar1));
        assertTrue(roshChodesh.matches(kislev1));
        assertTrue(roshChodesh.matches(teveth1));
        assertTrue(roshChodesh.matches(kislev30));
        assertFalse(roshChodesh.matches(shvat11));

    }

}