package collection.persistent.fingertree;

abstract class Tree<T> extends Fragment<T> {
  public final Tree<T> cons(T v) {
    return cons(new Elem<T>(v));
  }

  public final Tree<T> snoc(T v) {
    return snoc(new Elem<T>(v));
  }

  abstract Tree<T> cons(Fragment<T> v);

  abstract Tree<T> snoc(Fragment<T> v);

  abstract Tree<T> tail();

  abstract Tree<T> tree(Digit<T> r);
}
