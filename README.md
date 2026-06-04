# HyperCollections

Custom Java collection implementations modeled after Google Guava's collection library, built as a staged [Hyperskill](https://hyperskill.org) (JetBrains Academy) educational project. Each stage adds one or more generic collection types under the `collections` package, validated by a reflection-based test harness (`hs-test`) that checks class structure, method signatures, and behavior exactly.

## Tech stack

| Concern | Choice |
|---------|--------|
| Language | Java 17 |
| Build | Gradle (multi-module; wrapper pins 9.3.0) |
| Test framework | [`com.github.hyperskill:hs-test`](https://jitpack.io) (JUnit 4 under the hood) |
| Test helpers | `util` module (`DelegateSearcher`, `CustomMethod`) — reflectively locate & validate `collections.*` classes |
| Dependency source | Maven Central + JitPack |

## Project status

| Stage | Type(s) | Status |
|-------|---------|--------|
| 1 — Immutable Collections | `ImmutableCollection<E>` | ✅ Done |
| 2 — BiMaps and MultiSets | `BiMap<K,V>`, `Multiset<E>` | ✅ Done |
| 3 — Size Limited Queues | `SizeLimitedQueue<E>` | ✅ Done |
| 4 — Ranges | `Range<C extends Comparable<C>>` | ✅ Done |

## Layout

```
HyperCollections/task/src/collections/   ← implementation classes
HyperCollections/task/test/              ← course-provided tests (read-only)
util/                                    ← shared reflection test helpers
```

## How to run

There is no runnable application — this is a library exercised through tests.

> **Note:** the system Gradle (9.5.1) and an IDE-regenerated root `build.gradle` can reject `sourceCompatibility` at project scope. Prefer **IntelliJ IDEA's built-in Gradle integration** to build and run. From the terminal, use the wrapper:

```bash
# Run all tests for the task module
./gradlew :HyperCollections-task:test

# Run a single stage's test class
./gradlew :HyperCollections-task:test --tests "MultisetTests"
```

The module name is derived by `settings.gradle` (path segments joined with `-`), so `HyperCollections/task/` → `:HyperCollections-task`.

## API reference

### `ImmutableCollection<E>` — Stage 1
`public final class`, no public constructors; instances are created only via static factories.

| Method | Signature | Throws | Description |
|--------|-----------|--------|-------------|
| `of` | `static <E> ImmutableCollection<E> of()` | — | Empty immutable collection |
| `of` | `static <E> ImmutableCollection<E> of(E... elements)` | `NullPointerException` if any element is null | Immutable collection of the given elements (defensively copied) |
| `contains` | `boolean contains(Object element)` | `NullPointerException` if `element` is null | Membership test |
| `size` | `int size()` | — | Element count |
| `isEmpty` | `boolean isEmpty()` | — | True when empty |

### `BiMap<K,V>` — Stage 2
Bidirectional map enforcing uniqueness of **both** keys and values; public no-arg constructor.

| Method | Signature | Throws | Description |
|--------|-----------|--------|-------------|
| `put` | `V put(K key, V value)` | `IllegalArgumentException` if key **or** value already present | Add a mapping |
| `putAll` | `void putAll(Map<K,V> map)` | `IllegalArgumentException` on any conflicting key/value | Bulk add (delegates to `put`) |
| `forcePut` | `V forcePut(K key, V value)` | — | Add a mapping, evicting any conflicting key/value first |
| `values` | `Set<V> values()` | — | The set of values (unique by invariant) |
| `inverse` | `BiMap<V,K> inverse()` | — | A new value→key view |

`toString()` renders as a map: `{a=3, b=4}` (or `{}`).

### `Multiset<E>` — Stage 2
A set that allows duplicates by tracking per-element occurrence counts; public no-arg constructor.

| Method | Signature | Description |
|--------|-----------|-------------|
| `add` | `void add(E element)` | Add one occurrence |
| `add` | `void add(E element, int occurrences)` | Add N occurrences (no-op if `occurrences <= 0`) |
| `remove` | `void remove(E element)` | Remove one occurrence (no-op if absent) |
| `remove` | `void remove(E element, int occurrences)` | Remove N occurrences (floors at 0; no-op if `<= 0` or absent) |
| `setCount` | `void setCount(E element, int count)` | Set exact count (`0` removes; no-op if `count < 0`, or absent and `count > 0`) |
| `setCount` | `void setCount(E element, int oldCount, int newCount)` | Conditional set: only if current count equals `oldCount` |
| `count` | `int count(E element)` | Occurrences (0 if absent) |
| `contains` | `boolean contains(E element)` | True if at least one occurrence |
| `elementSet` | `Set<E> elementSet()` | Distinct elements |

`toString()` renders as a list with duplicates expanded: `[a, b, b, c]` (or `[]`).

### `Range<C extends Comparable<C>>` — Stage 4
An immutable interval over a comparable type. `public final`, no public constructors; every instance comes from a static factory.

| Factory | Signature | Interval |
|---------|-----------|----------|
| `open` | `static Range<C> open(C lower, C upper)` | `(lower, upper)` |
| `closed` | `static Range<C> closed(C lower, C upper)` | `[lower, upper]` |
| `openClosed` | `static Range<C> openClosed(C lower, C upper)` | `(lower, upper]` |
| `closedOpen` | `static Range<C> closedOpen(C lower, C upper)` | `[lower, upper)` |
| `greaterThan` | `static Range<C> greaterThan(C lower)` | `(lower, INF)` |
| `atLeast` | `static Range<C> atLeast(C lower)` | `[lower, INF)` |
| `lessThan` | `static Range<C> lessThan(C upper)` | `(-INF, upper)` |
| `atMost` | `static Range<C> atMost(C upper)` | `(-INF, upper]` |
| `all` | `static Range<C> all()` | `(-INF, INF)` |

| Method | Signature | Description |
|--------|-----------|-------------|
| `contains` | `boolean contains(C value)` | True if value lies in the range (honors open/closed ends); throws `NullPointerException` on null |
| `encloses` | `boolean encloses(Range<C> other)` | True if every value of `other` is in this range |
| `intersection` | `Range<C> intersection(Range<C> other)` | The overlap (an empty range if disjoint) |
| `span` | `Range<C> span(Range<C> other)` | The smallest range enclosing both |
| `isEmpty` | `boolean isEmpty()` | True for empty ranges such as `(x, x]` |

`toString()` renders as `[lower, upper]`/`(lower, upper)` with `-INF`/`INF` for unbounded ends, or `EMPTY`.

## Usage examples

```java
// ImmutableCollection
ImmutableCollection<Integer> nums = ImmutableCollection.of(1, 2, 3, 4);
nums.size();          // 4
nums.contains(2);     // true
ImmutableCollection.of();  // empty

// BiMap
BiMap<Character, Integer> bm = new BiMap<>();
bm.put('a', 3);
bm.put('b', 4);
bm.forcePut('a', 4);  // evicts ('b',4) and old ('a',3); now {a=4}
bm.values();          // [4]
bm.inverse();         // {4=a}

// Multiset
Multiset<String> ms = new Multiset<>();
ms.add("x", 3);
ms.remove("x");       // count("x") == 2
ms.setCount("x", 2, 5);  // current is 2 → now 5
ms.elementSet();      // [x]

// Range
Range<Integer> r = Range.closed(5, 15);
r.contains(10);                      // true
r.toString();                        // [5, 15]
r.encloses(Range.open(6, 9));        // true
r.intersection(Range.closed(10, 20)); // [10, 15]
r.span(Range.atLeast(20));           // [5, INF)
Range.openClosed(5, 5).isEmpty();    // true → EMPTY
```

## Validation rules

| Type | Rule |
|------|------|
| `ImmutableCollection` | `final` class; no public constructors; `of(...)` and `contains(null)` throw `NullPointerException` |
| `BiMap` | Keys and values both unique; `put`/`putAll` throw `IllegalArgumentException` on conflict; `forcePut` never throws |
| `Multiset` | Non-positive `add`/`remove` counts are no-ops; `setCount` with negative target is a no-op; count `0` removes the element |
| `Range` | No public constructors; factories reject null bounds with `NullPointerException`; `open(x,x)` and `lower > upper` throw `IllegalArgumentException`; `contains`/`encloses` reject null |

## How to run the tests

```bash
./gradlew :HyperCollections-task:test
```

`BUILD SUCCESSFUL` means every stage's reflection + behavior checks pass. Tests live in `HyperCollections/task/test/` and are provided by the course (read-only).
