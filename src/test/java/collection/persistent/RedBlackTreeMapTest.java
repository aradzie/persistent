package collection.persistent;

import org.junit.Test;

import java.util.Arrays;
import java.util.TreeSet;

import static org.junit.Assert.*;

public class RedBlackTreeMapTest {
  @Test
  public void smallMap() {
    Map<Integer, String> map = new RedBlackTreeMap<Integer, String>();

    assertNull(map.get(0));

    map = map.put(1, "one");
    assertEqualKeys(map, 1);
    assertEqualValues(map, "one");
    map = map.put(2, "two");
    assertEqualKeys(map, 1, 2);
    assertEqualValues(map, "one", "two");
    map = map.put(3, "three");
    assertEqualKeys(map, 1, 2, 3);
    assertEqualValues(map, "one", "two", "three");

    assertNull(map.get(0));
    assertEquals("one", map.get(1));
    assertEquals("two", map.get(2));
    assertEquals("three", map.get(3));
    assertEqualKeys(map, 1, 2, 3);
    assertEqualValues(map, "one", "two", "three");

    map = map.put(1, "uno");
    assertNull(map.get(0));
    assertEquals("uno", map.get(1));
    assertEquals("two", map.get(2));
    assertEquals("three", map.get(3));
    assertEqualKeys(map, 1, 2, 3);
    assertEqualValues(map, "uno", "two", "three");

    map = map.put(2, "due");
    assertNull(map.get(0));
    assertEquals("uno", map.get(1));
    assertEquals("due", map.get(2));
    assertEquals("three", map.get(3));
    assertEqualKeys(map, 1, 2, 3);
    assertEqualValues(map, "uno", "due", "three");

    map = map.put(3, "tre");
    assertNull(map.get(0));
    assertEquals("uno", map.get(1));
    assertEquals("due", map.get(2));
    assertEquals("tre", map.get(3));
    assertEqualKeys(map, 1, 2, 3);
    assertEqualValues(map, "uno", "due", "tre");
  }

  @Test
  public void largeMap() {
    Map<String, String> map = new RedBlackTreeMap<String, String>();

    for (int n = 0; n < 100000; n++) {
      map = map.put(Integer.toString(n, 8), Integer.toString(n, 16));
    }

    for (int n = 0; n < 100000; n++) {
      assertEquals(Integer.toString(n, 16), map.get(Integer.toString(n, 8)));
    }
  }

  static <K, V> void assertEqualKeys(Map<K, V> map, K... keys) {
    TreeSet<K> expected = new TreeSet<K>(Arrays.asList(keys));

    TreeSet<K> actual = new TreeSet<K>();
//    for (Map.Entry<K, V> entry : map) {
//      assertTrue(actual.add(entry.getKey()));
//    }
//    assertEquals(expected, actual);

    actual.clear();
    Listable.List<Map.Entry<K, V>> list = map.list();
    while (list != null) {
      assertTrue(actual.add(list.head().getKey()));
      list = list.tail();
    }
    assertEquals(expected, actual);
  }

  static <K, V> void assertEqualValues(Map<K, V> map, V... values) {
    TreeSet<V> expected = new TreeSet<V>(Arrays.asList(values));

    TreeSet<V> actual = new TreeSet<V>();
//    for (Map.Entry<K, V> entry : map) {
//      assertTrue(actual.add(entry.getValue()));
//    }
//    assertEquals(expected, actual);

    actual.clear();
    Listable.List<Map.Entry<K, V>> list = map.list();
    while (list != null) {
      assertTrue(actual.add(list.head().getValue()));
      list = list.tail();
    }
    assertEquals(expected, actual);
  }
}
