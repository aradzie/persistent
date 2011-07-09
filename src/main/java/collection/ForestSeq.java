package collection;

/**
 * A persistent random-access list implementation based on the publication
 * <a href="http://www.eecs.usma.edu/webs/people/okasaki/pubs.html#fpca95">Purely
 * Functional Random-Access Lists</a> by Chris Okasaki.
 * <p/>
 * Please note that ForestSeq is not the official name of this data structure, it is
 * an informal name given by the implementer. It was going to be simple and concise
 * name that is easy to remember and type.
 *
 * @param <T> Element type.
 */
public class ForestSeq<T> implements Seq<T> {
  private final Tree<T> head;

  private ForestSeq(Tree<T> head) {
    this.head = head;
  }

  public ForestSeq() {
    this(new Tree<T>());
  }

  @Override
  public T head()
      throws RangeException {
    return head.head();
  }

  @Override
  public ForestSeq<T> tail()
      throws RangeException {
    return new ForestSeq<T>(head.tail());
  }

  @Override
  public ForestSeq<T> cons(T v) {
    return new ForestSeq<T>(head.cons(v));
  }

  @Override
  public ForestSeq<T> snoc(T v) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ForestSeq<T> concat(Seq<T> that) {
    if (that instanceof ForestSeq) {
      return concat((ForestSeq<T>) that);
    }
    else {
      throw new UnsupportedOperationException();
    }
  }

  public ForestSeq<T> concat(ForestSeq<T> that) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int size() {
    return head.seqSize;
  }

  @Override
  public T nth(int index)
      throws RangeException {
    return head.nth(index);
  }

  @Override
  public ForestSeq<T> nth(int index, T v)
      throws RangeException {
    return new ForestSeq<T>(head.nth(index, v));
  }

  private static class Tree<T> {
    final Node<T> root;
    final int size;
    final Tree<T> next;
    final int seqSize;

    Tree() {
      root = null;
      size = 0;
      next = null;
      seqSize = 0;
    }

    Tree(Node<T> root, int size, Tree<T> next) {
      this.root = root;
      this.size = size;
      this.next = next;
      this.seqSize = this.next.seqSize + this.size;
    }

    T head() {
      if (size == 0) {
        throw new RangeException();
      }
      return root.v;
    }

    Tree<T> tail() {
      if (size == 0) {
        throw new RangeException();
      }
      if (size == 1) {
        return next;
      }
      return new Tree<T>(root.l, size - 1,
          new Tree<T>(root.r, size - 1,
              next));
    }

    Tree<T> cons(T v) {
      if (size == 0 || next.size == 0 || size != next.size) {
        return new Tree<T>(new Node<T>(v, null, null), 1, this);
      }
      else {
        return new Tree<T>(
            new Node<T>(v, root, next.root), size + next.size + 1, next.next);
      }
    }

    T nth(int index) {
      if (size == 0) {
        throw new RangeException();
      }
      if (index < size) {
        return root.nth(size, index);
      }
      else {
        return next.nth(index - size);
      }
    }

    Tree<T> nth(int index, T v) {
      if (size == 0) {
        throw new RangeException();
      }
      if (index < size) {
        return new Tree<T>(root.nth(size, index, v), size, next);
      }
      else {
        return new Tree<T>(root, size, next.nth(index - size, v));
      }
    }
  }

  private static class Node<T> {
    final T v;
    final Node<T> l, r;

    Node(T v, Node<T> l, Node<T> r) {
      this.v = v;
      this.l = l;
      this.r = r;
    }

    T nth(int size, int index) {
      if (index == 0) {
        return v;
      }
      size = size / 2;
      if (index <= size) {
        return l.nth(size, index - 1);
      }
      else {
        return r.nth(size, index - 1 - size);
      }
    }

    Node<T> nth(int size, int index, T v) {
      if (index == 0) {
        return new Node<T>(v, l, r);
      }
      size = size / 2;
      if (index <= size) {
        return new Node<T>(v, l.nth(size, index - 1, v), r);
      }
      else {
        return new Node<T>(v, l, r.nth(size, index - 1 - size, v));
      }
    }
  }
}
