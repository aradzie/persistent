package collection.persistent.fingertree;

import collection.persistent.Seq;

abstract class Digit<T> extends Fragment<T> {
  abstract Deep<T> cons(Fragment<T> v, Tree<T> m, Digit<T> r);

  abstract Deep<T> snoc(Digit<T> l, Tree<T> m, Fragment<T> v);

  abstract Tree<T> tail(Tree<T> m, Digit<T> r);

  abstract Tree<T> asTree();

  static final class One<T> extends Digit<T> {
    final Fragment<T> a;
    final int size;

    One(Fragment<T> a) {
      this.a = a;
      size = this.a.size();
    }

    @Override
    Deep<T> cons(Fragment<T> v, Tree<T> m, Digit<T> r) {
      return new Deep<T>(new Two<T>(v, a), m, r);
    }

    @Override
    Deep<T> snoc(Digit<T> l, Tree<T> m, Fragment<T> v) {
      return new Deep<T>(l, m, new Two<T>(a, v));
    }

    @Override
    T head() {
      return a.head();
    }

    @Override
    Tree<T> tail(Tree<T> m, Digit<T> r) {
      return m.tree(r);
    }

    @Override
    Digit<T> asDigit() {
      return a.asDigit();
    }

    @Override
    Tree<T> asTree() {
      return new Single<T>(a);
    }

    @Override
    int size() {
      return size;
    }

    @Override
    T get(int index) {
      if (index < a.size()) {
        return a.get(index);
      }
      throw new Seq.RangeException();
    }

    @Override
    One<T> set(int index, T v) {
      if (index < a.size()) {
        return new One<T>(a.set(index, v));
      }
      throw new Seq.RangeException();
    }

    @Override
    void accept(Seq.Visitor<T> visitor) {
      a.accept(visitor);
    }
  }

  static final class Two<T> extends Digit<T> {
    final Fragment<T> a;
    final Fragment<T> b;
    final int size;

    Two(Fragment<T> a, Fragment<T> b) {
      this.a = a;
      this.b = b;
      size = this.a.size() + this.b.size();
    }

    @Override
    Deep<T> cons(Fragment<T> v, Tree<T> m, Digit<T> r) {
      return new Deep<T>(new Three<T>(v, a, b), m, r);
    }

    @Override
    Deep<T> snoc(Digit<T> l, Tree<T> m, Fragment<T> v) {
      return new Deep<T>(l, m, new Three<T>(a, b, v));
    }

    @Override
    T head() {
      return a.head();
    }

    @Override
    Tree<T> tail(Tree<T> m, Digit<T> r) {
      return new Deep<T>(new One<T>(b), m, r);
    }

    @Override
    Digit<T> asDigit() {
      return a.asDigit();
    }

    @Override
    Tree<T> asTree() {
      return new Deep<T>(
          new One<T>(a), new FingerTree<T>(), new One<T>(b));
    }

    @Override
    int size() {
      return size;
    }

    @Override
    T get(int index) {
      if (index < a.size()) {
        return a.get(index);
      }
      index -= a.size();
      if (index < b.size()) {
        return b.get(index);
      }
      throw new Seq.RangeException();
    }

    @Override
    Two<T> set(int index, T v) {
      if (index < a.size()) {
        return new Two<T>(a.set(index, v), b);
      }
      index -= a.size();
      if (index < b.size()) {
        return new Two<T>(a, b.set(index, v));
      }
      throw new Seq.RangeException();
    }

    @Override
    void accept(Seq.Visitor<T> visitor) {
      a.accept(visitor);
      b.accept(visitor);
    }
  }

  static final class Three<T> extends Digit<T> {
    final Fragment<T> a;
    final Fragment<T> b;
    final Fragment<T> c;
    final int size;

    Three(Fragment<T> a, Fragment<T> b, Fragment<T> c) {
      this.a = a;
      this.b = b;
      this.c = c;
      size = this.a.size() + this.b.size() + this.c.size();
    }

    @Override
    Deep<T> cons(Fragment<T> v, Tree<T> m, Digit<T> r) {
      return new Deep<T>(new Four<T>(v, a, b, c), m, r);
    }

