package net.hebrewcalendar.impl;

import net.hebrewcalendar.ICalendar;
import net.hebrewcalendar.IDate;
import net.hebrewcalendar.SpecialDay;
import net.hebrewcalendar.impl.holiday.MonthDaySpecialDay;
import net.hebrewcalendar.impl.holiday.NthDayOfWeekFromPivot;
import org.junit.Test;

import static org.junit.Assert.*;

public class NthDayOfWeekFromPivotTest
{
    private static final SpecialDay pesach = new MonthDaySpecialDay(ICalendar.JEWISH, "Pesach", 1, 15);
    private static final SpecialDay shabbatHagadol = new NthDayOfWeekFromPivot(ICalendar.JEWISH,
            "Shabbos Hagodol", pesach, 7, -1, false);


    private static final SpecialDay ninthAv = new MonthDaySpecialDay(ICalendar.JEWISH,
            "9 Av", 5, 9);
    private static final SpecialDay shabbatChazon = new NthDayOfWeekFromPivot(ICalendar.JEWISH,
            "Shabbos Chazon", ninthAv, 7, -1, false);
    private static final SpecialDay shabbatNachamu = new NthDayOfWeekFromPivot(ICalendar.JEWISH,
            "Shabbos Nachamu", ninthAv, 7, 1, false);


    @Test
    public void getNextOccurrenceOnOrAfter()
            throws Exception
    {
        IDate d0 = ICalendar.JEWISH.fromYMD(5777, 1, 1);
        IDate d1 = ICalendar.JEWISH.fromYMD(5777, 1, 12);
        IDate d2 = ICalendar.JEWISH.fromYMD(5778, 1, 8);
        assertEquals(d1, shabbatHagadol.getNextOccurrence(d0, false));
        assertEquals(d1, shabbatHagadol.getNextOccurrence(d0, true));
        assertEquals(d1, shabbatHagadol.getNextOccurrence(d1, false));
        assertEquals(d2, shabbatHagadol.getNextOccurrence(d1.addDays(1), false));

        IDate d3 = ICalendar.JEWISH.fromYMD(5777, 5, 6);
        IDate d4 = ICalendar.JEWISH.fromYMD(5777, 5, 13);
        assertEquals(d3, shabbatChazon.getNextOccurrence(d0, false));
        assertEquals(d4, shabbatNachamu.getNextOccurrence(d0, false));
        assertEquals(d4, shabbatNachamu.getNextOccurrence(d3, false));
        assertEquals(d4, shabbatNachamu.getNextOccurrence(d4, false));
    }

    @Test
    public void matches()
            throws Exception
    {
        IDate d1 = ICalendar.JEWISH.fromYMD(5777, 1, 12);
        IDate d2 = ICalendar.JEWISH.fromYMD(5778, 1, 8);
        assertTrue(shabbatHagadol.matches(d1));
        assertTrue(shabbatHagadol.matches(d2));

        IDate d3 = ICalendar.JEWISH.fromYMD(5777, 5, 6);
        IDate d4 = ICalendar.JEWISH.fromYMD(5777, 5, 13);
        assertTrue(shabbatChazon.matches(d3));
        assertTrue(shabbatNachamu.matches(d4));

        IDate d0 = ICalendar.JEWISH.fromYMD(5777, 1, 1);
        assertFalse(shabbatNachamu.matches(d0));
        assertFalse(shabbatChazon.matches(d0));
        assertFalse(shabbatHagadol.matches(d0));
    }

    @Test
    public void getName()
            throws Exception
    {
        assertEquals("Shabbos Chazon", shabbatChazon.getName());
    }

    @Test
    public void getCalendar()
            throws Exception
    {
        assertEquals(ICalendar.JEWISH, shabbatChazon.getCalendar());
    }
}