package net.hebrewcalendar;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Compare this library's date conversion against opentorah's.
 *
 * <p>The fixture is every Jewish month opentorah reckons over two centuries --
 * its length and the Gregorian date it begins on. That pins every day in the
 * range: a month that starts a day late, or runs a day long, shows up here.
 *
 * <p>To regenerate it after opentorah changes:
 * {@code testsrc/oracle/export_dates.sh <opentorah checkout>
 * testsrc/resources/opentorah-dates.tsv}.
 */
public class DatesAgainstOpentorahTest {

    private static final String FIXTURE = "/resources/opentorah-dates.tsv";

    /**
     * Their month names, in our numbering: Nisan is 1, and a leap year's two
     * Adars are 12 and 13 while a plain year's single Adar is 12.
     */
    private static final Map<String, Integer> MONTH_NUMBER = new HashMap<>();
    static {
        MONTH_NUMBER.put("Nisan", 1);
        MONTH_NUMBER.put("Iyar", 2);
        MONTH_NUMBER.put("Sivan", 3);
        MONTH_NUMBER.put("Tammuz", 4);
        MONTH_NUMBER.put("Av", 5);
        MONTH_NUMBER.put("Elul", 6);
        MONTH_NUMBER.put("Tishrei", 7);
        MONTH_NUMBER.put("Marheshvan", 8);
        MONTH_NUMBER.put("Kislev", 9);
        MONTH_NUMBER.put("Teves", 10);
        MONTH_NUMBER.put("Shvat", 11);
        MONTH_NUMBER.put("Adar", 12);
        MONTH_NUMBER.put("AdarI", 12);
        MONTH_NUMBER.put("AdarII", 13);
    }

    /** One expected month: where it starts, and how long it runs. */
    private static final class Month {
        final int year, number, length;
        final String name;
        final LocalDate firstDay;
        Month(int year, int number, String name, int length, LocalDate firstDay) {
            this.year = year; this.number = number; this.name = name;
            this.length = length; this.firstDay = firstDay;
        }
        @Override public String toString() { return name + " " + year; }
    }

    private static List<Month> load() throws Exception {
        List<Month> months = new ArrayList<>();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(
                DatesAgainstOpentorahTest.class.getResourceAsStream(FIXTURE),
                StandardCharsets.UTF_8))) {
            String line;
            while ((line = in.readLine()) != null) {
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] f = line.split("\t");
                if (f.length != 5 || !f[0].equals("M")) fail("unexpected line: " + line);
                Integer number = MONTH_NUMBER.get(f[2]);
                if (number == null) fail("unknown month name: " + f[2]);
                months.add(new Month(Integer.parseInt(f[1]), number, f[2],
                        Integer.parseInt(f[3]), LocalDate.parse(f[4])));
            }
        }
        return months;
    }

    private static String greg(IDate<?> date) {
        return String.format("%04d-%02d-%02d", date.getYear(), date.getMonth(), date.getDay());
    }

    @Test
    public void monthsBeginAndEndWhereOpentorahSaysTheyDo() throws Exception {
        List<Month> months = load();
        assertTrue("fixture is empty", months.size() > 2000);
        List<String> wrong = new ArrayList<>();
        for (Month m : months) {
            IDate<?> first =
                ICalendar.GREGORIAN.convert(ICalendar.JEWISH.fromYMD(m.year, m.number, 1));
            if (!greg(first).equals(m.firstDay.toString())) {
                wrong.add(m + " begins " + greg(first) + ", opentorah says " + m.firstDay);
            }
            int length = ICalendar.JEWISH.monthLength(m.year, m.number);
            if (length != m.length) {
                wrong.add(m + " is " + length + " days, opentorah says " + m.length);
            }
        }
        report(wrong);
    }

    @Test
    public void everyDayConvertsBothWays() throws Exception {
        List<String> wrong = new ArrayList<>();
        int days = 0;
        for (Month m : load()) {
            LocalDate expected = m.firstDay;
            for (int day = 1; day <= m.length; day++, expected = expected.plusDays(1), days++) {
                IDate<?> g = ICalendar.GREGORIAN.fromYMD(
                        expected.getYear(), expected.getMonthValue(), expected.getDayOfMonth());
                IDate<JewishCalendar> h = ICalendar.JEWISH.convert(g);
                if (h.getYear() != m.year || h.getMonth() != m.number || h.getDay() != day) {
                    wrong.add(expected + " is " + h.getYear() + "-" + h.getMonth() + "-" + h.getDay()
                            + ", opentorah says " + m.year + "-" + m.number + "-" + day
                            + " (" + m.name + ")");
                } else if (!greg(ICalendar.GREGORIAN.convert(
                        ICalendar.JEWISH.fromYMD(m.year, m.number, day))).equals(expected.toString())) {
                    wrong.add(m.name + " " + day + ", " + m.year + " converts back to "
                            + greg(ICalendar.GREGORIAN.convert(
                                ICalendar.JEWISH.fromYMD(m.year, m.number, day)))
                            + ", opentorah says " + expected);
                }
                if (wrong.size() > 40) break;
            }
        }
        assertTrue("too few days checked: " + days, days > 70000);
        report(wrong);
    }

    @Test
    public void yearsHaveTheMonthsOpentorahGivesThem() throws Exception {
        Map<Integer, Integer> counted = new HashMap<>();
        for (Month m : load()) counted.merge(m.year, 1, Integer::sum);
        List<String> wrong = new ArrayList<>();
        for (Map.Entry<Integer, Integer> e : counted.entrySet()) {
            int year = e.getKey();
            int ours = ICalendar.JEWISH.monthsInYear(year);
            if (ours != e.getValue()) {
                wrong.add(year + " has " + ours + " months, opentorah says " + e.getValue());
            }
            boolean leap = ICalendar.JEWISH.isLeap(year);
            if (leap != (e.getValue() == 13)) {
                wrong.add(year + " leap=" + leap + ", opentorah gives it " + e.getValue() + " months");
            }
        }
        report(wrong);
    }

    private static void report(List<String> wrong) {
        if (wrong.isEmpty()) return;
        StringBuilder sb = new StringBuilder(wrong.size() + " disagreement(s) with opentorah:");
        for (String w : wrong.subList(0, Math.min(20, wrong.size()))) sb.append("\n  ").append(w);
        if (wrong.size() > 20) sb.append("\n  ... and ").append(wrong.size() - 20).append(" more");
        fail(sb.toString());
    }
}
