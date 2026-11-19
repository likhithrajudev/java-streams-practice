#!/usr/bin/env bash
# Usage: ./run.sh [--solutions] <lesson-number | all>     e.g.  ./run.sh 03     ./run.sh all
set -euo pipefail
cd "$(dirname "$0")"

src="src"
if [[ "${1:-}" == "--solutions" ]]; then src="solutions/src"; shift; fi
target="${1:-}"
if [[ -z "$target" ]]; then
  echo "Usage: ./run.sh [--solutions] <01..12 | all>"; ls src/streams | grep '^Lesson' | sed 's/\.java//'; exit 1
fi

out=".out/$(echo "$src" | tr '/' '_')"
mkdir -p "$out"
javac -d "$out" "$src"/streams/*.java

run_one() { java -Xmx256m -cp "$out" "streams.$(basename "$1" .java)"; }

if [[ "$target" == "all" ]]; then
  for f in "$src"/streams/Lesson*.java; do echo "##### $(basename "$f" .java)"; run_one "$f" | grep -E 'Result|=== '; done
else
  shopt -s nullglob
  files=("$src"/streams/Lesson"$target"_*.java)
  [[ ${#files[@]} -eq 1 ]] || { echo "No unique lesson matching '$target'"; exit 1; }
  run_one "${files[0]}"
fi
