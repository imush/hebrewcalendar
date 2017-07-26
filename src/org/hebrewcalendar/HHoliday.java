package org.hebrewcalendar;

public interface HHoliday
{
    String getName();

    HDate getNextOccurrenceOnOrAfter(HDate date);

    HCalendar getCalendar();
}
