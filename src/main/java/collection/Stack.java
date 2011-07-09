package collection;

import java.util.NoSuchElementException;

public final class Stack<T> {
  private final T v;
  private final Stack<T> next;

  public Stack() {
    v = null;
    next = null;
  }

  private Stack(T v, Stack<T> next) {
    this.v = v;
    this.next = next;
  }

  public Stack<T> push(T v) {
    return new Stack<T>(v, this);
  }

  public boolean isEmpty() {
    return next == null;
  }

  public T peek() {
    if (isEmpty()) {
      throw new NoSuchElementException();
    }
    return v;
  }

  public Stack<T> pop() {
    if (isEmpty()) {
      throw new NoSuchElementException();
    }
    return next;
  }
}
