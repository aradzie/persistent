package collection.persistent.fingertree;

public interface Measured<M extends Monoid<M>> {
  M measure();
}
