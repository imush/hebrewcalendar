package org.hebrewcalendar.impl;

/**
 * Created by itz on 7/20/17.
 */
public class GregorianDate
        extends CommonDate<GregorianDate>
{
    public GregorianDate(int year, int month, int day)
    {
        super(GregorianCalendar.INSTANCE, year, month, day);
    }

    @Override
    public GregorianDate fromYMD(int year, int month, int day)
    {
        return new GregorianDate(year, month, day);
    }
}
