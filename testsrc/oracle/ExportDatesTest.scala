package org.opentorah.calendar

import org.opentorah.calendar.jewish.Jewish
import Jewish.Year
import org.opentorah.calendar.roman.Gregorian
import org.scalatest.flatspec.AnyFlatSpec

import java.nio.file.{Files, Paths}

/** Writes every Jewish month of a long range -- its length, and the Gregorian
  * date it begins on -- as the oracle the ports check their date conversion
  * against. Pinning the months pins every day in them: a port that starts a
  * month a day late, or keeps it a day too long, disagrees here first.
  *
  * Not a test of anything; it is here because this is where their calendar and
  * its test classpath already are. Enable it with EXPORT_DATES=<path>.
  */
final class ExportDatesTest extends AnyFlatSpec:

  private val FromYear = 5700
  private val ToYear   = 5900

  "dates" should "be exported" in:
    val target = System.getenv("EXPORT_DATES")
    if target == null then cancel("set EXPORT_DATES=<path> to write the fixture")

    val rows =
      for
        yearNumber   <- FromYear to ToYear
        year          = Year(yearNumber)
        numberInYear <- 1 to year.lengthInMonths
      yield
        val month = year.month(numberInYear)
        val g     = month.firstDay.to(Gregorian)
        val greg  = f"${g.year.number}%04d-${g.month.numberInYear}%02d-${g.numberInMonth}%02d"
        // Their own name for the month, spaces dropped ("Adar I" -> "AdarI")
        // so a field never splits.
        val name  = month.name.toString.replace(" ", "")
        s"M\t$yearNumber\t$name\t${month.length}\t$greg"

    val header = Seq(
      "# Jewish months, exported from opentorah -- the oracle for date conversion.",
      "#",
      s"# opentorah ${Option(System.getenv("EXPORT_COMMIT")).getOrElse("unknown")}  (Apache 2.0; see opentorah/LICENSE.md)",
      s"# generated ${java.time.LocalDate.now} over Jewish years $FromYear-$ToYear",
      "#",
      "# M<TAB>jewish year<TAB>month<TAB>length in days<TAB>Gregorian date of its first day"
    )

    Files.write(Paths.get(target), (header ++ rows).mkString("", "\n", "\n").getBytes("UTF-8"))
