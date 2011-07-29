package collection.persistent.fingertree;

public abstract class FingerTree<M extends Monoid<M>, T extends Measured<M>>
    implements Measured<M> {
  abstract FingerTree<M, T> cons(T v);

  abstract FingerTree<M, T> snoc(T v);

  public static final class Empty<M extends Monoid<M>, T extends Measured<M>>
      extends FingerTree<M, T> {
    final M m;

    public Empty(M m) {
      this.m = m;
    }

    @Override
    FingerTree<M, T> cons(T v) {
      return new Single<M, T>(v);
    }

    @Override
    FingerTree<M, T> snoc(T v) {
      return new Single<M, T>(v);
    }

    @Override
    public M measure() {
      return m;
    }
  }
}
