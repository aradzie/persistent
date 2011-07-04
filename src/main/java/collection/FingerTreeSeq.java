package collection;

import debug.IndentingPrintWriter;
import debug.Printable;

public class FingerTreeSeq<T> implements Seq<T> {
  private Item<T> root;

  public FingerTreeSeq() {
    root = new Item.Empty<T>();
  }

  private FingerTreeSeq(Item<T> that) {
    root = that;
  }

  @Override
  public T head() throws EmptyException {
    return root.head();
  }

  @Override
  public Seq<T> tail() throws EmptyException {
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
  public Seq<T> concat(Seq<T> that) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int size() {
    return root.size();
  }

  @Override
  public T nth(int index) throws RangeException {
    return root.nth(index);
  }

  void dump(IndentingPrintWriter w) {
    root.print(w);
    w.flush();
  }

  private interface Measured<T> {
    int size();

    T nth(int index);

    T head();
  }

  private static class Elem<T> implements Measured<T>, Printable {
    final T e;

    Elem(T e) {
      this.e = e;
    }

    @Override
    public int size() {
      return 1;
    }

    @Override
    public T nth(int index) {
      if (index == 0) {
        return e;
      }
      throw new RangeException();
    }

    @Override
    public T head() {
      return e;
    }

    @Override
    public void print(IndentingPrintWriter w) {
      Printable.Util.print(w, e);
    }
  }

  private abstract static class Digit<T> implements Measured<T>, Printable {
    abstract Item.Deep<T> cons(Digit<T> digit, Item<T> item, Measured<T> v);

    abstract Item.Deep<T> snoc(Measured<T> v, Item<T> item, Digit<T> digit);

    static int measure(Measured... items) {
      int m = 0;
      for (Measured item : items) {
        m = m + item.size();
      }
      return m;
    }

    static void print(IndentingPrintWriter w, String name, Object... items) {
      w.write("Digit." + name + "[");
      FingerTreeSeq.print(w, items);
      w.write("]");
    }

    static class One<T> extends Digit<T> {
      final Measured<T> a;
      final int v;

      One(Measured<T> a) {
        this.a = a;
        v = measure(this.a);
      }

      @Override
      Item.Deep<T> cons(Digit<T> digit, Item<T> item, Measured<T> v) {
        return new Item.Deep<T>(digit, item, new Two<T>(a, v));
      }

      @Override
      Item.Deep<T> snoc(Measured<T> v, Item<T> item, Digit<T> digit) {
        return new Item.Deep<T>(new Two<T>(v, a), item, digit);
      }

      @Override
      public final int size() {
        return v;
      }

      @Override
      public T nth(int index) {
        if (index < a.size()) {
          return a.nth(index);
        }
        throw new RangeException();
      }

      @Override
      public T head() {
        return a.head();
      }

      @Override
      public void print(IndentingPrintWriter w) {
        print(w, getClass().getSimpleName(), a);
      }
    }

    static class Two<T> extends Digit<T> {
      final Measured<T> a;
      final Measured<T> b;
      final int v;

      Two(Measured<T> a, Measured<T> b) {
        this.a = a;
        this.b = b;
        v = measure(this.a, this.b);
      }

      @Override
      Item.Deep<T> cons(Digit<T> digit, Item<T> item, Measured<T> v) {
        return new Item.Deep<T>(digit, item, new Three<T>(a, b, v));
      }

      @Override
      Item.Deep<T> snoc(Measured<T> v, Item<T> item, Digit<T> digit) {
        return new Item.Deep<T>(new Three<T>(v, a, b), item, digit);
      }

      @Override
      public final int size() {
        return v;
      }

      @Override
      public T nth(int index) {
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
      public T head() {
        return b.head();
      }

      @Override
      public void print(IndentingPrintWriter w) {
        print(w, getClass().getSimpleName(), a, b);
      }
    }

    static class Three<T> extends Digit<T> {
      final Measured<T> a;
      final Measured<T> b;
      final Measured<T> c;
      final int v;

      Three(Measured<T> a, Measured<T> b, Measured<T> c) {
        this.a = a;
        this.b = b;
        this.c = c;
        v = measure(this.a, this.b, this.c);
      }

      @Override
      Item.Deep<T> cons(Digit<T> digit, Item<T> item, Measured<T> v) {
        return new Item.Deep<T>(digit, item, new Four<T>(a, b, c, v));
      }

      @Override
      Item.Deep<T> snoc(Measured<T> v, Item<T> item, Digit<T> digit) {
        return new Item.Deep<T>(new Four<T>(v, a, b, c), item, digit);
      }

      @Override
      public final int size() {
        return v;
      }

      @Override
      public T nth(int index) {
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
      public T head() {
        return c.head();
      }

      @Override
      public void print(IndentingPrintWriter w) {
        print(w, getClass().getSimpleName(), a, b, c);
      }
    }

    static class Four<T> extends Digit<T> {
      final Measured<T> a;
      final Measured<T> b;
      final Measured<T> c;
      final Measured<T> d;
      final int v;

      Four(Measured<T> a, Measured<T> b, Measured<T> c, Measured<T> d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        v = measure(this.a, this.b, this.c, this.d);
      }

      @Override
      Item.Deep<T> cons(Digit<T> digit, Item<T> item, Measured<T> v) {
        return new Item.Deep<T>(
            digit,
            item.cons(new Node.Node3<T>(a, b, c)),
            new Digit.Two<T>(d, v));
      }

      @Override
      Item.Deep<T> snoc(Measured<T> v, Item<T> item, Digit<T> digit) {
        return new Item.Deep<T>(
            new Digit.Two<T>(v, a),
            item.snoc(new Node.Node3<T>(b, c, d)),
            digit);
      }

      @Override
      public final int size() {
        return v;
      }

      @Override
      public T nth(int index) {
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
      public T head() {
        return d.head();
      }

      @Override
      public void print(IndentingPrintWriter w) {
        print(w, getClass().getSimpleName(), a, b, c, d);
      }
    }
  }

  private abstract static class Node<T> implements Measured<T>, Printable {
    static void print(IndentingPrintWriter w, Object... items) {
      w.write("Node(");
      FingerTreeSeq.print(w, items);
      w.write(")");
    }

    static class Node2<T> extends Node<T> {
      final Measured<T> a, b;
      final int v;

      Node2(Measured<T> a, Measured<T> b) {
        this.a = a;
        this.b = b;
        v = a.size() + b.size();
      }

      @Override
      public int size() {
        return v;
      }

      @Override
      public T nth(int index) {
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
      public T head() {
        return b.head();
      }

      @Override
      public void print(IndentingPrintWriter w) {
        print(w, a, b);
      }
    }

    static class Node3<T> extends Node<T> {
      final Measured<T> a, b, c;
      final int v;

      Node3(Measured<T> a, Measured<T> b, Measured<T> c) {
        this.a = a;
        this.b = b;
        this.c = c;
        v = a.size() + b.size() + c.size();
      }

      @Override
      public int size() {
        return v;
      }

      @Override
      public T nth(int index) {
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
      public T head() {
        return c.head();
      }

      @Override
      public void print(IndentingPrintWriter w) {
        print(w, a, b, c);
      }
    }
  }

  private abstract static class Item<T> implements Measured<T>, Printable {
    abstract Item<T> cons(Measured<T> v);

    abstract Item<T> snoc(Measured<T> v);

    static class Empty<T> extends Item<T> {
      @Override
      Single<T> cons(Measured<T> v) {
        return new Item.Single<T>(v);
      }

      @Override
      Single<T> snoc(Measured<T> v) {
        return new Item.Single<T>(v);
      }

      @Override
      public int size() {
        return 0;
      }

      @Override
      public T nth(int index) {
        throw new RangeException();
      }

      @Override
      public T head() {
        throw new EmptyException();
      }

      @Override
      public void print(IndentingPrintWriter w) {
        w.write("[EMPTY]");
      }
    }

    static class Single<T> extends Item<T> {
      final Measured<T> v;

      Single(Measured<T> v) {
        this.v = v;
      }

      @Override
      Deep<T> cons(Measured<T> v) {
        return new Item.Deep<T>(
            new Digit.One<T>(this.v),
            new Item.Empty<T>(),
            new Digit.One<T>(v));
      }

      @Override
      Deep<T> snoc(Measured<T> v) {
        return new Item.Deep<T>(
            new Digit.One<T>(v),
            new Item.Empty<T>(),
            new Digit.One<T>(this.v));
      }

      @Override
      public int size() {
        return v.size();
      }

      @Override
      public T nth(int index) {
        return v.nth(index);
      }

      @Override
      public T head() {
        return v.head();
      }

      @Override
      public void print(IndentingPrintWriter w) {
        if (v instanceof Printable) {
          w.write("Single(");
          ((Printable) v).print(w);
          w.write(")");
        }
        else {
          w.print(v);
        }
      }
    }

    static class Deep<T> extends Item<T> {
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
      Item<T> cons(Measured<T> v) {
        return dr.cons(dl, item, v);
      }

      @Override
      Deep<T> snoc(Measured<T> v) {
        return dl.snoc(v, item, dr);
      }

      @Override
      public int size() {
        return v;
      }

      @Override
      public T nth(int index) {
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
      public T head() {
        return dr.head();
      }

      @Override
      public void print(IndentingPrintWriter w) {
        w.write("Deep(\n");
        w.indent("    |");
        dl.print(w);
        w.write(";\n");
        item.print(w);
        w.write(";\n");
        dr.print(w);
        w.unindent();
        w.write("\n    )");
      }
    }
  }

  private static void print(IndentingPrintWriter w, Object[] items) {
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
}
