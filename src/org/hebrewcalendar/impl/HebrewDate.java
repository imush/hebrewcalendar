package org.hebrewcalendar.impl;

public class HebrewDate
    extends HDateImpl
{
    HebrewDate(int year, int month, int day)
    {
        super(HebrewCalendar.INSTANCE, year, month, day);
    }

    @Override
    public long absDay()
    {
        return 0;
    }


}
