package collection.persistent;

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
  private static final int MASK_WIDTH = 5;
  private static final int MASK = 31;
  private final Tree<K, V> root;
  private final int size;

  public HashTreeMap() {
    root = new Tree<K, V>();
    size = 0;
  }

  private HashTreeMap(Tree<K, V> root, int size) {
    this.root = root;
    this.size = size;
  }

  @Override
  public V get(K key) {
    return root.find(keyHashCode(key), key, 0);
  }

  @Override
  public HashTreeMap<K, V> put(K key, V value) {
    Tree<K, V> result = root.insert(keyHashCode(key), key, value, 0);
    if (root == result) {
      return this;
    }
    return new HashTreeMap<K, V>(result, size + 1);
  }

  @Override
  public HashTreeMap<K, V> remove(K key) {
    Tree<K, V> result = root.remove(keyHashCode(key), key, 0);
    if (root == result) {
      return this;
    }
    if (result == null) {
      return new HashTreeMap<K, V>(new Tree<K, V>(), 0);
    }
    else {
      return new HashTreeMap<K, V>(result, size - 1);
    }
  }

  @Override
  public Iterator<Map.Entry<K, V>> iterator() {
    return new It<K, V>(root);
  }

  private static final class Tree<K, V> extends Item<K, V> {
    final int mask;
    final Item<K, V>[] items;

    Tree() {
      mask = 0;
      items = new Item[]{};
    }

    Tree(Tree<K, V> that, Item<K, V> entry, int index) {
      int bit = 1 << index;
      if (entry != null) {
        if ((that.mask & (1 << index)) == 0) {
          // Insert new entry into node.
          mask = that.mask | (1 << index);
          items = new Item[Integer.bitCount(mask)];
          int shift = shift(bit);
          for (int n = 0; n < items.length; n++) {
            if (n < shift) {
              items[n] = that.items[n];
            }
            else if (n == shift) {
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
          int shift = shift(bit);
          for (int n = 0; n < items.length; n++) {
            if (n == shift) {
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
        int shift = shift(bit);
        for (int n = 0; n < items.length; n++) {
          if (n < shift) {
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
    V find(int hashCode, K key, int level) {
      int prefix = (hashCode >>> (level * MASK_WIDTH)) & MASK;
      Item<K, V> item = item(prefix);
      if (item instanceof Tree) {
        Tree<K, V> tree = (Tree<K, V>) item;
        return tree.find(hashCode, key, level + 1);
      }
      return Entry.find((Entry<K, V>) item, hashCode, key);
    }

    Tree<K, V> insert(int hashCode, K key, V value, int level) {
      int prefix = (hashCode >>> (level * MASK_WIDTH)) & MASK;
      Item<K, V> item = item(prefix);
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
      return new Tree<K, V>(this, item, prefix);
    }

    Tree<K, V> remove(int hashCode, K key, int level) {
      int prefix = (hashCode >>> (level * MASK_WIDTH)) & MASK;
      Item<K, V> item = item(prefix);
      if (item == null) {
        return this;
      }
      if (item instanceof Tree) {
        Tree<K, V> tree = (Tree<K, V>) item;
        Tree<K, V> result = tree.remove(hashCode, key, level + 1);
        if (tree != result) {
          return compact(this, result, prefix);
        }
        return this;
      }
      Entry<K, V> entry = (Entry<K, V>) item;
      Entry<K, V> result = entry.remove(hashCode, key);
      if (entry != result) {
        return compact(this, result, prefix);
      }
      return this;
    }

    static <K, V> Tree<K, V> compact(Tree<K, V> tree, Item<K, V> item, int index) {
      if (item == null) {
        int mask = tree.mask & ~(1 << index);
        if (mask == 0) {
          return null;
        }
        // TODO contract tables with only one entry
      }
      return new Tree<K, V>(tree, item, index);
    }

    Item<K, V> item(int prefix) {
      int bit = 1 << prefix;
      if ((mask & bit) == 0) {
        return null;
      }
      return items[shift(bit)];
    }

    int shift(int bit) {
      return Integer.bitCount(mask & (bit - 1));
    }
  }

  private static class It<K, V> implements Iterator<Map.Entry<K, V>> {
    abstract static class Stack<K, V> {
      final Stack<K, V> next;

      Stack(Stack<K, V> next) {
        this.next = next;
      }

      abstract boolean hasNext();

      abstract Item<K, V> next();
    }

    static class NodeLevel<K, V> extends Stack<K, V> {
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

    static class EntryLevel<K, V> extends Stack<K, V> {
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

    Stack<K, V> stack;

    It(Tree<K, V> root) {
      if (root.items.length > 0) {
        stack = new NodeLevel<K, V>(null, root);
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
            if (stack instanceof EntryLevel<?, ?>
                || entry.next == null) {
              while (stack != null) {
                if (stack.hasNext()) {
                  break;
                }
                stack = stack.next;
              }
              return entry;
            }
            stack = new EntryLevel<K, V>(stack, (Entry<K, V>) item);
          }
          else {
            stack = new NodeLevel<K, V>(stack, (Tree<K, V>) item);
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
