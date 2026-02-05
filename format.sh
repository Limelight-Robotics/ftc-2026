#!/usr/bin/env bash

# Format all Java files in TeamCode/ using clang-format and the repo .clang-format
set -euo pipefail

REPO_ROOT="$(cd "$(dirname "$0")" && pwd)"

if ! command -v clang-format >/dev/null 2>&1; then
    echo "Error: clang-format not found. Install it (dnf/apt/brew) and re-run." >&2
    echo "  Fedora: sudo dnf install -y clang-tools-extra || sudo dnf install -y clang-format" >&2
    echo "  Debian/Ubuntu: sudo apt install -y clang-format" >&2
    echo "  macOS (Homebrew): brew install clang-format" >&2
    exit 1
fi

TARGET_DIR="$REPO_ROOT/TeamCode"

if [ ! -d "$TARGET_DIR" ]; then
    echo "No TeamCode/ directory found at $TARGET_DIR" >&2
    exit 1
fi

JS=$(find "$TARGET_DIR" -name "*.java")
if [ -z "$JS" ]; then
    echo "No Java files found in TeamCode/. Nothing to format.";
    exit 0
fi

echo "Formatting Java files in TeamCode/..."

if [ -f "$REPO_ROOT/.clang-format" ]; then
    find "$TARGET_DIR" -name "*.java" -print0 | xargs -0 clang-format -i -style=file
else
    find "$TARGET_DIR" -name "*.java" -print0 | xargs -0 clang-format -i
fi

COUNT=$(find "$TARGET_DIR" -name "*.java" | wc -l | tr -d '[:space:]')
echo "Formatted $COUNT Java files in TeamCode/."
