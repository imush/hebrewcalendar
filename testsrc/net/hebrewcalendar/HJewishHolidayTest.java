package net.hebrewcalendar;

import net.hebrewcalendar.impl.NoSuchHolidayException;
import org.junit.Test;

import static org.junit.Assert.*;

public class HJewishHolidayTest
{
    private static HDate H20171231 = HCalendar.GREGORIAN.fromYMD(2017, 12, 31);
    private static HDate H20181231 = HCalendar.GREGORIAN.fromYMD(2018, 12, 31);

    @Test
    public void testPurim()
    {
        try {
            assertEquals(HCalendar.GREGORIAN.fromYMD(2018, 3, 1),
                    HJewishHoliday.PURIM.getNextOccurrence(H20171231, true));
            assertEquals(HCalendar.GREGORIAN.fromYMD(2019, 3, 21),
                    HJewishHoliday.PURIM.getNextOccurrence(H20181231, true));
        } catch (NoSuchHolidayException nshe) {
            fail(nshe.toString());
        }
    }

    @Test
    public void testShushanPurim()
    {
        try {
            assertEquals(HCalendar.GREGORIAN.fromYMD(2018, 3, 2),
                    HJewishHoliday.SHUSHAN_PURIM.getNextOccurrence(H20171231, true));
            assertEquals(HCalendar.GREGORIAN.fromYMD(2019, 3, 22),
                    HJewishHoliday.SHUSHAN_PURIM.getNextOccurrence(H20181231, true));
        } catch (NoSuchHolidayException nshe) {
            fail(nshe.toString());
        }
    }

    @Test
    public void testPurimKatan()
    {
        try {
            // no Purim Katan in 2018/5778
            assertEquals(HCalendar.GREGORIAN.fromYMD(2019, 2, 19),
                    HJewishHoliday.PURIM_KATAN.getNextOccurrence(H20171231, true));
            assertEquals(HCalendar.GREGORIAN.fromYMD(2019, 2, 19),
                    HJewishHoliday.PURIM_KATAN.getNextOccurrence(H20181231, true));
        } catch (NoSuchHolidayException nshe) {
            fail(nshe.toString());
        }
    }
}
