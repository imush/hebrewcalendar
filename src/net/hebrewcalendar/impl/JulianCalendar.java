package net.hebrewcalendar.impl;

import net.hebrewcalendar.CalendarType;

public final class JulianCalendar
    extends CommonCalendar<JulianCalendar>
{
    public static final JulianCalendar INSTANCE = new JulianCalendar();

    private JulianCalendar() {}

    @Override
    public final boolean isLeap(final int year)
    {
        return year%4 == 0;
    }

    @Override
    public final CalendarType getType()
    {
        return CalendarType.JULIAN;
    }

    @Override
    final long getStart()
    {
        return 1373427;
    }

}