    @Override
    Deep<T> snoc(Digit<T> l, Tree<T> m, Fragment<T> v) {
      return new Deep<T>(l, m, new Four<T>(a, b, c, v));
    }

    @Override
    T head() {
      return a.head();
    }

    @Override
    Tree<T> tail(Tree<T> m, Digit<T> r) {
      return new Deep<T>(new Two<T>(b, c), m, r);
    }

    @Override
    Digit<T> asDigit() {
      return a.asDigit();
    }

    @Override
    Tree<T> asTree() {
      return new Deep<T>(
          new Two<T>(a, b), new FingerTree<T>(), new One<T>(c));
    }

    @Override
    int size() {
      return size;
    }

    @Override
    T get(int index) {
      if (index < a.size()) {
        return a.get(index);
      }
      index -= a.size();
      if (index < b.size()) {
        return b.get(index);
      }
      index -= b.size();
      if (index < c.size()) {
        return c.get(index);
      }
      throw new Seq.RangeException();
    }

    @Override
    Three<T> set(int index, T v) {
      if (index < a.size()) {
        return new Three<T>(a.set(index, v), b, c);
      }
      index -= a.size();
      if (index < b.size()) {
        return new Three<T>(a, b.set(index, v), c);
      }
      index -= b.size();
      if (index < c.size()) {
        return new Three<T>(a, b, c.set(index, v));
      }
      throw new Seq.RangeException();
    }

    @Override
    void accept(Seq.Visitor<T> visitor) {
      a.accept(visitor);
      b.accept(visitor);
      c.accept(visitor);
    }
  }

  static final class Four<T> extends Digit<T> {
    final Fragment<T> a;
    final Fragment<T> b;
    final Fragment<T> c;
    final Fragment<T> d;
    final int size;

    Four(Fragment<T> a, Fragment<T> b, Fragment<T> c, Fragment<T> d) {
      this.a = a;
      this.b = b;
      this.c = c;
      this.d = d;
      size = this.a.size() + this.b.size() + this.c.size() + this.d.size();
    }

    @Override
    Deep<T> cons(Fragment<T> v, Tree<T> m, Digit<T> r) {
      return new Deep<T>(
          new Two<T>(v, a), m.cons(new Node.Node3<T>(b, c, d)), r);
    }

    @Override
    Deep<T> snoc(Digit<T> l, Tree<T> m, Fragment<T> v) {
      return new Deep<T>(
          l, m.snoc(new Node.Node3<T>(a, b, c)), new Two<T>(d, v));
    }

    @Override
    T head() {
      return a.head();
    }

    @Override
    Tree<T> tail(Tree<T> m, Digit<T> r) {
      return new Deep<T>(new Three<T>(b, c, d), m, r);
    }

    @Override
    Digit<T> asDigit() {
      return a.asDigit();
    }

    @Override
    Tree<T> asTree() {
      return new Deep<T>(
          new Three<T>(a, b, c), new FingerTree<T>(), new One<T>(d));
    }

    @Override
    int size() {
      return size;
    }

    @Override
    T get(int index) {
      if (index < a.size()) {
        return a.get(index);
      }
      index -= a.size();
      if (index < b.size()) {
        return b.get(index);
      }
      index -= b.size();
      if (index < c.size()) {
        return c.get(index);
      }
      index -= c.size();
      if (index < d.size()) {
        return d.get(index);
      }
      throw new Seq.RangeException();
    }

    @Override
    Four<T> set(int index, T v) {
      if (index < a.size()) {
        return new Four<T>(a.set(index, v), b, c, d);
      }
      index -= a.size();
      if (index < b.size()) {
        return new Four<T>(a, b.set(index, v), c, d);
      }
      index -= b.size();
      if (index < c.size()) {
        return new Four<T>(a, b, c.set(index, v), d);
      }
      index -= c.size();
      if (index < d.size()) {
        return new Four<T>(a, b, c, d.set(index, v));
      }
      throw new Seq.RangeException();
    }

    @Override
    void accept(Seq.Visitor<T> visitor) {
      a.accept(visitor);
      b.accept(visitor);
      c.accept(visitor);
      d.accept(visitor);
    }
  }
}
