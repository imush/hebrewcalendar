package net.hebrewcalendar.impl;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by itz on 7/20/17.
 */
public class HTimeTest
{
    @Test
    public void add1()
    {
        HTime t0 = new HTime(0L, 17, 19);
        HTime t1 = new HTime(0L, 22, 1076);

        HTime t2 = t0.add(t1);
        assertEquals(1L, t2.getDay());
        assertEquals(16, t2.getHour());
        assertEquals(15, t2.getPart());
    }

    @Test
    public void add2()
    {
        HTime t0 = new HTime(10000L, 17,19);
        HTime t1 = new HTime(10000L, 22,1076);

        HTime t2 = t0.add(t1);
        assertEquals(20001L, t2.getDay());
        assertEquals(16, t2.getHour());
        assertEquals(15, t2.getPart());
    }

    @Test
    public void subtract1()
    {
        HTime t0 = new HTime(10000L, 17,19);
        HTime t1 = new HTime(10000L, 22,1076);

        HTime t2 = t1.subtract(t0);
        assertEquals(0, t2.getDay());
        assertEquals(5, t2.getHour());
        assertEquals(1057, t2.getPart());

    }

    @Test
    public void subtract2()
    {
        HTime t0 = new HTime(10000L, 22,1076);
        HTime t1 = new HTime(10001L, 17,19);

        HTime t2 = t1.subtract(t0);
        assertEquals(0L, t2.getDay());
        assertEquals(18, t2.getHour());
        assertEquals(23, t2.getPart());

    }

    @Test
    public void subtract3()
    {
        HTime t0 = new HTime(10000L, 22,1076);
        assertEquals(new HTime(0,0,0), t0.subtract(t0));
    }

    @Test
    public void times1()
    {
        HTime t0 = new HTime(10000L, 22,1076);
        assertEquals(new HTime(20001L,21,1072), t0.times(2));
        assertEquals(new HTime(220021L,1,992), t0.times(22));
    }

    @Test
    public void times2()
    {
        HTime t0 = new HTime(0L, 0,1076);
        assertEquals(new HTime(0L,1,1072), t0.times(2));
        assertEquals(new HTime(0L,2,1068), t0.times(3));
    }

}