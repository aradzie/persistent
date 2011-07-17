package collection.persistent;

import debug.IndentingPrintWriter;
import debug.Printable;

/**
 * A sequence based on the monoidally annotated 2-3 finger tree
 * data structure as described in the publication
 * <a href="http://www.soi.city.ac.uk/~ross/papers/FingerTree.html">Finger
 * Trees: A Simple General-purpose Data Structure</a> by Ralf Hinze and
 * Ross Paterson.
 *
 * @param <T> Element type.
 */
public final class FingerTreeSeq<T> implements Seq<T> {
  private final Tree<T> root;

  public FingerTreeSeq() {
    root = new Tree.Empty<T>();
  }

  private FingerTreeSeq(Tree<T> that) {
    root = that;
  }

  @Override
  public T head()
      throws RangeException {
    return root.head();
  }

  @Override
  public FingerTreeSeq<T> tail()
      throws RangeException {
    return new FingerTreeSeq<T>(root.tail());
  }

  @Override
  public FingerTreeSeq<T> cons(T v) {
    return new FingerTreeSeq<T>(root.cons(new Elem<T>(v)));
  }

  @Override
  public FingerTreeSeq<T> snoc(T v) {
    return new FingerTreeSeq<T>(root.snoc(new Elem<T>(v)));
  }

  @Override
  public FingerTreeSeq<T> concat(Seq<T> that) {
    if (that instanceof FingerTreeSeq) {
      return concat((FingerTreeSeq<T>) that);
    }
    else {
      throw new UnsupportedOperationException();
    }
  }

  public FingerTreeSeq<T> concat(FingerTreeSeq<T> that) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int size() {
    return root.size();
  }

  @Override
  public T get(int index)
      throws RangeException {
    return root.get(index);
  }

  @Override
  public FingerTreeSeq<T> set(int index, T v)
      throws RangeException {
    return new FingerTreeSeq<T>(root.set(index, v));
  }

  @Override
  public void accept(Visitor<T> visitor) {
    visitor.before(size());
    root.accept(visitor);
    visitor.after();
  }

  void dump(IndentingPrintWriter w) {
    root.print(w);
    w.write("\n");
    w.flush();
  }

  private abstract static class Fragment<T> {
    abstract T head();

    abstract Digit<T> digit();

    abstract int size();

    abstract T get(int index);

    abstract Fragment<T> set(int index, T v);

    abstract void accept(Seq.Visitor<T> visitor);
  }

  private static final class Elem<T> extends Fragment<T> implements Printable {
    final T v;

    Elem(T v) {
      this.v = v;
    }

    @Override
    T head() {
      return v;
    }

    @Override
    Digit<T> digit() {
      throw new IllegalStateException(); // unreachable
    }

    @Override
    int size() {
      return 1;
    }

    @Override
    T get(int index) {
      if (index == 0) {
        return v;
      }
      throw new RangeException();
    }

    @Override
    Elem<T> set(int index, T v) {
      if (index == 0) {
        return new Elem<T>(v);
      }
      throw new RangeException();
    }

    @Override
    void accept(Seq.Visitor<T> visitor) {
      visitor.visit(v);
    }

    @Override
    public void print(IndentingPrintWriter w) {
      Printable.Util.print(w, v);
    }
  }

  private abstract static class Digit<T> extends Fragment<T> implements Printable {
    abstract Tree.Deep<T> cons(Fragment<T> v, Tree<T> m, Digit<T> r);

    abstract Tree.Deep<T> snoc(Digit<T> l, Tree<T> m, Fragment<T> v);

    @Override
    abstract T head();

    abstract Tree<T> tail(Tree<T> m, Digit<T> r);

    @Override
    abstract Digit<T> digit();

    abstract Tree<T> tree();

    @Override
    abstract int size();

    @Override
    abstract T get(int index);

    @Override
    abstract Digit<T> set(int index, T v);

    static final class One<T> extends Digit<T> {
      final Fragment<T> a;
      final int size;

      One(Fragment<T> a) {
        this.a = a;
        size = this.a.size();
      }

