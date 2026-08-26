package net.hebrewcalendar.learning;

import java.time.LocalDate;

/**
 * Sefer Hamitzvot Yomi (Rambam's Book of Commandments — Daily Study).
 * 339-day cycle. Cycle data comes from {@link net.hebrewcalendar.data.SeferHaMitzvot};
 * only the algorithm + label-formatting lives here.
 */
public final class SeferHaMitzvot {

    private SeferHaMitzvot() {}

    /** Immutable result: day-in-cycle + raw and expanded readings. */
    public static final class Result {
        private final int dayInCycle;
        private final String raw;
        Result(int dayInCycle, String raw) {
            this.dayInCycle = dayInCycle; this.raw = raw;
        }
        public int    dayInCycle() { return dayInCycle; }
        /** Compact form as printed on chabad.org, e.g. {@code "N193, N153, N194, P146"}. */
        public String raw()        { return raw; }
        /**
         * Expanded English form, e.g.
         * {@code "Negative Commandments 193, 153, 194; Positive Commandment 146"}.
         * Prose entries pass through unchanged.
         */
        public String label() {
            return format(raw, "en");
        }

        /**
         * Hebrew expansion: {@code "מצות עשה ע״ג"} for {@code "P73"},
         * {@code "מצות לא תעשה קצ״ג"} for {@code "N193"}, groups repeated
         * types like {@link #label()}. Prose entries pass through in
         * English — they have no standard Hebrew form in this table.
         */
        public String labelHe() { return format(raw, "he"); }
        public String labelRu() { return format(raw, "ru"); }
        public String labelFr() { return format(raw, "fr"); }
        public String label(String lang) {
            switch (lang) {
                case "he": return labelHe();
                case "ru": return labelRu();
                case "fr": return labelFr();
                default:   return label();
            }
        }

        private static String format(String raw, String lang) {
            boolean he = "he".equals(lang);
            String[] parts = raw.split(", ");
            // Fast-path: prose (no leading P/N + digit).
            for (String p : parts) {
                if (p.isEmpty()) continue;
                char c = p.charAt(0);
                if ((c != 'P' && c != 'N') || p.length() < 2
                        || p.charAt(1) < '0' || p.charAt(1) > '9') {
                    return raw;   // prose or mixed — return as-is (English only)
                }
            }
            StringBuilder sb = new StringBuilder();
            char group = 0;   // 'P' or 'N'
            java.util.List<String> bucket = new java.util.ArrayList<>();
            for (int i = 0; i <= parts.length; i++) {
                String p = i < parts.length ? parts[i] : null;
                char c  = p == null ? 0 : p.charAt(0);
                if (c != group) {
                    if (group != 0 && !bucket.isEmpty()) {
                        if (sb.length() > 0) sb.append("; ");
                        String noun;
                        switch (lang) {
                            case "he":
                                noun = group == 'P' ? "מצות עשה" : "מצות לא תעשה";
                                break;
                            case "ru":
                                noun = group == 'P'
                                        ? (bucket.size() > 1 ? "Положительные заповеди" : "Положительная заповедь")
                                        : (bucket.size() > 1 ? "Отрицательные заповеди" : "Отрицательная заповедь");
                                break;
                            case "fr":
                                noun = group == 'P'
                                        ? (bucket.size() > 1 ? "Commandements positifs"   : "Commandement positif")
                                        : (bucket.size() > 1 ? "Commandements négatifs"   : "Commandement négatif");
                                break;
                            default:
                                noun = group == 'P' ? "Positive Commandment" : "Negative Commandment";
                                if (bucket.size() > 1) noun += "s";
                        }
                        String nums = he
                                ? String.join(", ", bucket.stream().map(s -> Gematria.of(Integer.parseInt(s))).toArray(String[]::new))
                                : String.join(", ", bucket);
                        sb.append(noun).append(' ').append(nums);
                        bucket.clear();
                    }
                    group = c;
                }
                if (p != null) bucket.add(p.substring(1));
            }
            return sb.toString();
        }
    }

    /**
     * Reading for the given Gregorian date.
     * @return the reading, or {@code null} for dates before 29 April 1984.
     */
    public static Result forDate(LocalDate date) {
        long abs = date.toEpochDay();
        long epoch = net.hebrewcalendar.data.SeferHaMitzvot.EPOCH.toEpochDay();
        if (abs < epoch) return null;
        int day = (int)((abs - epoch) % net.hebrewcalendar.data.SeferHaMitzvot.CYCLE_DAYS) + 1;
        return new Result(day, net.hebrewcalendar.data.SeferHaMitzvot.READINGS[day - 1]);
    }
}
