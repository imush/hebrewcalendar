package net.hebrewcalendar.impl;

import net.hebrewcalendar.ICalendar;
import net.hebrewcalendar.IDate;
import net.hebrewcalendar.SpecialDay;
import net.hebrewcalendar.impl.holiday.MonthDaySpecialDay;
import net.hebrewcalendar.impl.holiday.UnionSpecialDay;
import org.junit.Test;

import static org.junit.Assert.*;

public class UnionSpecialDayTest
{
    private static final MonthDaySpecialDay roshChodesh0 =
            new MonthDaySpecialDay(ICalendar.JEWISH, "Rosh Chodesh", 0, 30);
    private static final MonthDaySpecialDay roshChodesh1 =
            new MonthDaySpecialDay(ICalendar.JEWISH, "Rosh Chodesh", 0, 1);
    private static final SpecialDay roshChodesh =
            new UnionSpecialDay("Rosh Chodesh", new SpecialDay[] {roshChodesh0, roshChodesh1});

    @Test
    public void getNextOccurrenceOnOrAfter()
            throws Exception
    {
        IDate cheshvan11 = ICalendar.JEWISH.fromYMD(5777, 8, 11);
        IDate kislev1 = ICalendar.JEWISH.fromYMD(5777, 9, 1);
        IDate shvat11 = ICalendar.JEWISH.fromYMD(5777, 11, 11);
        IDate shvat30 = ICalendar.JEWISH.fromYMD(5777, 11, 30);
        IDate kislev2 = ICalendar.JEWISH.fromYMD(5778, 9, 2);
        IDate kislev30 = ICalendar.JEWISH.fromYMD(5778, 9, 30);
        IDate teveth1 = ICalendar.JEWISH.fromYMD(5778, 10, 1);

        assertEquals(kislev1, roshChodesh.getNextOccurrence(cheshvan11, false));
        assertEquals(shvat30, roshChodesh.getNextOccurrence(shvat11, false));
        assertEquals(kislev30, roshChodesh.getNextOccurrence(kislev2, true));
        assertEquals(teveth1, roshChodesh.getNextOccurrence(teveth1, false));
        assertEquals(teveth1, roshChodesh.getPrevOccurrence(teveth1, false));
        assertEquals(kislev30, roshChodesh.getPrevOccurrence(teveth1, true));

    }

    @Test
    public void matches()
            throws Exception
    {
        IDate shvat11 = ICalendar.JEWISH.fromYMD(5777, 11, 11);
        IDate adar1 = ICalendar.JEWISH.fromYMD(5777, 12, 1);
        IDate kislev1 = ICalendar.JEWISH.fromYMD(5777, 9, 1);
        IDate kislev30 = ICalendar.JEWISH.fromYMD(5778, 9, 30);
        IDate teveth1 = ICalendar.JEWISH.fromYMD(5778, 10, 1);

        assertTrue(roshChodesh.matches(adar1));
        assertTrue(roshChodesh.matches(kislev1));
        assertTrue(roshChodesh.matches(teveth1));
        assertTrue(roshChodesh.matches(kislev30));
        assertFalse(roshChodesh.matches(shvat11));

    }

}