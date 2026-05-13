package net.hebrewcalendar.impl;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by itz on 7/20/17.
 */
public class HebrewTimeTest
{
    @Test
    public void add1()
    {
        HebrewTime t0 = new HebrewTime(0L, 17, 19);
        HebrewTime t1 = new HebrewTime(0L, 22, 1076);

        HebrewTime t2 = t0.add(t1);
        assertEquals(1L, t2.getDay());
        assertEquals(16, t2.getHour());
        assertEquals(15, t2.getPart());
    }

    @Test
    public void add2()
    {
        HebrewTime t0 = new HebrewTime(10000L, 17,19);
        HebrewTime t1 = new HebrewTime(10000L, 22,1076);

        HebrewTime t2 = t0.add(t1);
        assertEquals(20001L, t2.getDay());
        assertEquals(16, t2.getHour());
        assertEquals(15, t2.getPart());
    }

    @Test
    public void subtract1()
    {
        HebrewTime t0 = new HebrewTime(10000L, 17,19);
        HebrewTime t1 = new HebrewTime(10000L, 22,1076);

        HebrewTime t2 = t1.subtract(t0);
        assertEquals(0, t2.getDay());
        assertEquals(5, t2.getHour());
        assertEquals(1057, t2.getPart());

    }

    @Test
    public void subtract2()
    {
        HebrewTime t0 = new HebrewTime(10000L, 22,1076);
        HebrewTime t1 = new HebrewTime(10001L, 17,19);

        HebrewTime t2 = t1.subtract(t0);
        assertEquals(0L, t2.getDay());
        assertEquals(18, t2.getHour());
        assertEquals(23, t2.getPart());

    }

    @Test
    public void subtract3()
    {
        HebrewTime t0 = new HebrewTime(10000L, 22,1076);
        assertEquals(new HebrewTime(0,0,0), t0.subtract(t0));
    }

    @Test
    public void times1()
    {
        HebrewTime t0 = new HebrewTime(10000L, 22,1076);
        assertEquals(new HebrewTime(20001L,21,1072), t0.times(2));
        assertEquals(new HebrewTime(220021L,1,992), t0.times(22));
    }

    @Test
    public void times2()
    {
        HebrewTime t0 = new HebrewTime(0L, 0,1076);
        assertEquals(new HebrewTime(0L,1,1072), t0.times(2));
        assertEquals(new HebrewTime(0L,2,1068), t0.times(3));
    }

}
