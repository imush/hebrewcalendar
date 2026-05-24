package net.hebrewcalendar.impl;

import net.hebrewcalendar.IDate;
import net.hebrewcalendar.ICalendar;
import net.hebrewcalendar.impl.holiday.MonthDaySpecialDay;
import org.junit.Test;

import static org.junit.Assert.*;

public class MonthDaySpecialDayTest
{
    private static final MonthDaySpecialDay jan1G = new MonthDaySpecialDay(ICalendar.GREGORIAN, "January 1", 1, 1);
    private static final MonthDaySpecialDay jan31G = new MonthDaySpecialDay(ICalendar.GREGORIAN, "January 31",1, -1);
    private static final MonthDaySpecialDay firstOfAnyG = new MonthDaySpecialDay(ICalendar.GREGORIAN, "First of any month", 0, 1);
    private static final MonthDaySpecialDay lastOfYearG = new MonthDaySpecialDay(ICalendar.GREGORIAN, "Last day of year", -1, -1);

    private static final MonthDaySpecialDay pesach = new MonthDaySpecialDay(ICalendar.JEWISH, "Pesach", 1, 15);
    private static final MonthDaySpecialDay lastOfYearH = new MonthDaySpecialDay(ICalendar.JEWISH, "Last day of year", 6, -1);
    private static final MonthDaySpecialDay erevRHNisanH = new MonthDaySpecialDay(ICalendar.JEWISH, "Last day of Adar", -1, -1);

    private static final MonthDaySpecialDay anyDayH = new MonthDaySpecialDay(ICalendar.JEWISH, "Anything goes", 0, 0);

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
        assertEquals(ICalendar.GREGORIAN.fromYMD(2017,1,1),
                jan1G.getNextOccurrence(ICalendar.GREGORIAN.fromYMD(2017,1,1), false));
        assertEquals(ICalendar.GREGORIAN.fromYMD(2019,1,1),
                jan1G.getNextOccurrence(ICalendar.GREGORIAN.fromYMD(2018,1,1), true));
        assertEquals(ICalendar.GREGORIAN.fromYMD(2017,1,1),
                jan1G.getNextOccurrence(ICalendar.GREGORIAN.fromYMD(2016,1,2), false));
        assertEquals(ICalendar.GREGORIAN.fromYMD(2017,1,1),
                jan1G.getNextOccurrence(ICalendar.GREGORIAN.fromYMD(2016,11,2), false));

        assertEquals(ICalendar.JEWISH.fromYMD(5777,1,15),
                pesach.getNextOccurrence(ICalendar.JEWISH.fromYMD(5777,1,15), false));
        assertEquals(ICalendar.JEWISH.fromYMD(5777,1,15),
                pesach.getNextOccurrence(ICalendar.JEWISH.fromYMD(5777,12,15), false));
        assertEquals(ICalendar.JEWISH.fromYMD(5777,1,15),
                pesach.getNextOccurrence(ICalendar.JEWISH.fromYMD(5777,7,15), false));
        assertEquals(ICalendar.JEWISH.fromYMD(5777,1,15),
                pesach.getNextOccurrence(ICalendar.JEWISH.fromYMD(5777,7,15), true));
        assertEquals(ICalendar.JEWISH.fromYMD(5777,1,15),
                pesach.getNextOccurrence(ICalendar.JEWISH.fromYMD(5776,1,16), false));

