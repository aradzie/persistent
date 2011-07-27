package collection.persistent;

import java.util.Collections;
import java.util.Iterator;

/**
 * A persistent map for ordered keys implemented
 * as left-leaning red-black tree.
 *
 * This map does not allow <code>null</code> keys.
 *
 * @param <K> Key type.
 * @param <V> Value type.
 */
public final class RedBlackTreeMap<K extends Comparable<K>, V> implements Map<K, V> {
  @Override
  public V get(K key) {
    if (key == null) {
      throw new NullPointerException();
    }
    return null;
  }

  @Override
  public Map<K, V> put(K key, V value) {
    if (key == null) {
      throw new NullPointerException();
    }
    return Tree.insert(null, key, value);
  }

  @Override
  public Map<K, V> remove(K key) {
    if (key == null) {
      throw new NullPointerException();
    }
    return this;
  }

  @Override
  public Iterator<Entry<K, V>> iterator() {
    return Collections.emptyIterator();
  }

  @Override
  public List<Entry<K, V>> list() {
    return null;
  }

  private static final class Tree<K extends Comparable<K>, V>
      implements Map<K, V>, Entry<K, V> {
    static final boolean RED = true;
    final K key;
    final V value;
    final Tree<K, V> left, right;
    final boolean color;

    Tree(K key, V value) {
      this.key = key;
      this.value = value;
      left = null;
      right = null;
      color = RED;
    }

    Tree(K key, V value,
         Tree<K, V> left, Tree<K, V> right, boolean color) {
      this.key = key;
      this.value = value;
      this.left = left;
      this.right = right;
      this.color = color;
    }

    Tree<K, V> replaceLeft(Tree<K, V> left) {
      return new Tree<K, V>(key, value, left, right, color);
    }

    Tree<K, V> replaceRight(Tree<K, V> right) {
      return new Tree<K, V>(key, value, left, right, color);
    }

    Tree<K, V> replaceValue(K key, V value) {
      return new Tree<K, V>(key, value, left, right, color);
    }

    @Override
    public K getKey() {
      return key;
    }

    @Override
    public V getValue() {
      return value;
    }

    static <K extends Comparable<K>, V> Tree<K, V> insert(Tree<K, V> t, K key, V value) {
      if (t == null) {
        return new Tree<K, V>(key, value);
      }
      if (isRed(t.left) && isRed(t.right)) {
        t = t.flipColors();
      }
      int cmp = key.compareTo(t.key);
      if (cmp < 0) {
        t = t.replaceLeft(insert(t.left, key, value));
      }
      else if (cmp > 0) {
        t = t.replaceRight(insert(t.right, key, value));
      }
      else {
        t = t.replaceValue(key, value);
      }
      if (isRed(t.right) && !isRed(t.left)) {
        t = t.rotateLeft();
      }
      if (isRed(t.left) && isRed(t.left.left)) {
        t = t.rotateRight();
      }
      return t;
    }

    static <K extends Comparable<K>, V> boolean isRed(Tree<K, V> t) {
      return t != null && t.color == RED;
    }

    Tree<K, V> rotateLeft() {
      Tree<K, V> tree = new Tree<K, V>(key, value, left, right.left, RED);
      return new Tree<K, V>(right.key, right.value, tree, right.right, color);
    }

    Tree<K, V> rotateRight() {
      Tree<K, V> tree = new Tree<K, V>(key, value, left.right, right, RED);
      return new Tree<K, V>(left.key, left.value, left.left, tree, color);
    }

    Tree<K, V> flipColors() {
      return new Tree<K, V>(key, value, left.flipColor(), right.flipColor(), !color);
    }

    Tree<K, V> flipColor() {
      return new Tree<K, V>(key, value, left, right, !color);
    }

    static <K extends Comparable<K>, V> V find(Tree<K, V> tree, K key) {
      while (tree != null) {
        int i = key.compareTo(tree.key);
        if (i < 0) {
          tree = tree.left;
        }
        else if (i > 0) {
          tree = tree.right;
        }
        else {
          return tree.value;
        }
      }
      return null;
    }

    @Override
    public V get(K key) {
      return find(this, key);
    }

    @Override
    public Map<K, V> put(K key, V value) {
      if (key == null) {
        throw new NullPointerException();
      }
      return insert(this, key, value);
    }

    @Override
    public Map<K, V> remove(K key) {
      if (key == null) {
        throw new NullPointerException();
      }
      throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<Entry<K, V>> iterator() {
      return null;
    }

    @Override
    public List<Entry<K, V>> list() {
      return new ListImpl<K, V>(null, this).findLeaf();
    }
  }

  private static final class ListImpl<K extends Comparable<K>, V>
      implements List<Entry<K, V>> {
    enum Dir {
      LEFT, SELF, RIGHT
    }

    final ListImpl<K, V> parent;
    final Tree<K, V> tree;
    final Dir dir;

    ListImpl(ListImpl<K, V> parent, Tree<K, V> tree) {
      this.parent = parent;
      this.tree = tree;
      if (this.tree.left != null) {
        dir = Dir.LEFT;
      }
      else {
        dir = Dir.SELF;
      }
    }

    ListImpl(ListImpl<K, V> parent, Tree<K, V> tree, Dir dir) {
      this.parent = parent;
      this.tree = tree;
      this.dir = dir;
    }

    List<Entry<K, V>> findLeaf() {
      if (tree.left != null) {
        return new ListImpl<K, V>(this, tree.left).findLeaf();
      }
      else {
        return this;
      }
    }

    @Override
    public Entry<K, V> head() {
      return tree;
    }

    @Override
    public List<Entry<K, V>> tail() {
      switch (dir) {
        case LEFT:
          return new ListImpl<K, V>(parent, tree, Dir.SELF);
        case SELF:
          if (tree.right != null) {
            return new ListImpl<K, V>(
                new ListImpl<K, V>(parent, tree, Dir.RIGHT), tree.right
            ).findLeaf();
          }
          break;
        case RIGHT:
          break;
      }
      if (parent != null) {
        return parent.tail();
      }
      return null;
    }
  }
}
