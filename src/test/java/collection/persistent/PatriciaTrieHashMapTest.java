package collection.persistent;

public class PatriciaTrieHashMapTest extends MapTest {
  @Override
  <K, V> Map<K, V> create() {
    return new PatriciaTrieHashMap<K, V>();
  }
}
