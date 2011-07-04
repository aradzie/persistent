package fingertree;

import debug.IndentingPrintWriter;
import debug.Printable;

public class FingerTree {
  private Item root = new Item.Empty();

  public void cons(Object v) {
    root = root.cons(v);
  }

  public void snoc(Object v) {
    root = root.snoc(v);
  }

  void dump(IndentingPrintWriter w) {
    root.print(w);
    w.flush();
  }

  private static void printItems(IndentingPrintWriter w, Object[] items) {
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

  private abstract static class Digit implements Printable {
    abstract Item.Deep cons(Digit digit, Item item, Object v);

    abstract Item.Deep snoc(Object v, Item item, Digit digit);

    void print(IndentingPrintWriter w, Object... items) {
      w.write("Digit." + getClass().getSimpleName() + "[");
      printItems(w, items);
      w.write("]");
    }

    static class One extends Digit {
      final Object a;

      One(Object a) {
        this.a = a;
      }

      @Override
      Item.Deep cons(Digit digit, Item item, Object v) {
        return new Item.Deep(digit, item, new Two(a, v));
      }

      @Override
      Item.Deep snoc(Object v, Item item, Digit digit) {
        return new Item.Deep(new Two(v, a), item, digit);
      }

      @Override
      public void print(IndentingPrintWriter w) {
        print(w, a);
      }
    }

    static class Two extends Digit {
      final Object a;
      final Object b;

      Two(Object a, Object b) {
        this.a = a;
        this.b = b;
      }

      @Override
      Item.Deep cons(Digit digit, Item item, Object v) {
        return new Item.Deep(digit, item, new Three(a, b, v));
      }

      @Override
      Item.Deep snoc(Object v, Item item, Digit digit) {
        return new Item.Deep(new Three(v, a, b), item, digit);
      }

      @Override
      public void print(IndentingPrintWriter w) {
        print(w, a, b);
      }
    }

    static class Three extends Digit {
      final Object a;
      final Object b;
      final Object c;

      Three(Object a, Object b, Object c) {
        this.a = a;
        this.b = b;
        this.c = c;
      }

      @Override
      Item.Deep cons(Digit digit, Item item, Object v) {
        return new Item.Deep(digit, item, new Four(a, b, c, v));
      }

      @Override
      Item.Deep snoc(Object v, Item item, Digit digit) {
        return new Item.Deep(new Four(v, a, b, c), item, digit);
      }

      @Override
      public void print(IndentingPrintWriter w) {
        print(w, a, b, c);
      }
    }

    static class Four extends Digit {
      final Object a;
      final Object b;
      final Object c;
      final Object d;

      Four(Object a, Object b, Object c, Object d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
      }

      @Override
      Item.Deep cons(Digit digit, Item item, Object v) {
        return new Item.Deep(
            digit,
            item.cons(new Node.Node3(a, b, c)),
            new Digit.Two(d, v));
      }

      @Override
      Item.Deep snoc(Object v, Item item, Digit digit) {
        return new Item.Deep(
            new Digit.Two(v, a),
            item.snoc(new Node.Node3(b, c, d)),
            digit);
      }

      @Override
      public void print(IndentingPrintWriter w) {
        print(w, a, b, c, d);
      }
    }
  }

  private abstract static class Node implements Printable {
    static void print(IndentingPrintWriter w, Object... items) {
      w.write("Node(");
      printItems(w, items);
      w.write(")");
    }

    static class Node2 extends Node {
      final Object a, b;

      Node2(Object a, Object b) {
        this.a = a;
        this.b = b;
      }

      @Override
      public void print(IndentingPrintWriter w) {
        print(w, a, b);
      }
    }

    static class Node3 extends Node {
      final Object a, b, c;

      Node3(Object a, Object b, Object c) {
        this.a = a;
        this.b = b;
        this.c = c;
      }

      @Override
      public void print(IndentingPrintWriter w) {
        print(w, a, b, c);
      }
    }
  }

  private abstract static class Item implements Printable {
    abstract Item cons(Object v);

    abstract Item snoc(Object v);

    static class Empty extends Item {
      @Override
      Single cons(Object v) {
        return new Item.Single(v);
      }

      @Override
      Single snoc(Object v) {
        return new Item.Single(v);
      }

      @Override
      public void print(IndentingPrintWriter w) {
        w.write("[EMPTY]");
      }
    }

    static class Single extends Item {
      final Object v;

      Single(Object v) {
        this.v = v;
      }

      @Override
      Deep cons(Object v) {
        return new Item.Deep(
            new Digit.One(this.v),
            new Item.Empty(),
            new Digit.One(v));
      }

      @Override
      Deep snoc(Object v) {
        return new Item.Deep(
            new Digit.One(v),
            new Item.Empty(),
            new Digit.One(this.v));
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

    static class Deep extends Item {
      final Digit dl;
      final Item item;
      final Digit dr;

      Deep(Digit dl, Item item, Digit dr) {
        this.dl = dl;
        this.item = item;
        this.dr = dr;
      }

      @Override
      Item cons(Object v) {
        return dr.cons(dl, item, v);
      }

      @Override
      Deep snoc(Object v) {
        return dl.snoc(v, item, dr);
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

  //foldR :: (a -> b -> b) -> (f a -> b -> b)
  //foldL :: (b -> a -> b) -> (b -> f a -> b)
  interface Foldable {
    //
  }
}