      @Override
      Tree.Deep<T> cons(Fragment<T> v, Tree<T> m, Digit<T> r) {
        return new Tree.Deep<T>(new Two<T>(v, a), m, r);
      }

      @Override
      Tree.Deep<T> snoc(Digit<T> l, Tree<T> m, Fragment<T> v) {
        return new Tree.Deep<T>(l, m, new Two<T>(a, v));
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
      Digit<T> digit() {
        return a.digit();
      }

      @Override
      Tree<T> tree() {
        return new Tree.Single<T>(a);
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
        throw new RangeException();
      }

      @Override
      One<T> set(int index, T v) {
        if (index < a.size()) {
          return new One<T>(a.set(index, v));
        }
        throw new RangeException();
      }

      @Override
      void accept(Seq.Visitor<T> visitor) {
        a.accept(visitor);
      }

      @Override
      public void print(IndentingPrintWriter w) {
        printDigit(w, "One", this, a);
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
      Tree.Deep<T> cons(Fragment<T> v, Tree<T> m, Digit<T> r) {
        return new Tree.Deep<T>(new Three<T>(v, a, b), m, r);
      }

      @Override
      Tree.Deep<T> snoc(Digit<T> l, Tree<T> m, Fragment<T> v) {
        return new Tree.Deep<T>(l, m, new Three<T>(a, b, v));
      }

      @Override
      T head() {
        return a.head();
      }

      @Override
      Tree<T> tail(Tree<T> m, Digit<T> r) {
        return new Tree.Deep<T>(new One<T>(b), m, r);
      }

      @Override
      Digit<T> digit() {
        return a.digit();
      }

      @Override
      Tree<T> tree() {
        return new Tree.Deep<T>(
            new Digit.One<T>(a), new Tree.Empty<T>(), new Digit.One<T>(b));
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
        throw new RangeException();
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
        throw new RangeException();
      }

      @Override
      void accept(Seq.Visitor<T> visitor) {
        a.accept(visitor);
        b.accept(visitor);
      }

      @Override
      public void print(IndentingPrintWriter w) {
        printDigit(w, "Two", this, a, b);
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
      Tree.Deep<T> cons(Fragment<T> v, Tree<T> m, Digit<T> r) {
        return new Tree.Deep<T>(new Four<T>(v, a, b, c), m, r);
      }

      @Override
      Tree.Deep<T> snoc(Digit<T> l, Tree<T> m, Fragment<T> v) {
        return new Tree.Deep<T>(l, m, new Four<T>(a, b, c, v));
      }

      @Override
      T head() {
        return a.head();
      }

      @Override
      Tree<T> tail(Tree<T> m, Digit<T> r) {
        return new Tree.Deep<T>(new Two<T>(b, c), m, r);
      }

      @Override
      Digit<T> digit() {
        return a.digit();
      }

      @Override
      Tree<T> tree() {
        return new Tree.Deep<T>(
            new Digit.Two<T>(a, b), new Tree.Empty<T>(), new Digit.One<T>(c));
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
        throw new RangeException();
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
        throw new RangeException();
      }

      @Override
      void accept(Seq.Visitor<T> visitor) {
        a.accept(visitor);
        b.accept(visitor);
        c.accept(visitor);
      }

      @Override
      public void print(IndentingPrintWriter w) {
        printDigit(w, "Three", this, a, b, c);
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
      Tree.Deep<T> cons(Fragment<T> v, Tree<T> m, Digit<T> r) {
        return new Tree.Deep<T>(
            new Digit.Two<T>(v, a), m.cons(new Node.Node3<T>(b, c, d)), r);
      }

      @Override
      Tree.Deep<T> snoc(Digit<T> l, Tree<T> m, Fragment<T> v) {
        return new Tree.Deep<T>(
            l, m.snoc(new Node.Node3<T>(a, b, c)), new Digit.Two<T>(d, v));
      }

      @Override
      T head() {
        return a.head();
      }

      @Override
      Tree<T> tail(Tree<T> m, Digit<T> r) {
        return new Tree.Deep<T>(new Three<T>(b, c, d), m, r);
      }

      @Override
      Digit<T> digit() {
        return a.digit();
      }

      @Override
      Tree<T> tree() {
        return new Tree.Deep<T>(
            new Digit.Three<T>(a, b, c), new Tree.Empty<T>(), new Digit.One<T>(d));
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
        throw new RangeException();
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
        throw new RangeException();
      }

      @Override
      void accept(Seq.Visitor<T> visitor) {
        a.accept(visitor);
        b.accept(visitor);
        c.accept(visitor);
        d.accept(visitor);
      }

      @Override
      public void print(IndentingPrintWriter w) {
        printDigit(w, "Four", this, a, b, c, d);
      }
    }
  }

  private abstract static class Node<T> extends Fragment<T> implements Printable {
    @Override
    abstract Digit<T> digit();

    @Override
    abstract Node<T> set(int index, T v);

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
      Digit<T> digit() {
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
        throw new RangeException();
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
        throw new RangeException();
      }

      @Override
      void accept(Seq.Visitor<T> visitor) {
        a.accept(visitor);
        b.accept(visitor);
      }

      @Override
      public void print(IndentingPrintWriter w) {
        printNode(w, "Two", this, a, b);
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
      Digit<T> digit() {
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
        throw new RangeException();
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
        throw new RangeException();
      }

      @Override
      void accept(Seq.Visitor<T> visitor) {
        a.accept(visitor);
        b.accept(visitor);
        c.accept(visitor);
      }

      @Override
      public void print(IndentingPrintWriter w) {
        printNode(w, "Three", this, a, b, c);
      }
    }
  }

  private abstract static class Tree<T> extends Fragment<T> implements Printable {
    abstract Tree<T> cons(Fragment<T> v);

    abstract Tree<T> snoc(Fragment<T> v);

    abstract Tree<T> tail();

    @Override
    abstract Digit<T> digit();

    abstract Tree<T> tree(Digit<T> r);

    @Override
    abstract Tree<T> set(int index, T v);

    static final class Empty<T> extends Tree<T> {
      @Override
      Single<T> cons(Fragment<T> v) {
        return new Tree.Single<T>(v);
      }

      @Override
      Single<T> snoc(Fragment<T> v) {
        return new Tree.Single<T>(v);
      }

      @Override
      T head() {
        throw new RangeException();
      }

      @Override
      Tree<T> tail() {
        throw new RangeException();
      }

      @Override
      Digit<T> digit() {
        throw new IllegalStateException(); // unreachable
      }

      @Override
      Tree<T> tree(Digit<T> r) {
        return r.tree();
      }

      @Override
      int size() {
        return 0;
      }

      @Override
      T get(int index) {
        throw new RangeException();
      }

      @Override
      Empty<T> set(int index, T v) {
        throw new RangeException();
      }

      @Override
      void accept(Seq.Visitor<T> visitor) {}

      @Override
      public void print(IndentingPrintWriter w) {
        w.write("[EMPTY]");
      }
    }

    static final class Single<T> extends Tree<T> {
      final Fragment<T> f;

      Single(Fragment<T> f) {
        this.f = f;
      }

      @Override
      Deep<T> cons(Fragment<T> v) {
        return new Tree.Deep<T>(
            new Digit.One<T>(v), new Empty<T>(), new Digit.One<T>(f));
      }

      @Override
      Deep<T> snoc(Fragment<T> v) {
        return new Tree.Deep<T>(
            new Digit.One<T>(f), new Empty<T>(), new Digit.One<T>(v));
      }

      @Override
      T head() {
        return f.head();
      }

      @Override
      Tree<T> tail() {
        return new Empty<T>();
      }

      @Override
      Digit<T> digit() {
        throw new UnsupportedOperationException();
      }

      @Override
      Tree<T> tree(Digit<T> r) {
        return new Tree.Deep<T>(
            f.digit(), new Tree.Empty<T>(), r);
      }

      @Override
      int size() {
        return f.size();
      }

      @Override
      T get(int index) {
        return f.get(index);
      }

      @Override
      Single<T> set(int index, T v) {
        return new Single<T>(f.set(index, v));
      }

      @Override
      void accept(Seq.Visitor<T> visitor) {
        f.accept(visitor);
      }

      @Override
      public void print(IndentingPrintWriter w) {
        printSingle(w, this);
      }
    }

    static final class Deep<T> extends Tree<T> {
      final Digit<T> l;
      final Tree<T> m;
      final Digit<T> r;
      final int size;

      Deep(Digit<T> l, Tree<T> m, Digit<T> r) {
        this.l = l;
        this.m = m;
        this.r = r;
        size = this.l.size() + this.m.size() + this.r.size();
      }

      @Override
      Tree<T> cons(Fragment<T> v) {
        return l.cons(v, m, r);
      }

      @Override
      Deep<T> snoc(Fragment<T> v) {
        return r.snoc(l, m, v);
      }

      @Override
      T head() {
        return l.head();
      }

      @Override
      Tree<T> tail() {
        return l.tail(m, r);
      }

      @Override
      Digit<T> digit() {
        return l.digit();
      }

      @Override
      Tree<T> tree(Digit<T> r) {
        return new Tree.Deep<T>(digit(), tail(), r);
      }

      @Override
      int size() {
        return size;
      }

      @Override
      T get(int index) {
        if (index < l.size()) {
          return l.get(index);
        }
        index -= l.size();
        if (index < m.size()) {
          return m.get(index);
        }
        index -= m.size();
        if (index < r.size()) {
          return r.get(index);
        }
        throw new RangeException();
      }

      @Override
      Deep<T> set(int index, T v) {
        if (index < l.size()) {
          return new Deep<T>(l.set(index, v), m, r);
        }
        index -= l.size();
        if (index < m.size()) {
          return new Deep<T>(l, m.set(index, v), r);
        }
        index -= m.size();
        if (index < r.size()) {
          return new Deep<T>(l, m, r.set(index, v));
        }
        throw new RangeException();
      }

      @Override
      void accept(Seq.Visitor<T> visitor) {
        l.accept(visitor);
        m.accept(visitor);
        r.accept(visitor);
      }

      @Override
      public void print(IndentingPrintWriter w) {
        printDeep(w, this);
      }
    }
  }

  private static void printDigit(IndentingPrintWriter w,
                                 String name, Digit digit, Object... items) {
    w.write("Digit.");
    w.write(name);
    w.write("<");
    w.write(String.valueOf(digit.size()));
    w.write(">");
    w.write("[");
    printItems(w, items);
    w.write("]");
  }

  private static void printNode(IndentingPrintWriter w,
                                String name, Node node, Object... items) {
    w.write("Node.");
    w.write(name);
    w.write("<");
    w.write(String.valueOf(node.size()));
    w.write(">");
    w.write("[");
    printItems(w, items);
    w.write(")");
  }

  private static void printItems(IndentingPrintWriter w,
                                 Object[] items) {
    for (int i = 0; i < items.length; i++) {
      if (items[i] instanceof Node) {
        w.write("\n");
        w.indent("   ");
        Printable.Util.print(w, items[i]);
        if (i < items.length - 1) {
          w.write(",");
        }
        w.unindent();
      }
      else {
        Printable.Util.print(w, items[i]);
        if (i < items.length - 1) {
          w.write(",");
        }
      }
    }
  }

  private static <T> void printSingle(IndentingPrintWriter w,
                                      Tree.Single<T> single) {
    if (single.f instanceof Printable) {
      w.write("Single");
      w.write("<");
      w.write(String.valueOf(single.size()));
      w.write(">");
      w.write("(");
      ((Printable) single.f).print(w);
      w.write(")");
    }
    else {
      w.print(single.f);
    }
  }

  private static <T> void printDeep(IndentingPrintWriter w,
                                    Tree.Deep<T> deep) {
    w.write("Deep");
    w.write("<");
    w.write(String.valueOf(deep.size()));
    w.write(">");
    w.write("(\n");
    w.indent("   |");
    deep.l.print(w);
    w.write("\n");
    deep.m.print(w);
    w.write("\n");
    deep.r.print(w);
    w.unindent();
    w.write("\n)");
  }
}
