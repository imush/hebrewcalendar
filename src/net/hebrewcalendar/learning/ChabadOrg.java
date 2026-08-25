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
        return dailyStudyUrl(page, date, extraParams, "en");
    }

    /**
     * Locale-aware variant. chabad.org serves the same daily-study path
     * from {@code he.chabad.org}, {@code ru.chabad.org} and
     * {@code fr.chabad.org} for Hebrew, Russian and French readers.
     * Any other language falls back to {@code www.chabad.org}.
     */
    static String dailyStudyUrl(String page, LocalDate date, String extraParams, String lang) {
        String subdomain;
        String l = lang == null ? "en" : lang;
        if      ("he".equals(l)) subdomain = "he";
        else if ("ru".equals(l)) subdomain = "ru";
        else if ("fr".equals(l)) subdomain = "fr";
        else                     subdomain = "www";
        StringBuilder sb = new StringBuilder(80);
        sb.append("https://").append(subdomain).append(".chabad.org/dailystudy/").append(page)
          .append("?tdate=").append(date.getMonthValue())
          .append('/').append(date.getDayOfMonth())
          .append('/').append(date.getYear());
        if (extraParams != null && !extraParams.isEmpty()) sb.append('&').append(extraParams);
        return sb.toString();
    }
}
