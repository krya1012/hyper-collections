package collections;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A bidirectional map that enforces uniqueness of both keys and values,
 * allowing lookup in either direction.
 */
public class BiMap<K, V> {

    private final Map<K, V> forward = new HashMap<>();
    private final Map<V, K> inverse = new HashMap<>();

    /** Creates an empty BiMap. */
    public BiMap() {}

    /**
     * Associates key with value; throws if either already exists.
     * @throws IllegalArgumentException if the key or value is already present
     */
    public V put(K key, V value) {
        if (forward.containsKey(key) || inverse.containsKey(value)) {
            throw new IllegalArgumentException("Key or value already exists");
        }
        forward.put(key, value);
        inverse.put(value, key);
        return null;
    }

    /**
     * Inserts all entries from the given map; throws if any key or value conflicts.
     * @throws IllegalArgumentException if any key or value already exists
     */
    public void putAll(Map<K, V> map) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    /** Returns the set of values, which is unique by BiMap invariant. */
    public Set<V> values() {
        return inverse.keySet();
    }

    /**
     * Associates key with value, evicting any conflicting existing entries first.
     */
    public V forcePut(K key, V value) {
        V oldValue = forward.remove(key);
        if (oldValue != null) inverse.remove(oldValue);
        K oldKey = inverse.remove(value);
        if (oldKey != null) forward.remove(oldKey);
        forward.put(key, value);
        inverse.put(value, key);
        return oldValue;
    }

    /** Returns a new BiMap with keys and values swapped. */
    public BiMap<V, K> inverse() {
        BiMap<V, K> result = new BiMap<>();
        for (Map.Entry<K, V> entry : forward.entrySet()) {
            result.forcePut(entry.getValue(), entry.getKey());
        }
        return result;
    }

    @Override
    public String toString() {
        return forward.toString();
    }
}
