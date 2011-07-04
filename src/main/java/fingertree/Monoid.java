package fingertree;

public interface Monoid<T> {
  T unit();

  T combine(Monoid<T> that);
}
