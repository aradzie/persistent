package collection.persistent;

abstract class AbstractHashMap<K, V> implements Map<K, V> {
  protected static int keyHashCode(Object key) {
    if (key == null) {
      return 0;
    }
    return key.hashCode();
  }

  protected static boolean keysEqual(Object a, Object b) {
    return a == b || a != null && b != null && a.equals(b);
  }

  protected abstract static class Item<K, V> {
    V find(int hashCode, K key, int level) {
      return null;
    }
  }

  protected static final class Entry<K, V>
      extends Item<K, V> implements Map.Entry<K, V> {
    final int hashCode;
    final K key;
    final V value;
    final Entry<K, V> next;

    Entry(int hashCode, K key, V value, Entry<K, V> next) {
      this.hashCode = hashCode;
      this.key = key;
      this.value = value;
      this.next = next;
    }

    @Override
    public K getKey() {
      return key;
    }

    @Override
    public V getValue() {
      return value;
    }

    static <K, V> V find(Entry<K, V> entry, int hashCode, K key) {
      while (entry != null) {
        if (entry.hashCode == hashCode && keysEqual(entry.key, key)) {
          return entry.value;
        }
        entry = entry.next;
      }
      return null;
    }

    Entry<K, V> remove(int hashCode, K key) {
      if (this.hashCode == hashCode && keysEqual(this.key, key)) {
        return next;
      }
      if (next == null) {
        return this;
      }
      else {
        Entry<K, V> result = next.remove(hashCode, key);
        if (next != result) {
          return new Entry<K, V>(this.hashCode, this.key, this.value, result);
        }
        return this;
      }
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) { return true; }
      if (!(o instanceof Entry)) { return false; }
      Entry that = (Entry) o;
      if (hashCode != that.hashCode) { return false; }
      if (!key.equals(that.key)) { return false; }
      if (!value.equals(that.value)) { return false; }
      return true;
    }

    @Override
    public int hashCode() {
      int result = hashCode;
      result = 31 * result + key.hashCode();
      result = 31 * result + value.hashCode();
      return result;
    }

    @Override
    public String toString() {
      return "key=" + key + "; value=" + value;
    }
  }
}
