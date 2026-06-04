# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

HyperCollections is a [Hyperskill](https://hyperskill.org) (JetBrains Academy) educational project that teaches custom Java collection implementations. You implement four collection types across four staged tasks, all modeled after Google Guava's collection library.

## Build & Test

This is a multi-module Gradle project. The system Gradle (9.5.1) has a compatibility issue with `sourceCompatibility` in the root `build.gradle` — use **IntelliJ IDEA's built-in Gradle integration** to build and run tests.

To run tests from the terminal with a compatible Gradle version:
```bash
# Run all tests for the current task module
gradle :HyperCollections-task:test

# Run a specific test class
gradle :HyperCollections-task:test --tests "ImmutabilityTests"
```

The module name is derived by `settings.gradle`: path segments from the repo root are joined with `-`, with spaces/special chars replaced by `_`. So `HyperCollections/task/` → `:HyperCollections-task`.

## Architecture

### Source Layout

All implementation code lives in:
```
HyperCollections/task/src/collections/   ← your Java classes go here
HyperCollections/task/test/              ← test files (read-only, provided by course)
```

The `util/` module provides shared test helpers (`DelegateSearcher`, `CustomMethod`) used by all stage tests.

### Test Framework

Tests use `com.github.hyperskill:hs-test` (from JitPack) and **find your classes by name via reflection** inside the `collections` package. The `DelegateSearcher` base class scans `collections.*` for a class matching a hard-coded name (e.g., `"ImmutableCollection"`), then validates its structure (modifiers, generics, method signatures) and invokes methods reflectively.

This means:
- Class names and package (`collections`) must be exact.
- Method signatures must match precisely (name, parameter types, return type, static/instance, visibility).
- Constructors must have the specified visibility.

### The Four Stages

Each stage adds one or more classes to `HyperCollections/task/src/collections/`:

| Stage | Class(es) | Key Constraint |
|-------|-----------|----------------|
| 1 – Immutable Collections | `ImmutableCollection<E>` | `final` class, no public constructors, static `of()` factory |
| 2 – BiMaps and MultiSets | `BiMap<K,V>`, `Multiset<E>` | `BiMap` maintains key+value uniqueness; `Multiset` tracks occurrence counts |
| 3 – Size Limited Queues | `SizeLimitedQueue<E>` | Circular FIFO; evicts oldest when full; constructor takes `int` limit |
| 4 – Ranges | `Range<C extends Comparable<C>>` | No public constructor; nine static factory methods; `contains`, `encloses`, `intersection`, `span`, `isEmpty` |

### Design Patterns Required

- **`ImmutableCollection`**: `public final class`, private/package constructors only, `static <E> ImmutableCollection<E> of(E... elements)` and `static <E> ImmutableCollection<E> of()`. Throws `NullPointerException` on null elements.
- **`Range`**: Private constructor, all instantiation through static factory methods (`open`, `closed`, `openClosed`, `closedOpen`, `greaterThan`, `atLeast`, `lessThan`, `atMost`, `all`). Throws `IllegalArgumentException` for invalid bounds (e.g., `open(a,a)`), `NullPointerException` for null args.
- **`BiMap`**: `put()` throws `IllegalArgumentException` if key or value already exists; `forcePut()` replaces conflicting entries; `inverse()` returns a live inverse view.
- **`SizeLimitedQueue`**: Constructor `SizeLimitedQueue(int limit)` throws `IllegalArgumentException` if limit ≤ 0. `add()` throws `NullPointerException` for null. `remove()` throws `NoSuchElementException` when empty; `peek()` returns null when empty.
