package collection.persistent;

import java.util.Iterator;

public interface Map<K, V> extends Listable<Map.Entry<K, V>>, Iterable<Map.Entry<K, V>> {
  V get(K key);

  Map<K, V> put(K key, V value);

  Map<K, V> remove(K key);

  int size();

  @Override
  List<Entry<K, V>> list();

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
}
