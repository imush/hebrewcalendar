package org.hebrewcalendar.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by itz on 7/20/17.
 */
class HTimeTest
{
    @Test
    void add1()
    {
        HTime t0 = new HTime(0l, 17, 19);
        HTime t1 = new HTime(0l, 22, 1076);

        HTime t2 = t0.add(t1);
        assertEquals(1l, t2.getDay());
        assertEquals(16, t2.getHour());
        assertEquals(15, t2.getPart());
    }

    @Test
    void add2()
    {
        HTime t0 = new HTime(10000l, 17,19);
        HTime t1 = new HTime(10000l, 22,1076);

        HTime t2 = t0.add(t1);
        assertEquals(20001l, t2.getDay());
        assertEquals(16, t2.getHour());
        assertEquals(15, t2.getPart());
    }

    @Test
    void subtract1()
    {
        HTime t0 = new HTime(10000l, 17,19);
        HTime t1 = new HTime(10000l, 22,1076);

        HTime t2 = t1.subtract(t0);
        assertEquals(0, t2.getDay());
        assertEquals(5, t2.getHour());
        assertEquals(1057, t2.getPart());

    }

    @Test
    void subtract2()
    {
        HTime t0 = new HTime(10000l, 22,1076);
        HTime t1 = new HTime(10001l, 17,19);

        HTime t2 = t1.subtract(t0);
        assertEquals(0l, t2.getDay());
        assertEquals(18, t2.getHour());
        assertEquals(23, t2.getPart());

    }

    @Test
    void subtract3()
    {
        HTime t0 = new HTime(10000l, 22,1076);
        assertEquals(new HTime(0,0,0), t0.subtract(t0));
    }

    @Test
    void times1()
    {
        HTime t0 = new HTime(10000l, 22,1076);
        assertEquals(new HTime(20001,21,1072), t0.times(2));
        assertEquals(new HTime(220021,1,992), t0.times(22));
    }

    @Test
    void times2()
    {
        HTime t0 = new HTime(0, 0,1076);
        assertEquals(new HTime(0,1,1072), t0.times(2));
        assertEquals(new HTime(0,2,1068), t0.times(3));
    }

}