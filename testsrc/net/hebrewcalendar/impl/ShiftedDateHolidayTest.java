package net.hebrewcalendar.impl;

import net.hebrewcalendar.HCalendar;
import net.hebrewcalendar.HDate;
import net.hebrewcalendar.HHoliday;
import net.hebrewcalendar.impl.holiday.MonthDayHoliday;
import net.hebrewcalendar.impl.holiday.NthDayOfWeekInMonthHoliday;
import net.hebrewcalendar.impl.holiday.ShiftedDateHoliday;
import org.junit.Test;

import static org.junit.Assert.*;

public class ShiftedDateHolidayTest
{
    private static final NthDayOfWeekInMonthHoliday thanksgiving =
            new NthDayOfWeekInMonthHoliday(HCalendar.GREGORIAN, "Thanksgiving", 11, 5, 4);
    private static final MonthDayHoliday roshHashana =
            new MonthDayHoliday(HCalendar.HEBREW, "Rosh Hashana", 7, 1);
    private static final MonthDayHoliday chanukah =
            new MonthDayHoliday(HCalendar.HEBREW, "Chanukah 1st day", 9, 25);

    @Test
    public void testFridayAfterThanksgiving()
    {
        HHoliday fridayAfterThanksgiving = new ShiftedDateHoliday(
                "Friday after Thanksgiving", thanksgiving, 1);
        HDate d0 = HCalendar.GREGORIAN.fromYMD(2016, 11, 25);
        HDate d1 = HCalendar.GREGORIAN.fromYMD(2017, 12, 1);

        assertTrue(fridayAfterThanksgiving.matches(d0));
        assertTrue(fridayAfterThanksgiving.matches(d1));
        assertFalse(fridayAfterThanksgiving.matches(d1.addDays(1)));
        assertFalse(fridayAfterThanksgiving.matches(d1.subtractDays(1)));

        try {
            assertEquals(d1, fridayAfterThanksgiving.getNextOccurrence(d0.addDays(1), false));
            assertEquals(d1, fridayAfterThanksgiving.getNextOccurrence(d1, false));
            assertFalse(d1.equals(fridayAfterThanksgiving.getNextOccurrence(d1.addDays(1), false)));
        } catch (NoSuchHolidayException e) {
            fail();
        }
    }

    @Test
    public void testErevRoshHashana()
    {
        HHoliday erevRH = new ShiftedDateHoliday(
                "Erev Rosh Hashana", roshHashana, -1);
        HDate d0 = HCalendar.GREGORIAN.fromYMD(2016, 10, 2);
        HDate d1 = HCalendar.GREGORIAN.fromYMD(2017, 9, 20);
        HDate d2 = HCalendar.GREGORIAN.fromYMD(2016, 10, 3);
        assertTrue(erevRH.matches(HCalendar.HEBREW.convert(d0)));
        assertTrue(erevRH.matches(HCalendar.HEBREW.convert(d1)));
        assertFalse(erevRH.matches(HCalendar.HEBREW.convert(d2)));
    }

    @Test
    public void testZoisChanuka()
    {
        HHoliday zoisChanuka = new ShiftedDateHoliday("Zois Chanuka", chanukah, 7);
        HDate d0g = HCalendar.GREGORIAN.fromYMD(2017, 1, 1);
        HDate d0h = HCalendar.HEBREW.fromYMD(5777, 10, 3);

        HDate d1g = HCalendar.GREGORIAN.fromYMD(2017, 12, 20);
        HDate d1h = HCalendar.HEBREW.fromYMD(5778, 10, 2);

        assertTrue(zoisChanuka.matches(d0g));
        assertTrue(zoisChanuka.matches(d0h));
        assertFalse(zoisChanuka.matches(d0h.addDays(1)));
        assertFalse(zoisChanuka.matches(d0h.subtractDays(1)));
        assertTrue(zoisChanuka.matches(d1g));
        assertTrue(zoisChanuka.matches(d1h));
        assertFalse(zoisChanuka.matches(d1h.subtractDays(2)));
    }


}