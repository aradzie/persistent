package collection.persistent.fingertree;

public class Sandbox {
  static class Node<T> {
    final Node<Node<T>> deep;

    Node(Node<Node<T>> deep) {
      this.deep = deep;
    }
  }

  static void test() {
    Node<String> node = new Node<String>(
        new Node<Node<String>>(
            new Node<Node<Node<String>>>(
                new Node<Node<Node<Node<String>>>>(
                    new Node<Node<Node<Node<Node<String>>>>>(null)
                )
            )
        )
    );
    Node<Node<String>> deep0 = node.deep;
    Node<Node<Node<String>>> deep1 = deep0.deep;
    Node<Node<Node<Node<String>>>> deep2 = deep1.deep;
    Node<Node<Node<Node<Node<String>>>>> deep3 = deep2.deep;
    Node<Node<Node<Node<Node<Node<String>>>>>> deep4 = deep3.deep;
  }
}
