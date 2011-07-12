package collection;

import org.junit.Test;

import static org.junit.Assert.*;

public class HashTreeMapTest {
  @Test
  public void small() {
    HashTreeMap<Integer, String> map = new HashTreeMap<Integer, String>();
    assertNull(map.get(0));
    assertNull(map.get(1));
    assertNull(map.get(2));
    assertNull(map.get(3));
    map = map.put(1, "one");
    map = map.put(2, "two");
    map = map.put(3, "three");
    assertNull(map.get(0));
    assertEquals("one", map.get(1));
    assertEquals("two", map.get(2));
    assertEquals("three", map.get(3));
    map = map.put(1, "ONE");
    assertNull(map.get(0));
    assertEquals("ONE", map.get(1));
    assertEquals("two", map.get(2));
    assertEquals("three", map.get(3));
    map = map.put(2, "TWO");
    assertNull(map.get(0));
    assertEquals("ONE", map.get(1));
    assertEquals("TWO", map.get(2));
    assertEquals("three", map.get(3));
    map = map.put(3, "THREE");
    assertNull(map.get(0));
    assertEquals("ONE", map.get(1));
    assertEquals("TWO", map.get(2));
    assertEquals("THREE", map.get(3));
    map = map.remove(1);
    map = map.remove(2);
    map = map.remove(3);
    assertNull(map.get(0));
    assertNull(map.get(1));
    assertNull(map.get(2));
    assertNull(map.get(3));
  }

  @Test
  public void large() {
    HashTreeMap<String, String> map = new HashTreeMap<String, String>();
    for (int n = 0; n < 100000; n++) {
      map = map.put(Integer.toString(n, 8), Integer.toString(n, 16));
    }
    for (int n = 0; n < 100000; n++) {
      assertEquals(Integer.toString(n, 16), map.get(Integer.toString(n, 8)));
    }
    for (int n = 0; n < 100000; n++) {
      map = map.remove(Integer.toString(n, 8));
    }
    for (int n = 0; n < 100000; n++) {
      assertNull(map.get(Integer.toString(n, 8)));
    }
  }

  @Test
  public void collisions() {
    class Key {
      final String key;

      Key(String key) {
        this.key = key;
      }

      @Override
      public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof Key)) { return false; }
        return key.equals(((Key) o).key);
      }

      @Override
      public int hashCode() {
        return Integer.MAX_VALUE; // Eat it!
      }
    }

    HashTreeMap<Key, String> map = new HashTreeMap<Key, String>()
        .put(new Key("a"), "a")
        .put(new Key("b"), "b")
        .put(new Key("c"), "c");

    assertEquals("a", map.get(new Key("a")));
    assertEquals("b", map.get(new Key("b")));
    assertEquals("c", map.get(new Key("c")));

    map = map.remove(new Key("a"));

    assertNull(map.get(new Key("a")));
    assertEquals("b", map.get(new Key("b")));
    assertEquals("c", map.get(new Key("c")));

    map = map.remove(new Key("b"));

    assertNull(map.get(new Key("a")));
    assertNull(map.get(new Key("b")));
    assertEquals("c", map.get(new Key("c")));

    map = map.remove(new Key("c"));

    assertNull(map.get(new Key("a")));
    assertNull(map.get(new Key("b")));
    assertNull(map.get(new Key("c")));
  }
}
