# Changelog

All notable changes to this project are documented here. The format is loosely
based on [Keep a Changelog](https://keepachangelog.com/). This project has no
released versions; entries are grouped by Hyperskill stage.

## Stage 4 — Ranges

### Added
- `Range<C extends Comparable<C>>`: immutable interval, `public final`, no public
  constructors. Nine static factories (`open`, `closed`, `openClosed`, `closedOpen`,
  `greaterThan`, `atLeast`, `lessThan`, `atMost`, `all`) plus `contains`, `encloses`,
  `intersection`, `span`, and `isEmpty`. Factories reject null bounds with
  `NullPointerException`; `open(x,x)` and `lower > upper` throw `IllegalArgumentException`.
  `toString()` renders bounds with `-INF`/`INF` and prints `EMPTY` for empty ranges.

## Stage 3 — Size Limited Queues

### Added
- `SizeLimitedQueue<E>`: fixed-capacity circular FIFO backed by `ArrayDeque`. The
  `(int limit)` constructor throws `IllegalArgumentException` for non-positive limits.
  `add` evicts the oldest element when full and rejects null with `NullPointerException`;
  `remove` throws `NoSuchElementException` when empty; `peek` returns null when empty.
  `toArray(E[])` returns an array of the argument's runtime type. `toString()` renders
  oldest-first as `[a, b, c]`.

## Stage 2 — BiMaps and MultiSets

### Added
- `BiMap<K,V>`: bidirectional map enforcing key **and** value uniqueness.
  `put`/`putAll` throw `IllegalArgumentException` on conflict; `forcePut` evicts
  conflicting entries; `values()` exposes the value set; `inverse()` returns a
  value→key view; `toString()` renders as `{k=v, ...}`.
- `Multiset<E>`: occurrence-counting set. `add`/`remove` (single and N-occurrence),
  `count`, `contains`, `elementSet`, and both `setCount` overloads; `toString()`
  renders duplicates expanded as `[a, b, b, ...]`.

### Fixed
- `Multiset.add` now uses `getOrDefault` + `put` instead of `Map.merge`. `merge`
  places colliding keys in a different `HashMap` bucket-chain order than `put`,
  which made `toString()` diverge from the test's `put`-built reference map on
  mixed-type inputs (e.g. `Integer 2` and `Character 'b'` both hash to bucket 2).

## Stage 1 — Immutable Collections

### Added
- `ImmutableCollection<E>`: `public final` class with no public constructors.
  Static factories `of()` and `of(E... elements)` (defensive copy, null-rejecting),
  plus `contains`, `size`, and `isEmpty`. `contains(null)` throws `NullPointerException`.
- Project scaffolding: `.gitignore` for build output, IDE files, and Hyperskill
  infrastructure.
