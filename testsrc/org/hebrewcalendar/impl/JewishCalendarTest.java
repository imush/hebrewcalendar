package org.hebrewcalendar.impl;

import org.hebrewcalendar.HCalendarType;
import org.hebrewcalendar.HJewishCalendar;

import org.junit.Test;
import static org.junit.Assert.*;

public class JewishCalendarTest
{
    private static final HTime FIRST_MOLAD = new HTime(2, 5, 204);
    private static final HTime ONE_MONTH = new HTime(29, 12, 793);

    private static final JewishCalendar hc = JewishCalendar.INSTANCE;

    @Test
    public void testIsLeap()
    {
        assertFalse(JewishCalendar.INSTANCE.isLeap(5777));
        assertFalse(JewishCalendar.INSTANCE.isLeap(5778));
        assertTrue(JewishCalendar.INSTANCE.isLeap(5779));
        assertTrue(JewishCalendar.INSTANCE.isLeap(19));
    }

    @Test
    public void testMolad()
    {
        assertEquals(FIRST_MOLAD, hc.molad(1,7));
        assertEquals(FIRST_MOLAD.add(ONE_MONTH.times(12)), hc.molad(2,7));
        assertEquals(FIRST_MOLAD.add(ONE_MONTH.times(24)), hc.molad(3,7));
        assertEquals(FIRST_MOLAD.add(ONE_MONTH.times(37)), hc.molad(4,7));
        assertEquals(FIRST_MOLAD.add(ONE_MONTH.times(49)), hc.molad(5,7));
        assertEquals(FIRST_MOLAD.add(ONE_MONTH.times(61)), hc.molad(6,7));
        assertEquals(FIRST_MOLAD.add(ONE_MONTH.times(74)), hc.molad(7,7));
        assertEquals(FIRST_MOLAD.add(ONE_MONTH.times(86)), hc.molad(8,7));
        assertEquals(FIRST_MOLAD.add(ONE_MONTH.times(99)), hc.molad(9,7));

        HTime moladElul5777 = hc.molad(5777,6);
        HTime moladTishrei5778 = hc.molad(5778,7);

        HTime moladNisan5778 = hc.molad(5778,1);

        HTime moladNisan5779 = hc.molad(5779,1);
        HTime moladElul5779 = hc.molad(5779,6);
        HTime moladTishrei5780 = hc.molad(5780,7);

        HTime moladNisan5781 = hc.molad(5781,1);

        assertEquals(new HTime(2109992, 16, 807), moladElul5777);
        assertEquals(new HTime(2110022, 5, 520), moladTishrei5778);
        assertEquals(ONE_MONTH, moladTishrei5778.subtract(moladElul5777));

        assertEquals(ONE_MONTH, moladTishrei5780.subtract(moladElul5779));

        assertEquals(ONE_MONTH.times(5), moladElul5779.subtract(moladNisan5779));
        assertEquals(ONE_MONTH.times(5), moladElul5779.subtract(moladNisan5779));
        assertEquals(ONE_MONTH.times(13), moladNisan5779.subtract(moladNisan5778));

        assertEquals(ONE_MONTH.times(37), moladNisan5781.subtract(moladNisan5778));

    }

    @Test
    public void testYearType()
    {
        assertEquals(HJewishCalendar.YearType.SHORT, hc.getYearType(5777));
        assertEquals(HJewishCalendar.YearType.NORMAL, hc.getYearType(5778));
        assertEquals(HJewishCalendar.YearType.FULL, hc.getYearType(5779));
        assertEquals(HJewishCalendar.YearType.FULL, hc.getYearType(5780));
        assertEquals(HJewishCalendar.YearType.SHORT, hc.getYearType(5781));
    }

    @Test
    public void testAddSubtractDays()
    {
        HDateImpl d1 = new HDateImpl(hc, 5777,9,1);
        assertEquals(new HDateImpl(hc, 5777,8,29), d1.subtractDays(1));
    }

    @Test
    public void testDayOfWeek()
    {
        HDateImpl d20161003 = new HDateImpl(hc, 5777,7,1);

        assertEquals(2, d20161003.getDayOfWeek());

        HDateImpl d0 = new HDateImpl(hc, 5777,8,1);
        assertEquals(4, d0.getDayOfWeek());

        HDateImpl d1 = new HDateImpl(hc, 5777,9,1);
        assertEquals(5, d1.getDayOfWeek());

    }

    @Test
    public void testRoshHashana()
    {
        assertEquals(2, hc.absDayRoshHashana(1));
        assertEquals(GregorianCalendar.INSTANCE.fromYMD(2016,10,3).getAbsDay(), hc.absDayRoshHashana(5777));

    }

    @Test
    public void getCalendarType()
    {
        assertEquals(HCalendarType.HEBREW, JewishCalendar.INSTANCE.getType());
    }

    @Test
    public void stringRepresentation()
    {
        assertEquals("0001-01-01H", hc.fromYMD(1, JewishCalendar.NISAN,1).toString());
        assertEquals("5777-07-09H", hc.fromYMD(5777, JewishCalendar.TISHREI,9).toString());
        assertEquals("5711-11-10H", hc.fromYMD(5711, JewishCalendar.SHVAT,10).toString());
    }

}