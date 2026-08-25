package net.hebrewcalendar.learning;

/**
 * Number → Hebrew numeral conversion (gematria), formatted to match
 * sefaria.org's rendering conventions:
 * <ul>
 *   <li>single-letter numerals get a geresh (׳): {@code 5 → "ה׳"};</li>
 *   <li>multi-letter numerals get a gershayim (״) between the last two letters:
 *       {@code 24 → "כ״ד"}, {@code 115 → "קט״ו"};</li>
 *   <li>{@code 15 → "ט״ו"} and {@code 16 → "ט״ז"} to avoid spelling out
 *       divine names.</li>
 * </ul>
 */
public final class Gematria {

    private Gematria() {}

    private static final char GERESH    = '׳';  // ׳
    private static final char GERSHAYIM = '״';  // ״

    /** Convert an integer 1..999 (or higher, using ת=400) into its Hebrew numeral. */
    public static String of(int n) {
        if (n < 1) throw new IllegalArgumentException("gematria of non-positive: " + n);
        StringBuilder sb = new StringBuilder();
        int rem = n;
        while (rem >= 400) { sb.append('ת'); rem -= 400; }
        if (rem >= 100) {
            int h = rem / 100;
            sb.append("קרש".charAt(h - 1));
            rem %= 100;
        }
        // 15/16 as single "unit" to avoid י־ה / י־ו
        if (rem == 15) { sb.append("טו"); rem = 0; }
        else if (rem == 16) { sb.append("טז"); rem = 0; }
        if (rem >= 10) {
            int t = rem / 10;
            sb.append("יכלמנסעפצ".charAt(t - 1));
            rem %= 10;
        }
        if (rem > 0) sb.append("אבגדהוזחט".charAt(rem - 1));
        return addPunctuation(sb);
    }

    /**
     * Render a perek string that may contain dashes ({@code "8-9"}) or
     * colons ({@code "1:1-4:8"}) by gematria-fying each numeric run and
     * preserving separators. Non-numeric non-separator input passes through
     * unchanged. Useful for {@link Rambam.Reading#perek()} strings.
     */
    public static String verseRange(String perek) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < perek.length()) {
            char c = perek.charAt(i);
            if (c >= '0' && c <= '9') {
                int j = i;
                while (j < perek.length()
                        && perek.charAt(j) >= '0' && perek.charAt(j) <= '9') j++;
                sb.append(of(Integer.parseInt(perek.substring(i, j))));
                i = j;
            } else {
                sb.append(c);
                i++;
            }
        }
        return sb.toString();
    }

    private static String addPunctuation(StringBuilder sb) {
        if (sb.length() == 0) return "";
        if (sb.length() == 1) { sb.append(GERESH); return sb.toString(); }
        return sb.substring(0, sb.length() - 1) + GERSHAYIM + sb.charAt(sb.length() - 1);
    }
}
