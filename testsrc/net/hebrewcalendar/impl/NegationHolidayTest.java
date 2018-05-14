package net.hebrewcalendar.impl;

import net.hebrewcalendar.HDate;
import net.hebrewcalendar.HCalendar;
import net.hebrewcalendar.impl.holiday.MonthDayHoliday;
import net.hebrewcalendar.impl.holiday.NegationHoliday;
import org.junit.Test;

import static org.junit.Assert.*;

public class NegationHolidayTest
{
    private static final MonthDayHoliday roshHashana =
            new MonthDayHoliday(HCalendar.HEBREW, "Rosh Hashana", 7, 1);

    @Test
    public void testGetNextOccurrence()
            throws Exception
    {
        NegationHoliday notRoshHashana = new NegationHoliday("neg", roshHashana);
        HDate d0 = HCalendar.GREGORIAN.fromYMD(2016, 10, 3);
        assertEquals(d0.addDays(1), notRoshHashana.getNextOccurrence(d0, false));
        assertEquals(d0.addDays(1), notRoshHashana.getNextOccurrence(d0.subtractDays(1), true));
        assertEquals(d0.addDays(1), notRoshHashana.getNextOccurrence(d0.addDays(1), false));
        assertEquals(d0.addDays(10), notRoshHashana.getNextOccurrence(d0.addDays(10), false));
    }

    @Test
    public void testMatches()
            throws Exception
    {
        NegationHoliday notRoshHashana = new NegationHoliday("neg", roshHashana);
        HDate d0 = HCalendar.GREGORIAN.fromYMD(2016, 10, 3);
        assertTrue(notRoshHashana.matches(d0.addDays(1)));
        assertTrue(notRoshHashana.matches(d0.addDays(-1)));
        assertFalse(notRoshHashana.matches(d0));
    }
}