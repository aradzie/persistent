package collection.persistent.fingertree;

import collection.persistent.Seq;

final class Single<T> extends Tree<T> {
  final Fragment<T> f;

  Single(Fragment<T> f) {
    this.f = f;
  }

  @Override
  Deep<T> cons(Fragment<T> v) {
    return new Deep<T>(
        new Digit.One<T>(v), new FingerTree<T>(), new Digit.One<T>(f));
  }

  @Override
  Deep<T> snoc(Fragment<T> v) {
    return new Deep<T>(
        new Digit.One<T>(f), new FingerTree<T>(), new Digit.One<T>(v));
  }

  @Override
  T head() {
    return f.head();
  }

  @Override
  Tree<T> tail() {
    return new FingerTree<T>();
  }

  @Override
  Digit<T> asDigit() {
    throw new UnsupportedOperationException();
  }

  @Override
  Tree<T> tree(Digit<T> r) {
    return new Deep<T>(
        f.asDigit(), new FingerTree<T>(), r);
  }

  @Override
  int size() {
    return f.size();
  }

  @Override
  T get(int index) {
    return f.get(index);
  }

  @Override
  Single<T> set(int index, T v) {
    return new Single<T>(f.set(index, v));
  }

  @Override
  void accept(Seq.Visitor<T> visitor) {
    f.accept(visitor);
  }
}
