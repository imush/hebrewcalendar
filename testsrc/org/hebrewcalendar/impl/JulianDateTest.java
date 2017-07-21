package org.hebrewcalendar.impl;

import org.hebrewcalendar.HCalendarType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JulianDateTest
{
    @Test
    void addDays()
    {
        JulianDate d0 = new JulianDate(2000,1,1);
        assertEquals(new JulianDate(2000, 12, 31), d0.addDays(365));
        assertEquals(new JulianDate(2001, 1, 1), d0.addDays(366));
    }

    @Test
    void subtractDays()
    {
        JulianDate d0 = new JulianDate(2000,1,2);
        JulianDate d1 = new JulianDate(2001,1,1);
        assertEquals(d0, d1.subtractDays(365));
    }

    @Test
    void getAbsDay()
    {
        JulianDate d0 = new JulianDate(1,1,1);
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
        assertTrue(new JulianDate(1997,12, 31).isValid());
        assertFalse(new JulianDate(1997,12, 32).isValid());
        assertFalse(new JulianDate(1997,0, 31).isValid());
        assertTrue(new JulianDate(1996,2, 29).isValid());
        assertTrue(new JulianDate(2000,2, 29).isValid());
        assertFalse(new JulianDate(2000,2, 30).isValid());
    }

    @Test
    void getDayOfWeek()
    {
        JulianDate d0 = new JulianDate(2017,1,1);
        assertEquals(4, d0.getDayOfWeek());
    }

}