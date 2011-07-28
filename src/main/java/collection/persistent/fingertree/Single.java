package collection.persistent.fingertree;

final class Single<T> extends FingerTree<T> {
  final T f;

  Single(T f) {
    this.f = f;
  }

  @Override
  FingerTree<T> cons(T v) {
    return new Deep<T>(
        new Digit.One<T>(v), new Empty<Node<T>>(), new Digit.One<T>(f));
  }

  @Override
  FingerTree<T> snoc(T v) {
    return new Deep<T>(
        new Digit.One<T>(f), new Empty<Node<T>>(), new Digit.One<T>(v));
  }
}