        for (IDate d = ICalendar.JEWISH.fromYMD(5777, 7, 1); d.getYear() < 5790; d = d.addDays(1)) {
            assertEquals(d, anyDayH.getNextOccurrence(d, false));
            assertEquals(d.addDays(1), anyDayH.getNextOccurrence(d, true));
        }

    }

    @Test
    public void getPrevOccurrence()
            throws Exception
    {
        assertEquals(ICalendar.GREGORIAN.fromYMD(2017,1,1),
                jan1G.getPrevOccurrence(ICalendar.GREGORIAN.fromYMD(2017,1,1), false));
        assertEquals(ICalendar.GREGORIAN.fromYMD(2015,1,1),
                jan1G.getPrevOccurrence(ICalendar.GREGORIAN.fromYMD(2016,1,1), true));
        assertEquals(ICalendar.GREGORIAN.fromYMD(2016,1,1),
                jan1G.getPrevOccurrence(ICalendar.GREGORIAN.fromYMD(2016,1,2), false));
        assertEquals(ICalendar.GREGORIAN.fromYMD(2017,1,1),
                jan1G.getPrevOccurrence(ICalendar.GREGORIAN.fromYMD(2017,1,26), true));
        assertEquals(ICalendar.GREGORIAN.fromYMD(2016,1,1),
                jan1G.getPrevOccurrence(ICalendar.GREGORIAN.fromYMD(2016,11,2), false));

        assertEquals(ICalendar.JEWISH.fromYMD(5777,1,15),
                pesach.getPrevOccurrence(ICalendar.JEWISH.fromYMD(5777,1,15), false));
        assertEquals(ICalendar.JEWISH.fromYMD(5775,1,15),
                pesach.getPrevOccurrence(ICalendar.JEWISH.fromYMD(5776,1,15), true));
        assertEquals(ICalendar.JEWISH.fromYMD(5776,1,15),
                pesach.getPrevOccurrence(ICalendar.JEWISH.fromYMD(5777,12,15), false));

        for (IDate d = ICalendar.JEWISH.fromYMD(5777, 7, 1); d.getYear() < 5790; d = d.addDays(1)) {
            assertEquals(d, anyDayH.getNextOccurrence(d, false));
            assertEquals(d.addDays(1), anyDayH.getNextOccurrence(d, true));
        }

    }
    @Test
    public void matches()
            throws Exception
    {
        assertTrue(jan1G.matches(ICalendar.GREGORIAN.fromYMD(2000, 1, 1)));
        assertFalse(jan1G.matches(ICalendar.GREGORIAN.fromYMD(2000, 1, 9)));
        assertTrue(jan31G.matches(ICalendar.GREGORIAN.fromYMD(2000, 1, 31)));
        assertFalse(jan31G.matches(ICalendar.GREGORIAN.fromYMD(2000, 1, 30)));

        assertTrue(firstOfAnyG.matches(ICalendar.GREGORIAN.fromYMD(2000, 1, 1)));
        assertTrue(firstOfAnyG.matches(ICalendar.GREGORIAN.fromYMD(2000, 8, 1)));
        assertTrue(firstOfAnyG.matches(ICalendar.GREGORIAN.fromYMD(2000, 12, 1)));
        assertFalse(firstOfAnyG.matches(ICalendar.GREGORIAN.fromYMD(2000, 12, 7)));

        assertTrue(lastOfYearG.matches(ICalendar.GREGORIAN.fromYMD(2000, 12, 31)));
        assertFalse(lastOfYearG.matches(ICalendar.GREGORIAN.fromYMD(2008, 12, 30)));

        assertTrue(lastOfYearH.matches(ICalendar.JEWISH.fromYMD(5777, 7, 1).subtractDays(1)));
        assertTrue(erevRHNisanH.matches(ICalendar.JEWISH.fromYMD(5777, 12, 29)));
        assertTrue(erevRHNisanH.matches(ICalendar.JEWISH.fromYMD(5776, 13, 29)));

        assertFalse(lastOfYearH.matches(ICalendar.JEWISH.fromYMD(5777, 7, 1).subtractDays(2)));
        assertFalse(erevRHNisanH.matches(ICalendar.JEWISH.fromYMD(5777, 12, 2)));
        assertFalse(erevRHNisanH.matches(ICalendar.JEWISH.fromYMD(5776, 13, 20)));

        for (IDate d = ICalendar.JEWISH.fromYMD(5777, 7, 1); d.getYear() < 5790; d = d.addDays(1))
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
        assertEquals(ICalendar.JEWISH, pesach.getCalendar());
    }
}