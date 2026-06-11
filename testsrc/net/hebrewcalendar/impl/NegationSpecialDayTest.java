package net.hebrewcalendar.impl;

import net.hebrewcalendar.IDate;
import net.hebrewcalendar.ICalendar;
import net.hebrewcalendar.JewishCalendar;
import net.hebrewcalendar.impl.holiday.MonthDaySpecialDay;
import net.hebrewcalendar.impl.holiday.NegationSpecialDay;
import org.junit.Test;

import static org.junit.Assert.*;

public class NegationSpecialDayTest
{
    private static final MonthDaySpecialDay<JewishCalendar> roshHashana =
            new MonthDaySpecialDay<>(ICalendar.JEWISH, "Rosh Hashana", 7, 1);

    @Test
    public void testGetNextOccurrence()
            throws Exception
    {
        NegationSpecialDay<JewishCalendar> notRoshHashana = new NegationSpecialDay<>("neg", roshHashana);
        IDate<JewishCalendar> d0 = ICalendar.JEWISH.convert(ICalendar.GREGORIAN.fromYMD(2016, 10, 3));
        assertEquals(d0.addDays(1), notRoshHashana.getNextOccurrence(d0, false));
        assertEquals(d0.addDays(1), notRoshHashana.getNextOccurrence(d0.subtractDays(1), true));
        assertEquals(d0.addDays(1), notRoshHashana.getNextOccurrence(d0.addDays(1), false));
        assertEquals(d0.addDays(10), notRoshHashana.getNextOccurrence(d0.addDays(10), false));
    }

    @Test
    public void testMatches()
            throws Exception
    {
        NegationSpecialDay<JewishCalendar> notRoshHashana = new NegationSpecialDay<>("neg", roshHashana);
        IDate<JewishCalendar> d0 = ICalendar.JEWISH.convert(ICalendar.GREGORIAN.fromYMD(2016, 10, 3));
        assertTrue(notRoshHashana.matches(d0.addDays(1)));
        assertTrue(notRoshHashana.matches(d0.addDays(-1)));
        assertFalse(notRoshHashana.matches(d0));
    }
}
