package net.hebrewcalendar.impl;

import net.hebrewcalendar.CalendarType;
import net.hebrewcalendar.IDate;

import org.junit.Test;
import static org.junit.Assert.*;

public class JulianCalendarTest
{
    private static final AbstractCalendar cal = JulianCalendar.INSTANCE;
    
    @Test
    public void addDays()
    {
        IDate d0 = cal.fromYMD(2000,1,1);
        assertEquals(new IDateImpl(cal, 2000, 12, 31), d0.addDays(365));
        assertEquals(new IDateImpl(cal, 2001, 1, 1), d0.addDays(366));
    }

    @Test
    public void subtractDays()
    {
        IDateImpl d0 = new IDateImpl(cal, 2000,1,2);
        IDateImpl d1 = new IDateImpl(cal, 2001,1,1);
        assertEquals(d0, d1.subtractDays(365));
    }

    @Test
    public void getAbsDay()
    {
        IDateImpl d0 = new IDateImpl(cal, 1,1,1);
        assertEquals(cal.getStart()+1, d0.absDay());

        IDateImpl d20170101 = new IDateImpl(cal, 2017,1,1);
        assertEquals(cal.getStart()+2016*365+504+1, d20170101.absDay());

        IDateImpl d20170701 = new IDateImpl(cal, 2017,7,1);
        assertEquals(cal.getStart()+2016*365+504+31+28+31+30+31+30+1, d20170701.absDay());

        IDateImpl greg = new IDateImpl(GregorianCalendar.INSTANCE, 2017,1,1);
        assertEquals(13, d20170101.absDay()-greg.absDay());
    }

    @Test
    public void gregorianVsJulian()
    {
        IDateImpl j0 = new IDateImpl(JulianCalendar.INSTANCE, 1,1,3);
        IDateImpl g0 = new IDateImpl(GregorianCalendar.INSTANCE, 1,1,1);
        assertEquals(j0.absDay(), g0.absDay());

        IDateImpl j1 = new IDateImpl(JulianCalendar.INSTANCE, 2,1,3);
        IDateImpl g1 = new IDateImpl(GregorianCalendar.INSTANCE, 2,1,1);
        assertEquals(j1.absDay(), g1.absDay());

        IDateImpl j101 = new IDateImpl(JulianCalendar.INSTANCE, 101,1,2);
        IDateImpl g101 = new IDateImpl(GregorianCalendar.INSTANCE, 101,1,1);
        assertEquals(j101.absDay(), g101.absDay());

        IDateImpl j101a = new IDateImpl(JulianCalendar.INSTANCE, 100,2,29);
        IDateImpl g101a = new IDateImpl(GregorianCalendar.INSTANCE, 100,2,27);
        assertEquals(j101a.absDay(), g101a.absDay());

        IDateImpl j101b = new IDateImpl(JulianCalendar.INSTANCE, 100,3,1);
        IDateImpl g101b = new IDateImpl(GregorianCalendar.INSTANCE, 100,2,28);
        assertEquals(j101b.absDay(), g101b.absDay());

        IDateImpl j101c = new IDateImpl(JulianCalendar.INSTANCE, 100,3,2);
        IDateImpl g101c = new IDateImpl(GregorianCalendar.INSTANCE, 100,3,1);
        assertEquals(j101c.absDay(), g101c.absDay());

        IDateImpl j1900a = new IDateImpl(JulianCalendar.INSTANCE, 1900,2,17);
        IDateImpl g1900a = new IDateImpl(GregorianCalendar.INSTANCE, 1900,3,1);
        assertEquals(j1900a.absDay(), g1900a.absDay());

        IDateImpl j1900b = new IDateImpl(JulianCalendar.INSTANCE, 1900,2,29);
        IDateImpl g1900b = new IDateImpl(GregorianCalendar.INSTANCE, 1900,3,13);
        assertEquals(j1900b.absDay(), g1900b.absDay());

        IDateImpl j1900c = new IDateImpl(JulianCalendar.INSTANCE, 1900,3,1);
        IDateImpl g1900c = new IDateImpl(GregorianCalendar.INSTANCE, 1900,3,14);
        assertEquals(j1900c.absDay(), g1900c.absDay());

        IDateImpl j20170709 = new IDateImpl(JulianCalendar.INSTANCE, 2017,7, 9);
        IDateImpl g20170722 = new IDateImpl(GregorianCalendar.INSTANCE, 2017,7,22);
        assertEquals(j20170709.absDay(), g20170722.absDay());


    }
    @Test
    public void getCalendarType()
    {
        IDateImpl d0 = new IDateImpl(cal, 2017,7,31);

        assertEquals(CalendarType.JULIAN, d0.getCalendarType());
    }

    @Test
    public void isValid()
    {
        assertTrue(new IDateImpl(cal,1997,12, 31).isValid());
        try {
            new IDateImpl(cal,1997,12, 32);
            fail("Illegal date created, no exception thrown");
        } catch(IllegalStateException ise) {}

        try {
            new IDateImpl(cal,1997,0, 3);
            fail("Illegal date created, no exception thrown");
        } catch(IllegalStateException ise) {}

        assertTrue(new IDateImpl(cal,1996,2, 29).isValid());
        assertTrue(new IDateImpl(cal,2000,2, 29).isValid());
        assertTrue(new IDateImpl(cal,1900,2, 29).isValid());

        try {
            new IDateImpl(cal,2000,2, 30);
            fail("Illegal date 2000-02-30 created, no exception thrown");
        } catch(IllegalStateException ise) {}
    }

    @Test
    public void getDayOfWeek()
    {
        IDateImpl d0 = new IDateImpl(cal, 1,1,3);
        assertEquals(2, d0.getDayOfWeek());

        IDateImpl d1 = new IDateImpl(cal, 2,1,3);
        assertEquals(3, d1.getDayOfWeek());

        IDateImpl d2 = new IDateImpl(cal, 2017,7,9);
        assertEquals(7, d2.getDayOfWeek());

    }

    @Test
    public void testIsLeap()
    {
        assertTrue(cal.isLeap(100));
        assertFalse(cal.isLeap(1011));
        assertTrue(cal.isLeap(2000));
        assertTrue(cal.isLeap(2004));
        assertTrue(cal.isLeap(1900));
        assertFalse(cal.isLeap(1));
    }

    @Test
    public void testMonthLength()
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
    public void stringRepresentation()
    {
        assertEquals("0001-01-01J", cal.fromYMD(1,1,1).toString());
        assertEquals("2017-07-09J", cal.fromYMD(2017,7,9).toString());
        assertEquals("1812-11-12J", cal.fromYMD(1812,11,12).toString());
    }

}