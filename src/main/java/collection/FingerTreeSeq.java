package collection;

import debug.IndentingPrintWriter;
import debug.Printable;

public class FingerTreeSeq<T> implements Seq<T> {
  private final Item<T> root;

  public FingerTreeSeq() {
    root = new Item.Empty<T>();
  }

  private FingerTreeSeq(Item<T> that) {
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
    throw new UnsupportedOperationException();
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
  public T nth(int index)
      throws RangeException {
    return root.nth(index);
  }

  @Override
  public FingerTreeSeq<T> nth(int index, T v)
      throws RangeException {
    return new FingerTreeSeq<T>(root.nth(index, v));
  }

  void dump(IndentingPrintWriter w) {
    root.print(w);
    w.write("\n");
    w.flush();
  }

  private abstract static class Fragment<T> {
    abstract T head();

    abstract int size();

    abstract T nth(int index);

    abstract Fragment<T> nth(int index, T v);
  }

  private static final class Elem<T> extends Fragment<T> implements Printable {
    final T e;

    Elem(T e) {
      this.e = e;
    }

    @Override
    T head() {
      return e;
    }

    @Override
    int size() {
      return 1;
    }

    @Override
    T nth(int index) {
      if (index == 0) {
        return e;
      }
      throw new RangeException();
    }

    @Override
    Elem<T> nth(int index, T v) {
      if (index == 0) {
        return new Elem<T>(v);
      }
      throw new RangeException();
    }

    @Override
    public void print(IndentingPrintWriter w) {
      Printable.Util.print(w, e);
    }
  }

  private abstract static class Digit<T> extends Fragment<T> implements Printable {
    abstract Item.Deep<T> cons(Fragment<T> v, Item<T> item, Digit<T> digit);

    abstract Item.Deep<T> snoc(Digit<T> digit, Item<T> item, Fragment<T> v);

    @Override
    abstract Digit<T> nth(int index, T v);

    static final class One<T> extends Digit<T> {
      final Fragment<T> a;
      final int v;

      One(Fragment<T> a) {
        this.a = a;
        v = this.a.size();
      }

      @Override
      Item.Deep<T> cons(Fragment<T> v, Item<T> item, Digit<T> digit) {
        return new Item.Deep<T>(new Two<T>(v, a), item, digit);
      }

      @Override
      Item.Deep<T> snoc(Digit<T> digit, Item<T> item, Fragment<T> v) {
        return new Item.Deep<T>(digit, item, new Two<T>(a, v));
      }

      @Override
      T head() {
        return a.head();
      }

      @Override
      int size() {
        return v;
      }

      @Override
      T nth(int index) {
        if (index < a.size()) {
          return a.nth(index);
        }
        throw new RangeException();
      }

      @Override
      One<T> nth(int index, T v) {
        if (index < a.size()) {
          return new One<T>(a.nth(index, v));
        }
        throw new RangeException();
      }

      @Override
      public void print(IndentingPrintWriter w) {
        printDigit(w, "One", this, a);
      }
    }

    static final class Two<T> extends Digit<T> {
      final Fragment<T> a;
      final Fragment<T> b;
      final int v;

      Two(Fragment<T> a, Fragment<T> b) {
        this.a = a;
        this.b = b;
        v = this.a.size() + this.b.size();
      }

      @Override
      Item.Deep<T> cons(Fragment<T> v, Item<T> item, Digit<T> digit) {
        return new Item.Deep<T>(new Three<T>(v, a, b), item, digit);
      }

      @Override
      Item.Deep<T> snoc(Digit<T> digit, Item<T> item, Fragment<T> v) {
        return new Item.Deep<T>(digit, item, new Three<T>(a, b, v));
      }

      @Override
      T head() {
        return a.head();
      }

      @Override
      int size() {
        return v;
      }

      @Override
      T nth(int index) {
        if (index < a.size()) {
          return a.nth(index);
        }
        index -= a.size();
        if (index < b.size()) {
          return b.nth(index);
        }
        throw new RangeException();
      }

      @Override
      Two<T> nth(int index, T v) {
        if (index < a.size()) {
          return new Two<T>(a.nth(index, v), b);
        }
        index -= a.size();
        if (index < b.size()) {
          return new Two<T>(a, b.nth(index, v));
        }
        throw new RangeException();
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
      final int v;

      Three(Fragment<T> a, Fragment<T> b, Fragment<T> c) {
        this.a = a;
        this.b = b;
        this.c = c;
        v = this.a.size() + this.b.size() + this.c.size();
      }

      @Override
      Item.Deep<T> cons(Fragment<T> v, Item<T> item, Digit<T> digit) {
        return new Item.Deep<T>(new Four<T>(v, a, b, c), item, digit);
      }

      @Override
      Item.Deep<T> snoc(Digit<T> digit, Item<T> item, Fragment<T> v) {
        return new Item.Deep<T>(digit, item, new Four<T>(a, b, c, v));
      }

      @Override
      T head() {
        return a.head();
      }

      @Override
      int size() {
        return v;
      }

      @Override
      T nth(int index) {
        if (index < a.size()) {
          return a.nth(index);
        }
        index -= a.size();
        if (index < b.size()) {
          return b.nth(index);
        }
        index -= b.size();
        if (index < c.size()) {
          return c.nth(index);
        }
        throw new RangeException();
      }

      @Override
      Three<T> nth(int index, T v) {
        if (index < a.size()) {
          return new Three<T>(a.nth(index, v), b, c);
        }
        index -= a.size();
        if (index < b.size()) {
          return new Three<T>(a, b.nth(index, v), c);
        }
        index -= b.size();
        if (index < c.size()) {
          return new Three<T>(a, b, c.nth(index, v));
        }
        throw new RangeException();
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
      final int v;

      Four(Fragment<T> a, Fragment<T> b, Fragment<T> c, Fragment<T> d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        v = this.a.size() + this.b.size() + this.c.size() + this.d.size();
      }

      @Override
      Item.Deep<T> cons(Fragment<T> v, Item<T> item, Digit<T> digit) {
        return new Item.Deep<T>(
            new Digit.Two<T>(v, a),
            item.cons(new Node.Node3<T>(b, c, d)),
            digit);
      }

      @Override
      Item.Deep<T> snoc(Digit<T> digit, Item<T> item, Fragment<T> v) {
        return new Item.Deep<T>(
            digit,
            item.snoc(new Node.Node3<T>(a, b, c)),
            new Digit.Two<T>(d, v));
      }

      @Override
      T head() {
        return a.head();
      }

      @Override
      int size() {
        return v;
      }

      @Override
      T nth(int index) {
        if (index < a.size()) {
          return a.nth(index);
        }
        index -= a.size();
        if (index < b.size()) {
          return b.nth(index);
        }
        index -= b.size();
        if (index < c.size()) {
          return c.nth(index);
        }
        index -= c.size();
        if (index < d.size()) {
          return d.nth(index);
        }
        throw new RangeException();
      }

      @Override
      Four<T> nth(int index, T v) {
        if (index < a.size()) {
          return new Four<T>(a.nth(index, v), b, c, d);
        }
        index -= a.size();
        if (index < b.size()) {
          return new Four<T>(a, b.nth(index, v), c, d);
        }
        index -= b.size();
        if (index < c.size()) {
          return new Four<T>(a, b, c.nth(index, v), d);
        }
        index -= c.size();
        if (index < d.size()) {
          return new Four<T>(a, b, c, d.nth(index, v));
        }
        throw new RangeException();
      }

      @Override
      public void print(IndentingPrintWriter w) {
        printDigit(w, "Four", this, a, b, c, d);
      }
    }
  }

  private abstract static class Node<T> extends Fragment<T> implements Printable {
    @Override
    abstract Node<T> nth(int index, T v);

    static final class Node2<T> extends Node<T> {
      final Fragment<T> a, b;
      final int v;

      Node2(Fragment<T> a, Fragment<T> b) {
        this.a = a;
        this.b = b;
        v = a.size() + b.size();
      }

      @Override
      T head() {
        return a.head();
      }

      @Override
      int size() {
        return v;
      }

      @Override
      T nth(int index) {
        if (index < a.size()) {
          return a.nth(index);
        }
        index -= a.size();
        if (index < b.size()) {
          return b.nth(index);
        }
        throw new RangeException();
      }

      @Override
      Node2<T> nth(int index, T v) {
        if (index < a.size()) {
          return new Node2<T>(a.nth(index, v), b);
        }
        index -= a.size();
        if (index < b.size()) {
          return new Node2<T>(a, b.nth(index, v));
        }
        throw new RangeException();
      }

      @Override
      public void print(IndentingPrintWriter w) {
        printNode(w, "Two", this, a, b);
      }
    }

    static final class Node3<T> extends Node<T> {
      final Fragment<T> a, b, c;
      final int v;

      Node3(Fragment<T> a, Fragment<T> b, Fragment<T> c) {
        this.a = a;
        this.b = b;
        this.c = c;
        v = a.size() + b.size() + c.size();
      }

      @Override
      T head() {
        return a.head();
      }

      @Override
      int size() {
        return v;
      }

      @Override
      T nth(int index) {
        if (index < a.size()) {
          return a.nth(index);
        }
        index -= a.size();
        if (index < b.size()) {
          return b.nth(index);
        }
        index -= b.size();
        if (index < c.size()) {
          return c.nth(index);
        }
        throw new RangeException();
      }

      @Override
      Node3<T> nth(int index, T v) {
        if (index < a.size()) {
          return new Node3<T>(a.nth(index, v), b, c);
        }
        index -= a.size();
        if (index < b.size()) {
          return new Node3<T>(a, b.nth(index, v), c);
        }
        index -= b.size();
        if (index < c.size()) {
          return new Node3<T>(a, b, c.nth(index, v));
        }
        throw new RangeException();
      }

      @Override
      public void print(IndentingPrintWriter w) {
        printNode(w, "Three", this, a, b, c);
      }
    }
  }

  private abstract static class Item<T> extends Fragment<T> implements Printable {
    abstract Item<T> cons(Fragment<T> v);

    abstract Item<T> snoc(Fragment<T> v);

    @Override
    abstract Item<T> nth(int index, T v);

    static final class Empty<T> extends Item<T> {
      @Override
      Single<T> cons(Fragment<T> v) {
        return new Item.Single<T>(v);
      }

      @Override
      Single<T> snoc(Fragment<T> v) {
        return new Item.Single<T>(v);
      }

      @Override
      T head() {
        throw new RangeException();
      }

      @Override
      int size() {
        return 0;
      }

      @Override
      T nth(int index) {
        throw new RangeException();
      }

      @Override
      Empty<T> nth(int index, T v) {
        throw new RangeException();
      }

      @Override
      public void print(IndentingPrintWriter w) {
        w.write("[EMPTY]");
      }
    }

    static final class Single<T> extends Item<T> {
      final Fragment<T> f;

      Single(Fragment<T> f) {
        this.f = f;
      }

      @Override
      Deep<T> cons(Fragment<T> v) {
        return new Item.Deep<T>(
            new Digit.One<T>(v),
            new Item.Empty<T>(),
            new Digit.One<T>(f));
      }

      @Override
      Deep<T> snoc(Fragment<T> v) {
        return new Item.Deep<T>(
            new Digit.One<T>(f),
            new Item.Empty<T>(),
            new Digit.One<T>(v));
      }

      @Override
      T head() {
        return f.head();
      }

      @Override
      int size() {
        return f.size();
      }

      @Override
      T nth(int index) {
        return f.nth(index);
      }

      @Override
      Single<T> nth(int index, T v) {
        return new Single<T>(f.nth(index, v));
      }

      @Override
      public void print(IndentingPrintWriter w) {
        printSingle(w, this);
      }
    }

    static final class Deep<T> extends Item<T> {
      final Digit<T> dl;
      final Item<T> item;
      final Digit<T> dr;
      final int v;

      Deep(Digit<T> dl, Item<T> item, Digit<T> dr) {
        this.dl = dl;
        this.item = item;
        this.dr = dr;
        v = this.dl.size() + this.item.size() + this.dr.size();
      }

      @Override
      Item<T> cons(Fragment<T> v) {
        return dl.cons(v, item, dr);
      }

      @Override
      Deep<T> snoc(Fragment<T> v) {
        return dr.snoc(dl, item, v);
      }

      @Override
      T head() {
        return dl.head();
      }

      @Override
      int size() {
        return v;
      }

      @Override
      T nth(int index) {
        if (index < dl.size()) {
          return dl.nth(index);
        }
        index -= dl.size();
        if (index < item.size()) {
          return item.nth(index);
        }
        index -= item.size();
        if (index < dr.size()) {
          return dr.nth(index);
        }
        throw new RangeException();
      }

      @Override
      Deep<T> nth(int index, T v) {
        if (index < dl.size()) {
          return new Deep<T>(dl.nth(index, v), item, dr);
        }
        index -= dl.size();
        if (index < item.size()) {
          return new Deep<T>(dl, item.nth(index, v), dr);
        }
        index -= item.size();
        if (index < dr.size()) {
          return new Deep<T>(dl, item, dr.nth(index, v));
        }
        throw new RangeException();
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
                                      Item.Single<T> single) {
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
                                    Item.Deep<T> deep) {
    w.write("Deep");
    w.write("<");
    w.write(String.valueOf(deep.size()));
    w.write(">");
    w.write("(\n");
    w.indent("   |");
    deep.dl.print(w);
    w.write("\n");
    deep.item.print(w);
    w.write("\n");
    deep.dr.print(w);
    w.unindent();
    w.write("\n)");
  }
}
