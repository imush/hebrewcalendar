package net.hebrewcalendar.impl;

import net.hebrewcalendar.ICalendar;
import net.hebrewcalendar.CalendarType;
import net.hebrewcalendar.IDate;
import net.hebrewcalendar.JewishCalendar;
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

        DateImpl<JewishCalendar> h57750701 = hc.fromYMD(5775, 7, 1);
        DateImpl<JewishCalendar> h57760701 = hc.fromYMD(5776, 7, 1);

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
        DateImpl<JewishCalendar> d1 = new DateImpl<>((JewishCalendar) hc, 5777, 9, 1);
        assertEquals(new DateImpl<>((JewishCalendar) hc, 5777, 8, 29), d1.subtractDays(1));
    }

    @Test
    public void testDayOfWeek()
    {
        DateImpl<JewishCalendar> d20161003 = new DateImpl<>((JewishCalendar) hc, 5777, 7, 1);
        assertEquals(2, d20161003.getDayOfWeek());

        DateImpl<JewishCalendar> d0 = new DateImpl<>((JewishCalendar) hc, 5777, 8, 1);
        assertEquals(4, d0.getDayOfWeek());

        DateImpl<JewishCalendar> d1 = new DateImpl<>((JewishCalendar) hc, 5777, 9, 1);
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

    @Test
    public void testBirthdayRule()
    {
        // Born 12 Adar 5778 (non-leap) — should celebrate 13 Adar II in 5779 (leap)
        DateImpl<JewishCalendar> born = hc.fromYMD(5778, 12, 12);
        assertFalse(hc.isLeap(5778));
        assertTrue(hc.isLeap(5779));
        IDate<JewishCalendar> bday5779 = hc.anniversaryFor(born, 5779);
        assertEquals(13, bday5779.getMonth()); // Adar II
        assertEquals(12, bday5779.getDay());
        assertEquals(5779, bday5779.getYear());

        // Born 13 Adar II 5779 (leap) — should celebrate 12 Adar in 5780 (non-leap)
        DateImpl<JewishCalendar> born2 = hc.fromYMD(5779, 13, 15);
        assertFalse(hc.isLeap(5780));
        IDate<JewishCalendar> bday5780 = hc.anniversaryFor(born2, 5780);
        assertEquals(12, bday5780.getMonth()); // Adar
        assertEquals(15, bday5780.getDay());

        // Cheshvan 30 in a FULL year → same day in a SHORT year falls back to 1 Kislev
        // 5779 is FULL (Cheshvan has 30 days), 5777 is SHORT (Cheshvan has 29 days)
        DateImpl<JewishCalendar> cheshvan30 = hc.fromYMD(5779, 8, 30);
        assertEquals(YearCheshvanKislevType.SHORT, hc.getYearType(5777));
        IDate<JewishCalendar> fallen = hc.anniversaryFor(cheshvan30, 5777);
        assertEquals(9, fallen.getMonth()); // Kislev
        assertEquals(1, fallen.getDay());

        // Born 30 Adar I 5779 (leap) — anniversary in 5780 (non-leap) falls on 1 Nissan
        assertTrue(hc.isLeap(5779));
        assertFalse(hc.isLeap(5780));
        DateImpl<JewishCalendar> born30AdarI = hc.fromYMD(5779, 12, 30);
        IDate<JewishCalendar> bday30AdarI = hc.anniversaryFor(born30AdarI, 5780);
        assertEquals(1, bday30AdarI.getMonth()); // Nissan
        assertEquals(1, bday30AdarI.getDay());   // Rosh Chodesh Nissan
        assertEquals(5780, bday30AdarI.getYear());
    }

    @Test
    public void testYahrzeitRule()
    {
        // Died 13 Adar II 5779 (leap) — yahrzeit in 5780 (non-leap) falls on 12 Adar
        DateImpl<JewishCalendar> death = hc.fromYMD(5779, 13, 20);
        assertFalse(hc.isLeap(5780));
        IDate<JewishCalendar> yahrzeit = hc.yahrzeitFor(death, 5780);
        assertEquals(12, yahrzeit.getMonth());
        assertEquals(20, yahrzeit.getDay());

        // Died 12 Adar 5778 (non-leap) — yahrzeit in 5779 (leap) stays on 12 Adar I
        DateImpl<JewishCalendar> death2 = hc.fromYMD(5778, 12, 5);
        IDate<JewishCalendar> yahrzeit2 = hc.yahrzeitFor(death2, 5779);
        assertEquals(12, yahrzeit2.getMonth()); // Adar I (month 12 in leap year)
        assertEquals(5, yahrzeit2.getDay());

        // Died 30 Adar I 5779 (leap) — yahrzeit in 5780 (non-leap) falls on 1 Nissan
        DateImpl<JewishCalendar> death30AdarI = hc.fromYMD(5779, 12, 30);
        IDate<JewishCalendar> yahrzeit30AdarI = hc.yahrzeitFor(death30AdarI, 5780);
        assertEquals(1, yahrzeit30AdarI.getMonth()); // Nissan
        assertEquals(1, yahrzeit30AdarI.getDay());   // Rosh Chodesh Nissan
        assertEquals(5780, yahrzeit30AdarI.getYear());
    }

    @Test
    public void testYahrzeitCheshvan30()
    {
        // 5779=FULL, 5780=FULL, 5781=SHORT
        assertEquals(YearCheshvanKislevType.FULL,  hc.getYearType(5779));
        assertEquals(YearCheshvanKislevType.FULL,  hc.getYearType(5780));
        assertEquals(YearCheshvanKislevType.SHORT, hc.getYearType(5781));

        // Died 30 Cheshvan 5779 — first year 5780 is FULL (30 Cheshvan exists) → always 1 Kislev.

        DateImpl<JewishCalendar> death5779 = hc.fromYMD(5779, 8, 30);

        // In a FULL target year the old code returned 30 Cheshvan; new rule requires 1 Kislev.
        IDate<JewishCalendar> y5780 = hc.yahrzeitFor(death5779, 5780);
        assertEquals(9,    y5780.getMonth()); // Kislev
        assertEquals(1,    y5780.getDay());
        assertEquals(5780, y5780.getYear());

        // In a SHORT target year also 1 Kislev (unchanged from old behaviour, but now for the right reason).
        IDate<JewishCalendar> y5781 = hc.yahrzeitFor(death5779, 5781);
        assertEquals(9,    y5781.getMonth()); // Kislev
        assertEquals(1,    y5781.getDay());
        assertEquals(5781, y5781.getYear());

        // Died 30 Cheshvan 5780 — first year 5781 is SHORT (30 Cheshvan absent) → always 29 Cheshvan.

        DateImpl<JewishCalendar> death5780 = hc.fromYMD(5780, 8, 30);

        // In a SHORT target year the old code returned 1 Kislev; new rule requires 29 Cheshvan.
        IDate<JewishCalendar> y5781b = hc.yahrzeitFor(death5780, 5781);
        assertEquals(8,    y5781b.getMonth()); // Cheshvan
        assertEquals(29,   y5781b.getDay());
        assertEquals(5781, y5781b.getYear());

        // In a FULL target year also 29 Cheshvan (old code would have returned 30 Cheshvan).
        IDate<JewishCalendar> y5780b = hc.yahrzeitFor(death5780, 5780);
        assertEquals(8,    y5780b.getMonth()); // Cheshvan
        assertEquals(29,   y5780b.getDay());
        assertEquals(5780, y5780b.getYear());
    }

    @Test
    public void testYahrzeitKislev30()
    {
        // 5779=FULL, 5780=FULL, 5781=SHORT
        assertEquals(YearCheshvanKislevType.FULL,  hc.getYearType(5779));
        assertEquals(YearCheshvanKislevType.FULL,  hc.getYearType(5780));
        assertEquals(YearCheshvanKislevType.SHORT, hc.getYearType(5781));

        // Died 30 Kislev 5779 — first year 5780 is FULL (30 Kislev exists) → always 1 Tevet.

        DateImpl<JewishCalendar> death5779 = hc.fromYMD(5779, 9, 30);

        // In a FULL target year the old code returned 30 Kislev; new rule requires 1 Tevet.
        IDate<JewishCalendar> y5780 = hc.yahrzeitFor(death5779, 5780);
        assertEquals(10,   y5780.getMonth()); // Tevet
        assertEquals(1,    y5780.getDay());
        assertEquals(5780, y5780.getYear());

        // In a SHORT target year also 1 Tevet (unchanged from old behaviour).
        IDate<JewishCalendar> y5781 = hc.yahrzeitFor(death5779, 5781);
        assertEquals(10,   y5781.getMonth()); // Tevet
        assertEquals(1,    y5781.getDay());
        assertEquals(5781, y5781.getYear());

        // Died 30 Kislev 5780 — first year 5781 is SHORT (30 Kislev absent) → always 29 Kislev.

        DateImpl<JewishCalendar> death5780 = hc.fromYMD(5780, 9, 30);

        // In a SHORT target year the old code returned 1 Tevet; new rule requires 29 Kislev.
        IDate<JewishCalendar> y5781b = hc.yahrzeitFor(death5780, 5781);
        assertEquals(9,    y5781b.getMonth()); // Kislev
        assertEquals(29,   y5781b.getDay());
        assertEquals(5781, y5781b.getYear());

        // In a FULL target year also 29 Kislev (old code would have returned 30 Kislev).
        IDate<JewishCalendar> y5780b = hc.yahrzeitFor(death5780, 5780);
        assertEquals(9,    y5780b.getMonth()); // Kislev
        assertEquals(29,   y5780b.getDay());
        assertEquals(5780, y5780b.getYear());
    }
}
