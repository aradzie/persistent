package collection.persistent.fingertree;

import collection.persistent.Seq;

final class Deep<T> extends Tree<T> {
  final Digit<T> l;
  final Tree<T> m;
  final Digit<T> r;
  final int size;

  Deep(Digit<T> l, Tree<T> m, Digit<T> r) {
    this.l = l;
    this.m = m;
    this.r = r;
    size = this.l.size() + this.m.size() + this.r.size();
  }

  @Override
  Tree<T> cons(Fragment<T> v) {
    return l.cons(v, m, r);
  }

  @Override
  Deep<T> snoc(Fragment<T> v) {
    return r.snoc(l, m, v);
  }

  @Override
  T head() {
    return l.head();
  }

  @Override
  Tree<T> tail() {
    return l.tail(m, r);
  }

  @Override
  Digit<T> asDigit() {
    return l.asDigit();
  }

  @Override
  Tree<T> tree(Digit<T> r) {
    return new Deep<T>(asDigit(), tail(), r);
  }

  @Override
  int size() {
    return size;
  }

  @Override
  T get(int index) {
    if (index < l.size()) {
      return l.get(index);
    }
    index -= l.size();
    if (index < m.size()) {
      return m.get(index);
    }
    index -= m.size();
    if (index < r.size()) {
      return r.get(index);
    }
    throw new Seq.RangeException();
  }

  @Override
  Deep<T> set(int index, T v) {
    if (index < l.size()) {
      return new Deep<T>(l.set(index, v), m, r);
    }
    index -= l.size();
    if (index < m.size()) {
      return new Deep<T>(l, m.set(index, v), r);
    }
    index -= m.size();
    if (index < r.size()) {
      return new Deep<T>(l, m, r.set(index, v));
    }
    throw new Seq.RangeException();
  }

  @Override
  void accept(Seq.Visitor<T> visitor) {
    l.accept(visitor);
    m.accept(visitor);
    r.accept(visitor);
  }
}
