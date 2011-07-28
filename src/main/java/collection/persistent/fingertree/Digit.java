package collection.persistent.fingertree;

abstract class Digit<T> {
  abstract Deep<T> consImpl(T v, FingerTree<Node<T>> m, Digit<T> r);

  abstract Deep<T> snocImpl(Digit<T> l, FingerTree<Node<T>> m, T v);

  static final class One<T> extends Digit<T> {
    final T a;

    One(T a) {
      this.a = a;
    }

    @Override
    Deep<T> consImpl(T v, FingerTree<Node<T>> m, Digit<T> r) {
      return new Deep<T>(new Two<T>(v, a), m, r);
    }

    @Override
    Deep<T> snocImpl(Digit<T> l, FingerTree<Node<T>> m, T v) {
      return new Deep<T>(l, m, new Two<T>(a, v));
    }
  }

  static final class Two<T> extends Digit<T> {
    final T a;
    final T b;

    Two(T a, T b) {
      this.a = a;
      this.b = b;
    }

    @Override
    Deep<T> consImpl(T v, FingerTree<Node<T>> m, Digit<T> r) {
      return new Deep<T>(new Three<T>(v, a, b), m, r);
    }

    @Override
    Deep<T> snocImpl(Digit<T> l, FingerTree<Node<T>> m, T v) {
      return new Deep<T>(l, m, new Three<T>(a, b, v));
    }
  }

  static final class Three<T> extends Digit<T> {
    final T a;
    final T b;
    final T c;

    Three(T a, T b, T c) {
      this.a = a;
      this.b = b;
      this.c = c;
    }

    @Override
    Deep<T> consImpl(T v, FingerTree<Node<T>> m, Digit<T> r) {
      return new Deep<T>(new Four<T>(v, a, b, c), m, r);
    }

    @Override
    Deep<T> snocImpl(Digit<T> l, FingerTree<Node<T>> m, T v) {
      return new Deep<T>(l, m, new Four<T>(a, b, c, v));
    }
  }

  static final class Four<T> extends Digit<T> {
    final T a;
    final T b;
    final T c;
    final T d;

    Four(T a, T b, T c, T d) {
      this.a = a;
      this.b = b;
      this.c = c;
      this.d = d;
    }

    @Override
    Deep<T> consImpl(T v, FingerTree<Node<T>> m, Digit<T> r) {
      return new Deep<T>(
          new Two<T>(v, a), m.cons(new Node.Node3<T>(b, c, d)), r);
    }

    @Override
    Deep<T> snocImpl(Digit<T> l, FingerTree<Node<T>> m, T v) {
      return new Deep<T>(
          l, m.snoc(new Node.Node3<T>(a, b, c)), new Two<T>(d, v));
    }
  }
}
