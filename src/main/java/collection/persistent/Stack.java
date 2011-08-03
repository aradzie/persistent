package collection.persistent;

import java.util.NoSuchElementException;

/**
 * Trivial persistent stack implementation.
 *
 * @param <T> Element type.
 */
public final class Stack<T> {
  private final T v;
  private final Stack<T> next;
  private final int size;

  /** Create new empty stack instance. */
  public Stack() {
    v = null;
    next = null;
    size = 0;
  }

  private Stack(T v, Stack<T> next) {
    this.v = v;
    this.next = next;
    this.size = 1 + this.next.size();
  }

  /** @return Number of elements in this stack instance. */
  public int size() {
    return size;
  }

  /**
   * @return <code>true</code> if this stack is empty,
   *         <code>false</code> otherwise.
   */
  public boolean isEmpty() {
    return size == 0;
  }

  /**
   * @param v An element to push to the stack.
   * @return New stack instance with the pushed element on top.
   */
  public Stack<T> push(T v) {
    return new Stack<T>(v, this);
  }

  /**
   * @return The topmost element on this stack.
   * @throws NoSuchElementException If stack is empty.
   */
  public T peek() {
    if (isEmpty()) {
      throw new NoSuchElementException();
    }
    return v;
  }

  /**
   * @return New stack instance without the topmost element.
   * @throws NoSuchElementException If stack is empty.
   */
  public Stack<T> pop() {
    if (isEmpty()) {
      throw new NoSuchElementException();
    }
    return next;
  }

  /**
   * @return New stack instance with the same elements
   *         but with reversed order.
   */
  public Stack<T> reverse() {
    if (isEmpty() || next.isEmpty()) {
      return this;
    }
    else {
      Stack<T> r = new Stack<T>();
      for (Stack<T> s = this; !s.isEmpty(); s = s.pop()) {
        r = r.push(s.peek());
      }
      return r;
    }
  }
}
