package collection.persistent;

import collection.persistent.util.EmptyIterator;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A persistent map based on the data structure as described
 * in the publication <em>Ideal Hash Trees</em> by <em>Phil Bagwell</em>.
 *
 * @param <K> Key type.
 * @param <V> Value type.
 */
public final class ArrayTrieHashMap<K, V> implements Map<K, V> {
  @Nullable
  @Override
  public V get(K key) {
    return null;
  }

  @Override
  public Map<K, V> put(K key, V value) {
    return Tree.<K, V>empty().insert(keyHashCode(key), key, value, 0);
  }

  @Override
  public Map<K, V> remove(K key) {
    return this;
  }

  @Nullable
  @Override
  public List<Entry<K, V>> list() {
    return null;
  }

  @Override
  public Iterator<Entry<K, V>> iterator() {
    return new EmptyIterator<Entry<K, V>>();
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

  private interface Node<K, V> {
    //
  }

  private static final class Leaf<K, V>
      implements Node<K, V>, Entry<K, V> {
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

    static <K, V> V find(Leaf<K, V> leaf, int hashCode, K key) {
      while (leaf != null) {
        if (leaf.hashCode == hashCode && keysEqual(leaf.key, key)) {
          return leaf.value;
        }
        leaf = leaf.next;
      }
      return null;
    }

    Leaf<K, V> remove(int hashCode, K key) {
      if (this.hashCode == hashCode && keysEqual(this.key, key)) {
        return next;
      }
      if (next == null) {
        return this;
      }
      else {
        Leaf<K, V> result = next.remove(hashCode, key);
        if (next != result) {
          return new Leaf<K, V>(this.hashCode, this.key, this.value, result);
        }
        return this;
      }
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

  private static final class Tree<K, V>
      implements Node<K, V>, Map<K, V> {
    private static final Tree EMPTY = new Tree();
    private static final int MASK_WIDTH = 5;
    private static final int MASK = 31;
    final int mask;
    final Node<K, V>[] nodes;

    static <K, V> Tree<K, V> empty() {
      return EMPTY;
    }

    Tree() {
      mask = 0;
      nodes = new Node[]{};
    }

    Tree(Tree<K, V> that, Node<K, V> node, int index) {
      if (node != null) {
        if ((that.mask & (1 << index)) == 0) {
          // Insert new node into node.
          mask = that.mask | (1 << index);
          nodes = new Node[Integer.bitCount(mask)];
          int offset = offset(1 << index);
          for (int n = 0; n < nodes.length; n++) {
            if (n < offset) {
              nodes[n] = that.nodes[n];
            }
            else if (n == offset) {
              nodes[n] = node;
            }
            else {
              nodes[n] = that.nodes[n - 1];
            }
          }
        }
        else {
          // Replace existing node in node.
          mask = that.mask;
          nodes = new Node[Integer.bitCount(mask)];
          int offset = offset(1 << index);
          for (int n = 0; n < nodes.length; n++) {
            if (n == offset) {
              nodes[n] = node;
            }
            else {
              nodes[n] = that.nodes[n];
            }
          }
        }
      }
      else {
        // Remove node from node.
        mask = that.mask & ~(1 << index);
        nodes = new Node[Integer.bitCount(mask)];
        int offset = offset(1 << index);
        for (int n = 0; n < nodes.length; n++) {
          if (n < offset) {
            nodes[n] = that.nodes[n];
          }
          else {
            nodes[n] = that.nodes[n + 1];
          }
        }
      }
    }

    Tree(Leaf<K, V> e1, Leaf<K, V> e2, int level) {
      int index1 = (e1.hashCode >>> (MASK_WIDTH * level)) & MASK;
      int index2 = (e2.hashCode >>> (MASK_WIDTH * level)) & MASK;
      if (index1 == index2) {
        mask = 1 << index1;
        nodes = new Node[]{new Tree<K, V>(e1, e2, level + 1)};
      }
      else {
        mask = (1 << index1) | (1 << index2);
        if (index1 < index2) {
          nodes = new Node[]{e1, e2};
        }
        else {
          nodes = new Node[]{e2, e1};
        }
      }
    }

    @Override
    public V get(K key) {
      return find(keyHashCode(key), key, 0);
    }

    @Override
    public Map<K, V> put(K key, V value) {
      return insert(keyHashCode(key), key, value, 0);
    }

    @Override
    public Map<K, V> remove(K key) {
      Tree<K, V> tree = remove(keyHashCode(key), key, 0);
      if (tree == null) {
        return new ArrayTrieHashMap<K, V>();
      }
      return tree;
    }

    @Override
    public List<Entry<K, V>> list() {
      return new ListImpl.TreeLevel<K, V>(null, this, 0).findLeaf();
    }

    @Override
    public Iterator<Entry<K, V>> iterator() {
      return new It<K, V>(this);
    }

    V find(int hashCode, K key, int level) {
      int index = index(hashCode, level);
      Node<K, V> node = item(index);
      if (node instanceof Tree) {
        return ((Tree<K, V>) node).find(hashCode, key, level + 1);
      }
      else {
        return Leaf.find((Leaf<K, V>) node, hashCode, key);
      }
    }

    Tree<K, V> insert(int hashCode, K key, V value, int level) {
      int index = index(hashCode, level);
      Node<K, V> node = item(index);
      if (node == null) {
        // The slot is empty, put new entry in it.
        node = new Leaf<K, V>(hashCode, key, value, null);
      }
      else if (node instanceof Tree) {
        // The slot is occupied by a subtree, let it handle insertion.
        node = ((Tree<K, V>) node).insert(hashCode, key, value, level + 1);
      }
      else {
        // The slot is filled with an entry, either create a subtree
        // or resolve collision.
        Leaf<K, V> leaf = (Leaf<K, V>) node;
        Leaf<K, V> result = leaf.remove(hashCode, key);
        if (leaf != result) {
          // Simply replace entry with the same key.
          node = new Leaf<K, V>(hashCode, key, value, result);
        }
        else {
          // Prefixes of both current and new entry are equal so far.
          if (hashCode == leaf.hashCode) {
            // The suffixes are also equal, so this is a collision.
            node = new Leaf<K, V>(hashCode, key, value, leaf);
          }
          else {
            // The suffixes are different, create a subtree
            // to hold both entries.
            node = new Tree<K, V>(leaf,
                new Leaf<K, V>(hashCode, key, value, null), level + 1);
          }
        }
      }
      return new Tree<K, V>(this, node, index);
    }

    Tree<K, V> remove(int hashCode, K key, int level) {
      int index = index(hashCode, level);
      Node<K, V> node = item(index);
      if (node == null) {
        return this;
      }
      Node<K, V> result;
      if (node instanceof Tree) {
        result = ((Tree<K, V>) node).remove(hashCode, key, level + 1);
      }
      else {
        result = ((Leaf<K, V>) node).remove(hashCode, key);
      }
      if (node == result) {
        return this;
      }
      if (result == null) {
        if ((mask & ~(1 << index)) == 0) {
          return null; // Removed last entry from this tree.
        }
        // TODO contract tables with only one entry
      }
      return new Tree<K, V>(this, result, index);
    }

    Node<K, V> item(int prefix) {
      int bit = 1 << prefix;
      if ((mask & bit) == 0) {
        return null;
      }
      return nodes[offset(bit)];
    }

    int offset(int bit) {
      return Integer.bitCount(mask & (bit - 1));
    }

    static int index(int hashCode, int level) {
      return (hashCode >>> (level * MASK_WIDTH)) & MASK;
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
      final int index;

      TreeLevel(TreeLevel<K, V> parent, Tree<K, V> tree, int index) {
        this.parent = parent;
        this.tree = tree;
        this.index = index;
      }

      List<Entry<K, V>> findLeaf() {
        Node<K, V> node = tree.nodes[index];
        if (node instanceof Tree) {
          return new TreeLevel<K, V>(this, (Tree<K, V>) node, 0).findLeaf();
        }
        else {
          return new LeafLevel<K, V>(this, (Leaf<K, V>) node);
        }
      }

      List<Entry<K, V>> tail() {
        if (index + 1 < tree.nodes.length) {
          return new TreeLevel<K, V>(parent, tree, index + 1).findLeaf();
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
          else {
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
        final Node<K, V>[] nodes;
        int index;

        TreeLevel(Stack<K, V> next, Tree<K, V> tree) {
          super(next);
          nodes = tree.nodes;
        }

        @Override
        boolean hasNext() {
          return index < nodes.length;
        }

        @Override
        Node<K, V> next() {
          if (!hasNext()) {
            throw new IllegalStateException();
          }
          return nodes[index++];
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

    It(Tree<K, V> root) {
      if (root.nodes.length > 0) {
        stack = new Stack.TreeLevel<K, V>(null, root);
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
