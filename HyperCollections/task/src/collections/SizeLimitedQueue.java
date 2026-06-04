package collections;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;

/**
 * A fixed-capacity FIFO queue that evicts its oldest element when a new one is
 * added past the limit, so it never grows beyond {@code maxSize}.
 */
public class SizeLimitedQueue<E> {

    private final int limit;
    private final Deque<E> queue = new ArrayDeque<>();

    /**
     * Caps the queue at {@code limit} elements.
     * @throws IllegalArgumentException if {@code limit} is not positive
     */
    public SizeLimitedQueue(int limit) {
        if (limit <= 0) throw new IllegalArgumentException("limit must be positive");
        this.limit = limit;
    }

    /**
     * Appends an element, evicting the oldest first when already at capacity.
     * @throws NullPointerException if {@code element} is null
     */
    public void add(E element) {
        if (element == null) throw new NullPointerException("element must not be null");
        if (queue.size() == limit) queue.removeFirst();
        queue.addLast(element);
    }

    /**
     * Removes and returns the oldest element.
     * @throws NoSuchElementException if the queue is empty
     */
    public E remove() {
        if (queue.isEmpty()) throw new NoSuchElementException();
        return queue.removeFirst();
    }

    /** Returns the oldest element without removing it, or null if empty. */
    public E peek() {
        return queue.peekFirst();
    }

    /** Discards all elements so the queue can be reused. */
    public void clear() {
        queue.clear();
    }

    /** Returns true when the queue holds its maximum number of elements. */
    public boolean isAtFullCapacity() {
        return queue.size() == limit;
    }

    /** Returns true when the queue holds no elements. */
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    /** Returns the fixed capacity this queue was created with. */
    public int maxSize() {
        return limit;
    }

    /** Returns the current number of elements. */
    public int size() {
        return queue.size();
    }

    /** Returns the elements, oldest first, in a new Object array. */
    public Object[] toArray() {
        return queue.toArray();
    }

    /** Returns the elements, oldest first, in an array of the given array's runtime type. */
    public E[] toArray(E[] a) {
        return queue.toArray(a);
    }

    @Override
    public String toString() {
        return queue.toString();
    }
}
