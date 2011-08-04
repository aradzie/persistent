package collection.persistent.lazy;

import org.junit.Test;

import static org.junit.Assert.*;

public class CellTest {
  @Test
  public void test() {
    Cell<String> strict =
        new Cell<String>("1",
            new Cell<String>("2",
                new Cell<String>("3",
                    null)));

    assertEquals("1", strict.value());
    assertFalse(strict.isSuspended());
    assertEquals("2", strict.tail().value());
    assertFalse(strict.tail().isSuspended());
    assertEquals("3", strict.tail().tail().value());
    assertFalse(strict.tail().tail().isSuspended());
    assertNull(strict.tail().tail().tail());

    Cell<String> lazy = Cell.concat(strict, Cell.reverse(strict));

    assertEquals("1", lazy.value());
    assertTrue(lazy.isSuspended());
    assertEquals("2", lazy.tail().value());
    assertTrue(lazy.tail().isSuspended());
    assertEquals("3", lazy.tail().tail().value());
    assertTrue(lazy.tail().tail().isSuspended());
    assertEquals("3", lazy.tail().tail().tail().value());
    assertFalse(lazy.tail().tail().tail().isSuspended());
    assertEquals("2", lazy.tail().tail().tail().tail().value());
    assertFalse(lazy.tail().tail().tail().tail().isSuspended());
    assertEquals("1", lazy.tail().tail().tail().tail().tail().value());
    assertFalse(lazy.tail().tail().tail().tail().tail().isSuspended());
    assertNull(lazy.tail().tail().tail().tail().tail().tail());

    assertFalse(lazy.isSuspended());
    assertEquals("1", lazy.value());
    assertFalse(lazy.tail().isSuspended());
    assertEquals("2", lazy.tail().value());
    assertFalse(lazy.tail().tail().isSuspended());
    assertEquals("3", lazy.tail().tail().value());
    assertFalse(lazy.tail().tail().tail().isSuspended());
    assertEquals("3", lazy.tail().tail().tail().value());
    assertFalse(lazy.tail().tail().tail().tail().isSuspended());
    assertEquals("2", lazy.tail().tail().tail().tail().value());
    assertFalse(lazy.tail().tail().tail().tail().tail().isSuspended());
    assertEquals("1", lazy.tail().tail().tail().tail().tail().value());
    assertNull(lazy.tail().tail().tail().tail().tail().tail());

    Cell<String> take = Cell.take(lazy, 3);

    assertEquals("1", take.value());
    assertTrue(take.isSuspended());
    assertEquals("2", take.tail().value());
    assertTrue(take.tail().isSuspended());
    assertEquals("3", take.tail().tail().value());
    assertTrue(take.tail().tail().isSuspended());
    assertNull(take.tail().tail().tail());

    Cell<String> drop = Cell.drop(lazy, 3).eval();

    assertEquals("3", drop.value());
    assertEquals("2", drop.tail().value());
    assertEquals("1", drop.tail().tail().value());
    assertNull(drop.tail().tail().tail());
  }

  @Test
  public void take() {
    Cell<String> take = Cell.take(Cell.of("X"), 3);

    assertEquals("X", take.value());
    assertEquals("X", take.tail().value());
    assertEquals("X", take.tail().tail().value());
    assertNull(take.tail().tail().tail());

    Cell<String> drop = Cell.drop(take, 2).eval();
    assertEquals("X", drop.value());
    assertNull(drop.tail());
  }

  @Test
  public void of() {
    Cell<String> tail = Cell.of("X");

    assertEquals("X", tail.value());
    assertEquals("X", tail.tail().value());
    assertEquals("X", tail.tail().tail().value());
    assertEquals("X", tail.tail().tail().tail().value());

    assertEquals("X", tail.value());
    assertEquals("X", tail.tail().value());
    assertEquals("X", tail.tail().tail().value());
    assertEquals("X", tail.tail().tail().tail().value());
  }
}
