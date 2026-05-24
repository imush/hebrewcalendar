package net.hebrewcalendar;

import net.hebrewcalendar.impl.NoSuchHolidayException;
import org.junit.Test;

import static org.junit.Assert.*;

public class BirkatHachamaTest {

    private static final ICalendar GREG   = ICalendar.GREGORIAN;
    private static final ICalendar JEWISH = ICalendar.JEWISH;

    // Birkat HaChama 5769 = Wednesday April 8, 2009 (14 Nisan 5769)
    // Birkat HaChama 5797 = April 8, 2037 (next cycle, 28 years later)

    @Test
    public void matchesKnownOccurrence() {
        IDate apr8_2009 = GREG.fromYMD(2009, 4, 8);
        assertTrue("April 8 2009 should be Birkat HaChama",
            JewishSpecialDay.BIRKAT_HACHAMA.matches(apr8_2009));
    }

    @Test
    public void doesNotMatchDayBefore() {
        IDate apr7_2009 = GREG.fromYMD(2009, 4, 7);
        assertFalse("April 7 2009 should not be Birkat HaChama",
            JewishSpecialDay.BIRKAT_HACHAMA.matches(apr7_2009));
    }

    @Test
    public void doesNotMatchDayAfter() {
        IDate apr9_2009 = GREG.fromYMD(2009, 4, 9);
        assertFalse("April 9 2009 should not be Birkat HaChama",
            JewishSpecialDay.BIRKAT_HACHAMA.matches(apr9_2009));
    }

    @Test
    public void yearModulo() {
        // 5769 % 28 == 1
        assertEquals(1, 5769 % 28);
        // 5797 % 28 == 1
        assertEquals(1, 5797 % 28);
    }

    @Test
    public void getNextOccurrenceFromBeforeEvent() throws NoSuchHolidayException {
        IDate before = GREG.fromYMD(2009, 1, 1);
        IDate next   = JewishSpecialDay.BIRKAT_HACHAMA.getNextOccurrence(before, false);
        assertEquals(GREG.fromYMD(2009, 4, 8), GREG.convert(next));
    }

    @Test
    public void getNextOccurrenceStrict_onEventDay() throws NoSuchHolidayException {
        IDate onDay = GREG.fromYMD(2009, 4, 8);
        IDate next  = JewishSpecialDay.BIRKAT_HACHAMA.getNextOccurrence(onDay, true);
        // strict=true: must be strictly after Apr 8 2009 → next cycle in 5797
        IDate nextCycleDay = GREG.convert(next);
        IDate hebrewNext   = JEWISH.convert(next);
        assertEquals(1, hebrewNext.getYear() % 28);  // still a BH year
        assertTrue("Next strict occurrence must be after 2009-04-08",
            next.after(onDay));
    }

    @Test
    public void getPrevOccurrenceFromAfterEvent() throws NoSuchHolidayException {
        IDate after = GREG.fromYMD(2009, 12, 31);
        IDate prev  = JewishSpecialDay.BIRKAT_HACHAMA.getPrevOccurrence(after, false);
        assertEquals(GREG.fromYMD(2009, 4, 8), GREG.convert(prev));
    }

    @Test
    public void getPrevOccurrenceStrict_onEventDay() throws NoSuchHolidayException {
        IDate onDay = GREG.fromYMD(2009, 4, 8);
        IDate prev  = JewishSpecialDay.BIRKAT_HACHAMA.getPrevOccurrence(onDay, true);
        // strict=true: must be strictly before Apr 8 2009 → previous cycle, 28 years earlier (5741)
        assertTrue("Prev strict occurrence must be before 2009-04-08",
            prev.before(onDay));
        IDate hebrewPrev = JEWISH.convert(prev);
        assertEquals(1, hebrewPrev.getYear() % 28);
    }

    @Test
    public void prevAndNextAre28YearsFromAnchor() throws NoSuchHolidayException {
        IDate onDay  = GREG.fromYMD(2009, 4, 8);
        int   anchor = JEWISH.convert(onDay).getYear(); // 5769
        IDate prev   = JewishSpecialDay.BIRKAT_HACHAMA.getPrevOccurrence(onDay, true);
        IDate next   = JewishSpecialDay.BIRKAT_HACHAMA.getNextOccurrence(onDay, true);
        assertEquals(28, anchor - JEWISH.convert(prev).getYear()); // 5769 - 5741 = 28
        assertEquals(28, JEWISH.convert(next).getYear() - anchor); // 5797 - 5769 = 28
    }
}
