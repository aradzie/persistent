package collection.persistent;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.*;

public abstract class HashMapTest {
  abstract <K, V> Map<K, V> create();

  @Test
  public void nullKey() {
    Map<String, String> map = create();

    assertNull(map.get(null));
    assertNull(map.list());

    map = map.put(null, "null");
    assertEquals("null", map.get(null));
    //assertEquals(1, map.size());
    assertEquals(null, map.list().head().getKey());
    assertEquals("null", map.list().head().getValue());
    assertNull(map.list().tail());

    map = map.put(null, "haha");
    assertEquals("haha", map.get(null));
    //assertEquals(1, map.size());
    assertEquals(null, map.list().head().getKey());
    assertEquals("haha", map.list().head().getValue());
    assertNull(map.list().tail());

    map = map.remove(null);
    assertNull(map.get(null));
    //assertEquals(0, map.size());
    assertNull(map.list());
  }

  @Test
  public void smallMap() {
    Map<Integer, String> map = create();

    //assertEquals(0, map.size());
    assertNull(map.get(0));
    assertNull(map.get(1));
    assertNull(map.get(2));
    assertNull(map.get(3));
    assertFalse(map.iterator().hasNext());

    map = map.put(1, "one");
    map = map.put(2, "two");
    map = map.put(3, "three");
    //assertEquals(3, map.size());
    assertNull(map.get(0));
    assertEquals("one", map.get(1));
    assertEquals("two", map.get(2));
    assertEquals("three", map.get(3));
    assertEqualKeys(map, 1, 2, 3);
    assertEqualValues(map, "one", "two", "three");

    map = map.put(1, "ONE");
    //assertEquals(3, map.size());
    assertNull(map.get(0));
    assertEquals("ONE", map.get(1));
    assertEquals("two", map.get(2));
    assertEquals("three", map.get(3));
    assertEqualKeys(map, 1, 2, 3);
    assertEqualValues(map, "ONE", "two", "three");

    map = map.put(2, "TWO");
    //assertEquals(3, map.size());
    assertNull(map.get(0));
    assertEquals("ONE", map.get(1));
    assertEquals("TWO", map.get(2));
    assertEquals("three", map.get(3));
    assertEqualKeys(map, 1, 2, 3);
    assertEqualValues(map, "ONE", "TWO", "three");

    map = map.put(3, "THREE");
    //assertEquals(3, map.size());
    assertNull(map.get(0));
    assertEquals("ONE", map.get(1));
    assertEquals("TWO", map.get(2));
    assertEquals("THREE", map.get(3));
    assertEqualKeys(map, 1, 2, 3);
    assertEqualValues(map, "ONE", "TWO", "THREE");

    map = map.remove(0);
    map = map.remove(1);
    map = map.remove(2);
    map = map.remove(3);
    //assertEquals(0, map.size());
    assertNull(map.get(0));
    assertNull(map.get(1));
    assertNull(map.get(2));
    assertNull(map.get(3));
    assertFalse(map.iterator().hasNext());
  }

  @Test
  public void largeMap() {
    Map<String, String> map = create();

    for (int n = 0; n < 100000; n++) {
      map = map.put(Integer.toString(n, 8), Integer.toString(n, 16));
    }

    for (int n = 0; n < 100000; n++) {
      assertEquals(Integer.toString(n, 16), map.get(Integer.toString(n, 8)));
    }

    int count = 0;
    for (Map.Entry<String, String> entry : map) {
      assertEquals(
          Integer.valueOf(entry.getKey(), 8),
          Integer.valueOf(entry.getValue(), 16));
      count++;
    }
    assertEquals(100000, count);

    for (int n = 0; n < 100000; n++) {
      map = map.remove(Integer.toString(n, 8));
    }

    assertFalse(map.iterator().hasNext());

    for (int n = 0; n < 100000; n++) {
      assertNull(map.get(Integer.toString(n, 8)));
    }
  }

  @Test
  public void collisions() {
    class Key {
      final String key;
      final int hashCode;

      Key(String key, int hashCode) {
        this.key = key;
        this.hashCode = hashCode;
      }

      @Override
      public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof Key)) { return false; }
        return key.equals(((Key) o).key);
      }

      @Override
      public int hashCode() {
        return hashCode;
      }

      @Override
      public String toString() {
        return "{key='" + key + "\'; hashCode=" + hashCode + "}";
      }
    }

    Key a = new Key("a", 0);
    Key b = new Key("b", 0);
    Key c = new Key("c", 32);

    Map<Key, String> map = create();

    map = map.put(a, "a").put(b, "b").put(c, "c");

    //assertEquals(3, map.size());

    assertEquals("a", map.get(a));
    assertEquals("b", map.get(b));
    assertEquals("c", map.get(c));

    assertEqualKeys(map, a, b, c);
    assertEqualValues(map, "a", "b", "c");

    map = map.remove(a);

    //assertEquals(2, map.size());

    assertNull(map.get(a));
    assertEquals("b", map.get(b));
    assertEquals("c", map.get(c));

    assertEqualKeys(map, b, c);
    assertEqualValues(map, "b", "c");

    map = map.remove(b);

    //assertEquals(1, map.size());

    assertNull(map.get(a));
    assertNull(map.get(b));
    assertEquals("c", map.get(c));

    assertEqualKeys(map, c);
    assertEqualValues(map, "c");

    map = map.remove(c);

    //assertEquals(0, map.size());

    assertNull(map.get(a));
    assertNull(map.get(b));
    assertNull(map.get(c));

    assertFalse(map.iterator().hasNext());
  }

  static <K, V> void assertEqualKeys(Map<K, V> map, K... keys) {
    HashSet<K> expected = new HashSet<K>(Arrays.asList(keys));

    HashSet<K> actual = new HashSet<K>();
    for (Map.Entry<K, V> entry : map) {
      assertTrue(actual.add(entry.getKey()));
    }
    assertEquals(expected, actual);

    actual.clear();
    Listable.List<Map.Entry<K, V>> list = map.list();
    while (list != null) {
      assertTrue(actual.add(list.head().getKey()));
      list = list.tail();
    }
    assertEquals(expected, actual);
  }

  static <K, V> void assertEqualValues(Map<K, V> map, V... values) {
    HashSet<V> expected = new HashSet<V>(Arrays.asList(values));

    HashSet<V> actual = new HashSet<V>();
    for (Map.Entry<K, V> entry : map) {
      assertTrue(actual.add(entry.getValue()));
    }
    assertEquals(expected, actual);

    actual.clear();
    Listable.List<Map.Entry<K, V>> list = map.list();
    while (list != null) {
      assertTrue(actual.add(list.head().getValue()));
      list = list.tail();
    }
    assertEquals(expected, actual);
  }
}
