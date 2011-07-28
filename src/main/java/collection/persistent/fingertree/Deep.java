package collection.persistent.fingertree;

final class Deep<T> extends FingerTree<T> {
  final Digit<T> l;
  final FingerTree<Node<T>> m;
  final Digit<T> r;

  Deep(Digit<T> l, FingerTree<Node<T>> m, Digit<T> r) {
    this.l = l;
    this.m = m;
    this.r = r;
  }

  @Override
  FingerTree<T> cons(T v) {
    return l.consImpl(v, m, r);
  }

  @Override
  FingerTree<T> snoc(T v) {
    return r.snocImpl(l, m, v);
  }
}
