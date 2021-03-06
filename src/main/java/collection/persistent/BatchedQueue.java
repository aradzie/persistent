package collection.persistent;

import java.util.NoSuchElementException;

/**
 * Trivial persistent queue implementation
 * in an imperative language with eager evaluation.
 *
 * @param <T> Element type.
 */
public final class BatchedQueue<T> implements Queue<T> {
  private final Stack<T> b;
  private final Stack<T> f;

  public BatchedQueue() {
    b = f = new Stack<T>();
  }

  private BatchedQueue(Stack<T> b, Stack<T> f) {
    this.b = b;
    this.f = f;
  }

  @Override
  public int size() {
    return b.size() + f.size();
  }

  @Override
  public boolean isEmpty() {
    return f.isEmpty();
  }

  @Override
  public BatchedQueue<T> push(T v) {
    return check(b.push(v), f);
  }

  @Override
  public T peek() {
    if (isEmpty()) {
      throw new NoSuchElementException();
    }
    return f.peek();
  }

  @Override
  public BatchedQueue<T> pop() {
    if (isEmpty()) {
      throw new NoSuchElementException();
    }
    return check(b, f.pop());
  }

  private static <T> BatchedQueue<T> check(Stack<T> b, Stack<T> f) {
    if (f.isEmpty()) {
      return new BatchedQueue<T>(f, b.reverse());
    }
    else {
      return new BatchedQueue<T>(b, f);
    }
  }
}
