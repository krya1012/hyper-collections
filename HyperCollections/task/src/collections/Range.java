package collections;

import java.util.Objects;

/**
 * An immutable interval over a comparable type, bounded by an optional open or
 * closed endpoint on each side, created only through the static factory methods.
 */
public final class Range<C extends Comparable<C>> {

    private final C upperBound;
    private final C lowerBound;
    private final boolean upperBoundOpen;
    private final boolean lowerBoundOpen;

    private Range(C lower, C upper, boolean lowerOpen, boolean upperOpen) {
        upperBound = upper;
        lowerBound = lower;
        upperBoundOpen = upperOpen;
        lowerBoundOpen = lowerOpen;
        if (lower != null && upper != null) {
            if (lower.compareTo(upper) > 0) {
                throw new IllegalArgumentException();
            }
            if (upperOpen && lowerOpen && Objects.equals(upperBound, lowerBound)) {
                throw new IllegalArgumentException();
            }
        }
    }

    /** Returns the lower endpoint, or null when unbounded below. */
    public C getLowerBound() {
        return lowerBound;
    }

    /** Returns the upper endpoint, or null when unbounded above. */
    public C getUpperBound() {
        return upperBound;
    }

    /** Returns {@code (lower, upper)} — both endpoints excluded. */
    public static <C extends Comparable<C>> Range<C> open(C lower, C upper) {
        if (lower == null || upper == null) throw new NullPointerException();
        return new Range<>(lower, upper, true, true);
    }

    /** Returns {@code [lower, upper]} — both endpoints included. */
    public static <C extends Comparable<C>> Range<C> closed(C lower, C upper) {
        if (lower == null || upper == null) throw new NullPointerException();
        return new Range<>(lower, upper, false, false);
    }

    /** Returns {@code (lower, upper]} — lower excluded, upper included. */
    public static <C extends Comparable<C>> Range<C> openClosed(C lower, C upper) {
        if (lower == null || upper == null) throw new NullPointerException();
        return new Range<>(lower, upper, true, false);
    }

    /** Returns {@code [lower, upper)} — lower included, upper excluded. */
    public static <C extends Comparable<C>> Range<C> closedOpen(C lower, C upper) {
        if (lower == null || upper == null) throw new NullPointerException();
        return new Range<>(lower, upper, false, true);
    }

    /** Returns {@code (lower, INF)} — everything strictly above lower. */
    public static <C extends Comparable<C>> Range<C> greaterThan(C lower) {
        if (lower == null) throw new NullPointerException();
        return new Range<>(lower, null, true, true);
    }

    /** Returns {@code [lower, INF)} — everything at or above lower. */
    public static <C extends Comparable<C>> Range<C> atLeast(C lower) {
        if (lower == null) throw new NullPointerException();
        return new Range<>(lower, null, false, true);
    }

    /** Returns {@code (-INF, upper)} — everything strictly below upper. */
    public static <C extends Comparable<C>> Range<C> lessThan(C upper) {
        if (upper == null) throw new NullPointerException();
        return new Range<>(null, upper, true, true);
    }

    /** Returns {@code (-INF, upper]} — everything at or below upper. */
    public static <C extends Comparable<C>> Range<C> atMost(C upper) {
        if (upper == null) throw new NullPointerException();
        return new Range<>(null, upper, true, false);
    }

    /** Returns {@code (-INF, INF)} — the unbounded range containing every value. */
    public static <C extends Comparable<C>> Range<C> all() {
        return new Range<>(null, null, true, true);
    }

    /**
     * Returns true if value falls within this range, honoring open/closed endpoints.
     * @throws NullPointerException if value is null
     */
    public boolean contains(C value) {
        Objects.requireNonNull(value);
        return (lowerBound == null || (lowerBound.compareTo(value) < 0) || (lowerBound.compareTo(value) <= 0 && !lowerBoundOpen))
                && (upperBound == null || (upperBound.compareTo(value) > 0) || (upperBound.compareTo(value) >= 0 && !upperBoundOpen));
    }

    /**
     * Returns true if every value of other also lies in this range.
     * @throws NullPointerException if other is null
     */
    public boolean encloses(Range<C> other) {
        Objects.requireNonNull(other);

        if (isEmpty()) return false;
        if (other.isEmpty()) return true;

        boolean upper = other.upperBound != null
                && (contains(other.upperBound) || (other.upperBound == upperBound && upperBoundOpen && other.upperBoundOpen)) ||
                other.upperBound == null && upperBound == null;
        boolean lower = other.lowerBound != null
                && (contains(other.lowerBound) || (other.lowerBound == lowerBound && lowerBoundOpen && other.lowerBoundOpen)) ||
                other.lowerBound == null && lowerBound == null;

        return upper && lower;
    }

