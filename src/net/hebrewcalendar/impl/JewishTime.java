package net.hebrewcalendar.impl;

import net.hebrewcalendar.JewishMoment;

/**
 * Namespace for two distinct types that share the same units.
 *
 * <p>Units: 1 day = 24 hours; 1 hour = 1080 chalakim (parts); 1 chelek = 76 regaim.
 * Regaim are only used for Rav Ada tekufa precision; for ordinary molad calculations _rega is 0.
 *
 * <ul>
 *   <li>{@link Duration} — an amount of time (e.g. one lunar month); supports
 *       {@code add}, {@code subtract}, and {@code times}.</li>
 *   <li>{@link Moment} — an absolute point in time counted from the epoch;
 *       implements {@link JewishMoment} and supports {@code add(Duration)}
 *       and {@code subtract(Duration)}.</li>
 * </ul>
 */
final class JewishTime
{
    private JewishTime() {}

    // ------------------------------------------------------------------
    // Duration
    // ------------------------------------------------------------------

    static final class Duration
    {
        final long _day;
        final int  _hour;
        final int  _part;
        final int  _rega;

        Duration(long day, int hour, int part)
        {
            this(day, hour, part, 0);
        }

        Duration(long day, int hour, int part, int rega)
        {
            _day = day; _hour = hour; _part = part; _rega = rega;
        }

        private static Duration normalize(long day, int hour, int part, int rega)
        {
            long d = day; int h = hour; int p = part; int r = rega;

            if (r >= 76)    { p += r / 76;                              r %= 76; }
            else if (r < 0) { int b = (Math.abs(r) + 75) / 76;  p -= b; r += b * 76; }

            if (p >= 1080)     { h += p / 1080;                             p %= 1080; }
            else if (p < 0)    { int b = (Math.abs(p) + 1079) / 1080; h -= b; p += b * 1080; }

            if (h >= 24)    { d += h / 24;                              h %= 24; }
            else if (h < 0) { int b = (Math.abs(h) + 23) / 24;  d -= b; h += b * 24; }

            return new Duration(d, h, p, r);
        }

        Duration add(Duration toAdd)
        {
            return normalize(_day + toAdd._day, _hour + toAdd._hour, _part + toAdd._part, _rega + toAdd._rega);
        }

        Duration subtract(Duration toSubtract)
        {
            return normalize(_day - toSubtract._day, _hour - toSubtract._hour, _part - toSubtract._part, _rega - toSubtract._rega);
        }

        Duration times(int n)
        {
            return normalize(_day * n, _hour * n, _part * n, _rega * n);
        }

        public long getDay()  { return _day; }
        public int  getHour() { return _hour; }
        public int  getPart() { return _part; }
        public int  getRega() { return _rega; }

        @Override
        public boolean equals(final Object o)
        {
            if (!(o instanceof Duration)) return false;
            final Duration d = (Duration) o;
            return d._day == _day && d._hour == _hour && d._part == _part && d._rega == _rega;
        }

        @Override
        public String toString()
        {
            return "JewishTime.Duration[d=" + _day + ",h=" + _hour + ",p=" + _part + ",r=" + _rega + "]";
        }
    }

    // ------------------------------------------------------------------
    // Moment
    // ------------------------------------------------------------------

    static final class Moment implements JewishMoment
    {
        final long _day;
        final int  _hour;
        final int  _part;
        final int  _rega;

        Moment(long day, int hour, int part)
        {
            this(day, hour, part, 0);
        }

        Moment(long day, int hour, int part, int rega)
        {
            _day = day; _hour = hour; _part = part; _rega = rega;
        }

        private static Moment normalize(long day, int hour, int part, int rega)
        {
            long d = day; int h = hour; int p = part; int r = rega;

            if (r >= 76)    { p += r / 76;                              r %= 76; }
            else if (r < 0) { int b = (Math.abs(r) + 75) / 76;  p -= b; r += b * 76; }

            if (p >= 1080)     { h += p / 1080;                             p %= 1080; }
            else if (p < 0)    { int b = (Math.abs(p) + 1079) / 1080; h -= b; p += b * 1080; }

            if (h >= 24)    { d += h / 24;                              h %= 24; }
            else if (h < 0) { int b = (Math.abs(h) + 23) / 24;  d -= b; h += b * 24; }

            return new Moment(d, h, p, r);
        }

        Moment add(Duration toAdd)
        {
            return normalize(_day + toAdd._day, _hour + toAdd._hour, _part + toAdd._part, _rega + toAdd._rega);
        }

        Moment subtract(Duration toSubtract)
        {
            return normalize(_day - toSubtract._day, _hour - toSubtract._hour, _part - toSubtract._part, _rega - toSubtract._rega);
        }

        @Override public long getDay()  { return _day; }
        @Override public int  getHour() { return _hour; }
        @Override public int  getPart() { return _part; }
        @Override public int  getRega() { return _rega; }

        @Override
        public boolean equals(final Object o)
        {
            if (!(o instanceof Moment)) return false;
            final Moment m = (Moment) o;
            return m._day == _day && m._hour == _hour && m._part == _part && m._rega == _rega;
        }

        @Override
        public String toString()
        {
            return "JewishTime.Moment[d=" + _day + ",h=" + _hour + ",p=" + _part + ",r=" + _rega + "]";
        }
    }
}
