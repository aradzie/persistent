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
}
