package collection.persistent;

public interface Queue<T> {
  int size();

  boolean isEmpty();

  Queue<T> push(T v);

  T peek();

  Queue<T> pop();
}
