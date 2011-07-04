package collection;

public interface Assoc<K, V> {
  V get(K k);

  Assoc<K, V> assoc(K k, V v);

  Assoc<K, V> clear(K k);
}
