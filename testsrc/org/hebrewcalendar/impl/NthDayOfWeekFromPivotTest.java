package org.hebrewcalendar.impl;

import org.hebrewcalendar.HCalendar;
import org.hebrewcalendar.HDate;
import org.hebrewcalendar.HHoliday;
import org.hebrewcalendar.impl.holiday.MonthDayHoliday;
import org.hebrewcalendar.impl.holiday.NthDayOfWeekFromPivot;
import org.junit.Test;

import static org.junit.Assert.*;

public class NthDayOfWeekFromPivotTest
{
    private static final HHoliday pesach = new MonthDayHoliday(HCalendar.HEBREW, "Pesach", 1, 15);
    private static final HHoliday shabbosHagodol = new NthDayOfWeekFromPivot(HCalendar.HEBREW,
            "Shabbos Hagodol", pesach, 7, -1, false);


    private static final HHoliday ninthAv = new MonthDayHoliday(HCalendar.HEBREW,
            "9 Av", 5, 9);
    private static final HHoliday shabbosChazon = new NthDayOfWeekFromPivot(HCalendar.HEBREW,
            "Shabbos Chazon", ninthAv, 7, -1, false);
    private static final HHoliday shabbosNachamu = new NthDayOfWeekFromPivot(HCalendar.HEBREW,
            "Shabbos Nachamu", ninthAv, 7, 1, false);


    @Test
    public void getNextOccurrenceOnOrAfter()
            throws Exception
    {
        HDate d0 = HCalendar.HEBREW.fromYMD(5777, 1, 1);
        HDate d1 = HCalendar.HEBREW.fromYMD(5777, 1, 12);
        HDate d2 = HCalendar.HEBREW.fromYMD(5778, 1, 8);
        assertEquals(d1, shabbosHagodol.getNextOccurrence(d0, false));
        assertEquals(d1, shabbosHagodol.getNextOccurrence(d0, true));
        assertEquals(d1, shabbosHagodol.getNextOccurrence(d1, false));
        assertEquals(d2, shabbosHagodol.getNextOccurrence(d1.addDays(1), false));

        HDate d3 = HCalendar.HEBREW.fromYMD(5777, 5, 6);
        HDate d4 = HCalendar.HEBREW.fromYMD(5777, 5, 13);
        assertEquals(d3, shabbosChazon.getNextOccurrence(d0, false));
        assertEquals(d4, shabbosNachamu.getNextOccurrence(d0, false));
        assertEquals(d4, shabbosNachamu.getNextOccurrence(d3, false));
        assertEquals(d4, shabbosNachamu.getNextOccurrence(d4, false));
    }

    @Test
    public void matches()
            throws Exception
    {
        HDate d1 = HCalendar.HEBREW.fromYMD(5777, 1, 12);
        HDate d2 = HCalendar.HEBREW.fromYMD(5778, 1, 8);
        assertTrue(shabbosHagodol.matches(d1));
        assertTrue(shabbosHagodol.matches(d2));

        HDate d3 = HCalendar.HEBREW.fromYMD(5777, 5, 6);
        HDate d4 = HCalendar.HEBREW.fromYMD(5777, 5, 13);
        assertTrue(shabbosChazon.matches(d3));
        assertTrue(shabbosNachamu.matches(d4));

        HDate d0 = HCalendar.HEBREW.fromYMD(5777, 1, 1);
        assertFalse(shabbosNachamu.matches(d0));
        assertFalse(shabbosChazon.matches(d0));
        assertFalse(shabbosHagodol.matches(d0));
    }

    @Test
    public void getName()
            throws Exception
    {
        assertEquals("Shabbos Chazon", shabbosChazon.getName());
    }

    @Test
    public void getCalendar()
            throws Exception
    {
        assertEquals(HCalendar.HEBREW, shabbosChazon.getCalendar());
    }
}