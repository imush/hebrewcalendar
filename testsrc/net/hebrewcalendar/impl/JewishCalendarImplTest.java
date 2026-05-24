package net.hebrewcalendar.impl;

import net.hebrewcalendar.ICalendar;
import net.hebrewcalendar.CalendarType;
import net.hebrewcalendar.JewishMonth;

import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

public class JewishCalendarImplTest
{
    private static final JewishTime.Moment   FIRST_MOLAD = new JewishTime.Moment(2, 5, 204);
    private static final JewishTime.Duration ONE_MONTH   = new JewishTime.Duration(29, 12, 793);

    private static final JewishCalendarImpl hc = JewishCalendarImpl.INSTANCE;

    @Test
    public void testIsLeap()
    {
        assertFalse(JewishCalendarImpl.INSTANCE.isLeap(5777));
        assertFalse(JewishCalendarImpl.INSTANCE.isLeap(5778));
        assertTrue(JewishCalendarImpl.INSTANCE.isLeap(5779));
        assertTrue(JewishCalendarImpl.INSTANCE.isLeap(19));
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

        JewishTime.Moment moladElul5777    = hc.molad(5777, 6);
        JewishTime.Moment moladTishrei5778 = hc.molad(5778, 7);

        JewishTime.Moment moladNisan5778 = hc.molad(5778, 1);

        JewishTime.Moment moladNisan5779   = hc.molad(5779, 1);
        JewishTime.Moment moladElul5779    = hc.molad(5779, 6);
        JewishTime.Moment moladTishrei5780 = hc.molad(5780, 7);

        JewishTime.Moment moladNisan5781 = hc.molad(5781, 1);

        assertEquals(new JewishTime.Moment(2109992, 16, 807), moladElul5777);
        assertEquals(new JewishTime.Moment(2110022,  5, 520), moladTishrei5778);
        assertEquals(moladTishrei5778, moladElul5777.add(ONE_MONTH));

        assertEquals(moladTishrei5780, moladElul5779.add(ONE_MONTH));

        assertEquals(moladElul5779,  moladNisan5779.add(ONE_MONTH.times(5)));
        assertEquals(moladNisan5779, moladNisan5778.add(ONE_MONTH.times(13)));
        assertEquals(moladNisan5781, moladNisan5778.add(ONE_MONTH.times(37)));
    }

    @Test
    public void testRoshHashanaAbsDay()
    {
        long abs5777 = hc.absDayRoshHashana(5777);
        long abs5778 = hc.absDayRoshHashana(5778);
        assertEquals(2109669, abs5777);
        assertEquals(2110022, abs5778);

        JewishTime.Moment molad5775 = hc.molad(5775, 7);
        JewishTime.Moment molad5776 = hc.molad(5776, 7);

        assertEquals(molad5776, molad5775.add(ONE_MONTH.times(12)));

        DateImpl h57750701 = hc.fromYMD(5775, 7, 1);
        DateImpl h57760701 = hc.fromYMD(5776, 7, 1);

        assertEquals(354L, h57760701.absDay() - h57750701.absDay());
    }

    @Test
    public void testYearType()
    {
        assertEquals(YearCheshvanKislevType.SHORT,  hc.getYearType(5777));
        assertEquals(YearCheshvanKislevType.NORMAL, hc.getYearType(5778));
        assertEquals(YearCheshvanKislevType.FULL,   hc.getYearType(5779));
        assertEquals(YearCheshvanKislevType.FULL,   hc.getYearType(5780));
        assertEquals(YearCheshvanKislevType.SHORT,  hc.getYearType(5781));
    }

    @Test
    public void testAddSubtractDays()
    {
        DateImpl d1 = new DateImpl(hc, 5777, 9, 1);
        assertEquals(new DateImpl(hc, 5777, 8, 29), d1.subtractDays(1));
    }

    @Test
    public void testDayOfWeek()
    {
        DateImpl d20161003 = new DateImpl(hc, 5777, 7, 1);
        assertEquals(2, d20161003.getDayOfWeek());

        DateImpl d0 = new DateImpl(hc, 5777, 8, 1);
        assertEquals(4, d0.getDayOfWeek());

        DateImpl d1 = new DateImpl(hc, 5777, 9, 1);
        assertEquals(5, d1.getDayOfWeek());
    }

    @Test
    public void testRoshHashana()
    {
        assertEquals(2, hc.absDayRoshHashana(1));
        assertEquals(GregorianCalendar.INSTANCE.fromYMD(2016, 10, 3).absDay(), hc.absDayRoshHashana(5777));
    }

    @Test
    public void getCalendarType()
    {
        Assert.assertEquals(CalendarType.HEBREW, JewishCalendarImpl.INSTANCE.getType());
    }

    @Test
    public void stringRepresentation()
    {
        assertEquals("0001-01-01H", hc.fromYMD(1, JewishMonth.NISAN.getOrdinalNumber(), 1).toString());
        assertEquals("5777-07-09H", hc.fromYMD(5777, JewishMonth.TISHREI.getOrdinalNumber(), 9).toString());
        assertEquals("5711-11-10H", hc.fromYMD(5711, JewishMonth.SHVAT.getOrdinalNumber(), 10).toString());
    }

    @Test
    public void testSefira()
    {
        assertEquals(0,  ICalendar.JEWISH.getSefira(ICalendar.JEWISH.fromYMD(5777, 7, 7)));
        assertEquals(1,  ICalendar.JEWISH.getSefira(ICalendar.JEWISH.fromYMD(5777, 1, 16)));
        assertEquals(33, ICalendar.JEWISH.getSefira(ICalendar.JEWISH.fromYMD(5779, 2, 18)));
        assertEquals(49, ICalendar.JEWISH.getSefira(ICalendar.JEWISH.fromYMD(5777, 3, 5)));
    }
}
