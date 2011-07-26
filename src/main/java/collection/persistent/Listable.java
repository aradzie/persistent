package collection.persistent;

public interface Listable<V> {
  List<V> list();

  interface List<V> {
    V head();

    List<V> tail();
  }
}
