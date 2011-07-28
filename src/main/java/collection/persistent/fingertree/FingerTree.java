package collection.persistent.fingertree;

public abstract class FingerTree<T> {
  abstract FingerTree<T> cons(T v);

  abstract FingerTree<T> snoc(T v);

  public static final class Empty<T> extends FingerTree<T> {
    @Override
    FingerTree<T> cons(T v) {
      return new Single<T>(v);
    }

    @Override
    FingerTree<T> snoc(T v) {
      return new Single<T>(v);
    }
  }
}
