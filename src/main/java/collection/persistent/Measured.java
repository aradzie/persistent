package collection.persistent;

public interface Measured<V, M extends Monoid<M>> {
  M measure(V v);

  class Size implements Measured<Integer, Monoid.IntegerSum> {
    @Override
    public Monoid.IntegerSum measure(Integer integer) {
      return new Monoid.IntegerSum(integer);
    }
  }
}
