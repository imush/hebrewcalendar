package org.hebrewcalendar.impl;

import org.hebrewcalendar.HCalendarType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HebrewCalendarTest
{
    private static final HTime FIRST_MOLAD = new HTime(2, 5, 204);
    private static final HTime ONE_MONTH = new HTime(29, 12, 793);

    @Test
    void isLeap()
    {
        assertFalse(HebrewCalendar.INSTANCE.isLeap(5777));
        assertFalse(HebrewCalendar.INSTANCE.isLeap(5778));
        assertTrue(HebrewCalendar.INSTANCE.isLeap(5779));
        assertTrue(HebrewCalendar.INSTANCE.isLeap(19));
    }

    @Test
    void molad()
    {
        HebrewCalendar hc = HebrewCalendar.INSTANCE;
        assertEquals(new HTime(2, 5, 204), HebrewCalendar.INSTANCE.molad(1,7));

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
    void getCalendarType()
    {
        assertEquals(HCalendarType.HEBREW, HebrewCalendar.INSTANCE.getType());
    }

}