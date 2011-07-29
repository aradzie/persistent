package collection.persistent.fingertree;

final class Single<M extends Monoid<M>, T extends Measured<M>>
    extends FingerTree<M, T> {
  final T d;
  final M m;

  Single(T d) {
    this.d = d;
    m = d.measure();
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
        new Digit.One<M, T>(d));
  }

  @Override
  FingerTree<M, T> snoc(T v) {
    return new Deep<M, T>(
        new Digit.One<M, T>(d),
        new Empty<M, Node<M, T>>(m.unit()),
        new Digit.One<M, T>(v));
  }

  @Override
  public View<M, T> viewL() {
    return new View<M, T>(d, new FingerTree.Empty<M, T>(m.unit()));
  }

  @Override
  public View<M, T> viewR() {
    return new View<M, T>(d, new FingerTree.Empty<M, T>(m.unit()));
  }
}
