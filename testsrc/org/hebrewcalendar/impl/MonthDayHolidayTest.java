package org.hebrewcalendar.impl;

import org.hebrewcalendar.HCalendar;
import org.hebrewcalendar.HDate;
import org.hebrewcalendar.impl.holiday.MonthDayHoliday;
import org.junit.Test;

import static org.junit.Assert.*;

public class MonthDayHolidayTest
{
    private static final MonthDayHoliday jan1G = new MonthDayHoliday(HCalendar.GREGORIAN, "January 1", 1, 1);
    private static final MonthDayHoliday jan31G = new MonthDayHoliday(HCalendar.GREGORIAN, "January 31",1, -1);
    private static final MonthDayHoliday firstOfAnyG = new MonthDayHoliday(HCalendar.GREGORIAN, "First of any month", 0, 1);
    private static final MonthDayHoliday lastOfYearG = new MonthDayHoliday(HCalendar.GREGORIAN, "Last day of year", -1, -1);

    private static final MonthDayHoliday pesach = new MonthDayHoliday(HCalendar.HEBREW, "Pesach", 1, 15);
    private static final MonthDayHoliday lastOfYearH = new MonthDayHoliday(HCalendar.HEBREW, "Last day of year", 6, -1);
    private static final MonthDayHoliday erevRHNisanH = new MonthDayHoliday(HCalendar.HEBREW, "Last day of Adar", -1, -1);

    private static final MonthDayHoliday anyDayH = new MonthDayHoliday(HCalendar.HEBREW, "Anything goes", 0, 0);

    @Test
    public void getDayOfMonth()
            throws Exception
    {
        assertEquals(1, jan1G.getDayOfMonth());
    }

    @Test
    public void getMonth()
            throws Exception
    {
        assertEquals(1, jan1G.getMonth());
    }

    @Test
    public void getNextOccurrence()
            throws Exception
    {
        assertEquals(HCalendar.GREGORIAN.fromYMD(2017,1,1),
                jan1G.getNextOccurrence(HCalendar.GREGORIAN.fromYMD(2017,1,1), false));
        assertEquals(HCalendar.GREGORIAN.fromYMD(2019,1,1),
                jan1G.getNextOccurrence(HCalendar.GREGORIAN.fromYMD(2018,1,1), true));
        assertEquals(HCalendar.GREGORIAN.fromYMD(2017,1,1),
                jan1G.getNextOccurrence(HCalendar.GREGORIAN.fromYMD(2016,1,2), false));
        assertEquals(HCalendar.GREGORIAN.fromYMD(2017,1,1),
                jan1G.getNextOccurrence(HCalendar.GREGORIAN.fromYMD(2016,11,2), false));

        assertEquals(HCalendar.HEBREW.fromYMD(5777,1,15),
                pesach.getNextOccurrence(HCalendar.HEBREW.fromYMD(5777,1,15), false));
        assertEquals(HCalendar.HEBREW.fromYMD(5777,1,15),
                pesach.getNextOccurrence(HCalendar.HEBREW.fromYMD(5777,12,15), false));
        assertEquals(HCalendar.HEBREW.fromYMD(5777,1,15),
                pesach.getNextOccurrence(HCalendar.HEBREW.fromYMD(5777,7,15), false));
        assertEquals(HCalendar.HEBREW.fromYMD(5777,1,15),
                pesach.getNextOccurrence(HCalendar.HEBREW.fromYMD(5777,7,15), true));
        assertEquals(HCalendar.HEBREW.fromYMD(5777,1,15),
                pesach.getNextOccurrence(HCalendar.HEBREW.fromYMD(5776,1,16), false));

        for (HDate d = HCalendar.HEBREW.fromYMD(5777, 7, 1); d.getYear() < 5790; d = d.addDays(1)) {
            assertEquals(d, anyDayH.getNextOccurrence(d, false));
            assertEquals(d.addDays(1), anyDayH.getNextOccurrence(d, true));
        }

    }

