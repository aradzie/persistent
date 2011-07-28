package collection.persistent.fingertree;

final class Elem<T> implements Measured<Elem<T>, Elem.Size> {
  final T v;

  Elem(T v) {
    this.v = v;
  }

  @Override
  public Size measure(Elem<T> elem) {
    return Size.ONE;
  }

  static final class Size extends Number implements Monoid<Size> {
    static final Size ZERO = new Size(0);
    static final Size ONE = new Size(1);
    final int size;

    Size(int size) {
      this.size = size;
    }

    @Override
    public Size unit() {
      return ZERO;
    }

    @Override
    public Size combine(Size that) {
      return new Size(size + that.size);
    }

    @Override
    public int intValue() {
      return size;
    }

    @Override
    public long longValue() {
      return size;
    }

    @Override
    public float floatValue() {
      return size;
    }

    @Override
    public double doubleValue() {
      return size;
    }
  }
}
