package collection.persistent.fingertree;

import collection.persistent.Seq;

public final class FingerTree<T> extends Tree<T> {
  @Override
  Single<T> cons(Fragment<T> v) {
    return new Single<T>(v);
  }

  @Override
  Single<T> snoc(Fragment<T> v) {
    return new Single<T>(v);
  }

  @Override
  T head() {
    throw new Seq.RangeException();
  }

  @Override
  Tree<T> tail() {
    throw new Seq.RangeException();
  }

  @Override
  Digit<T> asDigit() {
    throw new IllegalStateException(); // unreachable
  }

  @Override
  Tree<T> tree(Digit<T> r) {
    return r.asTree();
  }

  @Override
  int size() {
    return 0;
  }

  @Override
  T get(int index) {
    throw new Seq.RangeException();
  }

  @Override
  FingerTree<T> set(int index, T v) {
    throw new Seq.RangeException();
  }

  @Override
  void accept(Seq.Visitor<T> visitor) {}
}
