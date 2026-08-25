package net.hebrewcalendar.learning;

import java.time.LocalDate;

/** URL builder for chabad.org daily-study pages, all of which accept the
 *  same {@code ?tdate=M/D/YYYY} parameter. */
final class ChabadOrg {

    private ChabadOrg() {}

    /**
     * @param page          the daily-study page basename, e.g. {@code "tanya.asp"}
     * @param date          Gregorian date
     * @param extraParams   additional query string (no leading &), or {@code null}
     * @return the full URL, e.g.
     *   {@code https://www.chabad.org/dailystudy/tanya.asp?tdate=8/23/2026}
     */
    static String dailyStudyUrl(String page, LocalDate date, String extraParams) {
        StringBuilder sb = new StringBuilder(80);
        sb.append("https://www.chabad.org/dailystudy/").append(page)
          .append("?tdate=").append(date.getMonthValue())
          .append('/').append(date.getDayOfMonth())
          .append('/').append(date.getYear());
        if (extraParams != null && !extraParams.isEmpty()) sb.append('&').append(extraParams);
        return sb.toString();
    }
}
