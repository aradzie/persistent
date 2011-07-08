package collection;

public interface Seq<T> {
  T head() throws RangeException;

  Seq<T> tail() throws RangeException;

  Seq<T> cons(T v);

  Seq<T> snoc(T v);

  Seq<T> concat(Seq<T> that);

  int size();

  T nth(int index) throws RangeException;

  class RangeException extends IllegalStateException {
    public RangeException() {}
  }
}
