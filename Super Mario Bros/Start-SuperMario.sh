#!/usr/bin/env bash
set -euo pipefail

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SRC_ROOT="$PROJECT_ROOT/src/main/java"
RESOURCES_ROOT="$PROJECT_ROOT/src/main/resources"
OUT_ROOT="$PROJECT_ROOT/out"
CLASSES_ROOT="$OUT_ROOT/classes"
JAVAFX_LIB="${JAVAFX_LIB:-$PROJECT_ROOT/lib/javafx-sdk-23.0.2/lib}"

if [[ ! -d "$JAVAFX_LIB" ]]; then
  echo "JavaFX SDK not found at $JAVAFX_LIB" >&2
  exit 1
fi

mkdir -p "$CLASSES_ROOT"
find "$SRC_ROOT" -name "*.java" -print0 | xargs -0 javac \
  --module-path "$JAVAFX_LIB" \
  --add-modules javafx.controls,javafx.fxml,javafx.media \
  -d "$CLASSES_ROOT"

cp -R "$RESOURCES_ROOT"/. "$CLASSES_ROOT"/

java \
  --module-path "$JAVAFX_LIB" \
  --add-modules javafx.controls,javafx.fxml,javafx.media \
  -cp "$CLASSES_ROOT" \
  edu.mvcc.jcovey.mario.app.SuperMarioApp
