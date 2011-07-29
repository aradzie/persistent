package collection.persistent.fingertree;

final class Deep<M extends Monoid<M>, T extends Measured<M>>
    extends FingerTree<M, T> {
  final Digit<M, T> l;
  final FingerTree<M, Node<M, T>> d;
  final Digit<M, T> r;
  final M m;

  Deep(Digit<M, T> l, FingerTree<M, Node<M, T>> d, Digit<M, T> r) {
    this.l = l;
    this.d = d;
    this.r = r;
    m = l.measure()
        .combine(d.measure())
        .combine(r.measure());
  }

  @Override
  public M measure() {
    return m;
  }

  @Override
  FingerTree<M, T> cons(T v) {
    return l.consImpl(v, d, r);
  }

  @Override
  FingerTree<M, T> snoc(T v) {
    return r.snocImpl(l, d, v);
  }
}
