package org.hebrewcalendar.impl;

public class HebrewDate
    extends HDateImpl
{
    HebrewDate(int year, int month, int day)
    {
        super(JewishCalendar.INSTANCE, year, month, day);
    }

    @Override
    public long getAbsDay()
    {
        return 0;
    }


}
