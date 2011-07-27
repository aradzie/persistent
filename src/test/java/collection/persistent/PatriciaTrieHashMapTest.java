package collection.persistent;

public class PatriciaTrieHashMapTest extends HashMapTest {
  @Override
  <K, V> Map<K, V> create() {
    return new PatriciaTrieHashMap<K, V>();
  }
}
