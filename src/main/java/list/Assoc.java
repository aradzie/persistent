package list;

public interface Assoc<K, V> {
  V find(K k);

  Assoc<K, V> assoc(K k, V v);

  Assoc<K, V> clear(K k);
}
