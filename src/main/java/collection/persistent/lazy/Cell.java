package collection.persistent.lazy;

/** Lazy stream of values. */
public final class Cell<T> implements Tail<T> {
  private final T value;
  private Tail<T> tail;

  public Cell(T value, Tail<T> tail) {
    this.value = value;
    this.tail = tail;
  }

  @Override
  public Cell<T> eval() {
    return this;
  }

  public T value() {
    return value;
  }

  public synchronized boolean isSuspended() {
    return !(tail == null || tail instanceof Cell<?>);
  }

  public synchronized Cell<T> tail() {
    if (isSuspended()) {
      tail = tail.eval(); // Evaluate and memoize tail.
    }
    return (Cell<T>) tail;
  }

  public static <T> Cell<T> concat(final Cell<T> a, final Tail<T> b) {
    if (a == null) {
      return b.eval();
    }
    else {
      return new Cell<T>(a.value(), new Tail<T>() {
        @Override
        public Cell<T> eval() {
          return concat(a.tail(), b);
        }
      });
    }
  }

  public static <T> Tail<T> reverse(final Cell<T> head) {
    return new Memoized<T>() {
      @Override
      protected Cell<T> doEval() {
        Cell<T> r = null;
        for (Cell<T> c = head; c != null; c = c.tail()) {
          r = new Cell<T>(c.value(), r);
        }
        return r;
      }
    };
  }

  public static <T> Cell<T> take(final Cell<T> head, final int n) {
    if (n > 0) {
      return new Cell<T>(head.value(), new Tail<T>() {
        @Override
        public Cell<T> eval() {
          return take(head.tail(), n - 1);
        }
      });
    }
    else {
      return null;
    }
  }

  public static <T> Tail<T> drop(final Cell<T> head, final int n) {
    return new Memoized<T>() {
      @Override
      protected Cell<T> doEval() {
        Cell<T> c = head;
        for (int x = 0; c != null && x < n; c = c.tail(), x++) {}
        return c;
      }
    };
  }

  public static <T> Cell<T> of(final T v) {
    Memoized<T> memoized = new Memoized<T>() {
      @Override
      public Cell<T> doEval() {
        return new Cell<T>(v, this);
      }
    };
    return memoized.eval();
  }
}
