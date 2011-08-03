package collection.persistent.fingertree;

import java.util.List;

abstract class Digit<M extends Monoid<M>, T extends Measured<M>>
    implements Measured<M> {
  abstract Deep<M, T> consImpl(T v, FingerTree<M, Node<M, T>> m, Digit<M, T> r);

  abstract Deep<M, T> snocImpl(Digit<M, T> l, FingerTree<M, Node<M, T>> m, T v);

  abstract T headL();

  abstract Digit<M, T> tailL();

  abstract T headR();

  abstract Digit<M, T> tailR();

  abstract FingerTree<M, T> toTreeL();

  abstract FingerTree<M, T> toTreeR();

  abstract void append(List<T> l);

  static final class One<M extends Monoid<M>, T extends Measured<M>>
      extends Digit<M, T> {
    final T a;
    final M m;

    One(T a) {
      this.a = a;
      m = a.measure();
    }

    @Override
    public M measure() {
      return m;
    }

    @Override
    Deep<M, T> consImpl(T v, FingerTree<M, Node<M, T>> m, Digit<M, T> r) {
      return new Deep<M, T>(new Two<M, T>(v, a), m, r);
    }

    @Override
    Deep<M, T> snocImpl(Digit<M, T> l, FingerTree<M, Node<M, T>> m, T v) {
      return new Deep<M, T>(l, m, new Two<M, T>(a, v));
    }

    @Override
    T headL() {
      return a;
    }

    @Override
    Digit<M, T> tailL() {
      return null;
    }

    @Override
    T headR() {
      return a;
    }

    @Override
    Digit<M, T> tailR() {
      return null;
    }

    @Override
    FingerTree<M, T> toTreeL() {
      return new Single<M, T>(a);
    }

    @Override
    FingerTree<M, T> toTreeR() {
      return new Single<M, T>(a);
    }

    @Override
    void append(List<T> l) {
      l.add(a);
    }
  }

  static final class Two<M extends Monoid<M>, T extends Measured<M>>
      extends Digit<M, T> {
    final T a;
    final T b;
    final M m;

    Two(T a, T b) {
      this.a = a;
      this.b = b;
      m = a.measure()
          .combine(b.measure());
    }

    @Override
    public M measure() {
      return m;
    }

    @Override
    Deep<M, T> consImpl(T v, FingerTree<M, Node<M, T>> m, Digit<M, T> r) {
      return new Deep<M, T>(new Three<M, T>(v, a, b), m, r);
    }

    @Override
    Deep<M, T> snocImpl(Digit<M, T> l, FingerTree<M, Node<M, T>> m, T v) {
      return new Deep<M, T>(l, m, new Three<M, T>(a, b, v));
    }

    @Override
    T headL() {
      return a;
    }

    @Override
    Digit<M, T> tailL() {
      return new One<M, T>(b);
    }

    @Override
    T headR() {
      return b;
    }

    @Override
    Digit<M, T> tailR() {
      return new One<M, T>(a);
    }

    @Override
    FingerTree<M, T> toTreeL() {
      return new Single<M, T>(a).cons(b);
    }

    @Override
    FingerTree<M, T> toTreeR() {
      return new Single<M, T>(a).snoc(b);
    }

    @Override
    void append(List<T> l) {
      l.add(a);
      l.add(b);
    }
  }

  static final class Three<M extends Monoid<M>, T extends Measured<M>>
      extends Digit<M, T> {
    final T a;
    final T b;
    final T c;
    final M m;

    Three(T a, T b, T c) {
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

    @Override
    Deep<M, T> consImpl(T v, FingerTree<M, Node<M, T>> m, Digit<M, T> r) {
      return new Deep<M, T>(new Four<M, T>(v, a, b, c), m, r);
    }

    @Override
    Deep<M, T> snocImpl(Digit<M, T> l, FingerTree<M, Node<M, T>> m, T v) {
      return new Deep<M, T>(l, m, new Four<M, T>(a, b, c, v));
    }

    @Override
    T headL() {
      return a;
    }

    @Override
    Digit<M, T> tailL() {
      return new Two<M, T>(b, c);
    }

    @Override
    T headR() {
      return c;
    }

    @Override
    Digit<M, T> tailR() {
      return new Two<M, T>(a, b);
    }

    @Override
    FingerTree<M, T> toTreeL() {
      return new Single<M, T>(a).cons(b).cons(c);
    }

    @Override
    FingerTree<M, T> toTreeR() {
      return new Single<M, T>(a).snoc(b).snoc(c);
    }

    @Override
    void append(List<T> l) {
      l.add(a);
      l.add(b);
      l.add(c);
    }
  }

  static final class Four<M extends Monoid<M>, T extends Measured<M>>
      extends Digit<M, T> {
    final T a;
    final T b;
    final T c;
    final T d;
    final M m;

    Four(T a, T b, T c, T d) {
      this.a = a;
      this.b = b;
      this.c = c;
      this.d = d;
      m = a.measure()
          .combine(b.measure())
          .combine(c.measure())
          .combine(d.measure());
    }

    @Override
    public M measure() {
      return m;
    }

    @Override
    Deep<M, T> consImpl(T v, FingerTree<M, Node<M, T>> m, Digit<M, T> r) {
      return new Deep<M, T>(
          new Two<M, T>(v, a), m.cons(new Node.Node3<M, T>(b, c, d)), r);
    }

    @Override
    Deep<M, T> snocImpl(Digit<M, T> l, FingerTree<M, Node<M, T>> m, T v) {
      return new Deep<M, T>(
          l, m.snoc(new Node.Node3<M, T>(a, b, c)), new Two<M, T>(d, v));
    }

    @Override
    T headL() {
      return a;
    }

    @Override
    Digit<M, T> tailL() {
      return new Three<M, T>(b, c, d);
    }

    @Override
    T headR() {
      return d;
    }

    @Override
    Digit<M, T> tailR() {
      return new Three<M, T>(a, b, c);
    }

    @Override
    FingerTree<M, T> toTreeL() {
      return new Single<M, T>(a).cons(b).cons(c).cons(d);
    }

    @Override
    FingerTree<M, T> toTreeR() {
      return new Single<M, T>(a).snoc(b).snoc(c).snoc(d);
    }

    @Override
    void append(List<T> l) {
      l.add(a);
      l.add(b);
      l.add(c);
      l.add(d);
    }
  }
}
