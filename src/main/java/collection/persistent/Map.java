package collection.persistent;

import javax.annotation.Nullable;
import java.util.Iterator;

public interface Map<K, V> extends Listable<Map.Entry<K, V>>,
    Iterable<Map.Entry<K, V>> {
  @Nullable
  V get(K key);

  Map<K, V> put(K key, V value);

  Map<K, V> remove(K key);

  /**
   * Please note that the returned iterator is not thread-safe
   * therefore it should not be shared between threads, otherwise
   * its behaviour is undefined.
   *
   * @return A thread-unsafe iterator over map entries.
   */
  @Override
  Iterator<Entry<K, V>> iterator();

  interface Entry<K, V> {
    K getKey();

    V getValue();

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();
  }

  /**
   * Fast and efficient single-threaded map builder
   * it uses mutable structures internally to speed up
   * construction of immutable maps.
   *
   * @param <K> Key type.
   * @param <V> Value type.
   */
  interface Builder<K, V> {
    /**
     * Add key/value mapping to the builder, replacing
     * any previously created mapping for the same key.
     *
     * @param key   A key.
     * @param value A value to associate with the key.
     * @return This builder instance.
     */
    Builder<K, V> put(K key, V value);

    /**
     * Build persistent map from the added keys and values.
     *
     * @return Persistent {@link Map map} built from
     *         the added key/value pairs.
     * @throws IllegalStateException If a map was built before.
     */
    Map<K, V> build();
  }
}
