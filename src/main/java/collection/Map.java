package collection;

public interface Map<K, V> extends Iterable<Map.Entry<K, V>> {
  V get(K key);

  Map<K, V> put(K key, V value);

  Map<K, V> remove(K key);

  interface Entry<K, V> {
    K getKey();

    V getValue();

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();
  }
}
