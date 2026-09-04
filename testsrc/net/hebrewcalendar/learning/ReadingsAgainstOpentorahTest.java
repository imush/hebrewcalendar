package net.hebrewcalendar.learning;

import net.hebrewcalendar.ICalendar;
import net.hebrewcalendar.IDate;
import net.hebrewcalendar.JewishCalendar;
import net.hebrewcalendar.data.Custom;
import net.hebrewcalendar.data.Haftarot;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Compare what this library reads against what opentorah reads.
 *
 * <p>The fixture is opentorah's own answer for every distinct reading situation
 * that occurs in 5780-5860, in both locations — keyed by the combination that
 * determines the reading rather than by date, since combinations are where the
 * bugs are, and dated so this test can ask for the same day without rebuilding
 * the key.
 *
 * <p>Regenerate by running the exporter against an opentorah checkout; see the
 * header of the fixture for the commit it came from.
  *
 * <p>To regenerate the fixture after opentorah changes:
 * {@code testsrc/oracle/export_readings.sh <opentorah checkout>
 * testsrc/resources/opentorah-readings.tsv}. The exporter it runs is a
 * Scala test only because reaching opentorah's schedule needs their
 * classpath; the script borrows their checkout and leaves it as found.
*/
public class ReadingsAgainstOpentorahTest {

    private static final String FIXTURE = "/resources/opentorah-readings.tsv";

    /** One expected value: what these customs read, of this kind, that day. */
    private static final class Row {
        final String date, situation, customs, kind, value;
        Row(String date, String situation, String customs, String kind, String value) {
            this.date = date; this.situation = situation; this.customs = customs;
            this.kind = kind; this.value = value;
        }
        String date() { return date; }
        String situation() { return situation; }
        String customs() { return customs; }
        String kind() { return kind; }
        String value() { return value; }
    }