    /** Returns the smallest range enclosing both this and other. */
    public Range<C> span(Range<C> other) {
        if (other == null) throw new NullPointerException();
        if (isEmpty()) return other;
        if (other.isEmpty()) return this;
        C upper;
        boolean upperOpen;
        if (upperBound == null || other.upperBound == null) {
            upper = null;
            upperOpen = true;

        } else if (upperBound.compareTo(other.upperBound) > 0) {
            upper = upperBound;
            upperOpen = upperBoundOpen;
        } else if (upperBound.compareTo(other.upperBound) < 0) {
            upper = other.upperBound;
            upperOpen = other.upperBoundOpen;
        } else {
            upper = upperBound;
            upperOpen = upperBoundOpen && other.upperBoundOpen;
        }
        C lower;
        boolean lowerOpen;
        if (lowerBound == null || other.lowerBound == null) {
            lower = null;
            lowerOpen = true;
        } else if (lowerBound.compareTo(other.lowerBound) < 0) {
            lower = lowerBound;
            lowerOpen = lowerBoundOpen;
        } else if (lowerBound.compareTo(other.lowerBound) > 0) {
            lower = other.lowerBound;
            lowerOpen = other.lowerBoundOpen;
        } else {
            lower = lowerBound;
            lowerOpen = lowerBoundOpen && other.lowerBoundOpen;
        }
        return new Range<>(lower, upper, lowerOpen, upperOpen);
    }

    /** Returns the overlap of this and other, or an empty range when they do not overlap. */
    public Range<C> intersection(Range<C> other) {
        if (isEmpty()) return this;
        if (other.isEmpty()) return other;
        if (other == null) throw new NullPointerException();


        C upper;
        boolean upperOpen;
        if (upperBound == null) {
            upper = other.upperBound;
            upperOpen = other.upperBoundOpen;

        } else if (other.upperBound == null) {
            upper = upperBound;
            upperOpen = upperBoundOpen;
        } else if (upperBound.compareTo(other.upperBound) > 0) {
            upper = other.upperBound;
            upperOpen = other.upperBoundOpen;
        } else if (upperBound.compareTo(other.upperBound) < 0) {
            upper = upperBound;
            upperOpen = upperBoundOpen;
        } else {
            upper = upperBound;
            upperOpen = upperBoundOpen || other.upperBoundOpen;
        }

        C lower;
        boolean lowerOpen;
        if (lowerBound == null) {
            lower = other.lowerBound;
            lowerOpen = other.lowerBoundOpen;

        } else if (other.lowerBound == null) {
            lower = lowerBound;
            lowerOpen = lowerBoundOpen;
        } else if (lowerBound.compareTo(other.lowerBound) < 0) {
            lower = other.lowerBound;
            lowerOpen = other.lowerBoundOpen;
        } else if (lowerBound.compareTo(other.lowerBound) > 0) {
            lower = lowerBound;
            lowerOpen = lowerBoundOpen;
        } else {
            lower = lowerBound;
            lowerOpen = lowerBoundOpen || other.lowerBoundOpen;
        }
        if (lower == null || upper == null) {
            return new Range<>(lower, upper, lowerOpen, upperOpen);
        }
        if (lower.compareTo(upper) > 0 || (lower.compareTo(upper) == 0 && lowerOpen && upperOpen)) {
            return new Range<>(upper, upper, true, false);
        }
        return new Range<>(lower, upper, lowerOpen, upperOpen);
    }

    /** Returns true if this range contains no values (e.g. {@code (x, x]}). */
    public boolean isEmpty() {
        return lowerBound != null && lowerBoundOpen != upperBoundOpen && Objects.equals(lowerBound, upperBound);
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "EMPTY";
        }
        StringBuilder builder = new StringBuilder();
        if (lowerBoundOpen) {
            builder.append("(");
        } else {
            builder.append("[");
        }
        if (lowerBound == null) {
            builder.append("-INF");
        } else {
            builder.append(lowerBound);
        }
        builder.append(", ");
        if (upperBound == null) {
            builder.append("INF");
        } else {
            builder.append(upperBound);
        }
        if (upperBoundOpen) {
            builder.append(")");
        } else {
            builder.append("]");
        }
        return builder.toString();
    }
}
