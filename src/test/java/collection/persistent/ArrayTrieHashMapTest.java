package collection.persistent;

public class ArrayTrieHashMapTest extends MapTest {
  @Override
  <K, V> Map<K, V> create() {
    return new ArrayTrieHashMap<K, V>();
  }
}