    private static List<Row> load() throws Exception {
        List<Row> rows = new ArrayList<>();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(
                ReadingsAgainstOpentorahTest.class.getResourceAsStream(FIXTURE),
                StandardCharsets.UTF_8))) {
            String line;
            while ((line = r.readLine()) != null) {
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] f = line.split("\t", 5);
                if (f.length == 5) rows.add(new Row(f[0], f[1], f[2], f[3], f[4]));
            }
        }
        return rows;
    }

    /** The fixture dates are Gregorian, so there is nothing to translate. */
    private static LocalDate gregorian(String date) {
        return LocalDate.parse(date);
    }

    private static String render(List<Haftarot.Reference> refs) {
        if (refs == null || refs.isEmpty()) return "-";
        StringBuilder sb = new StringBuilder();
        for (Haftarot.Reference r : refs) {
            if (sb.length() > 0) sb.append(' ');
            sb.append(r.book).append(':').append(r.fromCh).append(':').append(r.fromV)
              .append(':').append(r.toCh).append(':').append(r.toV);
        }
        return sb.toString();
    }

    /**
     * opentorah's enum renders the paired books as SamuelI/KingsI; the data
     * spells them as they are cited. Not a disagreement, so normalise it away
     * rather than let it drown the real ones.
     */
    /**
     * opentorah names a book by its Scala case, "SamuelI", and the exporter
     * writes that through unchanged. This library names the paired books
     * "Samuel I" -- the order a reader looking one up would say, and the order
     * every citation resolves by -- so the two are reconciled here, in the one
     * place where opentorah's spelling meets ours.
     */
    private static String normalizeBooks(String value) {
        return value.replace("SamuelI:", "Samuel I:").replace("SamuelII:", "Samuel II:")
                    .replace("KingsI:", "Kings I:").replace("KingsII:", "Kings II:")
                    .replace("ChroniclesI:", "Chronicles I:").replace("ChroniclesII:", "Chronicles II:");
    }

    private static List<Custom> customsOf(String field) {
        List<Custom> out = new ArrayList<>();
        if ("ALL".equals(field)) {
            java.util.Collections.addAll(out, Custom.values());
            return out;
        }
        for (String name : field.split("\\+")) {
            // opentorah spells them Ashkenaz, ChayeyOdom, PureSephardim
            String key = name.replaceAll("(?<=[a-z])(?=[A-Z])", "_").toUpperCase();
            out.add(Custom.valueOf(key));
        }
        return out;
    }

    /**
     * Haftarot only, for now: this library has no Torah readings, so the torah
     * and maftir rows of the fixture have nothing to compare against yet. What
     * this reports is the list of special readings that are wrong.
     */
    /** Days whose only "special" name is the Omer count read as an ordinary
     *  Shabbat: the Omer changes nothing about what is read. */
    private static final java.util.regex.Pattern ONLY_OMER =
            java.util.regex.Pattern.compile("Omer\\(\\d+\\)");

    private static boolean isAfternoon(Haftarah.Occasion o) {
        return o == Haftarah.Occasion.FAST_AFTERNOON
            || o == Haftarah.Occasion.TISHA_BAV_AFTERNOON
            || o == Haftarah.Occasion.YOM_KIPPUR_AFTERNOON;
    }

    /**
     * Weekdays and afternoons, which forDate cannot answer: it reports what is
     * read on the coming Shabbat, so on a Tuesday it describes Saturday. forDay
     * gives the day's own readings, tagged by occasion, so morning and
     * afternoon can be told apart.
     */
    @Test
    public void weekdayAndAfternoonHaftarotMatchOpentorah() throws Exception {
        Map<String, String> bySituation = new TreeMap<>();
        int checked = 0, wrong = 0;

        for (Row row : load()) {
            if (!"haftarah".equals(row.kind())) continue;
            boolean afternoon = row.situation().endsWith("|afternoon");
            boolean shabbat = row.situation().contains("|shabbos|");
            if (shabbat && !afternoon) continue;   // covered by the other test
            boolean inIsrael = row.situation().startsWith("EY|");
            LocalDate date = gregorian(row.date());

            for (Custom custom : customsOf(row.customs())) {
                checked++;
                String got = "-";
                try {
                    for (Haftarah.Result r : Haftarah.forDay(date, custom, inIsrael)) {
                        // an occasion that applies but is read by nobody in this
                        // custom renders as "-", the same as no occasion at all
                        if (isAfternoon(r.occasion) == afternoon) {
                            got = r.refs.isEmpty() ? "-" : render(r.refs);
                            break;
                        }
                    }
                } catch (RuntimeException e) {
                    got = "threw " + e.getClass().getSimpleName();
                }
                if (!got.equals(normalizeBooks(row.value()))) {
                    wrong++;
                    String prior = bySituation.get(row.situation());
                    String line = "      " + custom + ": expected "
                            + normalizeBooks(row.value()) + ", got " + got;
                    bySituation.put(row.situation(), prior == null ? line : prior + "\n" + line);
                }
            }
        }

        if (!bySituation.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append(bySituation.size()).append(" weekday/afternoon situations disagree (")
              .append(wrong).append(" of ").append(checked).append("):\n");
            int shown = 0;
            for (Map.Entry<String, String> e : bySituation.entrySet()) {
                if (shown++ == 25) { sb.append("  ... and ").append(bySituation.size() - 25).append(" more\n"); break; }
                sb.append("  ").append(e.getKey()).append('\n').append(e.getValue()).append('\n');
            }
            fail(sb.toString());
        }
    }

    private static String renderTorah(TorahReading.Result r) {
        if (r == null) return "-";
        StringBuilder sb = new StringBuilder();
        int n = 1;
        for (TorahReading.Span s : r.aliyot) {
            if (sb.length() > 0) sb.append(' ');
            sb.append(n++).append(':').append(s.book).append(':').append(s.fromCh)
              .append(':').append(s.fromV).append(':').append(s.toCh).append(':').append(s.toV);
        }
        return sb.length() == 0 ? "-" : sb.toString();
    }

    private static String renderMaftir(TorahReading.Result r) {
        if (r == null || r.maftir == null) return "-";
        TorahReading.Span s = r.maftir;
        return s.book + ":" + s.fromCh + ":" + s.fromV + ":" + s.toCh + ":" + s.toV;
    }

    /**
     * Every Torah reading opentorah records: the seven aliyot and the maftir
     * of a Shabbat morning, the five or six of a festival, the four of Chol
     * HaMoed and of Rosh Chodesh, the three of a Monday, a Thursday, a fast
     * and a Shabbat Mincha -- for all 26 customs, in both lands, over 81
     * years.
     */
    @Test
    public void torahReadingsMatchOpentorah() throws Exception {
        Map<String, String> bySituation = new TreeMap<>();
        int checked = 0, wrong = 0;

        for (Row row : load()) {
            boolean torah = "torah".equals(row.kind()), maftir = "maftir".equals(row.kind());
            if (!torah && !maftir) continue;
            String[] parts = row.situation().split("\\|");
            TorahReading.Slot slot =
                    "afternoon".equals(parts[4]) ? TorahReading.Slot.AFTERNOON
                  : "evening".equals(parts[4])   ? TorahReading.Slot.EVENING
                  :                                TorahReading.Slot.MORNING;
            boolean inIsrael = row.situation().startsWith("EY|");
            LocalDate date = gregorian(row.date());

            for (Custom custom : customsOf(row.customs())) {
                checked++;
                String got;
                try {
                    TorahReading.Result found = null;
                    for (TorahReading.Result r : TorahReading.forDay(date, custom, inIsrael))
                        if (r.slot == slot) found = r;
                    got = torah ? renderTorah(found) : renderMaftir(found);
                } catch (RuntimeException e) {
                    got = "threw " + e.getClass().getSimpleName() + ": " + e.getMessage();
                }
                if (!got.equals(row.value())) {
                    wrong++;
                    String key = row.situation() + " " + row.kind();
                    String prior = bySituation.get(key);
                    String line = "      " + custom + ": expected " + row.value() + ", got " + got;
                    bySituation.put(key, prior == null ? line : prior + "\n" + line);
                }
            }
        }

        assertTrue("no torah rows were checked", checked > 0);
        if (!bySituation.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append(bySituation.size()).append(" torah situations disagree (")
              .append(wrong).append(" of ").append(checked).append("):\n");
            int shown = 0;
            for (Map.Entry<String, String> e : bySituation.entrySet()) {
                if (shown++ == 10) { sb.append("  ... and ").append(bySituation.size() - 10).append(" more\n"); break; }
                sb.append("  ").append(e.getKey()).append('\n')
                  .append(e.getValue().split("\n")[0]).append('\n');
            }
            fail(sb.toString());
        }
    }

    @Test
    public void haftarotMatchOpentorah() throws Exception {
        Map<String, String> bySituation = new TreeMap<>();  // situation -> first mismatch
        int checked = 0, wrong = 0;

        for (Row row : load()) {
            if (!"haftarah".equals(row.kind())) continue;
            boolean inIsrael = row.situation().startsWith("EY|");
            // forDate answers "what is read on the coming Shabbat", so asking it
            // about a weekday gets tomorrow's answer, not today's. Compare the
            // Shabbat mornings, where the question it answers is the one asked;
            // weekdays and afternoons need forDay and a different comparison.
            if (!row.situation().contains("|shabbos|")) continue;
            if (!row.situation().endsWith("|morning")) continue;
            LocalDate date = gregorian(row.date());

            for (Custom custom : customsOf(row.customs())) {
                checked++;
                String got;
                try {
                    Haftarah.Result r = Haftarah.forDate(date, custom, inIsrael);
                    got = r == null ? "-" : render(r.refs);
                } catch (RuntimeException e) {
                    got = "threw " + e.getClass().getSimpleName();
                }
                if (!got.equals(normalizeBooks(row.value()))) {
                    wrong++;
                    // every custom that disagrees, not just the first: reading
                    // one custom as though it were the whole situation is how
                    // two separate defects looked like two, when they are one
                    String prior = bySituation.get(row.situation());
                    String line = "      " + custom + ": expected "
                            + normalizeBooks(row.value()) + ", got " + got;
                    bySituation.put(row.situation(), prior == null ? line : prior + "\n" + line);
                }
            }
        }

        if (!bySituation.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append(bySituation.size()).append(" situations disagree with opentorah (")
              .append(wrong).append(" of ").append(checked).append(" custom-readings):\n");
            for (Map.Entry<String, String> e : bySituation.entrySet()) {
                sb.append("  ").append(e.getKey()).append('\n').append(e.getValue()).append('\n');
            }
            fail(sb.toString());
        }
    }
}
