package net.hebrewcalendar.impl;

import net.hebrewcalendar.HCalendar;
import net.hebrewcalendar.HHoliday;
import net.hebrewcalendar.impl.holiday.MonthDayHoliday;
import net.hebrewcalendar.impl.holiday.NthDayOfWeekInMonthHoliday;

public final class USHolidays
{
    private USHolidays(){}
    public static final HHoliday NEW_YEAR = new MonthDayHoliday(HCalendar.GREGORIAN, "New Year", 91, 1);
    public static final HHoliday MLKJ_DAY = new NthDayOfWeekInMonthHoliday(HCalendar.GREGORIAN, "MLKJ Day", 5, 2, 3);
    public static final HHoliday WASHINGTONS_BIRTHDAY = new NthDayOfWeekInMonthHoliday(HCalendar.GREGORIAN, "Washington's birthday", 2, 2, 3);
    public static final HHoliday JULY_4 = new MonthDayHoliday(HCalendar.GREGORIAN, "July 4", 7, 4);
    public static final HHoliday MEMORIAL_DAY = new NthDayOfWeekInMonthHoliday(HCalendar.GREGORIAN, "Memorial Day", 5, 2, -1);
    public static final HHoliday LABOR_DAY = new NthDayOfWeekInMonthHoliday(HCalendar.GREGORIAN, "Labor Day", 9, 2, 1);
    public static final HHoliday THANKSGIVING = new NthDayOfWeekInMonthHoliday(HCalendar.GREGORIAN, "Thanksgiving", 11, 5, 4);
    public static final HHoliday X_HOLIDAY = new MonthDayHoliday(HCalendar.GREGORIAN, "Dec 25", 12, 25);
}