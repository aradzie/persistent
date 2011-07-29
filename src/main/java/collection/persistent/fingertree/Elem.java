package collection.persistent.fingertree;

final class Elem<T> implements Measured<Elem.Size> {
  final T v;

  Elem(T v) {
    this.v = v;
  }

  @Override
  public Size measure() {
    return Size.ONE;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) { return true; }
    if (!(o instanceof Elem)) { return false; }
    Elem elem = (Elem) o;
    return !(v != null ? !v.equals(elem.v) : elem.v != null);
  }

  @Override
  public int hashCode() {
    return v != null ? v.hashCode() : 0;
  }

  @Override
  public String toString() {
    return String.valueOf(v);
  }

  static final class Size extends Number implements Monoid<Size>, Comparable<Size> {
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

    @Override
    public int compareTo(Size o) {
      return size - o.size;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) { return true; }
      if (!(o instanceof Size)) { return false; }
      return size == ((Size) o).size;
    }

    @Override
    public int hashCode() {
      return size;
    }

    @Override
    public String toString() {
      return String.valueOf(size);
    }
  }
}
