package org.hebrewcalendar.impl;

import org.hebrewcalendar.HCalendar;
import org.hebrewcalendar.HDate;
import org.junit.Test;

import static org.junit.Assert.*;

public class NegationHolidayTest
{
    private static final MonthDayHoliday roshHashana =
            new MonthDayHoliday(HCalendar.HEBREW, "Rosh Hashana", 7, 1);

    @Test
    public void testGetNextOccurrenceOnOrAfter()
            throws Exception
    {
        NegationHoliday notRoshHashana = new NegationHoliday("neg", roshHashana);
        HDate d0 = HCalendar.GREGORIAN.fromYMD(2016, 10, 3);
        assertEquals(d0.addDays(1), notRoshHashana.getNextOccurrenceOnOrAfter(d0));
        assertEquals(d0.addDays(1), notRoshHashana.getNextOccurrenceOnOrAfter(d0.addDays(1)));
        assertEquals(d0.addDays(10), notRoshHashana.getNextOccurrenceOnOrAfter(d0.addDays(10)));
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