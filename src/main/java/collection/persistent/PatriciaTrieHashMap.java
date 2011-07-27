package collection.persistent;

import collection.persistent.util.EmptyIterator;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class PatriciaTrieHashMap<K, V> implements Map<K, V> {
  @Nullable
  @Override
  public V get(K key) {
    return null;
  }

  @Override
  public Map<K, V> put(K key, V value) {
    return new Leaf<K, V>(keyHashCode(key), key, value, null);
  }

  @Override
  public Map<K, V> remove(K key) {
    return this;
  }

  @Override
  public Iterator<Entry<K, V>> iterator() {
    return new EmptyIterator<Entry<K, V>>();
  }

  @Nullable
  @Override
  public List<Entry<K, V>> list() {
    return null;
  }

  private static int keyHashCode(Object key) {
    if (key == null) {
      return 0;
    }
    return key.hashCode();
  }

  private static boolean keysEqual(Object a, Object b) {
    return a == b || a != null && b != null && a.equals(b);
  }

  private abstract static class Node<K, V> implements Map<K, V> {
    abstract V find(int hashCode, K key);

    abstract Node<K, V> insert(int hc, K key, V value);

    abstract Node<K, V> remove(int hc, K key);
  }

  private static final class Leaf<K, V> extends Node<K, V>
      implements Entry<K, V> {
    final int hashCode;
    final K key;
    final V value;
    final Leaf<K, V> next;

    Leaf(int hashCode, K key, V value, Leaf<K, V> next) {
      this.hashCode = hashCode;
      this.key = key;
      this.value = value;
      this.next = next;
    }

    @Override
    public K getKey() {
      return key;
    }

    @Override
    public V getValue() {
      return value;
    }

    @Override
    public V get(K key) {
      int hc = keyHashCode(key);
      return find(hc, key);
    }

    @Override
    public Map<K, V> put(K key, V value) {
      int hc = keyHashCode(key);
      return insert(hc, key, value);
    }

    @Override
    public Map<K, V> remove(K key) {
      int hc = keyHashCode(key);
      Leaf<K, V> result = remove(hc, key);
      if (result == null) {
        return new PatriciaTrieHashMap<K, V>();
      }
      return result;
    }

    @Override
    public Iterator<Entry<K, V>> iterator() {
      return new It<K, V>(this);
    }

    @Override
    public List<Entry<K, V>> list() {
      return new ListImpl.LeafLevel<K, V>(null, this);
    }

    @Override
    V find(int hashCode, K key) {
      if (this.hashCode == hashCode && keysEqual(this.key, key)) {
        return value;
      }
      if (next == null) {
        return null;
      }
      return next.find(hashCode, key);
    }

    @Override
    Node<K, V> insert(int hashCode, K key, V value) {
      if (this.hashCode == hashCode) {
        return new Leaf<K, V>(hashCode, key, value, remove(hashCode, key));
      }
      else {
        return new Tree<K, V>(this, new Leaf<K, V>(hashCode, key, value, null));
      }
    }

    @Override
    Leaf<K, V> remove(int hashCode, K key) {
      if (this.hashCode == hashCode && keysEqual(this.key, key)) {
        return next;
      }
      if (next == null) {
        return this;
      }
      Leaf<K, V> result = next.remove(hashCode, key);
      if (next != result) {
        return new Leaf<K, V>(this.hashCode, this.key, this.value, result);
      }
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) { return true; }
      if (!(o instanceof Leaf)) { return false; }
      Leaf that = (Leaf) o;
      if (hashCode != that.hashCode) { return false; }
      if (key != null ? !key.equals(that.key) : that.key != null) { return false; }
      if (value != null ? !value.equals(that.value) : that.value != null) { return false; }
      return true;
    }

    @Override
    public int hashCode() {
      int result = hashCode;
      result = 31 * result + (key != null ? key.hashCode() : 0);
      result = 31 * result + (value != null ? value.hashCode() : 0);
      return result;
    }

    @Override
    public String toString() {
      return "key=" + key + "; value=" + value;
    }
  }

  private static final class Tree<K, V> extends Node<K, V> {
    final byte bit;
    final int prefix;
    final Node<K, V> left, right;

    Tree(byte bit, int prefix, Node<K, V> left, Node<K, V> right) {
      this.bit = bit;
      this.prefix = prefix;
      this.left = left;
      this.right = right;
    }

    Tree(Leaf<K, V> l, Leaf<K, V> r) {
      bit = (byte) Integer.numberOfTrailingZeros(l.hashCode ^ r.hashCode);
      prefix = l.hashCode & ((1 << bit) - 1);
      if ((l.hashCode & (1 << bit)) == 0) {
        left = l;
        right = r;
      }
      else {
        left = r;
        right = l;
      }
    }

    @Override
    public V get(K key) {
      int hc = keyHashCode(key);
      return find(hc, key);
    }

    @Override
    public Map<K, V> put(K key, V value) {
      int hc = keyHashCode(key);
      return insert(hc, key, value);
    }

    @Override
    public Map<K, V> remove(K key) {
      int hc = keyHashCode(key);
      return remove(hc, key);
    }

    @Override
    V find(int hashCode, K key) {
      if ((hashCode & ((1 << bit) - 1)) != prefix) {
        return null;
      }
      if ((hashCode & (1 << bit)) == 0) {
        return left.find(hashCode, key);
      }
      else {
        return right.find(hashCode, key);
      }
    }

    @Override
    Node<K, V> insert(int hc, K key, V value) {
      int p = hc & ((1 << bit) - 1);
      if (p == prefix) {
        if ((hc & (1 << bit)) == 0) {
          return new Tree<K, V>(bit, prefix, left.insert(hc, key, value), right);
        }
        else {
          return new Tree<K, V>(bit, prefix, left, right.insert(hc, key, value));
        }
      }
      else {
        byte b = (byte) Integer.numberOfTrailingZeros(hc ^ prefix);
        p = hc & ((1 << b) - 1);
        if ((hc & (1 << b)) == 0) {
          return new Tree<K, V>(b, p, new Leaf<K, V>(hc, key, value, null), this);
        }
        else {
          return new Tree<K, V>(b, p, this, new Leaf<K, V>(hc, key, value, null));
        }
      }
    }

    @Override
    Node<K, V> remove(int hc, K key) {
      int p = hc & ((1 << bit) - 1);
      if (p == prefix) {
        if ((hc & (1 << bit)) == 0) {
          Node<K, V> node = left.remove(hc, key);
          if (node == null) {
            return right;
          }
          if (node == left) {
            return this;
          }
          return new Tree<K, V>(bit, prefix, node, right);
        }
        else {
          Node<K, V> node = right.remove(hc, key);
          if (node == null) {
            return left;
          }
          if (node == right) {
            return this;
          }
          return new Tree<K, V>(bit, prefix, left, node);
        }
      }
      else {
        return this;
      }
    }

    @Override
    public Iterator<Entry<K, V>> iterator() {
      return new It<K, V>(this);
    }

    @Override
    public List<Entry<K, V>> list() {
      return new ListImpl.TreeLevel<K, V>(null, this, false).findLeaf();
    }
  }

  /**
   * Implements list of map entries.
   *
   * @param <K> Key type.
   * @param <V> Value type.
   */
  private abstract static class ListImpl<K, V> {
    static final class TreeLevel<K, V> extends ListImpl<K, V> {
      final TreeLevel<K, V> parent;
      final Tree<K, V> tree;
      final boolean dir;

      TreeLevel(TreeLevel<K, V> parent, Tree<K, V> tree, boolean dir) {
        this.parent = parent;
        this.tree = tree;
        this.dir = dir;
      }

      List<Entry<K, V>> findLeaf() {
        Node<K, V> node = dir ? tree.right : tree.left;
        if (node instanceof Tree) {
          return new TreeLevel<K, V>(this, (Tree<K, V>) node, false).findLeaf();
        }
        else {
          return new LeafLevel<K, V>(this, (Leaf<K, V>) node);
        }
      }

      List<Entry<K, V>> tail() {
        if (!dir) {
          return new TreeLevel<K, V>(parent, tree, true).findLeaf();
        }
        if (parent != null) {
          return parent.tail();
        }
        return null;
      }
    }

    static final class LeafLevel<K, V> extends ListImpl<K, V>
        implements List<Entry<K, V>> {
      final TreeLevel<K, V> parent;
      final Leaf<K, V> leaf;
      List<Entry<K, V>> tail;

      LeafLevel(TreeLevel<K, V> parent, Leaf<K, V> leaf) {
        this.parent = parent;
        this.leaf = leaf;
      }

      @Override
      public Leaf<K, V> head() {
        return leaf;
      }

      @Override
      public synchronized List<Entry<K, V>> tail() {
        if (tail == null) {
          if (leaf.next != null) {
            tail = new LeafLevel<K, V>(parent, leaf.next);
          }
          else if (parent != null) {
            tail = parent.tail();
          }
        }
        return tail;
      }
    }
  }

  /**
   * Implements thread-unsafe iterator over map entries.
   *
   * @param <K> Key type.
   * @param <V> Value type.
   */
  private static final class It<K, V> implements Iterator<Entry<K, V>> {
    abstract static class Stack<K, V> {
      final Stack<K, V> next;

      Stack(Stack<K, V> next) {
        this.next = next;
      }

      abstract boolean hasNext();

      abstract Node<K, V> next();

      static final class TreeLevel<K, V> extends Stack<K, V> {
        final Node<K, V> left, right;
        int index;

        TreeLevel(Stack<K, V> next, Tree<K, V> tree) {
          super(next);
          left = tree.left;
          right = tree.right;
        }

        @Override
        boolean hasNext() {
          return index < 2;
        }

        @Override
        Node<K, V> next() {
          if (!hasNext()) {
            throw new IllegalStateException();
          }
          switch (index) {
            case 0:
              index++;
              return left;
            case 1:
              index++;
              return right;
          }
          throw new IllegalStateException();
        }
      }

      static final class LeafLevel<K, V> extends Stack<K, V> {
        Leaf<K, V> leaf;

        LeafLevel(Stack<K, V> next, Leaf<K, V> leaf) {
          super(next);
          this.leaf = leaf;
        }

        @Override
        boolean hasNext() {
          return leaf != null;
        }

        @Override
        Node<K, V> next() {
          if (!hasNext()) {
            throw new IllegalStateException();
          }
          Leaf<K, V> current = leaf;
          leaf = leaf.next;
          return current;
        }
      }
    }

    Stack<K, V> stack;

    It(Node<K, V> root) {
      if (root instanceof Leaf) {
        stack = new Stack.LeafLevel<K, V>(null, (Leaf<K, V>) root);
      }
      else if (root instanceof Tree) {
        stack = new Stack.TreeLevel<K, V>(null, (Tree<K, V>) root);
      }
      else {
        throw new IllegalStateException();
      }
    }

    @Override
    public boolean hasNext() {
      return stack != null;
    }

    @Override
    public Entry<K, V> next() {
      while (true) {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        if (stack.hasNext()) {
          Node<K, V> node = stack.next();
          if (node instanceof Leaf<?, ?>) {
            Leaf<K, V> leaf = (Leaf<K, V>) node;
            if (stack instanceof Stack.LeafLevel<?, ?>
                || leaf.next == null) {
              while (stack != null) {
                if (stack.hasNext()) {
                  break;
                }
                stack = stack.next;
              }
              return leaf;
            }
            stack = new Stack.LeafLevel<K, V>(stack, (Leaf<K, V>) node);
          }
          else {
            stack = new Stack.TreeLevel<K, V>(stack, (Tree<K, V>) node);
          }
        }
        else {
          stack = stack.next;
        }
      }
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }
}
