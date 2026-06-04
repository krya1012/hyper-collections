package collections;

import java.util.Arrays;

/**
 * An unmodifiable generic collection whose contents are fixed at creation time.
 */
public final class ImmutableCollection<E> {

    private final Object[] elements;

    private ImmutableCollection(Object[] elements) {
        this.elements = elements;
    }

    /** Returns an empty immutable collection. */
    public static <E> ImmutableCollection<E> of() {
        return new ImmutableCollection<>(new Object[0]);
    }

    /**
     * Returns an immutable collection containing the given elements.
     * @throws NullPointerException if any element is null
     */
    public static <E> ImmutableCollection<E> of(E... elements) {
        Object[] copy = Arrays.copyOf(elements, elements.length);
        for (Object e : copy) {
            if (e == null) throw new NullPointerException();
        }
        return new ImmutableCollection<>(copy);
    }

    /**
     * Returns true if this collection contains the given element.
     * @throws NullPointerException if element is null
     */
    public boolean contains(Object element) {
        if (element == null) throw new NullPointerException();
        for (Object e : elements) {
            if (e.equals(element)) return true;
        }
        return false;
    }

    /** Returns the number of elements in this collection. */
    public int size() {
        return elements.length;
    }

    /** Returns true if this collection contains no elements. */
    public boolean isEmpty() {
        return elements.length == 0;
    }
}
