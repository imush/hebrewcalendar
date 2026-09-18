# Hebrew Calendar

[![Maven Central](https://img.shields.io/maven-central/v/net.hebrewcalendar/hebrewcalendar)](https://central.sonatype.com/artifact/net.hebrewcalendar/hebrewcalendar)
[![License](https://img.shields.io/badge/license-BSD--3--Clause-blue)](LICENSE)

A Java library for Jewish calendar computations — dates, festivals, zmanim, Torah
readings and daily learning. Java 11, no runtime dependencies.

**[Project page and API documentation](https://imush.github.io/hebrewcalendar/)**

## It powers hebrewcalendar.net

Every date, zman and reading on [hebrewcalendar.net](https://hebrewcalendar.net) — its
calendars, zmanim pages, printable schedules and shul displays — is computed by this
library. The mobile app runs the same calendar as a [companion C library](https://github.com/imush/calendar),
checked against this one day by day.

## What it does

- **Dates** — Jewish, Gregorian and Julian calendars, converted in any direction; leap
  years, month lengths, anniversaries and yahrzeits, including the dates that fall only
  in some years.
- **Festivals and fasts** — Yom Tov, chol hamoed, rosh chodesh, fasts and their
  deferrals, the named Shabbatot, the omer, Chabad dates, and US holidays. Israel and
  the diaspora each keep their own schedule.
- **Zmanim** — sunrise and sunset from the NOAA solar calculations, dawn and nightfall
  in several opinions, candle lighting, chametz deadlines, when a fast begins and ends,
  and sane answers where the sun does not rise or set.
- **Tekufot and the molad** — the four solar seasons according to Shmuel or Rav Ada, the
  molad of any month, when Tal uMatar begins, and Birkat HaChama.
- **Torah readings** — the weekly parsha with its aliyot, festival and special readings,
  and haftarot across twenty-six customs, resolved through their fallback tree.
- **Daily learning** — Chumash, Daf Yomi, Rambam (one chapter and three), Sefer
  HaMitzvot, Tanya, Tehillim by day of the month and through Elul, Pirkei Avot, and the
  Sotah sefirah count.

## Using it

```xml
<dependency>
  <groupId>net.hebrewcalendar</groupId>
  <artifactId>hebrewcalendar</artifactId>
  <version>2.1.0</version>
</dependency>
```

```java
// A Gregorian date as a Jewish one
IDate<JewishCalendar> today =
    ICalendar.JEWISH.convert(ICalendar.GREGORIAN.fromYMD(2026, 9, 18));

// Zmanim for a place and a day
Location montreal = new Location(45.5017, -73.5673, 0.0, "America/Montreal", false, false);
Zmanim zmanim = new Zmanim(LocalDate.now(), montreal);

ZonedDateTime sunset = zmanim.getSunset().getTime();
Zman candles = zmanim.getCandleLightingZman();   // null unless candles are lit tonight
```

## Building

```bash
git clone --recurse-submodules https://github.com/imush/hebrewcalendar.git
cd hebrewcalendar
mvn package
```

Calendar names and reading schedules come from the shared
[hebrewcalendar-data](https://github.com/imush/hebrewcalendar-data) repository, included
as a submodule and compiled into the jar.

## Checked against opentorah

Readings and date conversion are both compared against
[opentorah](https://www.opentorah.org), the source of the reading data: every reading
situation over eighty years, and every day of two centuries. `mvn test` runs those
comparisons with the rest of the suite.

## Releasing

See [RELEASING.md](RELEASING.md).

## License

BSD 3-Clause. See [LICENSE](LICENSE).
