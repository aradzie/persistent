package collection.persistent.fingertree;

public abstract class FingerTree<M extends Monoid<M>, T extends Measured<M>>
    implements Measured<M> {
  abstract FingerTree<M, T> cons(T v);

  abstract FingerTree<M, T> snoc(T v);

  public abstract View<M, T> viewL();

  public abstract View<M, T> viewR();

  public static final class View<M extends Monoid<M>, T extends Measured<M>> {
    final T v;
    final FingerTree<M, T> t;

    View(T v, FingerTree<M, T> t) {
      this.v = v;
      this.t = t;
    }

    public T elem() {
      return v;
    }

    public FingerTree<M, T> tree() {
      return t;
    }
  }

  FingerTree<M, T> deepL(Digit<M, T> tail, FingerTree<M, Node<M, T>> d, Digit<M, T> r) {
    if (tail != null) {
      return new Deep<M, T>(tail, d, r);
    }
    View<M, Node<M, T>> view = d.viewL();
    if (view != null) {
      return new Deep<M, T>(view.elem().toDigit(), view.tree(), r);
    }
    return r.toTreeL();
  }

  FingerTree<M, T> deepR(Digit<M, T> l, FingerTree<M, Node<M, T>> d, Digit<M, T> tail) {
    if (tail != null) {
      return new Deep<M, T>(l, d, tail);
    }
    View<M, Node<M, T>> view = d.viewR();
    if (view != null) {
      return new Deep<M, T>(l, view.tree(), view.elem().toDigit());
    }
    return l.toTreeR();
  }

  public static class Empty<M extends Monoid<M>, T extends Measured<M>>
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

    @Override
    public View<M, T> viewL() {
      return null;
    }

    @Override
    public View<M, T> viewR() {
      return null;
    }
  }
}