    @Test
    public void getPrevOccurrence()
            throws Exception
    {
        assertEquals(HCalendar.GREGORIAN.fromYMD(2017,1,1),
                jan1G.getPrevOccurrence(HCalendar.GREGORIAN.fromYMD(2017,1,1), false));
        assertEquals(HCalendar.GREGORIAN.fromYMD(2015,1,1),
                jan1G.getPrevOccurrence(HCalendar.GREGORIAN.fromYMD(2016,1,1), true));
        assertEquals(HCalendar.GREGORIAN.fromYMD(2016,1,1),
                jan1G.getPrevOccurrence(HCalendar.GREGORIAN.fromYMD(2016,1,2), false));
        assertEquals(HCalendar.GREGORIAN.fromYMD(2017,1,1),
                jan1G.getPrevOccurrence(HCalendar.GREGORIAN.fromYMD(2017,1,26), true));
        assertEquals(HCalendar.GREGORIAN.fromYMD(2016,1,1),
                jan1G.getPrevOccurrence(HCalendar.GREGORIAN.fromYMD(2016,11,2), false));

        assertEquals(HCalendar.HEBREW.fromYMD(5777,1,15),
                pesach.getPrevOccurrence(HCalendar.HEBREW.fromYMD(5777,1,15), false));
        assertEquals(HCalendar.HEBREW.fromYMD(5775,1,15),
                pesach.getPrevOccurrence(HCalendar.HEBREW.fromYMD(5776,1,15), true));
        assertEquals(HCalendar.HEBREW.fromYMD(5776,1,15),
                pesach.getPrevOccurrence(HCalendar.HEBREW.fromYMD(5777,12,15), false));

        for (HDate d = HCalendar.HEBREW.fromYMD(5777, 7, 1); d.getYear() < 5790; d = d.addDays(1)) {
            assertEquals(d, anyDayH.getNextOccurrence(d, false));
            assertEquals(d.addDays(1), anyDayH.getNextOccurrence(d, true));
        }

    }
    @Test
    public void matches()
            throws Exception
    {
        assertTrue(jan1G.matches(HCalendar.GREGORIAN.fromYMD(2000, 1, 1)));
        assertFalse(jan1G.matches(HCalendar.GREGORIAN.fromYMD(2000, 1, 9)));
        assertTrue(jan31G.matches(HCalendar.GREGORIAN.fromYMD(2000, 1, 31)));
        assertFalse(jan31G.matches(HCalendar.GREGORIAN.fromYMD(2000, 1, 30)));

        assertTrue(firstOfAnyG.matches(HCalendar.GREGORIAN.fromYMD(2000, 1, 1)));
        assertTrue(firstOfAnyG.matches(HCalendar.GREGORIAN.fromYMD(2000, 8, 1)));
        assertTrue(firstOfAnyG.matches(HCalendar.GREGORIAN.fromYMD(2000, 12, 1)));
        assertFalse(firstOfAnyG.matches(HCalendar.GREGORIAN.fromYMD(2000, 12, 7)));

        assertTrue(lastOfYearG.matches(HCalendar.GREGORIAN.fromYMD(2000, 12, 31)));
        assertFalse(lastOfYearG.matches(HCalendar.GREGORIAN.fromYMD(2008, 12, 30)));

        assertTrue(lastOfYearH.matches(HCalendar.HEBREW.fromYMD(5777, 7, 1).subtractDays(1)));
        assertTrue(erevRHNisanH.matches(HCalendar.HEBREW.fromYMD(5777, 12, 29)));
        assertTrue(erevRHNisanH.matches(HCalendar.HEBREW.fromYMD(5776, 13, 29)));

        assertFalse(lastOfYearH.matches(HCalendar.HEBREW.fromYMD(5777, 7, 1).subtractDays(2)));
        assertFalse(erevRHNisanH.matches(HCalendar.HEBREW.fromYMD(5777, 12, 2)));
        assertFalse(erevRHNisanH.matches(HCalendar.HEBREW.fromYMD(5776, 13, 20)));

        for (HDate d = HCalendar.HEBREW.fromYMD(5777, 7, 1); d.getYear() < 5790; d = d.addDays(1))
            assertTrue(anyDayH.matches(d));
    }

    @Test
    public void getName()
            throws Exception
    {
        assertEquals("Pesach", pesach.getName());
    }

    @Test
    public void getCalendar()
            throws Exception
    {
        assertEquals(HCalendar.HEBREW, pesach.getCalendar());
    }
}