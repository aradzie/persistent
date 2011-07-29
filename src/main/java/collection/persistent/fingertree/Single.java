package collection.persistent.fingertree;

final class Single<M extends Monoid<M>, T extends Measured<M>>
    extends FingerTree<M, T> {
  final T f;
  final M m;

  Single(T f) {
    this.f = f;
    m = f.measure();
  }

  @Override
  public M measure() {
    return m;
  }

  @Override
  FingerTree<M, T> cons(T v) {
    return new Deep<M, T>(
        new Digit.One<M, T>(v),
        new Empty<M, Node<M, T>>(m.unit()),
        new Digit.One<M, T>(f));
  }

  @Override
  FingerTree<M, T> snoc(T v) {
    return new Deep<M, T>(
        new Digit.One<M, T>(f),
        new Empty<M, Node<M, T>>(m.unit()),
        new Digit.One<M, T>(v));
  }
}
