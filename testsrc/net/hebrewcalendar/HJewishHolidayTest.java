package net.hebrewcalendar;

import net.hebrewcalendar.impl.NoSuchHolidayException;
import org.junit.Test;

import static org.junit.Assert.*;

public class HJewishHolidayTest
{
    private static HDate H20171231 = HCalendar.GREGORIAN.fromYMD(2017, 12, 31);
    private static HDate H20181231 = HCalendar.GREGORIAN.fromYMD(2018, 12, 31);

    @Test
    public void testAllBackAndForth()
    {
        try {
            for (HJewishHoliday h : HJewishHoliday.values()) {
                final HDate d0 = h.getNextOccurrence(H20171231, true);
                HDate d = d0;
                for (int i = 0; i < 5; i++) {
                    d = h.getNextOccurrence(d, true);
                }
                for (int i = 0; i < 10; i++) {
                    d = h.getPrevOccurrence(d, true);
                }
                for (int i = 0; i < 5; i++) {
                    d = h.getNextOccurrence(d, true);
                }
                assertEquals(d0, d);
            }
        } catch (NoSuchHolidayException nshe) {
            fail(nshe.getMessage());
        }
    }

    @Test
    public void test11Nisan()
    {
        try {
            assertEquals(HCalendar.GREGORIAN.fromYMD(2018, 3, 27),
                    HJewishHoliday.NISAN_11.getNextOccurrence(H20171231, true));
            assertEquals(HCalendar.GREGORIAN.fromYMD(2019, 4, 16),
                    HJewishHoliday.NISAN_11.getNextOccurrence(H20181231, true));
        } catch (NoSuchHolidayException nshe) {
            fail(nshe.toString());
        }
        assertTrue(HJewishHoliday.CHABAD_DAYS.contains(HJewishHoliday.NISAN_11));
        assertFalse(HJewishHoliday.YOM_TOV_DAYS.contains(HJewishHoliday.NISAN_11));
    }

    @Test
    public void testErevPesach()
    {
        try {
            assertEquals(HCalendar.GREGORIAN.fromYMD(2018, 3, 30),
                    HJewishHoliday.EREV_PESACH.getNextOccurrence(H20171231, true));
            assertEquals(HCalendar.GREGORIAN.fromYMD(2019, 4, 19),
                    HJewishHoliday.EREV_PESACH.getNextOccurrence(H20181231, true));
        } catch (NoSuchHolidayException nshe) {
            fail(nshe.toString());
        }
        assertFalse(HJewishHoliday.CHABAD_DAYS.contains(HJewishHoliday.EREV_PESACH));
        assertFalse(HJewishHoliday.YOM_TOV_DAYS.contains(HJewishHoliday.EREV_PESACH));
    }

    @Test
    public void testPesach1()
    {
        try {
            assertEquals(HCalendar.GREGORIAN.fromYMD(2018, 3, 31),
                    HJewishHoliday.FIRST_DAY_PESACH.getNextOccurrence(H20171231, true));
            assertEquals(HCalendar.GREGORIAN.fromYMD(2019, 4, 20),
                    HJewishHoliday.FIRST_DAY_PESACH.getNextOccurrence(H20181231, true));
        } catch (NoSuchHolidayException nshe) {
            fail(nshe.toString());
        }
        assertTrue(HJewishHoliday.FIRST_DAY_PESACH.applies(true));
        assertTrue(HJewishHoliday.FIRST_DAY_PESACH.applies(false));
        assertTrue(HJewishHoliday.YOM_TOV_DAYS.contains(HJewishHoliday.FIRST_DAY_PESACH));
    }

