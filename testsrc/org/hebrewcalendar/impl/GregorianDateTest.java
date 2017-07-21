package org.hebrewcalendar.impl;

import org.hebrewcalendar.HCalendarType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GregorianDateTest
{
    @Test
    void addDays()
    {
        GregorianDate d0 = new GregorianDate(2000,1,1);
        GregorianDate d1 = new GregorianDate(1900,1,1);
        GregorianDate d2 = new GregorianDate(1996,2,22);
        assertEquals(new GregorianDate(2001, 1, 2), d0.addDays(367));
        assertEquals(new GregorianDate(2000, 12, 30), d0.addDays(364));
        assertEquals(new GregorianDate(1900, 12, 31), d1.addDays(364));
        assertEquals(new GregorianDate(1997, 2, 21), d2.addDays(365));
    }

    @Test
    void subtractDays()
    {
        GregorianDate d0 = new GregorianDate(1900,2,4);
        GregorianDate d1 = new GregorianDate(1901,2,4);
        assertEquals(d0, d1.subtractDays(365));
    }

    @Test
    void getAbsDay()
    {
        GregorianDate d0 = new GregorianDate(1,1,1);
        assertEquals(CommonDate.COMMON_START+1, d0.getAbsDay());
    }

    @Test
    void getCalendarType()
    {
        JulianDate d0 = new JulianDate(2017,7,31);

        assertEquals(HCalendarType.JULIAN, d0.getCalendarType());
    }

    @Test
    void isValid()
    {
        assertTrue(new GregorianDate(1997,12, 31).isValid());
        assertFalse(new GregorianDate(1997,12, 32).isValid());
        assertFalse(new GregorianDate(1997,0, 31).isValid());
        assertTrue(new GregorianDate(1996,2, 29).isValid());
        assertTrue(new GregorianDate(2000,2, 29).isValid());
        assertFalse(new GregorianDate(1900,2, 29).isValid());
        assertFalse(new GregorianDate(2000,2, 30).isValid());
    }

    @Test
    void getDayOfWeek()
    {

        GregorianDate d0 = new GregorianDate(2017,7,21);
        assertEquals(6, d0.getDayOfWeek());

        GregorianDate d1 = new GregorianDate(2017,7,4);
        assertEquals(3, d1.getDayOfWeek());

        GregorianDate d2 = new GregorianDate(2011,9,9);
        assertEquals(5, d2.getDayOfWeek());

        GregorianDate d3 = new GregorianDate(2011,9,8);
        assertEquals(7, d2.getDayOfWeek());
    }

}