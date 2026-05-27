from __future__ import annotations

import argparse
import os
from dataclasses import dataclass
from pathlib import Path


EXCLUDE_DIRS = {
    ".git",
    ".gradle",
    "build",
    "out",
    "node_modules",
    ".idea",
}


@dataclass(frozen=True)
class Stats:
    files_changed: int
    files_seen: int
    bytes_before: int
    bytes_after: int


def should_skip_path(p: Path) -> bool:
    parts = {part.lower() for part in p.parts}
    return any(excl.lower() in parts for excl in EXCLUDE_DIRS)


def strip_kotlin_comments(src: str) -> str:
    """
    Remove // and /* */ comments from Kotlin/Gradle Kotlin DSL sources,
    preserving content inside:
      - "..." strings (with escapes)
      - 'c' char literals (with escapes)
      - \"\"\" raw strings (no escapes, end at next \"\"\")
    """
    i = 0
    n = len(src)
    out: list[str] = []

    in_line_comment = False
    block_comment_depth = 0
    in_double = False
    in_single = False
    in_triple = False

    while i < n:
        ch = src[i]
        nxt = src[i + 1] if i + 1 < n else ""

        if in_line_comment:
            if ch == "\n":
                in_line_comment = False
                out.append(ch)
            i += 1
            continue

        if block_comment_depth > 0:
            if ch == "/" and nxt == "*":
                block_comment_depth += 1
                i += 2
                continue
            if ch == "*" and nxt == "/":
                block_comment_depth -= 1
                i += 2
                continue
            i += 1
            continue

        if in_triple:
            if src.startswith('"""', i):
                out.append('"""')
                i += 3
                in_triple = False
                continue
            out.append(ch)
            i += 1
            continue

        if in_double:
            out.append(ch)
            if ch == "\\" and i + 1 < n:
                out.append(src[i + 1])
                i += 2
                continue
            if ch == '"':
                in_double = False
            i += 1
            continue

        if in_single:
            out.append(ch)
            if ch == "\\" and i + 1 < n:
                out.append(src[i + 1])
                i += 2
                continue
            if ch == "'":
                in_single = False
            i += 1
            continue

        # Not inside any string/comment: detect strings and comments.
        if src.startswith('"""', i):
            out.append('"""')
            i += 3
            in_triple = True
            continue

        if ch == '"':
            out.append(ch)
            in_double = True
            i += 1
            continue

        if ch == "'":
            out.append(ch)
            in_single = True
            i += 1
            continue

        if ch == "/" and nxt == "/":
            in_line_comment = True
            i += 2
            continue

        if ch == "/" and nxt == "*":
            block_comment_depth = 1
            i += 2
            continue

        out.append(ch)
        i += 1

    return "".join(out)


def iter_targets(root: Path) -> list[Path]:
    targets: list[Path] = []
    for ext in (".kt", ".kts"):
        for p in root.rglob(f"*{ext}"):
            if should_skip_path(p):
                continue
            targets.append(p)
    return targets


def process_file(p: Path, write: bool) -> tuple[bool, int, int]:
    before = p.read_text(encoding="utf-8")
    after = strip_kotlin_comments(before)
    changed = after != before
    if changed and write:
        p.write_text(after, encoding="utf-8")
    return changed, len(before.encode("utf-8")), len(after.encode("utf-8"))


def run(root: Path, write: bool) -> Stats:
    files_changed = 0
    files_seen = 0
    bytes_before = 0
    bytes_after = 0

    for p in iter_targets(root):
        files_seen += 1
        changed, b0, b1 = process_file(p, write=write)
        bytes_before += b0
        bytes_after += b1
        if changed:
            files_changed += 1

    return Stats(
        files_changed=files_changed,
        files_seen=files_seen,
        bytes_before=bytes_before,
        bytes_after=bytes_after,
    )


def main() -> None:
    ap = argparse.ArgumentParser(description="Remove comments from Kotlin sources.")
    ap.add_argument("--root", default=".", help="Project root (default: .)")
    ap.add_argument("--write", action="store_true", help="Write changes to disk.")
    args = ap.parse_args()

    root = Path(args.root).resolve()
    stats = run(root, write=args.write)
    mode = "WROTE" if args.write else "DRY_RUN"
    print(
        f"{mode}: seen={stats.files_seen} changed={stats.files_changed} "
        f"bytes_before={stats.bytes_before} bytes_after={stats.bytes_after}"
    )


if __name__ == "__main__":
    main()