    @Test
    public void testPesach2()
    {
        try {
            assertEquals(HCalendar.GREGORIAN.fromYMD(2018, 4, 1),
                    HJewishHoliday.SECOND_DAY_PESACH_C.getNextOccurrence(H20171231, true));
            assertEquals(HCalendar.GREGORIAN.fromYMD(2019, 4, 21),
                    HJewishHoliday.SECOND_DAY_PESACH_C.getNextOccurrence(H20181231, true));
            assertEquals(HCalendar.GREGORIAN.fromYMD(2019, 4, 21),
                    HJewishHoliday.CHOL_HAMOED_PESACH_1I.getNextOccurrence(H20181231, true));
        } catch (NoSuchHolidayException nshe) {
            fail(nshe.toString());
        }
        assertFalse(HJewishHoliday.SECOND_DAY_PESACH_C.applies(true));
        assertTrue(HJewishHoliday.SECOND_DAY_PESACH_C.applies(false));
        assertTrue(HJewishHoliday.YOM_TOV_DAYS.contains(HJewishHoliday.SECOND_DAY_PESACH_C));

        assertTrue(HJewishHoliday.CHOL_HAMOED_PESACH_1I.applies(true));
        assertFalse(HJewishHoliday.CHOL_HAMOED_PESACH_1I.applies(false));
        assertFalse(HJewishHoliday.YOM_TOV_DAYS.contains(HJewishHoliday.CHOL_HAMOED_PESACH_1I));
    }

    @Test
    public void testPesachCholHamoed()
    {
        try {
            assertEquals(HCalendar.GREGORIAN.fromYMD(2019, 4, 21),
                    HJewishHoliday.CHOL_HAMOED_PESACH_1I.getNextOccurrence(H20181231, true));
            assertEquals(HCalendar.GREGORIAN.fromYMD(2019, 4, 22),
                    HJewishHoliday.CHOL_HAMOED_PESACH_2I.getNextOccurrence(H20181231, true));
            assertEquals(HCalendar.GREGORIAN.fromYMD(2019, 4, 22),
                    HJewishHoliday.CHOL_HAMOED_PESACH_1C.getNextOccurrence(H20181231, true));
            assertEquals(HCalendar.GREGORIAN.fromYMD(2019, 4, 23),
                    HJewishHoliday.CHOL_HAMOED_PESACH_3I.getNextOccurrence(H20181231, true));
            assertEquals(HCalendar.GREGORIAN.fromYMD(2019, 4, 23),
                    HJewishHoliday.CHOL_HAMOED_PESACH_2C.getNextOccurrence(H20181231, true));
            assertEquals(HCalendar.GREGORIAN.fromYMD(2019, 4, 24),
                    HJewishHoliday.CHOL_HAMOED_PESACH_4I.getNextOccurrence(H20181231, true));
            assertEquals(HCalendar.GREGORIAN.fromYMD(2019, 4, 24),
                    HJewishHoliday.CHOL_HAMOED_PESACH_3C.getNextOccurrence(H20181231, true));
            assertEquals(HCalendar.GREGORIAN.fromYMD(2019, 4, 25),
                    HJewishHoliday.CHOL_HAMOED_PESACH_5I.getNextOccurrence(H20181231, true));
            assertEquals(HCalendar.GREGORIAN.fromYMD(2019, 4, 25),
                    HJewishHoliday.CHOL_HAMOED_PESACH_4C.getNextOccurrence(H20181231, true));
        } catch (NoSuchHolidayException nshe) {
            fail(nshe.toString());
        }
        assertFalse(HJewishHoliday.SECOND_DAY_PESACH_C.applies(true));
        assertTrue(HJewishHoliday.SECOND_DAY_PESACH_C.applies(false));
        assertTrue(HJewishHoliday.YOM_TOV_DAYS.contains(HJewishHoliday.SECOND_DAY_PESACH_C));

        assertTrue(HJewishHoliday.CHOL_HAMOED_PESACH_1I.applies(true));
        assertTrue(HJewishHoliday.CHOL_HAMOED_PESACH_2I.applies(true));
        assertTrue(HJewishHoliday.CHOL_HAMOED_PESACH_3I.applies(true));
        assertTrue(HJewishHoliday.CHOL_HAMOED_PESACH_4I.applies(true));
        assertTrue(HJewishHoliday.CHOL_HAMOED_PESACH_5I.applies(true));
        assertFalse(HJewishHoliday.CHOL_HAMOED_PESACH_1I.applies(false));
        assertFalse(HJewishHoliday.CHOL_HAMOED_PESACH_2I.applies(false));
        assertFalse(HJewishHoliday.CHOL_HAMOED_PESACH_3I.applies(false));
        assertFalse(HJewishHoliday.CHOL_HAMOED_PESACH_4I.applies(false));
        assertFalse(HJewishHoliday.CHOL_HAMOED_PESACH_5I.applies(false));
        assertFalse(HJewishHoliday.YOM_TOV_DAYS.contains(HJewishHoliday.CHOL_HAMOED_PESACH_1I));
        assertFalse(HJewishHoliday.YOM_TOV_DAYS.contains(HJewishHoliday.CHOL_HAMOED_PESACH_2I));
        assertFalse(HJewishHoliday.YOM_TOV_DAYS.contains(HJewishHoliday.CHOL_HAMOED_PESACH_3I));
        assertFalse(HJewishHoliday.YOM_TOV_DAYS.contains(HJewishHoliday.CHOL_HAMOED_PESACH_4I));
        assertFalse(HJewishHoliday.YOM_TOV_DAYS.contains(HJewishHoliday.CHOL_HAMOED_PESACH_5I));

        assertFalse(HJewishHoliday.CHOL_HAMOED_PESACH_1C.applies(true));
        assertFalse(HJewishHoliday.CHOL_HAMOED_PESACH_2C.applies(true));
        assertFalse(HJewishHoliday.CHOL_HAMOED_PESACH_3C.applies(true));
        assertFalse(HJewishHoliday.CHOL_HAMOED_PESACH_4C.applies(true));
        assertTrue(HJewishHoliday.CHOL_HAMOED_PESACH_1C.applies(false));
        assertTrue(HJewishHoliday.CHOL_HAMOED_PESACH_2C.applies(false));
        assertTrue(HJewishHoliday.CHOL_HAMOED_PESACH_3C.applies(false));
        assertTrue(HJewishHoliday.CHOL_HAMOED_PESACH_4C.applies(false));
        assertFalse(HJewishHoliday.YOM_TOV_DAYS.contains(HJewishHoliday.CHOL_HAMOED_PESACH_1C));
        assertFalse(HJewishHoliday.YOM_TOV_DAYS.contains(HJewishHoliday.CHOL_HAMOED_PESACH_2C));
        assertFalse(HJewishHoliday.YOM_TOV_DAYS.contains(HJewishHoliday.CHOL_HAMOED_PESACH_3C));
        assertFalse(HJewishHoliday.YOM_TOV_DAYS.contains(HJewishHoliday.CHOL_HAMOED_PESACH_4C));
    }

