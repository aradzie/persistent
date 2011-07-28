package collection.persistent.fingertree;

import org.junit.Test;

public class FingerTreeTest {
  @Test
  public void test() {
    FingerTree<Elem<String>> tree = new FingerTree.Empty<Elem<String>>();
    tree = tree.cons(new Elem<String>("hello"));
    tree = tree.cons(new Elem<String>("world"));
    tree = tree.cons(new Elem<String>("one"));
    tree = tree.cons(new Elem<String>("two"));
    tree = tree.cons(new Elem<String>("three"));
    tree = tree.cons(new Elem<String>("four"));
    tree = tree.cons(new Elem<String>("five"));
  }
}
