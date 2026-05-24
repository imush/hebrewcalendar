/**
 * Java library for Jewish calendar computations.
 *
 * <p>The three calendar systems are available as constants on {@link ICalendar}:
 * <ul>
 *   <li>{@link ICalendar#GREGORIAN} — proleptic Gregorian calendar</li>
 *   <li>{@link ICalendar#JULIAN} — Julian calendar</li>
 *   <li>{@link ICalendar#JEWISH} — Hebrew calendar ({@link JewishCalendar})</li>
 * </ul>
 *
 * <h2>Convert a date between calendars</h2>
 * <pre>{@code
 * ICalendar greg   = ICalendar.GREGORIAN;
 * JewishCalendar hebrew = ICalendar.JEWISH;
 *
 * // May 16, 2026 → Hebrew
 * IDate gDate  = greg.fromYMD(2026, 5, 16);
 * IDate hDate  = hebrew.convert(gDate);
 * System.out.printf("%d %s %d%n",
 *         hDate.getDay(),
 *         JewishMonth.get(hDate.getMonth()).name(),   // SIVAN
 *         hDate.getYear());                            // 5786
 *
 * // 1 Sivan 5786 → Gregorian
 * IDate hDate2 = hebrew.fromYMD(5786, JewishMonth.SIVAN.getNum(), 1);
 * IDate gDate2 = greg.convert(hDate2);
 * System.out.printf("%d/%d/%d%n",
 *         gDate2.getYear(), gDate2.getMonth(), gDate2.getDay()); // 2026/5/17
 * }</pre>
 *
 * <h2>Compute zmanim (halachic times)</h2>
 * <pre>{@code
 * Location nyc = new Location(
 *         40.6501, -73.9496, 0.0,
 *         "America/New_York",
 *         false,   // not in Israel
 *         false);  // not Jerusalem custom
 *
 * Zmanim z = new Zmanim(LocalDate.of(2026, 5, 16), nyc);
 *
 * ZonedDateTime sunrise = z.getSunrise().getTime();
 * ZonedDateTime sunset  = z.getSunset().getTime();
 * ZonedDateTime shema   = z.getLatestShema().getTime();
 * ZonedDateTime candles = z.getCandleLightingZman().getTime();
 * System.out.println("Sunrise:        " + sunrise);
 * System.out.println("Latest Shema:   " + shema);
 * System.out.println("Sunset:         " + sunset);
 * System.out.println("Candle lighting:" + candles);
 * }</pre>
 *
 * <h2>Compute tekufa (solar season)</h2>
 * <pre>{@code
 * JewishCalendar hebrew = ICalendar.JEWISH;
 *
 * // Tekufat Nisan 5786 (Shmuel — governs Tal u'Matar and Birkat HaChama)
 * // Note: getTekufatShmuel(Y, TISHREI) returns the tekufa at the start of year Y+1;
 * // pass Y-1 to get the tekufa at the start of year Y.
 * JewishMoment tkf = hebrew.getTekufatShmuel(5786, JewishCalendar.Season.NISAN);
 * IDate hDate = hebrew.fromMoment(tkf);
 * int civilHour = (tkf.getHour() + 18) % 24;   // library hour 0 = 18:00 civil time
 * System.out.printf("Tekufat Nisan 5786: %d/%d/%d at %02d:%02d%n",
 *         hDate.getDay(), hDate.getMonth(), hDate.getYear(),
 *         civilHour, tkf.getPart() / 18);        // chalakim / 18 = minutes
 * }</pre>
 *
 * <h2>Compute molad (new moon)</h2>
 * <pre>{@code
 * JewishCalendar hebrew = ICalendar.JEWISH;
 *
 * // Molad of Sivan 5786
 * JewishMoment molad = hebrew.moladOfMonth(5786, JewishMonth.SIVAN.getNum());
 * IDate hDate   = hebrew.fromMoment(molad);
 * int civilHour = (molad.getHour() + 18) % 24;  // library hour 0 = 18:00 civil
 * int minutes   = molad.getPart() / 18;           // chalakim / 18 = minutes
 * int chalakim  = molad.getPart() % 18;           // remainder chalakim
 * System.out.printf("Molad Sivan 5786: %d/%d/%d at %02d:%02d and %d chalakim%n",
 *         hDate.getDay(), hDate.getMonth(), hDate.getYear(),
 *         civilHour, minutes, chalakim);
 * }</pre>
 *
 * <h2>Find 1st day of Sukkot 5786</h2>
 * <pre>{@code
 * ICalendar greg   = ICalendar.GREGORIAN;
 * JewishCalendar hebrew = ICalendar.JEWISH;
 *
 * // Search forward from 1 Tishrei 5786
 * IDate startOfYear = hebrew.fromYMD(5786, JewishMonth.TISHREI.getNum(), 1);
 * IDate sukkot = JewishSpecialDay.FIRST_DAY_SUKKOT
 *         .getNextOccurrence(startOfYear, false);   // false = include startOfYear itself
 *
 * IDate gregDate = greg.convert(sukkot);
 * System.out.printf("1st day Sukkot 5786: %d Tishrei — %d/%d/%d%n",
 *         sukkot.getDay(),
 *         gregDate.getMonth(), gregDate.getDay(), gregDate.getYear()); // 10/2/2025
 * }</pre>
 */
package net.hebrewcalendar;
