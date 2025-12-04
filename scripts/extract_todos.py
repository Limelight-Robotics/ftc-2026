#!/usr/bin/python3
"""
Extract TODOs from Java files in TeamCode directory.

Supports standard TODO comments and custom assignment syntax:
  // TODO: description
  // TODO(assignee): description
  // TODO @assignee: description
  // TODO <assignee@email.com>: description
"""

import os
import re
import sys
from dataclasses import dataclass
from pathlib import Path
from typing import Optional


@dataclass
class TodoItem:
    """Represents a single TODO item extracted from source code."""
    file: str
    line: int
    text: str
    rel_path: str = ""  # Relative path from repo root
    assignee: Optional[str] = None

    def __str__(self) -> str:
        loc = f"{self.file}:{self.line}"
        if self.assignee:
            return f"[{self.assignee}] {loc} - {self.text}"
        return f"{loc} - {self.text}"


# Regex patterns for TODO extraction
TODO_PATTERNS = [
    # TODO(assignee): description
    re.compile(r'//\s*TODO\s*\(([^)]+)\)\s*:?\s*(.*)$', re.IGNORECASE),
    # TODO @assignee: description
    re.compile(r'//\s*TODO\s*@(\S+)\s*:?\s*(.*)$', re.IGNORECASE),
    # TODO <email>: description
    re.compile(r'//\s*TODO\s*<([^>]+)>\s*:?\s*(.*)$', re.IGNORECASE),
    # TODO: description (no assignee)
    re.compile(r'//\s*TODO\s*:?\s*(.*)$', re.IGNORECASE),
]


def extract_todo(line: str) -> Optional[tuple[Optional[str], str]]:
    """Extract assignee and description from a TODO comment line."""
    for pattern in TODO_PATTERNS:
        match = pattern.search(line)
        if match:
            groups = match.groups()
            if len(groups) == 2:
                return groups[0].strip(), groups[1].strip()
            else:
                return None, groups[0].strip()
    return None


def scan_file(filepath: Path, repo_root: Path) -> list[TodoItem]:
    """Scan a single Java file for TODOs."""
    todos = []
    rel_path = str(filepath.relative_to(repo_root))
    
    try:
        with open(filepath, 'r', encoding='utf-8') as f:
            for line_num, line in enumerate(f, 1):
                result = extract_todo(line)
                if result:
                    assignee, text = result
                    if text:  # Only include if there's actual text
                        todos.append(TodoItem(
                            file=filepath.name,
                            line=line_num,
                            text=text,
                            rel_path=rel_path,
                            assignee=assignee
                        ))
    except (IOError, UnicodeDecodeError) as e:
        print(f"Warning: Could not read {filepath}: {e}", file=sys.stderr)
    
    return todos


def scan_directory(root: Path, repo_root: Path) -> list[TodoItem]:
    """Recursively scan directory for Java files and extract TODOs."""
    todos = []
    for filepath in root.rglob("*.java"):
        todos.extend(scan_file(filepath, repo_root))
    return todos


def group_by_assignee(todos: list[TodoItem]) -> dict[str, list[TodoItem]]:
    """Group TODOs by assignee. Unassigned goes under 'Unassigned'."""
    groups: dict[str, list[TodoItem]] = {}
    for todo in todos:
        key = todo.assignee or "Unassigned"
        groups.setdefault(key, []).append(todo)
    return groups

def group_by_file(todos: list[TodoItem]) -> dict[str, list[TodoItem]]:
    """Group TODOs by file."""
    groups: dict[str, list[TodoItem]] = {}
    for todo in todos:
        key = todo.file
        groups.setdefault(key, []).append(todo)
    return groups

def group(todos: list[TodoItem]):
    """Group TODOs by assignee and file."""
    global groups
    groups = group_by_assignee(todos)
    for assignee in groups:
        groups[assignee] = group_by_file(groups[assignee])
    return groups

def print_report(todos: list[TodoItem]) -> None:
    """Print formatted TODO report."""
    if not todos:
        print("No TODOs found.")
        return

    print(f"# TODO Report")
    print(f"Total TODOs: {len(todos)}\n")

    groups = group(todos)
    
    # Sort assignees with Unassigned always last
    assignees = sorted(groups.keys())
    if "Unassigned" in assignees:
        assignees.remove("Unassigned")
        assignees.append("Unassigned")

    # Table of Contents
    print("## Table of Contents\n")
    for assignee in assignees:
        file_groups = groups[assignee]
        item_count = sum(len(items) for items in file_groups.values())
        anchor = assignee.lower().replace(" ", "-").replace("@", "").replace(".", "")
        print(f"- [{assignee} ({item_count} items)](#{anchor}-{item_count}-items)")
    print()

    # Content
    for assignee in assignees:
        file_groups = groups[assignee]
        item_count = sum(len(items) for items in file_groups.values())
        print(f"## {assignee} ({item_count} items)")
        for filename in sorted(file_groups.keys()):
            print(f"\n### {filename}")
            for todo in file_groups[filename]:
                print(f"- **[Line {todo.line}]({todo.rel_path}#L{todo.line}):** {todo.text}")
        print()


def main():
    # Find TeamCode directory
    script_dir = Path(__file__).parent
    repo_root = script_dir.parent
    teamcode_dir = repo_root / "TeamCode" / "src" / "main" / "java"

    if not teamcode_dir.exists():
        print(f"Error: TeamCode directory not found at {teamcode_dir}", file=sys.stderr)
        sys.exit(1)

    print(f"Scanning: {teamcode_dir}")
    todos = scan_directory(teamcode_dir, repo_root)

    # Write to output file
    output_file = repo_root / "TODOS.md"
    with open(output_file, 'w', encoding='utf-8') as f:
        original_stdout = sys.stdout
        sys.stdout = f
        print_report(todos)
        sys.stdout = original_stdout
    print(f"TODO report written to {output_file}")

if __name__ == "__main__":
    main()