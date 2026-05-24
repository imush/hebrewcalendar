package net.hebrewcalendar;

import net.hebrewcalendar.impl.NoSuchHolidayException;
import org.junit.Test;

import static org.junit.Assert.*;

public class JewishSpecialDayTest
{
    private static IDate H20171231 = ICalendar.GREGORIAN.fromYMD(2017, 12, 31);
    private static IDate H20181231 = ICalendar.GREGORIAN.fromYMD(2018, 12, 31);

    @Test
    public void testAllBackAndForth()
    {
        try {
            for (JewishSpecialDay h : JewishSpecialDay.values()) {
                final IDate d0 = h.getNextOccurrence(H20171231, true);
                IDate d = d0;
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
            assertEquals(ICalendar.GREGORIAN.fromYMD(2018, 3, 27),
                    JewishSpecialDay.NISAN_11.getNextOccurrence(H20171231, true));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2019, 4, 16),
                    JewishSpecialDay.NISAN_11.getNextOccurrence(H20181231, true));
        } catch (NoSuchHolidayException nshe) {
            fail(nshe.toString());
        }
        assertTrue(JewishSpecialDay.NISAN_11.isChabad());
        assertFalse(JewishSpecialDay.NISAN_11.isYomTov());
    }

    @Test
    public void testErevPesach()
    {
        try {
            assertEquals(ICalendar.GREGORIAN.fromYMD(2018, 3, 30),
                    JewishSpecialDay.EREV_PESACH.getNextOccurrence(H20171231, true));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2019, 4, 19),
                    JewishSpecialDay.EREV_PESACH.getNextOccurrence(H20181231, true));
        } catch (NoSuchHolidayException nshe) {
            fail(nshe.toString());
        }
        assertFalse(JewishSpecialDay.EREV_PESACH.isChabad());
        assertFalse(JewishSpecialDay.EREV_PESACH.isYomTov());
    }

    @Test
    public void testPesach1()
    {
        try {
            assertEquals(ICalendar.GREGORIAN.fromYMD(2018, 3, 31),
                    JewishSpecialDay.FIRST_DAY_PESACH.getNextOccurrence(H20171231, true));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2019, 4, 20),
                    JewishSpecialDay.FIRST_DAY_PESACH.getNextOccurrence(H20181231, true));
        } catch (NoSuchHolidayException nshe) {
            fail(nshe.toString());
        }
        assertTrue(JewishSpecialDay.FIRST_DAY_PESACH.applies(true));
        assertTrue(JewishSpecialDay.FIRST_DAY_PESACH.applies(false));
        assertTrue(JewishSpecialDay.FIRST_DAY_PESACH.isYomTov());
    }

    @Test
    public void testPesach2()
    {
        try {
            assertEquals(ICalendar.GREGORIAN.fromYMD(2018, 4, 1),
                    JewishSpecialDay.SECOND_DAY_PESACH_C.getNextOccurrence(H20171231, true));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2019, 4, 21),
                    JewishSpecialDay.SECOND_DAY_PESACH_C.getNextOccurrence(H20181231, true));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2019, 4, 21),
                    JewishSpecialDay.CHOL_HAMOED_PESACH_1I.getNextOccurrence(H20181231, true));
        } catch (NoSuchHolidayException nshe) {
            fail(nshe.toString());
        }
        assertFalse(JewishSpecialDay.SECOND_DAY_PESACH_C.applies(true));
        assertTrue(JewishSpecialDay.SECOND_DAY_PESACH_C.applies(false));
        assertTrue(JewishSpecialDay.SECOND_DAY_PESACH_C.isYomTov());

        assertTrue(JewishSpecialDay.CHOL_HAMOED_PESACH_1I.applies(true));
        assertFalse(JewishSpecialDay.CHOL_HAMOED_PESACH_1I.applies(false));
        assertFalse(JewishSpecialDay.CHOL_HAMOED_PESACH_1I.isYomTov());
    }

    @Test
    public void testPesachCholHamoed()
    {
        try {
            assertEquals(ICalendar.GREGORIAN.fromYMD(2019, 4, 21),
                    JewishSpecialDay.CHOL_HAMOED_PESACH_1I.getNextOccurrence(H20181231, true));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2019, 4, 22),
                    JewishSpecialDay.CHOL_HAMOED_PESACH_2I.getNextOccurrence(H20181231, true));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2019, 4, 22),
                    JewishSpecialDay.CHOL_HAMOED_PESACH_1C.getNextOccurrence(H20181231, true));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2019, 4, 23),
                    JewishSpecialDay.CHOL_HAMOED_PESACH_3I.getNextOccurrence(H20181231, true));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2019, 4, 23),
                    JewishSpecialDay.CHOL_HAMOED_PESACH_2C.getNextOccurrence(H20181231, true));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2019, 4, 24),
                    JewishSpecialDay.CHOL_HAMOED_PESACH_4I.getNextOccurrence(H20181231, true));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2019, 4, 24),
                    JewishSpecialDay.CHOL_HAMOED_PESACH_3C.getNextOccurrence(H20181231, true));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2019, 4, 25),
                    JewishSpecialDay.CHOL_HAMOED_PESACH_5I.getNextOccurrence(H20181231, true));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2019, 4, 25),
                    JewishSpecialDay.CHOL_HAMOED_PESACH_4C.getNextOccurrence(H20181231, true));
        } catch (NoSuchHolidayException nshe) {
            fail(nshe.toString());
        }
        assertFalse(JewishSpecialDay.SECOND_DAY_PESACH_C.applies(true));
        assertTrue(JewishSpecialDay.SECOND_DAY_PESACH_C.applies(false));
        assertTrue(JewishSpecialDay.SECOND_DAY_PESACH_C.isYomTov());

        assertTrue(JewishSpecialDay.CHOL_HAMOED_PESACH_1I.applies(true));
        assertTrue(JewishSpecialDay.CHOL_HAMOED_PESACH_2I.applies(true));
        assertTrue(JewishSpecialDay.CHOL_HAMOED_PESACH_3I.applies(true));
        assertTrue(JewishSpecialDay.CHOL_HAMOED_PESACH_4I.applies(true));
        assertTrue(JewishSpecialDay.CHOL_HAMOED_PESACH_5I.applies(true));
        assertFalse(JewishSpecialDay.CHOL_HAMOED_PESACH_1I.applies(false));
        assertFalse(JewishSpecialDay.CHOL_HAMOED_PESACH_2I.applies(false));
        assertFalse(JewishSpecialDay.CHOL_HAMOED_PESACH_3I.applies(false));
        assertFalse(JewishSpecialDay.CHOL_HAMOED_PESACH_4I.applies(false));
        assertFalse(JewishSpecialDay.CHOL_HAMOED_PESACH_5I.applies(false));
        assertFalse(JewishSpecialDay.CHOL_HAMOED_PESACH_1I.isYomTov());
        assertFalse(JewishSpecialDay.CHOL_HAMOED_PESACH_2I.isYomTov());
        assertFalse(JewishSpecialDay.CHOL_HAMOED_PESACH_3I.isYomTov());
        assertFalse(JewishSpecialDay.CHOL_HAMOED_PESACH_4I.isYomTov());
        assertFalse(JewishSpecialDay.CHOL_HAMOED_PESACH_5I.isYomTov());

        assertFalse(JewishSpecialDay.CHOL_HAMOED_PESACH_1C.applies(true));
        assertFalse(JewishSpecialDay.CHOL_HAMOED_PESACH_2C.applies(true));
        assertFalse(JewishSpecialDay.CHOL_HAMOED_PESACH_3C.applies(true));
        assertFalse(JewishSpecialDay.CHOL_HAMOED_PESACH_4C.applies(true));
        assertTrue(JewishSpecialDay.CHOL_HAMOED_PESACH_1C.applies(false));
        assertTrue(JewishSpecialDay.CHOL_HAMOED_PESACH_2C.applies(false));
        assertTrue(JewishSpecialDay.CHOL_HAMOED_PESACH_3C.applies(false));
        assertTrue(JewishSpecialDay.CHOL_HAMOED_PESACH_4C.applies(false));
        assertFalse(JewishSpecialDay.CHOL_HAMOED_PESACH_1C.isYomTov());
        assertFalse(JewishSpecialDay.CHOL_HAMOED_PESACH_2C.isYomTov());
        assertFalse(JewishSpecialDay.CHOL_HAMOED_PESACH_3C.isYomTov());
        assertFalse(JewishSpecialDay.CHOL_HAMOED_PESACH_4C.isYomTov());
    }

    @Test
    public void testPesachLast() {
        try {
            assertEquals(ICalendar.GREGORIAN.fromYMD(2018, 4, 6),
                    JewishSpecialDay.SEVENTH_DAY_PESACH.getNextOccurrence(H20171231, true));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2018, 4, 7),
                    JewishSpecialDay.LAST_DAY_PESACH_C.getNextOccurrence(H20171231, true));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2019, 4, 26),
                    JewishSpecialDay.SEVENTH_DAY_PESACH.getNextOccurrence(H20181231, true));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2019, 4, 27),
                    JewishSpecialDay.LAST_DAY_PESACH_C.getNextOccurrence(H20181231, true));
        } catch (NoSuchHolidayException nshe) {
            fail(nshe.toString());
        }
        assertTrue(JewishSpecialDay.SEVENTH_DAY_PESACH.isYomTov());
        assertTrue(JewishSpecialDay.LAST_DAY_PESACH_C.isYomTov());

        assertTrue(JewishSpecialDay.SEVENTH_DAY_PESACH.applies(true));
        assertTrue(JewishSpecialDay.SEVENTH_DAY_PESACH.applies(false));
        assertFalse(JewishSpecialDay.LAST_DAY_PESACH_C.applies(true));
        assertTrue(JewishSpecialDay.LAST_DAY_PESACH_C.applies(false));
    }

    @Test
    public void testShavuos() {
        try {
            assertEquals(ICalendar.GREGORIAN.fromYMD(2018, 5, 20),
                    JewishSpecialDay.SHAVUOT.getNextOccurrence(H20171231, true));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2018, 5, 21),
                    JewishSpecialDay.SHAVUOT_2C.getNextOccurrence(H20171231, true));
        } catch (NoSuchHolidayException nshe) {
            fail(nshe.toString());
        }
        assertTrue(JewishSpecialDay.SHAVUOT.isYomTov());
        assertTrue(JewishSpecialDay.SHAVUOT_2C.isYomTov());

        assertTrue(JewishSpecialDay.SHAVUOT.applies(true));
        assertTrue(JewishSpecialDay.SHAVUOT.applies(false));
        assertFalse(JewishSpecialDay.SHAVUOT_2C.applies(true));
        assertTrue(JewishSpecialDay.SHAVUOT_2C.applies(false));
    }

    @Test
    public void testTamuz12() {
        try {
            assertEquals(ICalendar.GREGORIAN.fromYMD(2017, 7, 6),
                    JewishSpecialDay.TAMUZ_12.getPrevOccurrence(H20171231, true));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2018, 6, 25),
                    JewishSpecialDay.TAMUZ_12.getNextOccurrence(H20171231, true));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2019, 7, 15),
                    JewishSpecialDay.TAMUZ_12.getNextOccurrence(H20181231, true));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2019, 7, 16),
                    JewishSpecialDay.TAMUZ_13.getNextOccurrence(H20181231, true));
        } catch (NoSuchHolidayException nshe) {
            fail(nshe.toString());
        }
        assertFalse(JewishSpecialDay.TAMUZ_12.isFast());
        assertFalse(JewishSpecialDay.TAMUZ_13.isFast());

        assertTrue(JewishSpecialDay.TAMUZ_12.applies(true));
        assertTrue(JewishSpecialDay.TAMUZ_12.applies(false));
        assertTrue(JewishSpecialDay.TAMUZ_12.isChabad());
        assertTrue(JewishSpecialDay.TAMUZ_13.isChabad());
        assertFalse(JewishSpecialDay.TAMUZ_12.isYomTov());
        assertFalse(JewishSpecialDay.TAMUZ_13.isYomTov());
    }

    @Test
    public void testTamuz17() {
        try {
            assertEquals(ICalendar.GREGORIAN.fromYMD(2017, 7, 11),
                    JewishSpecialDay.FAST_TAMUZ_17.getPrevOccurrence(H20171231, true));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2018, 7, 1),
                    JewishSpecialDay.FAST_TAMUZ_17.getNextOccurrence(H20171231, true));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2019, 7, 21),
                    JewishSpecialDay.FAST_TAMUZ_17.getNextOccurrence(H20181231, true));
        } catch (NoSuchHolidayException nshe) {
            fail(nshe.toString());
        }
        assertTrue(JewishSpecialDay.FAST_TAMUZ_17.isFast());

        assertTrue(JewishSpecialDay.FAST_TAMUZ_17.applies(true));
        assertTrue(JewishSpecialDay.FAST_TAMUZ_17.applies(false));
    }

    @Test
    public void testAv9() {
        try {
            assertEquals(ICalendar.GREGORIAN.fromYMD(2017, 8, 1),
                    JewishSpecialDay.FAST_AV_9.getPrevOccurrence(H20171231, true));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2018, 7, 22),
                    JewishSpecialDay.FAST_AV_9.getNextOccurrence(H20171231, true));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2019, 8, 11),
                    JewishSpecialDay.FAST_AV_9.getNextOccurrence(H20181231, true));
        } catch (NoSuchHolidayException nshe) {
            fail(nshe.toString());
        }
        assertTrue(JewishSpecialDay.FAST_AV_9.isFast());

        assertTrue(JewishSpecialDay.FAST_AV_9.applies(true));
        assertTrue(JewishSpecialDay.FAST_AV_9.applies(false));
    }

    @Test
    public void testRoshHashana() {
        try {
            assertEquals(ICalendar.GREGORIAN.fromYMD(2017, 9, 21),
                    JewishSpecialDay.ROSH_HASHANA_1.getPrevOccurrence(H20171231, true));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2017, 9, 22),
                    JewishSpecialDay.ROSH_HASHANA_2.getPrevOccurrence(H20171231, true));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2018, 9, 10),
                    JewishSpecialDay.ROSH_HASHANA_1.getNextOccurrence(H20171231, true));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2018, 9, 11),
                    JewishSpecialDay.ROSH_HASHANA_2.getNextOccurrence(H20171231, true));
            assertTrue(JewishSpecialDay.ROSH_HASHANA_1.isYomTov());
            assertTrue(JewishSpecialDay.ROSH_HASHANA_2.isYomTov());
            assertTrue(JewishSpecialDay.ROSH_HASHANA_1.applies(true));
            assertTrue(JewishSpecialDay.ROSH_HASHANA_2.applies(true));
        } catch (NoSuchHolidayException e) {
            fail(e.toString());
        }
    }

    @Test
    public void testTzomGedalia() {
        try {
            assertEquals(ICalendar.GREGORIAN.fromYMD(2017, 9, 23),
                    JewishSpecialDay.TZOM_GEDALIA.getPrevOccurrence(H20171231, true));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2018, 9, 12),
                    JewishSpecialDay.TZOM_GEDALIA.getNextOccurrence(H20171231, true));
            assertTrue(JewishSpecialDay.TZOM_GEDALIA.applies(true));
            assertTrue(JewishSpecialDay.TZOM_GEDALIA.applies(false));
        } catch (NoSuchHolidayException e) {
            fail(e.toString());
        }
    }

    @Test
    public void testSukkos() {
        try {
            assertEquals(ICalendar.GREGORIAN.fromYMD(2017, 10, 5),
                    JewishSpecialDay.FIRST_DAY_SUKKOT.getPrevOccurrence(H20171231, true));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2018, 9, 24),
                    JewishSpecialDay.FIRST_DAY_SUKKOT.getNextOccurrence(H20171231, true));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2017, 10, 6),
                    JewishSpecialDay.SECOND_DAY_SUKKOT_C.getPrevOccurrence(H20171231, true));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2018, 9, 25),
                    JewishSpecialDay.SECOND_DAY_SUKKOT_C.getNextOccurrence(H20171231, true));
            assertFalse(JewishSpecialDay.SECOND_DAY_SUKKOT_C.applies(true));
            assertTrue(JewishSpecialDay.SECOND_DAY_SUKKOT_C.applies(false));
            assertTrue(JewishSpecialDay.FIRST_DAY_SUKKOT.isYomTov());
            assertTrue(JewishSpecialDay.SECOND_DAY_SUKKOT_C.isYomTov());

            assertEquals(ICalendar.GREGORIAN.fromYMD(2017, 10, 6),
                    JewishSpecialDay.CHOL_HAMOED_SUKKOT_1I.getPrevOccurrence(H20171231, true));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2017, 10, 7),
                    JewishSpecialDay.CHOL_HAMOED_SUKKOT_2I.getPrevOccurrence(H20171231, true));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2017, 10, 8),
                    JewishSpecialDay.CHOL_HAMOED_SUKKOT_3I.getPrevOccurrence(H20171231, true));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2017, 10, 9),
                    JewishSpecialDay.CHOL_HAMOED_SUKKOT_4I.getPrevOccurrence(H20171231, true));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2017, 10, 10),
                    JewishSpecialDay.CHOL_HAMOED_SUKKOT_5I.getPrevOccurrence(H20171231, true));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2017, 10, 7),
                    JewishSpecialDay.CHOL_HAMOED_SUKKOT_1C.getPrevOccurrence(H20171231, true));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2017, 10, 8),
                    JewishSpecialDay.CHOL_HAMOED_SUKKOT_2C.getPrevOccurrence(H20171231, true));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2017, 10, 9),
                    JewishSpecialDay.CHOL_HAMOED_SUKKOT_3C.getPrevOccurrence(H20171231, true));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2017, 10, 10),
                    JewishSpecialDay.CHOL_HAMOED_SUKKOT_4C.getPrevOccurrence(H20171231, true));

            assertEquals(ICalendar.GREGORIAN.fromYMD(2017, 10, 11),
                    JewishSpecialDay.HOSHANA_RABBA.getPrevOccurrence(H20171231, true));

            assertEquals(ICalendar.GREGORIAN.fromYMD(2017, 10, 12),
                    JewishSpecialDay.SHMINI_ATZERES_C.getPrevOccurrence(H20171231, true));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2017, 10, 13),
                    JewishSpecialDay.SIMCHAS_TORAH_C.getPrevOccurrence(H20171231, true));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2017, 10, 12),
                    JewishSpecialDay.SIMCHAS_TORAH_I.getPrevOccurrence(H20171231, true));

            assertTrue(JewishSpecialDay.SHMINI_ATZERES_C.isYomTov());
            assertTrue(JewishSpecialDay.SIMCHAS_TORAH_I.isYomTov());
            assertTrue(JewishSpecialDay.SIMCHAS_TORAH_C.isYomTov());
            assertFalse(JewishSpecialDay.CHOL_HAMOED_SUKKOT_3C.isYomTov());
            assertFalse(JewishSpecialDay.CHOL_HAMOED_SUKKOT_3I.isYomTov());

            assertFalse(JewishSpecialDay.SIMCHAS_TORAH_C.applies(true));
            assertTrue(JewishSpecialDay.SIMCHAS_TORAH_I.applies(true));
            assertFalse(JewishSpecialDay.SIMCHAS_TORAH_I.applies(false));
            assertTrue(JewishSpecialDay.SIMCHAS_TORAH_C.applies(false));
            assertFalse(JewishSpecialDay.SHMINI_ATZERES_C.applies(true));
            assertTrue(JewishSpecialDay.SHMINI_ATZERES_C.applies(false));

        } catch (NoSuchHolidayException e) {
            fail(e.toString());
        }
    }

    @Test
    public void testChanukkah() {
        try {
            assertEquals(ICalendar.GREGORIAN.fromYMD(2017, 12, 13),
                    JewishSpecialDay.FIRST_DAY_CHANUKAH.getPrevOccurrence(H20171231, true));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2017, 12, 14),
                    JewishSpecialDay.SECOND_DAY_CHANUKAH.getPrevOccurrence(H20171231, true));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2017, 12, 15),
                    JewishSpecialDay.THIRD_DAY_CHANUKAH.getPrevOccurrence(H20171231, true));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2017, 12, 16),
                    JewishSpecialDay.FOURTH_DAY_CHANUKAH.getPrevOccurrence(H20171231, true));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2017, 12, 17),
                    JewishSpecialDay.FIFTH_DAY_CHANUKAH.getPrevOccurrence(H20171231, true));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2017, 12, 18),
                    JewishSpecialDay.SIXTH_DAY_CHANUKAH.getPrevOccurrence(H20171231, true));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2017, 12, 19),
                    JewishSpecialDay.SEVENTH_DAY_CHANUKAH.getPrevOccurrence(H20171231, true));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2017, 12, 20),
                    JewishSpecialDay.EIGHTH_DAY_CHANUKAH.getPrevOccurrence(H20171231, true));
        } catch (NoSuchHolidayException e) {
            fail(e.toString());
        }
        assertTrue(JewishSpecialDay.FIRST_DAY_CHANUKAH.applies(true));
        assertTrue(JewishSpecialDay.EIGHTH_DAY_CHANUKAH.applies(false));

        assertFalse(JewishSpecialDay.FIRST_DAY_CHANUKAH.isYomTov());
        assertFalse(JewishSpecialDay.SEVENTH_DAY_CHANUKAH.isYomTov());
    }

    @Test
    public void testPurim()
    {
        try {
            assertEquals(ICalendar.GREGORIAN.fromYMD(2018, 3, 1),
                    JewishSpecialDay.PURIM.getNextOccurrence(H20171231, true));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2019, 3, 21),
                    JewishSpecialDay.PURIM.getNextOccurrence(H20181231, true));
        } catch (NoSuchHolidayException nshe) {
            fail(nshe.toString());
        }
    }

    @Test
    public void testShushanPurim()
    {
        try {
            assertEquals(ICalendar.GREGORIAN.fromYMD(2018, 3, 2),
                    JewishSpecialDay.SHUSHAN_PURIM.getNextOccurrence(H20171231, true));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2019, 3, 22),
                    JewishSpecialDay.SHUSHAN_PURIM.getNextOccurrence(H20181231, true));
        } catch (NoSuchHolidayException nshe) {
            fail(nshe.toString());
        }
    }

    @Test
    public void testPurimKatan()
    {
        try {
            // no Purim Katan in 2018/5778
            assertEquals(ICalendar.GREGORIAN.fromYMD(2019, 2, 19),
                    JewishSpecialDay.PURIM_KATAN.getNextOccurrence(H20171231, true));
            assertEquals(ICalendar.GREGORIAN.fromYMD(2019, 2, 19),
                    JewishSpecialDay.PURIM_KATAN.getNextOccurrence(H20181231, true));
        } catch (NoSuchHolidayException nshe) {
            fail(nshe.toString());
        }
    }
}
