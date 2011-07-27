package collection.persistent.fingertree;

import collection.persistent.Seq;

abstract class Node<T> extends Fragment<T> {
  static final class Node2<T> extends Node<T> {
    final Fragment<T> a, b;
    final int size;

    Node2(Fragment<T> a, Fragment<T> b) {
      this.a = a;
      this.b = b;
      size = a.size() + b.size();
    }

    @Override
    T head() {
      return a.head();
    }

    @Override
    Digit<T> asDigit() {
      return new Digit.Two<T>(a, b);
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
    Node2<T> set(int index, T v) {
      if (index < a.size()) {
        return new Node2<T>(a.set(index, v), b);
      }
      index -= a.size();
      if (index < b.size()) {
        return new Node2<T>(a, b.set(index, v));
      }
      throw new Seq.RangeException();
    }

    @Override
    void accept(Seq.Visitor<T> visitor) {
      a.accept(visitor);
      b.accept(visitor);
    }
  }

  static final class Node3<T> extends Node<T> {
    final Fragment<T> a, b, c;
    final int size;

    Node3(Fragment<T> a, Fragment<T> b, Fragment<T> c) {
      this.a = a;
      this.b = b;
      this.c = c;
      size = a.size() + b.size() + c.size();
    }

    @Override
    T head() {
      return a.head();
    }

    @Override
    Digit<T> asDigit() {
      return new Digit.Three<T>(a, b, c);
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
    Node3<T> set(int index, T v) {
      if (index < a.size()) {
        return new Node3<T>(a.set(index, v), b, c);
      }
      index -= a.size();
      if (index < b.size()) {
        return new Node3<T>(a, b.set(index, v), c);
      }
      index -= b.size();
      if (index < c.size()) {
        return new Node3<T>(a, b, c.set(index, v));
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
}
