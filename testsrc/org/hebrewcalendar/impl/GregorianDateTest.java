package org.hebrewcalendar.impl;

import org.hebrewcalendar.HCalendarType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GregorianDateTest
{
    @Test
    void addDays()
    {
        GregorianDate d1 = new GregorianDate(2000,1,1);
        GregorianDate d2 = new GregorianDate(1900,1,1);
        GregorianDate d3 = new GregorianDate(1996,2,22);
        assertEquals(new GregorianDate(2001, 1, 2), d1.addDays(367));
        assertEquals(new GregorianDate(2000, 12, 30), d1.addDays(364));
        assertEquals(new GregorianDate(1900, 12, 31), d2.addDays(364));
        assertEquals(new GregorianDate(1997, 2, 21), d3.addDays(365));
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
    void absDay()
    {
        GregorianDate d0 = new GregorianDate(1,1,1);
        assertEquals(1373430, d0.getAbsDay());
        GregorianDate d1 = new GregorianDate(2,1,1);
        assertEquals(1373430+365, d1.getAbsDay());

        GregorianDate d1996 = new GregorianDate(1996,1,11);
        GregorianDate d1997 = new GregorianDate(1997,1,11);
        assertEquals(366, d1997.getAbsDay()-d1996.getAbsDay());

        GregorianDate d1998 = new GregorianDate(1998,1,11);
        assertEquals(365, d1998.getAbsDay()-d1997.getAbsDay());

        GregorianDate d1898 = new GregorianDate(1898,1,11);
        assertEquals(365*100+24, d1998.getAbsDay()-d1898.getAbsDay());
    }


    @Test
    void getDayOfWeek()
    {
        GregorianDate d0 = new GregorianDate(1,1,1);
        assertEquals(2, d0.getDayOfWeek());

        GregorianDate d20170721 = new GregorianDate(2017,7,21);
        assertEquals(6, d20170721.getDayOfWeek());

        GregorianDate d20110909 = new GregorianDate(2011,9,9);
        assertEquals(6, d20110909.getDayOfWeek());

        GregorianDate d20010911 = new GregorianDate(2001,9,11);
        assertEquals(3, d20010911.getDayOfWeek());
    }
}