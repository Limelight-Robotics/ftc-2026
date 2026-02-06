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
    echo "No "$TARGET_DIR" directory found" >&2
    exit 1
fi

JS=$(find "$TARGET_DIR" -name "*.java")
if [ -z "$JS" ]; then
    echo "No Java files found in "$TARGET_DIR". Nothing to format.";
    exit 0
fi

echo "Formatting Java files in "$TARGET_DIR"..."

find "$TARGET_DIR" -name "*.java" -exec clang-format -i {} +

COUNT=$(find "$TARGET_DIR" -name "*.java" | wc -l | tr -d '[:space:]')
echo "Formatted $COUNT Java files in "$TARGET_DIR"."
