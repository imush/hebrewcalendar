package org.hebrewcalendar.impl;

import org.hebrewcalendar.HCalendarType;
import org.hebrewcalendar.HDate;
import org.junit.Test;

import static org.junit.Assert.*;

public class GregorianCalendarTest
{
    private static final AbstractCalendar cal = GregorianCalendar.INSTANCE;
    @Test
    public void addDays()
    {
        HDate d1 = new HDateImpl(GregorianCalendar.INSTANCE, 2000,1,1);
        HDate d2 = new HDateImpl(GregorianCalendar.INSTANCE, 1900,1,1);
        HDate d3 = new HDateImpl(GregorianCalendar.INSTANCE,1996,2,22);
        assertEquals(new HDateImpl(cal,2001, 1, 2), d1.addDays(367));
        assertEquals(new HDateImpl(cal,2000, 12, 30), d1.addDays(364));
        assertEquals(new HDateImpl(cal,1900, 12, 31), d2.addDays(364));
        assertEquals(new HDateImpl(cal, 1997, 2, 21), d3.addDays(365));
    }

    @Test
    public void subtractDays()
    {
        HDateImpl d0 = new HDateImpl(cal,1900,2,4);
        HDateImpl d1 = new HDateImpl(cal,1901,2,4);
        assertEquals(d0, d1.subtractDays(365));
    }

    @Test
    public void getAbsDay()
    {
        HDateImpl d0 = new HDateImpl(cal,1,1,1);
        assertEquals(1373430, d0.absDay());
        assertEquals(GregorianCalendar.INSTANCE.getStart()+1, d0.absDay());

        HDateImpl d1 = new HDateImpl(cal,2,1,1);
        assertEquals(1373430+365, d1.absDay());

        HDateImpl d1996 = new HDateImpl(cal, 1996,1,11);
        HDateImpl d1997 = new HDateImpl(cal,1997,1,11);
        assertEquals(366, d1997.absDay()-d1996.absDay());

        HDateImpl d1998 = new HDateImpl(cal,1998,1,11);
        assertEquals(365, d1998.absDay()-d1997.absDay());

        HDateImpl d1898 = new HDateImpl(cal,1898,1,11);
        assertEquals(365*100+24, d1998.absDay()-d1898.absDay());


        for (int y = 1; y < 2100; y+=111) {
            for (int m = 1; m <=12; m++) {
                for (int d =1; d <= cal.monthLength(y,m); d++) {
                    HDateImpl nextDate = new HDateImpl(cal, y, m, d);
                    //System.out.println(nextDate);
                    assertEquals(nextDate, cal.fromAbs(nextDate.absDay()));
                    if (nextDate.absDay() > cal.getStart()+1)
                        assertEquals(nextDate.absDay(), nextDate.subtractDays(1).absDay()+1);
                }
            }
        }
        HDateImpl d20161003 = new HDateImpl(cal, 2016,10, 3);
        assertEquals(d20161003, cal.fromAbs(d20161003.absDay()));

    }

    @Test
    public void getCalendarType()
    {
        HDate d0 = new HDateImpl(cal, 2017,7,31);

        assertEquals(HCalendarType.GREGORIAN, d0.getCalendarType());
    }

    @Test
    public void isValid()
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

        try {
            new HDateImpl(cal,1900,2, 29);
            fail("Illegal date 1900-02-29 created, no exception thrown");
        } catch(IllegalStateException ise) {}
        try {
            new HDateImpl(cal,2000,2, 30);
            fail("Illegal date 2000-02-30 created, no exception thrown");
        } catch(IllegalStateException ise) {}
    }

    @Test
    public void absDay()
    {
        HDateImpl d0 = new HDateImpl(cal,1,1,1);
        assertEquals(1373430, d0.absDay());
        HDateImpl d1 = new HDateImpl(cal,2,1,1);
        assertEquals(1373430+365, d1.absDay());

        HDateImpl d1996 = new HDateImpl(cal, 1996,1,11);
        HDateImpl d1997 = new HDateImpl(cal,1997,1,11);
        assertEquals(366, d1997.absDay()-d1996.absDay());

        HDateImpl d1998 = new HDateImpl(cal,1998,1,11);
        assertEquals(365, d1998.absDay()-d1997.absDay());

        HDateImpl d1898 = new HDateImpl(cal,1898,1,11);
        assertEquals(365*100+24, d1998.absDay()-d1898.absDay());
    }


    @Test
    public void getDayOfWeek()
    {
        HDateImpl d0 = new HDateImpl(cal, 1,1,1);
        assertEquals(2, d0.getDayOfWeek());

        HDateImpl d1 = new HDateImpl(cal, 2,1,1);
        assertEquals(3, d1.getDayOfWeek());

        HDateImpl d401 = new HDateImpl(cal, 401,1,1);
        assertEquals((2 + 400 * 365 + 97)%7, d401.getDayOfWeek()%7);

        HDateImpl d2001 = new HDateImpl(cal, 2001,1,1);
        assertEquals((2+(400 * 365 + 97)*5)%7, d2001.getDayOfWeek()%7);

        HDateImpl d20170721 = new HDateImpl(cal,2017,7,21);
        assertEquals(6, d20170721.getDayOfWeek());

        HDateImpl d20170722 = new HDateImpl(cal,2017,7,22);
        assertEquals(7, d20170722.getDayOfWeek());

        HDateImpl d20170723 = new HDateImpl(cal,2017,7,23);
        assertEquals(1, d20170723.getDayOfWeek());

        HDateImpl d20110909 = new HDateImpl(cal,2011,9,9);
        assertEquals(6, d20110909.getDayOfWeek());

        HDateImpl d20010911 = new HDateImpl(cal,2001,9,11);
        assertEquals(3, d20010911.getDayOfWeek());

        HDateImpl d20161003 = new HDateImpl(cal, 2016,10,3);
        assertEquals(2, d20161003.getDayOfWeek());

    }

    @Test
    public void testIsLeap()
    {
        assertFalse(cal.isLeap(100));
        assertFalse(cal.isLeap(1011));
        assertTrue(cal.isLeap(2000));
        assertTrue(cal.isLeap(2004));
        assertFalse(cal.isLeap(1900));
        assertFalse(cal.isLeap(1));
    }

    @Test
    public void testMonthLength()
    {
        assertEquals(31, cal.monthLength(2010, 7));
        assertEquals(31, cal.monthLength(2010, 8));
        assertEquals(30, cal.monthLength(2010, 9));
        assertEquals(28, cal.monthLength(2010, 2));
        assertEquals(28, cal.monthLength(2011, 2));
        assertEquals(28, cal.monthLength(1900, 2));
        assertEquals(29, cal.monthLength(2000, 2));
        assertEquals(29, cal.monthLength(2004, 2));
    }

    @Test
    public void stringRepresentation()
    {
        assertEquals("0001-01-01G", cal.fromYMD(1,1,1).toString());
        assertEquals("2017-07-09G", cal.fromYMD(2017,7,9).toString());
        assertEquals("1812-11-12G", cal.fromYMD(1812,11,12).toString());
    }
}