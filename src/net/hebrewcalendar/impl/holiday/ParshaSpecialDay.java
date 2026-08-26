package net.hebrewcalendar.impl.holiday;

import net.hebrewcalendar.ICalendar;
import net.hebrewcalendar.IDate;
import net.hebrewcalendar.JewishCalendar;
import net.hebrewcalendar.data.Parsha;

import java.util.List;

/**
 * A special day that occurs on the Shabbat when a given parsha is read. Used
 * for named Shabbats that are tied to a Torah reading rather than to a date
 * (e.g. Shabbat Shirah — the Shabbat of Parashat Beshalach).
 *
 * <p>Matches when the given date is a Shabbat AND the parsha (in either the
 * Diaspora or Eretz Yisrael reading cycle) contains the target parsha. The
 * two cycles only differ during Yom Tov-adjacent weeks; the check accepts a
 * match in either one so this class works regardless of the caller's
 * {@code inIsrael} setting.</p>
 */
public class ParshaSpecialDay
    extends AbstractRecurringSpecialDay<JewishCalendar>
{
    private final Parsha _parsha;

    public ParshaSpecialDay(final String name, final Parsha parsha)
    {
        super(ICalendar.JEWISH, name);
        _parsha = parsha;
    }

    @Override
    public boolean matches(final IDate<JewishCalendar> date)
    {
        if (date.getDayOfWeek() != 7) return false;
        final JewishCalendar cal = getCalendar();
        final List<Parsha> diaspora = cal.getParsha(date, false);
        if (diaspora.contains(_parsha)) return true;
        final List<Parsha> israel = cal.getParsha(date, true);
        return israel.contains(_parsha);
    }
}