    @Test
    public void testPesachLast() {
        try {
            assertEquals(HCalendar.GREGORIAN.fromYMD(2018, 4, 6),
                    HJewishHoliday.SEVENTH_DAY_PESACH.getNextOccurrence(H20171231, true));
            assertEquals(HCalendar.GREGORIAN.fromYMD(2018, 4, 7),
                    HJewishHoliday.LAST_DAY_PESACH_C.getNextOccurrence(H20171231, true));
            assertEquals(HCalendar.GREGORIAN.fromYMD(2019, 4, 26),
                    HJewishHoliday.SEVENTH_DAY_PESACH.getNextOccurrence(H20181231, true));
            assertEquals(HCalendar.GREGORIAN.fromYMD(2019, 4, 27),
                    HJewishHoliday.LAST_DAY_PESACH_C.getNextOccurrence(H20181231, true));
        } catch (NoSuchHolidayException nshe) {
            fail(nshe.toString());
        }
        assertTrue(HJewishHoliday.YOM_TOV_DAYS.contains(HJewishHoliday.SEVENTH_DAY_PESACH));
        assertTrue(HJewishHoliday.YOM_TOV_DAYS.contains(HJewishHoliday.LAST_DAY_PESACH_C));

        assertTrue(HJewishHoliday.SEVENTH_DAY_PESACH.applies(true));
        assertTrue(HJewishHoliday.SEVENTH_DAY_PESACH.applies(false));
        assertFalse(HJewishHoliday.LAST_DAY_PESACH_C.applies(true));
        assertTrue(HJewishHoliday.LAST_DAY_PESACH_C.applies(false));
    }

    @Test
    public void testShavuos() {
        try {
            assertEquals(HCalendar.GREGORIAN.fromYMD(2018, 5, 20),
                    HJewishHoliday.SHAVUOS.getNextOccurrence(H20171231, true));
            assertEquals(HCalendar.GREGORIAN.fromYMD(2018, 5, 21),
                    HJewishHoliday.SHAVUOS_2C.getNextOccurrence(H20171231, true));
        } catch (NoSuchHolidayException nshe) {
            fail(nshe.toString());
        }
        assertTrue(HJewishHoliday.YOM_TOV_DAYS.contains(HJewishHoliday.SHAVUOS));
        assertTrue(HJewishHoliday.YOM_TOV_DAYS.contains(HJewishHoliday.SHAVUOS_2C));

        assertTrue(HJewishHoliday.SHAVUOS.applies(true));
        assertTrue(HJewishHoliday.SHAVUOS.applies(false));
        assertFalse(HJewishHoliday.SHAVUOS_2C.applies(true));
        assertTrue(HJewishHoliday.SHAVUOS_2C.applies(false));
    }

