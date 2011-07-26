package collection.persistent;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A persistent map based on the data structure as described
 * in the publication <em>Ideal Hash Trees</em> by <em>Phil Bagwell</em>.
 *
 * @param <K> Key type.
 * @param <V> Value type.
 */
public final class HashTreeMap<K, V> extends AbstractHashMap<K, V> {
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
  public List<Map.Entry<K, V>> list() {
    return null;
  }

  @Override
  public Iterator<Map.Entry<K, V>> iterator() {
    return Collections.emptyIterator();
  }

  private static final class Tree<K, V>
      extends AbstractHashMap<K, V> implements Item<K, V> {
    private static final Tree EMPTY = new Tree();
    private static final int MASK_WIDTH = 5;
    private static final int MASK = 31;
    final int mask;
    final Item<K, V>[] items;

    static <K, V> Tree<K, V> empty() {
      return EMPTY;
    }

    Tree() {
      mask = 0;
      items = new Item[]{};
    }

    Tree(Tree<K, V> that, Item<K, V> entry, int index) {
      if (entry != null) {
        if ((that.mask & (1 << index)) == 0) {
          // Insert new entry into node.
          mask = that.mask | (1 << index);
          items = new Item[Integer.bitCount(mask)];
          int offset = offset(1 << index);
          for (int n = 0; n < items.length; n++) {
            if (n < offset) {
              items[n] = that.items[n];
            }
            else if (n == offset) {
              items[n] = entry;
            }
            else {
              items[n] = that.items[n - 1];
            }
          }
        }
        else {
          // Replace existing entry in node.
          mask = that.mask;
          items = new Item[Integer.bitCount(mask)];
          int offset = offset(1 << index);
          for (int n = 0; n < items.length; n++) {
            if (n == offset) {
              items[n] = entry;
            }
            else {
              items[n] = that.items[n];
            }
          }
        }
      }
      else {
        // Remove entry from node.
        mask = that.mask & ~(1 << index);
        items = new Item[Integer.bitCount(mask)];
        int offset = offset(1 << index);
        for (int n = 0; n < items.length; n++) {
          if (n < offset) {
            items[n] = that.items[n];
          }
          else {
            items[n] = that.items[n + 1];
          }
        }
      }
    }

    Tree(Entry<K, V> e1, Entry<K, V> e2, int level) {
      int index1 = (e1.hashCode >>> (MASK_WIDTH * level)) & MASK;
      int index2 = (e2.hashCode >>> (MASK_WIDTH * level)) & MASK;
      if (index1 == index2) {
        mask = 1 << index1;
        items = new Item[]{new Tree<K, V>(e1, e2, level + 1)};
      }
      else {
        mask = (1 << index1) | (1 << index2);
        if (index1 < index2) {
          items = new Item[]{e1, e2};
        }
        else {
          items = new Item[]{e2, e1};
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
        return new HashTreeMap<K, V>();
      }
      return tree;
    }

    @Override
    public List<Map.Entry<K, V>> list() {
      return new ListImpl.NodeLevel<K, V>(null, this, 0).findEntry();
    }

    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
      return new It<K, V>(this);
    }

    V find(int hashCode, K key, int level) {
      int index = index(hashCode, level);
      Item<K, V> item = item(index);
      if (item instanceof Tree) {
        return ((Tree<K, V>) item).find(hashCode, key, level + 1);
      }
      else {
        return Entry.find((Entry<K, V>) item, hashCode, key);
      }
    }

    Tree<K, V> insert(int hashCode, K key, V value, int level) {
      int index = index(hashCode, level);
      Item<K, V> item = item(index);
      if (item == null) {
        // The slot is empty, put new entry in it.
        item = new Entry<K, V>(hashCode, key, value, null);
      }
      else if (item instanceof Tree) {
        // The slot is occupied by a subtree, let it handle insertion.
        item = ((Tree<K, V>) item).insert(hashCode, key, value, level + 1);
      }
      else {
        // The slot is filled with an entry, either create a subtree
        // or resolve collision.
        Entry<K, V> entry = (Entry<K, V>) item;
        Entry<K, V> result = entry.remove(hashCode, key);
        if (entry != result) {
          // Simply replace entry with the same key.
          item = new Entry<K, V>(hashCode, key, value, result);
        }
        else {
          // Prefixes of both current and new entry are equal so far.
          if (hashCode == entry.hashCode) {
            // The suffixes are also equal, so this is a collision.
            item = new Entry<K, V>(hashCode, key, value, entry);
          }
          else {
            // The suffixes are different, create a subtree
            // to hold both entries.
            item = new Tree<K, V>(entry,
                new Entry<K, V>(hashCode, key, value, null), level + 1);
          }
        }
      }
      return new Tree<K, V>(this, item, index);
    }

    Tree<K, V> remove(int hashCode, K key, int level) {
      int index = index(hashCode, level);
      Item<K, V> item = item(index);
      if (item == null) {
        return this;
      }
      Item<K, V> result;
      if (item instanceof Tree) {
        result = ((Tree<K, V>) item).remove(hashCode, key, level + 1);
      }
      else {
        result = ((Entry<K, V>) item).remove(hashCode, key);
      }
      if (item == result) {
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

    Item<K, V> item(int prefix) {
      int bit = 1 << prefix;
      if ((mask & bit) == 0) {
        return null;
      }
      return items[offset(bit)];
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
    static final class NodeLevel<K, V> extends ListImpl<K, V> {
      final NodeLevel<K, V> parent;
      final Tree<K, V> tree;
      final int index;

      NodeLevel(NodeLevel<K, V> parent, Tree<K, V> tree, int index) {
        this.parent = parent;
        this.tree = tree;
        this.index = index;
      }

      List<Map.Entry<K, V>> findEntry() {
        Item<K, V> item = tree.items[index];
        if (item instanceof Tree) {
          return new NodeLevel<K, V>(this, (Tree<K, V>) item, 0).findEntry();
        }
        else {
          return new EntryLevel<K, V>(this, (Entry<K, V>) item);
        }
      }

      List<Map.Entry<K, V>> tail() {
        if (index + 1 < tree.items.length) {
          return new NodeLevel<K, V>(parent, tree, index + 1).findEntry();
        }
        if (parent != null) {
          return parent.tail();
        }
        return null;
      }
    }

    static final class EntryLevel<K, V> extends ListImpl<K, V>
        implements List<Map.Entry<K, V>> {
      final NodeLevel<K, V> parent;
      final Entry<K, V> entry;
      List<Map.Entry<K, V>> tail;

      EntryLevel(NodeLevel<K, V> parent, Entry<K, V> entry) {
        this.parent = parent;
        this.entry = entry;
      }

      @Override
      public Entry<K, V> head() {
        return entry;
      }

      @Override
      public synchronized List<Map.Entry<K, V>> tail() {
        if (tail == null) {
          if (entry.next != null) {
            tail = new EntryLevel<K, V>(parent, entry.next);
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
  private static final class It<K, V> implements Iterator<Map.Entry<K, V>> {
    abstract static class Stack<K, V> {
      final Stack<K, V> next;

      Stack(Stack<K, V> next) {
        this.next = next;
      }

      abstract boolean hasNext();

      abstract Item<K, V> next();

      static final class NodeLevel<K, V> extends Stack<K, V> {
        final Item<K, V>[] items;
        int index;

        NodeLevel(Stack<K, V> next, Tree<K, V> tree) {
          super(next);
          items = tree.items;
        }

        @Override
        boolean hasNext() {
          return index < items.length;
        }

        @Override
        Item<K, V> next() {
          if (!hasNext()) {
            throw new IllegalStateException();
          }
          return items[index++];
        }
      }

      static final class EntryLevel<K, V> extends Stack<K, V> {
        Entry<K, V> entry;

        EntryLevel(Stack<K, V> next, Entry<K, V> entry) {
          super(next);
          this.entry = entry;
        }

        @Override
        boolean hasNext() {
          return entry != null;
        }

        @Override
        Item<K, V> next() {
          if (!hasNext()) {
            throw new IllegalStateException();
          }
          Entry<K, V> current = entry;
          entry = entry.next;
          return current;
        }
      }
    }

    Stack<K, V> stack;

    It(Tree<K, V> root) {
      if (root.items.length > 0) {
        stack = new Stack.NodeLevel<K, V>(null, root);
      }
    }

    @Override
    public boolean hasNext() {
      return stack != null;
    }

    @Override
    public Map.Entry<K, V> next() {
      while (true) {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        if (stack.hasNext()) {
          Item<K, V> item = stack.next();
          if (item instanceof Entry<?, ?>) {
            Entry<K, V> entry = (Entry<K, V>) item;
            if (stack instanceof Stack.EntryLevel<?, ?>
                || entry.next == null) {
              while (stack != null) {
                if (stack.hasNext()) {
                  break;
                }
                stack = stack.next;
              }
              return entry;
            }
            stack = new Stack.EntryLevel<K, V>(stack, (Entry<K, V>) item);
          }
          else {
            stack = new Stack.NodeLevel<K, V>(stack, (Tree<K, V>) item);
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
