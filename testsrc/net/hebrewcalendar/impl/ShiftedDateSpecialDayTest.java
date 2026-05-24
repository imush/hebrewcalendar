package net.hebrewcalendar.impl;

import net.hebrewcalendar.ICalendar;
import net.hebrewcalendar.IDate;
import net.hebrewcalendar.SpecialDay;
import net.hebrewcalendar.impl.holiday.MonthDaySpecialDay;
import net.hebrewcalendar.impl.holiday.NthDayOfWeekInMonthSpecialDay;
import net.hebrewcalendar.impl.holiday.ShiftedDateSpecialDay;
import org.junit.Test;

import static org.junit.Assert.*;

public class ShiftedDateSpecialDayTest
{
    private static final NthDayOfWeekInMonthSpecialDay thanksgiving =
            new NthDayOfWeekInMonthSpecialDay(ICalendar.GREGORIAN, "Thanksgiving", 11, 5, 4);
    private static final MonthDaySpecialDay roshHashana =
            new MonthDaySpecialDay(ICalendar.JEWISH, "Rosh Hashana", 7, 1);
    private static final MonthDaySpecialDay chanukah =
            new MonthDaySpecialDay(ICalendar.JEWISH, "Chanukah 1st day", 9, 25);

    @Test
    public void testFridayAfterThanksgiving()
    {
        SpecialDay fridayAfterThanksgiving = new ShiftedDateSpecialDay(
                "Friday after Thanksgiving", thanksgiving, 1);
        IDate d0 = ICalendar.GREGORIAN.fromYMD(2016, 11, 25);
        IDate d1 = ICalendar.GREGORIAN.fromYMD(2017, 11, 24);

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
        SpecialDay erevRH = new ShiftedDateSpecialDay(
                "Erev Rosh Hashana", roshHashana, -1);
        IDate d0 = ICalendar.GREGORIAN.fromYMD(2016, 10, 2);
        IDate d1 = ICalendar.GREGORIAN.fromYMD(2017, 9, 20);
        IDate d2 = ICalendar.GREGORIAN.fromYMD(2016, 10, 3);
        assertTrue(erevRH.matches(ICalendar.JEWISH.convert(d0)));
        assertTrue(erevRH.matches(ICalendar.JEWISH.convert(d1)));
        assertFalse(erevRH.matches(ICalendar.JEWISH.convert(d2)));
    }

    @Test
    public void testZoisChanuka()
    {
        SpecialDay zoisChanuka = new ShiftedDateSpecialDay("Zois Chanuka", chanukah, 7);
        IDate d0g = ICalendar.GREGORIAN.fromYMD(2017, 1, 1);
        IDate d0h = ICalendar.JEWISH.fromYMD(5777, 10, 3);

        IDate d1g = ICalendar.GREGORIAN.fromYMD(2017, 12, 20);
        IDate d1h = ICalendar.JEWISH.fromYMD(5778, 10, 2);

        assertTrue(zoisChanuka.matches(d0g));
        assertTrue(zoisChanuka.matches(d0h));
        assertFalse(zoisChanuka.matches(d0h.addDays(1)));
        assertFalse(zoisChanuka.matches(d0h.subtractDays(1)));
        assertTrue(zoisChanuka.matches(d1g));
        assertTrue(zoisChanuka.matches(d1h));
        assertFalse(zoisChanuka.matches(d1h.subtractDays(2)));
    }


}