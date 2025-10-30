"""
generate_todos.py

Scan a directory tree (default: TeamCode) for TODO comments and write a
consolidated Markdown file (default: TeamCode/TODO.md).

Usage:
    python3 scripts/generate_todos.py            # scan TeamCode -> TeamCode/TODO.md
    python3 scripts/generate_todos.py --root TeamCode --out TeamCode/TODO.md

The script searches for the token TODO (case-sensitive) anywhere on a line and
records the file path, line number and the full TODO line. It skips common
build/generated folders.

This is intentionally simple and uses only the Python standard library so it
works on CI and developer machines without extra deps.
"""

import argparse
import os
import re
from pathlib import Path
from datetime import datetime

# Regex to find TODOs in a line. Will match 'TODO' anywhere, plus the rest of the line.
TODO_RE = re.compile(r"\bTODO\b[:]?(.+)?")
# Regex to extract %%author <email>%% style signature
AUTHOR_RE = re.compile(r"%%\s*(.+?)\s*%%")

# File extensions to scan (empty means all files except binary-ish)
# We'll accept most textual files but skip large binary and build directories.
DEFAULT_IGNORE_DIRS = {"build", ".gradle", ".git", "out"}


def find_todos(root: Path, out_path: Path = None):
    todos = []  # list of tuples (relpath, lineno, line, author)
    out_rel = None
    if out_path is not None:
        try:
            out_rel = out_path.resolve()
        except Exception:
            out_rel = None

    for dirpath, dirnames, filenames in os.walk(root):
        # filter out known ignore dirs in-place so os.walk won't recurse them
        dirnames[:] = [d for d in dirnames if d not in DEFAULT_IGNORE_DIRS]
        for fname in filenames:
            fpath = Path(dirpath) / fname
            # skip the output file if it's inside the scanned tree
            if out_rel is not None and fpath.resolve() == out_rel:
                continue
            # small heuristic: skip binary files and markdown by extension
            if fpath.suffix.lower() in {'.class', '.dex', '.jar', '.so', '.png', '.jpg', '.jpeg', '.gif', '.md'}:
                continue
            # read file safely
            try:
                text = fpath.read_text(encoding='utf-8')
            except Exception:
                # try latin-1 fallback for weird encodings
                try:
                    text = fpath.read_text(encoding='latin-1')
                except Exception:
                    continue
            lines = text.splitlines()
            for i, line in enumerate(lines, start=1):
                if 'TODO' in line:
                    m = TODO_RE.search(line)
                    if m:
                        text = (m.group(1) or '').strip()
                        # try to extract an author signature inside %%...%%
                        author = None
                        a = AUTHOR_RE.search(line)
                        if a:
                            author = a.group(1).strip()
                            # strip the signature from the text for clarity
                            text = AUTHOR_RE.sub('', text).strip()
                        todos.append((fpath.relative_to(root), i, line.strip(), author))
    return todos


def write_md(todos, out_path: Path, root: Path):
    now = datetime.utcnow().strftime('%Y-%m-%d %H:%M UTC')
    header = (
        f"# TODOs found in {root}\n\nCollected on: {now}\n\n"
        f"This file consolidates all `TODO` comments found under `{root}`.\n"
        "Entries preserve the original TODO line and show file + line for quick reference.\n\n---\n\n"
    )
    grouped = {}
    for relpath, lineno, line, author in todos:
        grouped.setdefault(str(relpath), []).append((lineno, line, author))

    lines = [header]
    for path in sorted(grouped.keys()):
        lines.append(f"## {path}\n")
        for lineno, line, author in grouped[path]:
            target = f"{root}/{path}#L{lineno}"
            lines.append(f"- [Line {lineno}]({target}):\n\n    {line}\n")
            if author:
                lines.append(f"    - Assigned to: {author}\n")
        lines.append('\n')

    footer = (
        "---\n\nNotes\n- TODO format: single-line comment containing `TODO:` followed by optional metadata.\n"
    )
    lines.append(footer)

    out_path.parent.mkdir(parents=True, exist_ok=True)
    out_path.write_text('\n'.join(lines), encoding='utf-8')


def main():
    p = argparse.ArgumentParser(description='Collect TODO comments into a Markdown file')
    p.add_argument('--root', default='TeamCode', help='Root folder to scan (default: TeamCode)')
    p.add_argument('--out', default='./TODO.md', help='Output markdown path (default: ./TODO.md)')
    args = p.parse_args()

    root = Path(args.root)
    if not root.exists() or not root.is_dir():
        print(f"Root path '{root}' does not exist or is not a directory")
        raise SystemExit(1)

    todos = find_todos(root)
    out_path = Path(args.out)
    write_md(todos, out_path, root)
    print(f"Wrote {len(todos)} TODO(s) to {out_path}")


if __name__ == '__main__':
    main()
