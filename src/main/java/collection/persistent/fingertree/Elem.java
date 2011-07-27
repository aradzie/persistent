package collection.persistent.fingertree;

import collection.persistent.Seq;

final class Elem<T> extends Fragment<T> {
  final T v;

  Elem(T v) {
    this.v = v;
  }

  @Override
  T head() {
    return v;
  }

  @Override
  Digit<T> asDigit() {
    throw new IllegalStateException(); // unreachable
  }

  @Override
  int size() {
    return 1;
  }

  @Override
  T get(int index) {
    if (index == 0) {
      return v;
    }
    throw new Seq.RangeException();
  }

  @Override
  Elem<T> set(int index, T v) {
    if (index == 0) {
      return new Elem<T>(v);
    }
    throw new Seq.RangeException();
  }

  @Override
  void accept(Seq.Visitor<T> visitor) {
    visitor.visit(v);
  }
}
