package net.hebrewcalendar.impl;

import net.hebrewcalendar.ICalendar;
import net.hebrewcalendar.impl.holiday.NthDayOfWeekInMonthSpecialDay;
import org.junit.Test;

import static org.junit.Assert.*;

public class NthDayOfWeekInMonthSpecialDayTest
{
    private static final NthDayOfWeekInMonthSpecialDay thanksgiving =
            new NthDayOfWeekInMonthSpecialDay(ICalendar.GREGORIAN, "Thanksgiving", 11, 5, 4);
    private static final NthDayOfWeekInMonthSpecialDay memorialDay =
            new NthDayOfWeekInMonthSpecialDay(ICalendar.GREGORIAN, "Memorial Day", 5, 2, -1);
    private static final NthDayOfWeekInMonthSpecialDay laborDay =
            new NthDayOfWeekInMonthSpecialDay(ICalendar.GREGORIAN, "Labor Day", 9, 2, 1);
    private static final NthDayOfWeekInMonthSpecialDay firstShabbos =
            new NthDayOfWeekInMonthSpecialDay(ICalendar.JEWISH, "First Shabbos of month", 0, 7, 1);

    @Test
    public void getDayOfWeek()
            throws Exception
    {
        assertEquals(5, thanksgiving.getDayOfWeek());
        assertEquals(2, memorialDay.getDayOfWeek());
        assertEquals(2, laborDay.getDayOfWeek());
    }

    @Test
    public void getMonth()
            throws Exception
    {
        assertEquals(11, thanksgiving.getMonth());
        assertEquals(5, memorialDay.getMonth());
        assertEquals(9, laborDay.getMonth());

    }

    @Test
    public void getN()
            throws Exception
    {
        assertEquals(4, thanksgiving.getN());
        assertEquals(-1, memorialDay.getN());
        assertEquals(1, laborDay.getN());
    }

    @Test
    public void getNextOccurrence()
            throws Exception
    {
        assertEquals(ICalendar.GREGORIAN.fromYMD(2016, 11,24),
                thanksgiving.getNextOccurrence(ICalendar.GREGORIAN.fromYMD(2016, 8,24), false));
        assertEquals(ICalendar.GREGORIAN.fromYMD(2016, 11,24),
                thanksgiving.getNextOccurrence(ICalendar.GREGORIAN.fromYMD(2016, 11,24), false));
        assertEquals(ICalendar.GREGORIAN.fromYMD(2017, 11,23),
                thanksgiving.getNextOccurrence(ICalendar.GREGORIAN.fromYMD(2016, 11,25), false));
        assertEquals(ICalendar.GREGORIAN.fromYMD(2017, 5,29),
                memorialDay.getNextOccurrence(ICalendar.GREGORIAN.fromYMD(2016, 11,25), false));
        assertEquals(ICalendar.GREGORIAN.fromYMD(2016, 5,30),
                memorialDay.getNextOccurrence(ICalendar.GREGORIAN.fromYMD(2016, 1,2), false));
        assertEquals(ICalendar.GREGORIAN.fromYMD(2016, 9,5),
                laborDay.getNextOccurrence(ICalendar.GREGORIAN.fromYMD(2016, 1,25), false));
        assertEquals(ICalendar.GREGORIAN.fromYMD(2017, 9,4),
                laborDay.getNextOccurrence(ICalendar.GREGORIAN.fromYMD(2016, 12,2), false));
        assertEquals(ICalendar.GREGORIAN.fromYMD(2017, 11,23),
                thanksgiving.getNextOccurrence(ICalendar.GREGORIAN.fromYMD(2016, 12,2), false));
        assertEquals(ICalendar.GREGORIAN.convert(ICalendar.JEWISH.fromYMD(5777 ,1,5)),
                firstShabbos.getNextOccurrence(ICalendar.GREGORIAN.fromYMD(2017, 3,22), false));
    }

    @Test
    public void matches()
            throws Exception
    {
        assertTrue(thanksgiving.matches(ICalendar.GREGORIAN.fromYMD(2016, 11,24)));
        assertTrue(thanksgiving.matches(ICalendar.GREGORIAN.fromYMD(2016, 11,24)));
        assertTrue(memorialDay.matches(ICalendar.GREGORIAN.fromYMD(2017, 5,29)));
        assertTrue(laborDay.matches(ICalendar.GREGORIAN.fromYMD(2017, 9,4)));

    }

    @Test
    public void getName()
            throws Exception
    {
    }

    @Test
    public void getCalendar()
            throws Exception
    {
    }

    @Test
    public void matches1()
            throws Exception
    {
    }

}