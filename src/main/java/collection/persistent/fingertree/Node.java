package collection.persistent.fingertree;

abstract class Node<T> {
  static final class Node2<T> extends Node<T> {
    final T a, b;

    Node2(T a, T b) {
      this.a = a;
      this.b = b;
    }
  }

  static final class Node3<T> extends Node<T> {
    final T a, b, c;

    Node3(T a, T b, T c) {
      this.a = a;
      this.b = b;
      this.c = c;
    }
  }
}
