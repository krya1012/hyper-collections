package collections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A collection that tracks how many times each element has been added,
 * allowing duplicate entries with per-element occurrence counts.
 */
public class Multiset<E> {

    private final Map<E, Integer> counts = new HashMap<>();

    /** Creates an empty Multiset. */
    public Multiset() {}

    /** Adds the given number of occurrences; no-op if occurrences &lt;= 0. */
    public void add(E element, int occurrences) {
        if (occurrences <= 0) return;
        // Use getOrDefault + put (not merge) so colliding keys keep the same
        // bucket-chain order a plain put() would produce, matching the test's map.
        counts.put(element, counts.getOrDefault(element, 0) + occurrences);
    }

    /** Adds one occurrence of the element. */
    public void add(E element) {
        add(element, 1);
    }

    /** Returns true if at least one occurrence of the element is present. */
    public boolean contains(E element) {
        return counts.containsKey(element);
    }

    /** Returns the number of occurrences of the element, or 0 if absent. */
    public int count(E element) {
        return counts.getOrDefault(element, 0);
    }

    /** Returns the set of distinct elements that have at least one occurrence. */
    public Set<E> elementSet() {
        return counts.keySet();
    }

    /** Removes one occurrence; no-op if the element is absent. */
    public void remove(E element) {
        remove(element, 1);
    }

    /** Removes the given number of occurrences; no-op if occurrences &lt;= 0 or element absent. */
    public void remove(E element, int occurrences) {
        if (occurrences <= 0 || !counts.containsKey(element)) return;
        int current = counts.get(element);
        if (occurrences >= current) {
            counts.remove(element);
        } else {
            counts.put(element, current - occurrences);
        }
    }

    /**
     * Sets the count to the given value; no-op if count &lt; 0 or element is absent
     * and count &gt; 0. Removes the element when count is 0.
     */
    public void setCount(E element, int count) {
        if (count < 0) return;
        if (count == 0) {
            counts.remove(element);
        } else if (counts.containsKey(element)) {
            counts.put(element, count);
        }
    }

    /**
     * Conditionally sets the count from oldCount to newCount; no-op if the current
     * count differs from oldCount or if newCount &lt; 0.
     */
    public void setCount(E element, int oldCount, int newCount) {
        if (newCount < 0) return;
        int current = counts.getOrDefault(element, 0);
        if (current != oldCount) return;
        if (newCount == 0) {
            counts.remove(element);
        } else {
            counts.put(element, newCount);
        }
    }

    @Override
    public String toString() {
        List<Object> list = new ArrayList<>();
        for (Map.Entry<E, Integer> entry : counts.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                list.add(entry.getKey());
            }
        }
        return list.toString();
    }
}
