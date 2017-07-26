package org.hebrewcalendar;

import org.hebrewcalendar.impl.HDateImpl;
import org.junit.Test;

import static org.junit.Assert.*;

public class ConversionTest
{
    @Test
    public void testConvert1()
    {
        HDate g20170725 = HCalendar.GREGORIAN.fromYMD(2017, 7, 25);
        HDate h57770502 = HCalendar.HEBREW.fromYMD(5777, 5, 2);
        assertEquals(h57770502, HCalendar.HEBREW.convert((g20170725)));

        HDate g20161003 = HCalendar.GREGORIAN.fromYMD(2016, 10, 3);
        HDate h57770701 = HCalendar.HEBREW.fromYMD(5777, 7, 1);
        assertEquals(h57770701, HCalendar.HEBREW.convert((g20161003)));

        for (int y = 2; y < 2100; y += 7) {
            for (int m = 1; m <= 12; m++) {
                for (HDate g : new HDate[]{HCalendar.GREGORIAN.fromYMD(y, m, 1),
                        HCalendar.GREGORIAN.fromYMD(y, m, 12),
                        HCalendar.GREGORIAN.fromYMD(y, m, HCalendar.GREGORIAN.monthLength(y, m) - 1),
                        HCalendar.GREGORIAN.fromYMD(y, m, HCalendar.GREGORIAN.monthLength(y, m))}) {
                    HDate j = HCalendar.JULIAN.convert(g);
                    assertEquals(g, HCalendar.GREGORIAN.convert(j));
                    HDate h = HCalendar.HEBREW.convert(g);
                    assertEquals(g, HCalendar.GREGORIAN.convert(h));
                }

                for (HDate j : new HDate[]{HCalendar.JULIAN.fromYMD(y, m, 1),
                        HCalendar.JULIAN.fromYMD(y, m, 12),
                        HCalendar.JULIAN.fromYMD(y, m, HCalendar.JULIAN.monthLength(y, m) - 1),
                        HCalendar.JULIAN.fromYMD(y, m, HCalendar.JULIAN.monthLength(y, m))}) {
                    HDate g = HCalendar.GREGORIAN.convert(j);
                    assertEquals(j, HCalendar.JULIAN.convert(g));
                    HDate h = HCalendar.HEBREW.convert(j);
                    assertEquals(j, HCalendar.JULIAN.convert(h));
                }
            }
        }

        for (int y = 3777; y < 5800; y += 7) {
            for (int m = 1; m <= HCalendar.HEBREW.monthsInYear(y); m++) {
                for (HDate h : new HDate[]{HCalendar.HEBREW.fromYMD(y, m, 1),
                        HCalendar.HEBREW.fromYMD(y, m, 12),
                        HCalendar.HEBREW.fromYMD(y, m, HCalendar.HEBREW.monthLength(y, m) - 1),
                        HCalendar.HEBREW.fromYMD(y, m, HCalendar.HEBREW.monthLength(y, m))}) {
                    HDate g = HCalendar.GREGORIAN.convert(h);
                    assertEquals(h, HCalendar.HEBREW.convert(g));
                    HDate j = HCalendar.JULIAN.convert(h);
                    assertEquals(h, HCalendar.HEBREW.convert(j));
                }

            }
        }
    }

    @Test
    public void testConvert2()
    {
        HDate gregDate = HCalendar.GREGORIAN.fromYMD(1, 1, 1);
        HDate julDate = HCalendar.JULIAN.fromYMD(1, 1, 3);
        HDate hebDate = HCalendar.HEBREW.fromYMD(3761, 10, 18);
        assertEquals(gregDate, HCalendar.GREGORIAN.convert(gregDate));
        assertEquals(gregDate, HCalendar.GREGORIAN.convert(julDate));
        assertEquals(gregDate, HCalendar.GREGORIAN.convert(hebDate));
        assertEquals(julDate, HCalendar.JULIAN.convert(gregDate));
        assertEquals(julDate, HCalendar.JULIAN.convert(julDate));
        assertEquals(julDate, HCalendar.JULIAN.convert(hebDate));
        assertEquals(hebDate, HCalendar.HEBREW.convert(gregDate));
        assertEquals(hebDate, HCalendar.HEBREW.convert(julDate));
        assertEquals(hebDate, HCalendar.HEBREW.convert(hebDate));

        while (gregDate.getYear() < 2300) {
            int toAdd = (int)(Math.random()*100.0);
            gregDate = gregDate.addDays(toAdd);
            julDate = julDate.addDays(toAdd);
            hebDate = hebDate.addDays(toAdd);

            assertEquals("failed after adding " + toAdd + " days", gregDate, HCalendar.GREGORIAN.convert(gregDate));
            assertEquals("failed after adding " + toAdd + " days", gregDate, HCalendar.GREGORIAN.convert(julDate));
            assertEquals("failed after adding " + toAdd + " days", gregDate, HCalendar.GREGORIAN.convert(hebDate));
            assertEquals("failed after adding " + toAdd + " days", julDate, HCalendar.JULIAN.convert(gregDate));
            assertEquals("failed after adding " + toAdd + " days", julDate, HCalendar.JULIAN.convert(julDate));
            assertEquals("failed after adding " + toAdd + " days", julDate, HCalendar.JULIAN.convert(hebDate));
            assertEquals("failed after adding " + toAdd + " days", hebDate, HCalendar.HEBREW.convert(gregDate));
            assertEquals("failed after adding " + toAdd + " days", hebDate, HCalendar.HEBREW.convert(julDate));
            assertEquals("failed after adding " + toAdd + " days", hebDate, HCalendar.HEBREW.convert(hebDate));

            assertEquals(hebDate.getDayOfWeek(), gregDate.getDayOfWeek());
            assertEquals(hebDate.getDayOfWeek(), julDate.getDayOfWeek());
        }

    }
}