package collection.persistent.fingertree;

import org.junit.Test;

import static org.junit.Assert.*;

public class FingerTreeTest {
  @Test
  public void test() {
    FingerTree<Elem.Size, Elem<String>> tree =
        new FingerTree.Empty<Elem.Size, Elem<String>>(Elem.Size.ZERO);
    tree = tree.cons(new Elem<String>("hello"));
    tree = tree.cons(new Elem<String>("world"));
    tree = tree.cons(new Elem<String>("one"));
    tree = tree.cons(new Elem<String>("two"));
    tree = tree.cons(new Elem<String>("three"));
    tree = tree.cons(new Elem<String>("four"));
    tree = tree.cons(new Elem<String>("five"));

    FingerTree<Elem.Size, Elem<String>> t;
    FingerTree.View<Elem.Size, Elem<String>> v;

    t = tree;

    v = t.viewL();
    assertEquals("five", v.elem().v);
    t = v.tree();

    v = t.viewL();
    assertEquals("four", v.elem().v);
    t = v.tree();

    v = t.viewL();
    assertEquals("three", v.elem().v);
    t = v.tree();

    v = t.viewL();
    assertEquals("two", v.elem().v);
    t = v.tree();

    v = t.viewL();
    assertEquals("one", v.elem().v);
    t = v.tree();

    v = t.viewL();
    assertEquals("world", v.elem().v);
    t = v.tree();

    v = t.viewL();
    assertEquals("hello", v.elem().v);
    t = v.tree();

    t = tree;

    v = t.viewR();
    assertEquals("hello", v.elem().v);
    t = v.tree();

    v = t.viewR();
    assertEquals("world", v.elem().v);
    t = v.tree();

    v = t.viewR();
    assertEquals("one", v.elem().v);
    t = v.tree();

    v = t.viewR();
    assertEquals("two", v.elem().v);
    t = v.tree();

    v = t.viewR();
    assertEquals("three", v.elem().v);
    t = v.tree();

    v = t.viewR();
    assertEquals("four", v.elem().v);
    t = v.tree();

    v = t.viewR();
    assertEquals("five", v.elem().v);
    t = v.tree();
  }

  @Test
  public void concat() {
    FingerTree<Elem.Size, Elem<String>> a =
        new FingerTree.Empty<Elem.Size, Elem<String>>(Elem.Size.ZERO);
    FingerTree<Elem.Size, Elem<String>> b =
        new FingerTree.Empty<Elem.Size, Elem<String>>(Elem.Size.ZERO);
    FingerTree.View<Elem.Size, Elem<String>> view;

    a = a.cons(new Elem<String>("e"));
    a = a.cons(new Elem<String>("d"));
    a = a.cons(new Elem<String>("c"));
    a = a.cons(new Elem<String>("b"));
    a = a.cons(new Elem<String>("a"));

    b = b.cons(new Elem<String>("4"));
    b = b.cons(new Elem<String>("3"));
    b = b.cons(new Elem<String>("2"));
    b = b.cons(new Elem<String>("1"));

    System.out.println("tree A");
    view = a.viewL();
    while (view != null) {
      System.out.println(view.elem().v);
      view = view.tree().viewL();
    }

    System.out.println("tree B");
    view = b.viewL();
    while (view != null) {
      System.out.println(view.elem().v);
      view = view.tree().viewL();
    }

    FingerTree<Elem.Size, Elem<String>> tree = FingerTree.concat(a, b);

    System.out.println("joined");
    view = tree.viewL();
    while (view != null) {
      System.out.println(view.elem().v);
      view = view.tree().viewL();
    }

    tree = FingerTree.concat(tree, tree);

    System.out.println("joined*joined");
    view = tree.viewL();
    while (view != null) {
      System.out.println(view.elem().v);
      view = view.tree().viewL();
    }
  }
}
