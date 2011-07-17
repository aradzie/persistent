package collection.persistent.fingertree;

public interface Monoid<T extends Monoid<T>> {
  /**
   * @return An identity value that is when combined with any
   *         other value is just that other value.
   */
  T unit();

  /**
   * An associative combining operation. The operation must obey
   * the following rules in order for a class to be a proper monoid:
   * <ul>
   * <li>assert m.combine(m.unit()).equals(m);</li>
   * <li>assert m.unit().combine(m).equals(m);</li>
   * <li>assert a.combine(b).combine(c).equals(a.combine(b.combine(c)));</li>
   * </ul>
   *
   * @param that Other monoid value.
   * @return Combined monoid value.
   */
  T combine(T that);

  /**
   * A specific monoid instance over set of strings with empty string as the unit
   * and concatenation as the combining operation.
   */
  final class StringConcat implements Monoid<StringConcat>, CharSequence {
    public static final StringConcat UNIT = new StringConcat();
    private final String v;

    private StringConcat() {
      v = "";
    }

    public StringConcat(String v) {
      this.v = v;
    }

    @Override
    public StringConcat unit() {
      return UNIT;
    }

    @Override
    public StringConcat combine(StringConcat that) {
      if (this == UNIT) {
        return that;
      }
      if (that == UNIT) {
        return this;
      }
      return new StringConcat(v + that.v);
    }

    @Override
    public int length() {
      return v.length();
    }

    @Override
    public char charAt(int index) {
      return v.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
      return v.subSequence(start, end);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) { return true; }
      if (!(o instanceof StringConcat)) { return false; }
      StringConcat that = (StringConcat) o;
      return v.equals(that.v);
    }

    @Override
    public int hashCode() {
      return v.hashCode();
    }
  }

  /**
   * A specific monoid instance over set of integers with 0 (zero) as the unit
   * and summation as the combining operation.
   */
  final class IntegerSum extends Number implements Monoid<IntegerSum> {
    public static final IntegerSum UNIT = new IntegerSum();
    private final int v;

    private IntegerSum() {
      v = 0;
    }

    public IntegerSum(int v) {
      this.v = v;
    }

    @Override
    public IntegerSum unit() {
      return UNIT;
    }

    @Override
    public IntegerSum combine(IntegerSum that) {
      if (this == UNIT) {
        return that;
      }
      if (that == UNIT) {
        return this;
      }
      return new IntegerSum(v + that.v);
    }

    @Override
    public int intValue() {
      return v;
    }

    @Override
    public long longValue() {
      return v;
    }

    @Override
    public float floatValue() {
      return v;
    }

    @Override
    public double doubleValue() {
      return v;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) { return true; }
      if (!(o instanceof IntegerSum)) { return false; }
      IntegerSum that = (IntegerSum) o;
      return v == that.v;
    }

    @Override
    public int hashCode() {
      return v;
    }
  }

  /**
   * A specific monoid instance over set of integers with 1 (one) as the unit
   * and multiplication as the combining operation.
   */
  final class IntegerMul extends Number implements Monoid<IntegerMul> {
    public static final IntegerMul UNIT = new IntegerMul();
    private final int v;

    private IntegerMul() {
      v = 1;
    }

    public IntegerMul(int v) {
      this.v = v;
    }

    @Override
    public IntegerMul unit() {
      return UNIT;
    }

    @Override
    public IntegerMul combine(IntegerMul that) {
      if (this == UNIT) {
        return that;
      }
      if (that == UNIT) {
        return this;
      }
      return new IntegerMul(v * that.v);
    }

    @Override
    public int intValue() {
      return v;
    }

    @Override
    public long longValue() {
      return v;
    }

    @Override
    public float floatValue() {
      return v;
    }

    @Override
    public double doubleValue() {
      return v;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) { return true; }
      if (!(o instanceof IntegerMul)) { return false; }
      IntegerMul that = (IntegerMul) o;
      return v == that.v;
    }

    @Override
    public int hashCode() {
      return v;
    }
  }

  /**
   * A specific monoid instance over set of integers with maximal value as the unit
   * and minimal element selection as the combining operation.
   */
  final class IntegerMin extends Number implements Monoid<IntegerMin> {
    public static final IntegerMin UNIT = new IntegerMin();
    private final int v;

    private IntegerMin() {
      v = Integer.MAX_VALUE;
    }

    public IntegerMin(int v) {
      this.v = v;
    }

    @Override
    public IntegerMin unit() {
      return UNIT;
    }

    @Override
    public IntegerMin combine(IntegerMin that) {
      if (this == UNIT) {
        return that;
      }
      if (that == UNIT) {
        return this;
      }
      return new IntegerMin(Math.min(v, that.v));
    }

    @Override
    public int intValue() {
      return v;
    }

    @Override
    public long longValue() {
      return v;
    }

    @Override
    public float floatValue() {
      return v;
    }

    @Override
    public double doubleValue() {
      return v;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) { return true; }
      if (!(o instanceof IntegerMin)) { return false; }
      IntegerMin that = (IntegerMin) o;
      return v == that.v;
    }

    @Override
    public int hashCode() {
      return v;
    }
  }

  /**
   * A specific monoid instance over set of integers with minimal value as the unit
   * and maximal element selection as the combining operation.
   */
  final class IntegerMax extends Number implements Monoid<IntegerMax> {
    public static final IntegerMax UNIT = new IntegerMax();
    private final int v;

    private IntegerMax() {
      v = Integer.MIN_VALUE;
    }

    public IntegerMax(int v) {
      this.v = v;
    }

    @Override
    public IntegerMax unit() {
      return UNIT;
    }

    @Override
    public IntegerMax combine(IntegerMax that) {
      if (this == UNIT) {
        return that;
      }
      if (that == UNIT) {
        return this;
      }
      return new IntegerMax(Math.max(v, that.v));
    }

    @Override
    public int intValue() {
      return v;
    }

    @Override
    public long longValue() {
      return v;
    }

    @Override
    public float floatValue() {
      return v;
    }

    @Override
    public double doubleValue() {
      return v;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) { return true; }
      if (!(o instanceof IntegerMax)) { return false; }
      IntegerMax that = (IntegerMax) o;
      return v == that.v;
    }

    @Override
    public int hashCode() {
      return v;
    }
  }
}
