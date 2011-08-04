package collection.persistent;

import org.junit.Test;

import static org.junit.Assert.*;

public abstract class QueueTest {
  abstract <T> Queue<T> create();

  @Test
  public void queue() {
    Queue<Integer> q = create();
    assertEquals(0, q.size());
    assertTrue(q.isEmpty());
    q = q.push(1).push(2).push(3);
    assertEquals(3, q.size());
    assertFalse(q.isEmpty());
    assertEquals(1, (int) q.peek());
    q = q.pop();
    assertEquals(2, q.size());
    assertFalse(q.isEmpty());
    q = q.push(4).push(5).push(6);
    assertEquals(5, q.size());
    assertFalse(q.isEmpty());
    assertEquals(2, (int) q.peek());
    q = q.pop();
    assertEquals(3, (int) q.peek());
    q = q.pop();
    assertEquals(4, (int) q.peek());
    q = q.pop();
    assertEquals(5, (int) q.peek());
    q = q.pop();
    assertEquals(6, (int) q.peek());
    q = q.pop();
    assertEquals(0, q.size());
    assertTrue(q.isEmpty());
  }
}
