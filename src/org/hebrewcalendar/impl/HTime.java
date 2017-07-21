package org.hebrewcalendar.impl;

/**
 * Created by itz on 7/20/17.
 */
final class HTime
{
    private long _day;
    private int _hour;
    private int _part;

    HTime(long day, int hour, int part)
    {
        _day = day;
        _hour = hour;
        _part = part;
    }

    private HTime normalize(final long day, final int hour, final int part)
    {
        long d = day;
        int h = hour;
        int p = part;
        if (p > 1079) {
            h += p/1080;
            p %= 1080;
        } else if (p < 0) {
            h = h - Math.abs(p)/1080 - 1;
            p = 1080 - Math.abs(p)%1080;
        }

        if (h > 23) {
            d += h/24;
            h %= 24;
        } else if (h < 0) {
            d = d - Math.abs(h)/24 -1;
            h = 24 - Math.abs(h) %24;
        }
        return new HTime(d, h, p);
    }

    HTime add(HTime toAdd)
    {
        return normalize(_day + toAdd._day, _hour + toAdd._hour, _part + toAdd._part);
    }

    HTime subtract(HTime toSubtract)
    {

        return normalize(_day - toSubtract._day, _hour - toSubtract._hour, _part - toSubtract._part);
    }

    HTime times(int n)
    {
        return normalize(_day*n, _hour*n, _part*n);
    }

    public long getDay() {
        return _day;
    }

    public int getHour() {
        return _hour;
    }

    public int getPart() {
        return _part;
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof HTime))
            return false;
        HTime htime = (HTime)o;
        return htime._day == _day && htime._hour == _hour && htime._part == _part;
    }

    @Override
    public String toString()
    {
        return "HTime[d=" + _day+
                ",h=" + _hour +
                ",p=" + _part + "]";
    }
}
