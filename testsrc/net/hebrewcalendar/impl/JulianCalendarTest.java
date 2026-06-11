package net.hebrewcalendar.impl;

import net.hebrewcalendar.CalendarType;
import net.hebrewcalendar.IDate;

import org.junit.Test;
import static org.junit.Assert.*;

public class JulianCalendarTest
{
    private static final JulianCalendar cal = JulianCalendar.INSTANCE;

    @Test
    public void testIsLeap()
    {
        // Every multiple of 4 is leap — no century exception
        assertTrue(cal.isLeap(4));
        assertTrue(cal.isLeap(100));
        assertTrue(cal.isLeap(1900));
        assertTrue(cal.isLeap(2000));
        assertTrue(cal.isLeap(2004));
        assertFalse(cal.isLeap(1));
        assertFalse(cal.isLeap(1011));
    }

    @Test
    public void testMonthLength()
    {
        assertEquals(31, cal.monthLength(2010, 7));
        assertEquals(31, cal.monthLength(2010, 8));
        assertEquals(31, cal.monthLength(2009, 12));
        assertEquals(31, cal.monthLength(2010, 1));
        assertEquals(30, cal.monthLength(2010, 9));
        assertEquals(30, cal.monthLength(2010, 11));
        assertEquals(28, cal.monthLength(2010, 2));
        assertEquals(28, cal.monthLength(2011, 2));
        // Century years are leap in Julian (unlike Gregorian)
        assertEquals(29, cal.monthLength(100,  2));
        assertEquals(29, cal.monthLength(1900, 2));
        assertEquals(29, cal.monthLength(2000, 2));
        assertEquals(29, cal.monthLength(2004, 2));
    }

    @Test
    public void absDay()
    {
        final DateImpl<JulianCalendar> d0 = new DateImpl<>(cal, 1, 1, 1);
        assertEquals(cal.getStart() + 1, d0.absDay());

        final DateImpl<JulianCalendar> d20170101 = new DateImpl<>(cal, 2017, 1, 1);
        assertEquals(cal.getStart() + 2016 * 365 + 504 + 1, d20170101.absDay());

        final DateImpl<JulianCalendar> d20170701 = new DateImpl<>(cal, 2017, 7, 1);
        assertEquals(cal.getStart() + 2016 * 365 + 504 + 31 + 28 + 31 + 30 + 31 + 30 + 1,
                     d20170701.absDay());

        // 100-year span: 25 leap years (every 4th, including centuries)
        final DateImpl<JulianCalendar> d1898 = new DateImpl<>(cal, 1898, 1, 11);
        final DateImpl<JulianCalendar> d1998 = new DateImpl<>(cal, 1998, 1, 11);
        assertEquals(365 * 100 + 25, (int)(d1998.absDay() - d1898.absDay()));

        // Roundtrip: fromAbs(date.absDay()) must recover the original date
        for (int y = 1; y < 2100; y += 111) {
            for (int m = 1; m <= 12; m++) {
                for (int d = 1; d <= cal.monthLength(y, m); d++) {
                    final DateImpl<JulianCalendar> date = new DateImpl<>(cal, y, m, d);
                    assertEquals(date, cal.fromAbs(date.absDay()));
                    if (date.absDay() > cal.getStart() + 1)
                        assertEquals(date.absDay(), date.subtractDays(1).absDay() + 1);
                }
            }
        }
    }

    @Test
    public void gregorianVsJulian()
    {
        // At the epoch Julian Jan 3 = Gregorian Jan 1 (2-day offset at year 1)
        assertEquals(new DateImpl<>(JulianCalendar.INSTANCE,    1, 1, 3).absDay(),
                     new DateImpl<>(GregorianCalendar.INSTANCE, 1, 1, 1).absDay());
        assertEquals(new DateImpl<>(JulianCalendar.INSTANCE,    2, 1, 3).absDay(),
                     new DateImpl<>(GregorianCalendar.INSTANCE, 2, 1, 1).absDay());

        // After year 100 the offset grows to 3 (Gregorian skips Feb 29 year 100)
        assertEquals(new DateImpl<>(JulianCalendar.INSTANCE,    101, 1, 2).absDay(),
                     new DateImpl<>(GregorianCalendar.INSTANCE, 101, 1, 1).absDay());
        assertEquals(new DateImpl<>(JulianCalendar.INSTANCE,    100, 2, 29).absDay(),
                     new DateImpl<>(GregorianCalendar.INSTANCE, 100, 2, 27).absDay());
        assertEquals(new DateImpl<>(JulianCalendar.INSTANCE,    100, 3,  1).absDay(),
                     new DateImpl<>(GregorianCalendar.INSTANCE, 100, 2, 28).absDay());
        assertEquals(new DateImpl<>(JulianCalendar.INSTANCE,    100, 3,  2).absDay(),
                     new DateImpl<>(GregorianCalendar.INSTANCE, 100, 3,  1).absDay());

        // By 1900 the offset is 13
        assertEquals(new DateImpl<>(JulianCalendar.INSTANCE,    1900, 2, 17).absDay(),
                     new DateImpl<>(GregorianCalendar.INSTANCE, 1900, 3,  1).absDay());
        assertEquals(new DateImpl<>(JulianCalendar.INSTANCE,    1900, 2, 29).absDay(),
                     new DateImpl<>(GregorianCalendar.INSTANCE, 1900, 3, 13).absDay());
        assertEquals(new DateImpl<>(JulianCalendar.INSTANCE,    1900, 3,  1).absDay(),
                     new DateImpl<>(GregorianCalendar.INSTANCE, 1900, 3, 14).absDay());

        // Verify the 13-day offset directly
        final DateImpl<JulianCalendar>    j20170101 = new DateImpl<>(JulianCalendar.INSTANCE,    2017, 1, 1);
        final DateImpl<GregorianCalendar> g20170101 = new DateImpl<>(GregorianCalendar.INSTANCE, 2017, 1, 1);
        assertEquals(13, j20170101.absDay() - g20170101.absDay());

        assertEquals(new DateImpl<>(JulianCalendar.INSTANCE,    2017, 7,  9).absDay(),
                     new DateImpl<>(GregorianCalendar.INSTANCE, 2017, 7, 22).absDay());
    }

