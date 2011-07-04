package list;

public interface Seq<T> {
  T head() throws EmptyException;

  Seq<T> tail() throws EmptyException;

  Seq<T> cons(T v);

  Seq<T> snoc(T v);

  Seq<T> concat(Seq<T> that);

  int size();

  T nth(int index) throws RangeException;

  class EmptyException extends IllegalStateException {
    public EmptyException() {}
  }

  class RangeException extends IllegalStateException {
    private final int index;

    public RangeException(int index) {
      this.index = index;
    }

    public int getIndex() {
      return index;
    }
  }
}
