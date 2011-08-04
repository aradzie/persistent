package collection.persistent.lazy;

/** Suspended evaluation of lazy stream tail. */
public interface Tail<T> {
  Cell<T> eval();

  abstract class Memoized<T> implements Tail<T> {
    private static final Cell NULL = new Cell(null, null);
    private Cell<T> r = NULL;

    @Override
    public final synchronized Cell<T> eval() {
      if (r == NULL) {
        r = doEval();
      }
      return r;
    }

    protected abstract Cell<T> doEval();
  }
}