    @Test
    public void testTamuz12() {
        try {
            assertEquals(HCalendar.GREGORIAN.fromYMD(2017, 7, 6),
                    HJewishHoliday.TAMUZ_12.getPrevOccurrence(H20171231, true));
            assertEquals(HCalendar.GREGORIAN.fromYMD(2018, 6, 25),
                    HJewishHoliday.TAMUZ_12.getNextOccurrence(H20171231, true));
            assertEquals(HCalendar.GREGORIAN.fromYMD(2019, 7, 15),
                    HJewishHoliday.TAMUZ_12.getNextOccurrence(H20181231, true));
            assertEquals(HCalendar.GREGORIAN.fromYMD(2019, 7, 16),
                    HJewishHoliday.TAMUZ_13.getNextOccurrence(H20181231, true));
        } catch (NoSuchHolidayException nshe) {
            fail(nshe.toString());
        }
        assertFalse(HJewishHoliday.FAST_DAYS.contains(HJewishHoliday.TAMUZ_12));
        assertFalse(HJewishHoliday.FAST_DAYS.contains(HJewishHoliday.TAMUZ_13));

        assertTrue(HJewishHoliday.TAMUZ_12.applies(true));
        assertTrue(HJewishHoliday.TAMUZ_12.applies(false));
        assertTrue(HJewishHoliday.CHABAD_DAYS.contains(HJewishHoliday.TAMUZ_12));
        assertTrue(HJewishHoliday.CHABAD_DAYS.contains(HJewishHoliday.TAMUZ_13));
        assertFalse(HJewishHoliday.YOM_TOV_DAYS.contains(HJewishHoliday.TAMUZ_12));
        assertFalse(HJewishHoliday.YOM_TOV_DAYS.contains(HJewishHoliday.TAMUZ_13));
    }

    @Test
    public void testTamuz17() {
        try {
            assertEquals(HCalendar.GREGORIAN.fromYMD(2017, 7, 11),
                    HJewishHoliday.FAST_TAMUZ_17.getPrevOccurrence(H20171231, true));
            assertEquals(HCalendar.GREGORIAN.fromYMD(2018, 7, 1),
                    HJewishHoliday.FAST_TAMUZ_17.getNextOccurrence(H20171231, true));
            assertEquals(HCalendar.GREGORIAN.fromYMD(2019, 7, 21),
                    HJewishHoliday.FAST_TAMUZ_17.getNextOccurrence(H20181231, true));
        } catch (NoSuchHolidayException nshe) {
            fail(nshe.toString());
        }
        assertTrue(HJewishHoliday.FAST_DAYS.contains(HJewishHoliday.FAST_TAMUZ_17));

        assertTrue(HJewishHoliday.FAST_TAMUZ_17.applies(true));
        assertTrue(HJewishHoliday.FAST_TAMUZ_17.applies(false));
    }

    @Test
    public void testAv9() {
        try {
            assertEquals(HCalendar.GREGORIAN.fromYMD(2017, 8, 1),
                    HJewishHoliday.FAST_AV_9.getPrevOccurrence(H20171231, true));
            assertEquals(HCalendar.GREGORIAN.fromYMD(2018, 7, 22),
                    HJewishHoliday.FAST_AV_9.getNextOccurrence(H20171231, true));
            assertEquals(HCalendar.GREGORIAN.fromYMD(2019, 8, 11),
                    HJewishHoliday.FAST_AV_9.getNextOccurrence(H20181231, true));
        } catch (NoSuchHolidayException nshe) {
            fail(nshe.toString());
        }
        assertTrue(HJewishHoliday.FAST_DAYS.contains(HJewishHoliday.FAST_AV_9));

        assertTrue(HJewishHoliday.FAST_AV_9.applies(true));
        assertTrue(HJewishHoliday.FAST_AV_9.applies(false));
    }

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
