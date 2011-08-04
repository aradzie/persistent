package collection.persistent;

import collection.persistent.lazy.Cell;
import collection.persistent.lazy.Tail;

import java.util.NoSuchElementException;

public final class RealTimeQueue<T> implements Queue<T> {
  private final Cell<T> b;
  private final Cell<T> f;
  private final Cell<T> s;
  private final int size;

  public RealTimeQueue() {
    this(null, null, null, 0);
  }

  private RealTimeQueue(Cell<T> b, Cell<T> f, Cell<T> s, int size) {
    this.b = b;
    this.f = f;
    this.s = s;
    this.size = size;
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public boolean isEmpty() {
    return f == null;
  }

  @Override
  public RealTimeQueue<T> push(T v) {
    return exec(new Cell<T>(v, b), f, s, size + 1);
  }

  @Override
  public T peek() {
    if (f == null) {
      throw new NoSuchElementException();
    }
    return f.value();
  }

  @Override
  public RealTimeQueue<T> pop() {
    if (f == null) {
      throw new NoSuchElementException();
    }
    return exec(b, f.tail(), s, size - 1);
  }

  private static <T> RealTimeQueue<T> exec(Cell<T> b, Cell<T> f, Cell<T> s, int size) {
    if (s == null) {
      Cell<T> r = rotate(f, b, null);
      return new RealTimeQueue<T>(null, r, r, size);
    }
    else {
      return new RealTimeQueue<T>(b, f, s.tail(), size);
    }
  }

  private static <T> Cell<T> rotate(final Cell<T> x, final Cell<T> y, final Cell<T> a) {
    if (x == null) {
      return new Cell<T>(y.value(), a);
    }
    else {
      return new Cell<T>(x.value(), new Tail<T>() {
        @Override
        public Cell<T> eval() {
          return rotate(x.tail(), y.tail(), new Cell<T>(y.value(), a));
        }
      });
    }
  }
}
