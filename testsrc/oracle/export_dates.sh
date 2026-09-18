#!/usr/bin/env bash
# Export every Jewish month opentorah reckons, as the oracle the ports check
# their date conversion against (testsrc/resources/opentorah-dates.tsv).
#
# Like export_readings.sh beside it: the exporter is a Scala test only because
# reaching their calendar needs their classpath, so this drops it into a
# checkout, runs it, and takes it out again, leaving the checkout as found.
set -euo pipefail

checkout="${1:-}"
out="${2:-}"
if [ -z "$checkout" ] || [ -z "$out" ]; then
  echo "usage: $0 <path to opentorah checkout> <output .tsv>" >&2
  exit 1
fi

here="$(cd "$(dirname "$0")" && pwd)"
# Absolute before anything changes directory: the exporter runs from inside the
# checkout, where a relative path would mean somewhere else entirely.
mkdir -p "$(dirname "$out")"
out="$(cd "$(dirname "$out")" && pwd)/$(basename "$out")"
dest="$checkout/core/src/test/scala/org/opentorah/calendar/ExportDatesTest.scala"

borrowed=no
if [ -e "$dest" ]; then
  if ! cmp -s "$here/ExportDatesTest.scala" "$dest"; then
    echo "$dest exists and differs from ours -- refusing to touch it." >&2
    exit 1
  fi
else
  borrowed=yes
  cp "$here/ExportDatesTest.scala" "$dest"
fi

cleanup() { [ "$borrowed" = yes ] && rm -f "$dest"; }
trap cleanup EXIT
( cd "$checkout" \
  && EXPORT_DATES="$out" \
     EXPORT_COMMIT="$(git rev-parse HEAD)" \
     ./gradlew :opentorah-core:test --tests '*ExportDatesTest*' -q --rerun-tasks )

echo "OK  wrote $out from opentorah $(cd "$checkout" && git rev-parse --short HEAD)"
