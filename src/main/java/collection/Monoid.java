package collection;

public interface Monoid<T extends Monoid<T>> {
  T unit();

  T combine(T that);

  class StringConcat implements Monoid<StringConcat>, CharSequence {
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
  }

  class IntegerSum extends Number implements Monoid<IntegerSum> {
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
  }

  class IntegerMul extends Number implements Monoid<IntegerMul> {
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
  }

  class IntegerMin extends Number implements Monoid<IntegerMin> {
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
  }

  class IntegerMax extends Number implements Monoid<IntegerMax> {
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
  }
}
