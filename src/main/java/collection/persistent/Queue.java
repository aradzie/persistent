package collection.persistent;

import java.util.NoSuchElementException;

/**
 * Trivial persistent queue implementation
 * in an imperative language with eager evaluation.
 *
 * @param <T> Element type.
 */
public final class Queue<T> {
  private final Stack<T> b;
  private final Stack<T> f;

  public Queue() {
    b = f = new Stack<T>();
  }

  private Queue(Stack<T> b, Stack<T> f) {
    this.b = b;
    this.f = f;
  }

  /** @return Number of elements in this queue instance. */
  public int size() {
    return b.size() + f.size();
  }

  /**
   * @return <code>true</code> if this queue is empty,
   *         <code>false</code> otherwise.
   */
  public boolean isEmpty() {
    return b.isEmpty() && f.isEmpty();
  }

  public Queue<T> push(T v) {
    if (isEmpty()) {
      return new Queue<T>(b, f.push(v));
    }
    else {
      return new Queue<T>(b.push(v), f);
    }
  }

  public T peek() {
    if (isEmpty()) {
      throw new NoSuchElementException();
    }
    if (f.isEmpty()) {
      return b.reverse().peek();
    }
    else {
      return f.peek();
    }
  }

  public Queue<T> pop() {
    if (isEmpty()) {
      throw new NoSuchElementException();
    }
    if (f.isEmpty()) {
      return new Queue<T>(f, b.reverse().pop());
    }
    else {
      return new Queue<T>(b, f.pop());
    }
  }
}
