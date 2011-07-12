package collection;

public interface Map<K, V> {
  V get(K key);

  Map<K, V> put(K key, V value);

  Map<K, V> remove(K key);

  final class Entry<K, V> {
    private final K key;
    private final V value;

    public Entry(K key, V value) {
      this.key = key;
      this.value = value;
    }

    public K getKey() {
      return key;
    }

    public V getValue() {
      return value;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) { return true; }
      if (!(o instanceof Entry)) { return false; }
      Entry that = (Entry) o;
      return key.equals(that.key);
    }

    @Override
    public int hashCode() {
      return key.hashCode();
    }
  }
}
