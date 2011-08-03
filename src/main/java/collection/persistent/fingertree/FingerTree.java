package collection.persistent.fingertree;

import java.util.LinkedList;
import java.util.List;

public abstract class FingerTree<M extends Monoid<M>, T extends Measured<M>>
    implements Measured<M> {
  public static <M extends Monoid<M>, T extends Measured<M>>
  FingerTree<M, T> concat(FingerTree<M, T> x, FingerTree<M, T> y) {
    return app3(x, new Seq<M, T>(), y);
  }

  static <M extends Monoid<M>, T extends Measured<M>>
  FingerTree<M, T> app3(FingerTree<M, T> a, Seq<M, T> m, FingerTree<M, T> b) {
    if (a instanceof Deep<?, ?> && b instanceof Deep<?, ?>) {
      return app3((Deep<M, T>) a, m, (Deep<M, T>) b);
    }
    else {
      if (a == null || a instanceof Empty<?, ?>) {
        return m.insR(b);
      }
      if (b == null || b instanceof Empty<?, ?>) {
        return m.insL(a);
      }
      if (a instanceof Single<?, ?>) {
        return app3(((Single<M, T>) a), m, b);
      }
      if (b instanceof Single<?, ?>) {
        return app3(a, m, ((Single<M, T>) b));
      }
      throw new IllegalStateException();
    }
  }

  static <M extends Monoid<M>, T extends Measured<M>> Deep<M, T>
  app3(Deep<M, T> a, Seq<M, T> m, Deep<M, T> b) {
    return new Deep<M, T>(a.l, app3(a.d, nodes(a.r, m, b.l), b.d), b.r);
  }

  static <M extends Monoid<M>, T extends Measured<M>> Deep<M, T>
  app3(Single<M, T> a, Seq<M, T> m, FingerTree<M, T> b) {
    throw new UnsupportedOperationException(); // TODO implement me
  }

  static <M extends Monoid<M>, T extends Measured<M>> Deep<M, T>
  app3(FingerTree<M, T> a, Seq<M, T> m, Single<M, T> b) {
    throw new UnsupportedOperationException(); // TODO implement me
  }

  static <M extends Monoid<M>, T extends Measured<M>>
  Seq<M, Node<M, T>> nodes(Digit<M, T> l, List<T> m, Digit<M, T> r) {
    Seq<M, T> x = new Seq<M, T>();
    l.append(x);
    x.addAll(m);
    r.append(x);
    return x.nodes();
  }

  static class Seq<M extends Monoid<M>, T extends Measured<M>>
      extends LinkedList<T> {
    Seq<M, Node<M, T>> nodes() {
      Seq<M, Node<M, T>> nodes = new Seq<M, Node<M, T>>();
      while (size() > 0) {
        switch (size()) {
          case 2:
            nodes.add(new Node.Node2<M, T>(remove(0), remove(0)));
            break;
          case 4:
            nodes.add(new Node.Node2<M, T>(remove(0), remove(0)));
            nodes.add(new Node.Node2<M, T>(remove(0), remove(0)));
            break;
          default:
            nodes.add(new Node.Node3<M, T>(remove(0), remove(0), remove(0)));
            break;
        }
      }
      return nodes;
    }

    FingerTree<M, T> insL(FingerTree<M, T> t) {
      for (T v : this) {
        t = t.cons(v);
      }
      return t;
    }

    FingerTree<M, T> insR(FingerTree<M, T> t) {
      for (T v : this) {
        t = t.snoc(v);
      }
      return t;
    }
  }

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
