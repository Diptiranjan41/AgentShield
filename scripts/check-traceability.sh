#!/usr/bin/env bash
# Phase-aware requirement traceability check.
#  - Every requirement with Verify=Test and Phase <= current phase must be referenced in src/test.
#  - Every REQ-ID referenced in src/test must exist in docs/requirements.md.
# Current phase: env CURRENT_PHASE, else docs/current-phase.txt, else 1.
set -euo pipefail

REQ_FILE="docs/requirements.md"
TEST_DIR="src/test"

[ -f "$REQ_FILE" ] || { echo "Missing $REQ_FILE"; exit 1; }
[ -d "$TEST_DIR" ] || { echo "Missing $TEST_DIR"; exit 1; }

if [ -n "${CURRENT_PHASE:-}" ]; then
  phase="$CURRENT_PHASE"
elif [ -f docs/current-phase.txt ]; then
  phase=$(tr -d '[:space:]' < docs/current-phase.txt)
else
  phase=1
fi
echo "Current phase: $phase"

# Table columns: | ID | Requirement | Pri | Phase | Verify | Status |
required=$(awk -F'|' -v cur="$phase" '
  /^\| *REQ-[A-Z]+-[0-9]{3} *\|/ {
    id=$2; ph=$5; ver=$6
    gsub(/[ \t]/, "", id); gsub(/[ \t]/, "", ph); gsub(/[ \t]/, "", ver)
    if (ver == "Test" && ph+0 <= cur+0) print id
  }' "$REQ_FILE" | sort -u)

all_ids=$(grep -oE '^\| *REQ-[A-Z]+-[0-9]{3}' "$REQ_FILE" | grep -oE 'REQ-[A-Z]+-[0-9]{3}' | sort -u)

missing=0
for id in $required; do
  if ! grep -rq "$id" "$TEST_DIR"; then
    echo "NOT COVERED: $id"
    missing=$((missing + 1))
  fi
done

unknown=0
for id in $(grep -rhoE 'REQ-[A-Z]+-[0-9]{3}' "$TEST_DIR" | sort -u); do
  if ! echo "$all_ids" | grep -qx "$id"; then
    echo "UNKNOWN ID in tests: $id"
    unknown=$((unknown + 1))
  fi
done

echo "Required up to phase $phase: $(echo "$required" | grep -c . || true), uncovered: $missing, unknown ids: $unknown"
[ "$missing" -eq 0 ] && [ "$unknown" -eq 0 ]