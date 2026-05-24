package net.hebrewcalendar;

import org.junit.Test;

import static org.junit.Assert.*;

public class ConversionTest
{
    @Test
    public void testConvert1()
    {
        IDate g20170725 = ICalendar.GREGORIAN.fromYMD(2017, 7, 25);
        IDate h57770502 = ICalendar.JEWISH.fromYMD(5777, 5, 2);
        assertEquals(h57770502, ICalendar.JEWISH.convert((g20170725)));

        IDate g20161003 = ICalendar.GREGORIAN.fromYMD(2016, 10, 3);
        IDate h57770701 = ICalendar.JEWISH.fromYMD(5777, 7, 1);
        assertEquals(h57770701, ICalendar.JEWISH.convert((g20161003)));

        for (int y = 2; y < 2100; y += 7) {
            for (int m = 1; m <= 12; m++) {
                for (IDate g : new IDate[]{ICalendar.GREGORIAN.fromYMD(y, m, 1),
                        ICalendar.GREGORIAN.fromYMD(y, m, 12),
                        ICalendar.GREGORIAN.fromYMD(y, m, ICalendar.GREGORIAN.monthLength(y, m) - 1),
                        ICalendar.GREGORIAN.fromYMD(y, m, ICalendar.GREGORIAN.monthLength(y, m))}) {
                    IDate j = ICalendar.JULIAN.convert(g);
                    assertEquals(g, ICalendar.GREGORIAN.convert(j));
                    IDate h = ICalendar.JEWISH.convert(g);
                    assertEquals(g, ICalendar.GREGORIAN.convert(h));
                }

                for (IDate j : new IDate[]{ICalendar.JULIAN.fromYMD(y, m, 1),
                        ICalendar.JULIAN.fromYMD(y, m, 12),
                        ICalendar.JULIAN.fromYMD(y, m, ICalendar.JULIAN.monthLength(y, m) - 1),
                        ICalendar.JULIAN.fromYMD(y, m, ICalendar.JULIAN.monthLength(y, m))}) {
                    IDate g = ICalendar.GREGORIAN.convert(j);
                    assertEquals(j, ICalendar.JULIAN.convert(g));
                    IDate h = ICalendar.JEWISH.convert(j);
                    assertEquals(j, ICalendar.JULIAN.convert(h));
                }
            }
        }

        for (int y = 3777; y < 5800; y += 7) {
            for (int m = 1; m <= ICalendar.JEWISH.monthsInYear(y); m++) {
                for (IDate h : new IDate[]{ICalendar.JEWISH.fromYMD(y, m, 1),
                        ICalendar.JEWISH.fromYMD(y, m, 12),
                        ICalendar.JEWISH.fromYMD(y, m, ICalendar.JEWISH.monthLength(y, m) - 1),
                        ICalendar.JEWISH.fromYMD(y, m, ICalendar.JEWISH.monthLength(y, m))}) {
                    IDate g = ICalendar.GREGORIAN.convert(h);
                    assertEquals(h, ICalendar.JEWISH.convert(g));
                    IDate j = ICalendar.JULIAN.convert(h);
                    assertEquals(h, ICalendar.JEWISH.convert(j));
                }

            }
        }
    }

    @Test
    public void testConvert2()
    {
        IDate gregDate = ICalendar.GREGORIAN.fromYMD(1, 1, 1);
        IDate julDate = ICalendar.JULIAN.fromYMD(1, 1, 3);
        IDate hebDate = ICalendar.JEWISH.fromYMD(3761, 10, 18);
        assertEquals(gregDate, ICalendar.GREGORIAN.convert(gregDate));
        assertEquals(gregDate, ICalendar.GREGORIAN.convert(julDate));
        assertEquals(gregDate, ICalendar.GREGORIAN.convert(hebDate));
        assertEquals(julDate, ICalendar.JULIAN.convert(gregDate));
        assertEquals(julDate, ICalendar.JULIAN.convert(julDate));
        assertEquals(julDate, ICalendar.JULIAN.convert(hebDate));
        assertEquals(hebDate, ICalendar.JEWISH.convert(gregDate));
        assertEquals(hebDate, ICalendar.JEWISH.convert(julDate));
        assertEquals(hebDate, ICalendar.JEWISH.convert(hebDate));

        while (gregDate.getYear() < 2300) {
            int toAdd = (int)(Math.random()*100.0);
            gregDate = gregDate.addDays(toAdd);
            julDate = julDate.addDays(toAdd);
            hebDate = hebDate.addDays(toAdd);

            assertEquals("failed after adding " + toAdd + " days", gregDate, ICalendar.GREGORIAN.convert(gregDate));
            assertEquals("failed after adding " + toAdd + " days", gregDate, ICalendar.GREGORIAN.convert(julDate));
            assertEquals("failed after adding " + toAdd + " days", gregDate, ICalendar.GREGORIAN.convert(hebDate));
            assertEquals("failed after adding " + toAdd + " days", julDate, ICalendar.JULIAN.convert(gregDate));
            assertEquals("failed after adding " + toAdd + " days", julDate, ICalendar.JULIAN.convert(julDate));
            assertEquals("failed after adding " + toAdd + " days", julDate, ICalendar.JULIAN.convert(hebDate));
            assertEquals("failed after adding " + toAdd + " days", hebDate, ICalendar.JEWISH.convert(gregDate));
            assertEquals("failed after adding " + toAdd + " days", hebDate, ICalendar.JEWISH.convert(julDate));
            assertEquals("failed after adding " + toAdd + " days", hebDate, ICalendar.JEWISH.convert(hebDate));

            assertEquals(hebDate.getDayOfWeek(), gregDate.getDayOfWeek());
            assertEquals(hebDate.getDayOfWeek(), julDate.getDayOfWeek());
        }

    }
}