package org.hebrewcalendar.impl;

import org.hebrewcalendar.HCalendarType;
import org.hebrewcalendar.HDate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JulianCalendarTest
{
    private static final AbstractCalendar cal = JulianCalendar.INSTANCE;
    
    @Test
    void addDays()
    {
        HDate d0 = cal.fromYMD(2000,1,1);
        assertEquals(new HDateImpl(cal, 2000, 12, 31), d0.addDays(365));
        assertEquals(new HDateImpl(cal, 2001, 1, 1), d0.addDays(366));
    }

    @Test
    void subtractDays()
    {
        HDateImpl d0 = new HDateImpl(cal, 2000,1,2);
        HDateImpl d1 = new HDateImpl(cal, 2001,1,1);
        assertEquals(d0, d1.subtractDays(365));
    }

    @Test
    void getAbsDay()
    {
        HDateImpl d0 = new HDateImpl(cal, 1,1,1);
        assertEquals(cal.getStart()+1, d0.getAbsDay());

        HDateImpl d20170101 = new HDateImpl(cal, 2017,1,1);
        assertEquals(cal.getStart()+2016*365+504+1, d20170101.getAbsDay());

        HDateImpl d20170701 = new HDateImpl(cal, 2017,7,1);
        assertEquals(cal.getStart()+2016*365+504+31+28+31+30+31+30+1, d20170701.getAbsDay());

        HDateImpl greg = new HDateImpl(GregorianCalendar.INSTANCE, 2017,1,1);
        assertEquals(13, d20170101.getAbsDay()-greg.getAbsDay());
    }

    @Test
    void gregorianVsJulian()
    {
        HDateImpl j0 = new HDateImpl(JulianCalendar.INSTANCE, 1,1,3);
        HDateImpl g0 = new HDateImpl(GregorianCalendar.INSTANCE, 1,1,1);
        assertEquals(j0.getAbsDay(), g0.getAbsDay());

        HDateImpl j1 = new HDateImpl(JulianCalendar.INSTANCE, 2,1,3);
        HDateImpl g1 = new HDateImpl(GregorianCalendar.INSTANCE, 2,1,1);
        assertEquals(j1.getAbsDay(), g1.getAbsDay());

        HDateImpl j101 = new HDateImpl(JulianCalendar.INSTANCE, 101,1,2);
        HDateImpl g101 = new HDateImpl(GregorianCalendar.INSTANCE, 101,1,1);
        assertEquals(j101.getAbsDay(), g101.getAbsDay());

        HDateImpl j101a = new HDateImpl(JulianCalendar.INSTANCE, 100,2,29);
        HDateImpl g101a = new HDateImpl(GregorianCalendar.INSTANCE, 100,2,27);
        assertEquals(j101a.getAbsDay(), g101a.getAbsDay());

        HDateImpl j101b = new HDateImpl(JulianCalendar.INSTANCE, 100,3,1);
        HDateImpl g101b = new HDateImpl(GregorianCalendar.INSTANCE, 100,2,28);
        assertEquals(j101b.getAbsDay(), g101b.getAbsDay());

        HDateImpl j101c = new HDateImpl(JulianCalendar.INSTANCE, 100,3,2);
        HDateImpl g101c = new HDateImpl(GregorianCalendar.INSTANCE, 100,3,1);
        assertEquals(j101c.getAbsDay(), g101c.getAbsDay());

        HDateImpl j1900a = new HDateImpl(JulianCalendar.INSTANCE, 1900,2,17);
        HDateImpl g1900a = new HDateImpl(GregorianCalendar.INSTANCE, 1900,3,1);
        assertEquals(j1900a.getAbsDay(), g1900a.getAbsDay());

        HDateImpl j1900b = new HDateImpl(JulianCalendar.INSTANCE, 1900,2,29);
        HDateImpl g1900b = new HDateImpl(GregorianCalendar.INSTANCE, 1900,3,13);
        assertEquals(j1900b.getAbsDay(), g1900b.getAbsDay());

        HDateImpl j1900c = new HDateImpl(JulianCalendar.INSTANCE, 1900,3,1);
        HDateImpl g1900c = new HDateImpl(GregorianCalendar.INSTANCE, 1900,3,14);
        assertEquals(j1900c.getAbsDay(), g1900c.getAbsDay());

        HDateImpl j20170709 = new HDateImpl(JulianCalendar.INSTANCE, 2017,7, 9);
        HDateImpl g20170722 = new HDateImpl(GregorianCalendar.INSTANCE, 2017,7,22);
        assertEquals(j20170709.getAbsDay(), g20170722.getAbsDay());


    }
    @Test
    void getCalendarType()
    {
        HDateImpl d0 = new HDateImpl(cal, 2017,7,31);

        assertEquals(HCalendarType.JULIAN, d0.getCalendarType());
    }

    @Test
    void isValid()
    {
        assertTrue(new HDateImpl(cal,1997,12, 31).isValid());
        try {
            new HDateImpl(cal,1997,12, 32);
            fail("Illegal date created, no exception thrown");
        } catch(IllegalStateException ise) {}

        try {
            new HDateImpl(cal,1997,0, 3);
            fail("Illegal date created, no exception thrown");
        } catch(IllegalStateException ise) {}

        assertTrue(new HDateImpl(cal,1996,2, 29).isValid());
        assertTrue(new HDateImpl(cal,2000,2, 29).isValid());
        assertTrue(new HDateImpl(cal,1900,2, 29).isValid());

        try {
            new HDateImpl(cal,2000,2, 30);
            fail("Illegal date 2000-02-30 created, no exception thrown");
        } catch(IllegalStateException ise) {}
    }

    @Test
    void getDayOfWeek()
    {
        HDateImpl d0 = new HDateImpl(cal, 1,1,3);
        assertEquals(2, d0.getDayOfWeek());

        HDateImpl d1 = new HDateImpl(cal, 2,1,3);
        assertEquals(3, d1.getDayOfWeek());

        HDateImpl d2 = new HDateImpl(cal, 2017,7,9);
        assertEquals(7, d2.getDayOfWeek());

    }

    @Test
    void testIsLeap()
    {
        assertTrue(cal.isLeap(100));
        assertFalse(cal.isLeap(1011));
        assertTrue(cal.isLeap(2000));
        assertTrue(cal.isLeap(2004));
        assertTrue(cal.isLeap(1900));
        assertFalse(cal.isLeap(1));
    }

    @Test
    void testMonthLength()
    {
        assertEquals(31, cal.monthLength(2010, 7));
        assertEquals(31, cal.monthLength(2009, 12));
        assertEquals(31, cal.monthLength(2010, 1));
        assertEquals(31, cal.monthLength(2010, 8));
        assertEquals(30, cal.monthLength(2010, 9));
        assertEquals(30, cal.monthLength(2010, 11));
        assertEquals(28, cal.monthLength(2010, 2));
        assertEquals(28, cal.monthLength(2011, 2));
        assertEquals(29, cal.monthLength(1900, 2));
        assertEquals(29, cal.monthLength(2000, 2));
        assertEquals(29, cal.monthLength(2004, 2));
    }

    @Test
    void stringRepresentation()
    {
        assertEquals("0001-01-01J", cal.fromYMD(1,1,1).toString());
        assertEquals("2017-07-09J", cal.fromYMD(2017,7,9).toString());
        assertEquals("1812-11-12J", cal.fromYMD(1812,11,12).toString());
    }

}