    @Test
    public void addDays()
    {
        // Julian 1900 is leap (366 days), unlike Gregorian
        final IDate<JulianCalendar> jan1_1900 = cal.fromYMD(1900, 1, 1);
        assertEquals(new DateImpl<>(cal, 1900, 12, 31), jan1_1900.addDays(365));
        assertEquals(new DateImpl<>(cal, 1901,  1,  1), jan1_1900.addDays(366));

        final IDate<JulianCalendar> jan1_2000 = cal.fromYMD(2000, 1, 1);
        assertEquals(new DateImpl<>(cal, 2000, 12, 31), jan1_2000.addDays(365));
        assertEquals(new DateImpl<>(cal, 2001,  1,  1), jan1_2000.addDays(366));
    }

    @Test
    public void subtractDays()
    {
        final DateImpl<JulianCalendar> d0 = new DateImpl<>(cal, 2000, 1, 2);
        final DateImpl<JulianCalendar> d1 = new DateImpl<>(cal, 2001, 1, 1);
        assertEquals(d0, d1.subtractDays(365));
    }

    @Test
    public void getDayOfWeek()
    {
        // Julian Jan 3 year 1 = Gregorian Jan 1 year 1 = day 2 (Monday)
        assertEquals(2, new DateImpl<>(cal, 1, 1, 3).getDayOfWeek());
        assertEquals(3, new DateImpl<>(cal, 2, 1, 3).getDayOfWeek());
        // Julian Jul 9 2017 = Gregorian Jul 22 2017 = Saturday (day 7)
        assertEquals(7, new DateImpl<>(cal, 2017, 7, 9).getDayOfWeek());
    }

    @Test
    public void isValid()
    {
        assertTrue(new DateImpl<>(cal, 1997, 12, 31).isValid());

        // Feb 29 valid for all century years in Julian
        assertTrue(new DateImpl<>(cal, 100,  2, 29).isValid());
        assertTrue(new DateImpl<>(cal, 1900, 2, 29).isValid());
        assertTrue(new DateImpl<>(cal, 1996, 2, 29).isValid());
        assertTrue(new DateImpl<>(cal, 2000, 2, 29).isValid());

        try {
            new DateImpl<>(cal, 1997, 12, 32);
            fail("Illegal date created, no exception thrown");
        } catch (IllegalStateException ignored) {}

        try {
            new DateImpl<>(cal, 1997, 0, 3);
            fail("Illegal date created, no exception thrown");
        } catch (IllegalStateException ignored) {}

        try {
            new DateImpl<>(cal, 2000, 2, 30);
            fail("Illegal date 2000-02-30 created, no exception thrown");
        } catch (IllegalStateException ignored) {}
    }

    @Test
    public void testAnniversaryFor()
    {
        // Feb 29 in a Julian leap year → same date in another Julian leap year
        final IDate<JulianCalendar> feb29y4 = cal.fromYMD(4, 2, 29);
        assertEquals(cal.fromYMD(8, 2, 29), cal.anniversaryFor(feb29y4, 8));

        // Feb 29 → non-leap year falls to March 1
        final IDate<JulianCalendar> march1y5 = cal.anniversaryFor(feb29y4, 5);
        assertEquals(3, march1y5.getMonth());
        assertEquals(1, march1y5.getDay());

        // Julian century years are leap, so Feb 29 year 100 → year 104 succeeds
        final IDate<JulianCalendar> feb29y100 = cal.fromYMD(100, 2, 29);
        assertEquals(cal.fromYMD(104, 2, 29), cal.anniversaryFor(feb29y100, 104));

        // Feb 29 year 100 → year 101 (non-leap) falls to March 1
        final IDate<JulianCalendar> result = cal.anniversaryFor(feb29y100, 101);
        assertEquals(3, result.getMonth());
        assertEquals(1, result.getDay());
    }

    @Test
    public void getCalendarType()
    {
        assertEquals(CalendarType.JULIAN, new DateImpl<>(cal, 2017, 7, 31).getCalendarType());
    }

    @Test
    public void stringRepresentation()
    {
        assertEquals("0001-01-01J", cal.fromYMD(1, 1, 1).toString());
        assertEquals("2017-07-09J", cal.fromYMD(2017, 7, 9).toString());
        assertEquals("1812-11-12J", cal.fromYMD(1812, 11, 12).toString());
    }
}
