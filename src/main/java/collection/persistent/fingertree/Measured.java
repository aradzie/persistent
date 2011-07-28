package collection.persistent.fingertree;

public interface Measured<V, M extends Monoid<M>> {
  M measure(V v);
}
