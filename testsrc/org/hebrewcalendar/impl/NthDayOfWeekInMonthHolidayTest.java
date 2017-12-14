package org.hebrewcalendar.impl;

import org.hebrewcalendar.HCalendar;
import org.hebrewcalendar.impl.holiday.NthDayOfWeekInMonthHoliday;
import org.junit.Test;

import static org.junit.Assert.*;

public class NthDayOfWeekInMonthHolidayTest
{
    private static final NthDayOfWeekInMonthHoliday thanksgiving =
            new NthDayOfWeekInMonthHoliday(HCalendar.GREGORIAN, "Thanksgiving", 11, 5, 4);
    private static final NthDayOfWeekInMonthHoliday memorialDay =
            new NthDayOfWeekInMonthHoliday(HCalendar.GREGORIAN, "Memorial Day", 5, 2, -1);
    private static final NthDayOfWeekInMonthHoliday laborDay =
            new NthDayOfWeekInMonthHoliday(HCalendar.GREGORIAN, "Labor Day", 9, 2, 1);
    private static final NthDayOfWeekInMonthHoliday firstShabbos =
            new NthDayOfWeekInMonthHoliday(HCalendar.HEBREW, "First Shabbos of month", 0, 7, 1);

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
        assertEquals(-1, thanksgiving.getN());
        assertEquals(-1, memorialDay.getN());
        assertEquals(1, laborDay.getN());
    }

    @Test
    public void getNextOccurrence()
            throws Exception
    {
        assertEquals(HCalendar.GREGORIAN.fromYMD(2016, 11,24),
                thanksgiving.getNextOccurrence(HCalendar.GREGORIAN.fromYMD(2016, 8,24), false));
        assertEquals(HCalendar.GREGORIAN.fromYMD(2016, 11,24),
                thanksgiving.getNextOccurrence(HCalendar.GREGORIAN.fromYMD(2016, 11,24), false));
        assertEquals(HCalendar.GREGORIAN.fromYMD(2017, 11,23),
                thanksgiving.getNextOccurrence(HCalendar.GREGORIAN.fromYMD(2016, 11,25), false));
        assertEquals(HCalendar.GREGORIAN.fromYMD(2017, 5,29),
                memorialDay.getNextOccurrence(HCalendar.GREGORIAN.fromYMD(2016, 11,25), false));
        assertEquals(HCalendar.GREGORIAN.fromYMD(2016, 5,30),
                memorialDay.getNextOccurrence(HCalendar.GREGORIAN.fromYMD(2016, 1,2), false));
        assertEquals(HCalendar.GREGORIAN.fromYMD(2016, 9,5),
                laborDay.getNextOccurrence(HCalendar.GREGORIAN.fromYMD(2016, 1,25), false));
        assertEquals(HCalendar.GREGORIAN.fromYMD(2017, 9,4),
                laborDay.getNextOccurrence(HCalendar.GREGORIAN.fromYMD(2016, 12,2), false));
        assertEquals(HCalendar.GREGORIAN.fromYMD(2017, 11,23),
                thanksgiving.getNextOccurrence(HCalendar.GREGORIAN.fromYMD(2016, 12,2), false));
        assertEquals(HCalendar.GREGORIAN.convert(HCalendar.HEBREW.fromYMD(5777 ,1,5)),
                firstShabbos.getNextOccurrence(HCalendar.GREGORIAN.fromYMD(2017, 3,22), false));
    }

    @Test
    public void matches()
            throws Exception
    {
        assertTrue(thanksgiving.matches(HCalendar.GREGORIAN.fromYMD(2016, 11,24)));
        assertTrue(thanksgiving.matches(HCalendar.GREGORIAN.fromYMD(2016, 11,24)));
        assertTrue(memorialDay.matches(HCalendar.GREGORIAN.fromYMD(2017, 5,29)));
        assertTrue(laborDay.matches(HCalendar.GREGORIAN.fromYMD(2017, 9,4)));

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