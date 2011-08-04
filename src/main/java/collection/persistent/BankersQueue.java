package collection.persistent;

import collection.persistent.lazy.Cell;

import java.util.NoSuchElementException;

public final class BankersQueue<T> implements Queue<T> {
  private final int sb;
  private final Cell<T> b;
  private final int sf;
  private final Cell<T> f;

  public BankersQueue() {
    this(0, null, 0, null);
  }

  private BankersQueue(int sb, Cell<T> b, int sf, Cell<T> f) {
    this.sb = sb;
    this.b = b;
    this.sf = sf;
    this.f = f;
  }

  @Override
  public int size() {
    return sb + sf;
  }

  @Override
  public boolean isEmpty() {
    return f == null;
  }

  @Override
  public BankersQueue<T> push(T v) {
    return check(sb + 1, new Cell<T>(v, b), sf, f);
  }

  @Override
  public T peek() {
    if (f == null) {
      throw new NoSuchElementException();
    }
    return f.value();
  }

  @Override
  public BankersQueue<T> pop() {
    if (f == null) {
      throw new NoSuchElementException();
    }
    return check(sb, b, sf - 1, f.tail());
  }

  private static <T> BankersQueue<T> check(int sb, Cell<T> b, int sf, Cell<T> f) {
    if (sb < sf) {
      return new BankersQueue<T>(sb, b, sf, f);
    }
    else {
      return new BankersQueue<T>(0, null, sb + sf, Cell.concat(f, Cell.reverse(b)));
    }
  }
}
