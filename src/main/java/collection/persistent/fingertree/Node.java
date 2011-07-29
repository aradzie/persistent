package collection.persistent.fingertree;

abstract class Node<M extends Monoid<M>, T extends Measured<M>>
    implements Measured<M> {
  static final class Node2<M extends Monoid<M>, T extends Measured<M>>
      extends Node<M, T> {
    final T a, b;
    final M m;

    Node2(T a, T b) {
      this.a = a;
      this.b = b;
      m = a.measure()
          .combine(b.measure());
    }

    @Override
    public M measure() {
      return m;
    }
  }

  static final class Node3<M extends Monoid<M>, T extends Measured<M>>
      extends Node<M, T> {
    final T a, b, c;
    final M m;

    Node3(T a, T b, T c) {
      this.a = a;
      this.b = b;
      this.c = c;
      m = a.measure()
          .combine(b.measure())
          .combine(c.measure());
    }

    @Override
    public M measure() {
      return m;
    }
  }
}
