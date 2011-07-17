package collection;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A persistent map based on the data structure as described
 * in the publication <em>Ideal Hash Trees</em> by <em>Phil Bagwell</em>.
 *
 * @param <K> Key type.
 * @param <V> Value type.
 */
public class HashTreeMap<K, V> extends AbstractHashMap<K, V> {
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
    if (key == null) {
      throw new NullPointerException();
    }
    int hashCode = key.hashCode();
    return root.find(hashCode, key, hashCode);
  }

  @Override
  public HashTreeMap<K, V> put(K key, V value) {
    if (key == null) {
      throw new NullPointerException();
    }
    int hashCode = key.hashCode();
    Tree<K, V> result = root.insert(hashCode, key, value, hashCode, 0);
    if (root == result) {
      return this;
    }
    return new HashTreeMap<K, V>(result, size + 1);
  }

  @Override
  public HashTreeMap<K, V> remove(K key) {
    if (key == null) {
      throw new NullPointerException();
    }
    int hashCode = key.hashCode();
    Tree<K, V> result = root.remove(hashCode, key, hashCode);
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
      int bit = 1 << (index & MASK);
      if (entry != null) {
        if ((that.mask & (1 << index)) == 0) {
          // insert new entry into node
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
          // replace existing entry in node
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
        // remove entry from node
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

    Tree(int hashCode1, K key1, V value1,
         int hashCode2, K key2, V value2,
         int level) {
      int index1 = (hashCode1 >>> (MASK_WIDTH * level)) & MASK;
      int index2 = (hashCode2 >>> (MASK_WIDTH * level)) & MASK;
      if (index1 == index2) {
        mask = 1 << index1;
        if (level < 5) {
          items = new Item[]{
              new Tree<K, V>(hashCode1, key1, value1,
                  hashCode2, key2, value2,
                  level + 1)
          };
        }
        else {
          items = new Item[]{
              new Entry<K, V>(hashCode1, key1, value1,
                  new Entry<K, V>(hashCode2, key2, value2,
                      null))
          };
        }
      }
      else {
        mask = (1 << index1) | (1 << index2);
        if (index1 < index2) {
          items = new Item[]{
              new Entry<K, V>(hashCode1, key1, value1, null),
              new Entry<K, V>(hashCode2, key2, value2, null),
          };
        }
        else {
          items = new Item[]{
              new Entry<K, V>(hashCode2, key2, value2, null),
              new Entry<K, V>(hashCode1, key1, value1, null),
          };
        }
      }
    }

    @Override
    V find(int hashCode, K key, int prefix) {
      Item<K, V> item = item(prefix);
      if (item instanceof Tree) {
        Tree<K, V> tree = (Tree<K, V>) item;
        return tree.find(hashCode, key, prefix >>> MASK_WIDTH);
      }
      return Entry.find((Entry<K, V>) item, hashCode, key);
    }

    Tree<K, V> insert(int hashCode, K key, V value, int prefix, int level) {
      Item<K, V> item = item(prefix);
      if (item == null) {
        item = new Entry<K, V>(hashCode, key, value, null);
      }
      else if (item instanceof Tree) {
        item = ((Tree<K, V>) item).insert(
            hashCode, key, value, prefix >>> MASK_WIDTH, level + 1);
      }
      else {
        Entry<K, V> entry = (Entry<K, V>) item;
        Entry<K, V> result = entry.remove(hashCode, key);
        if (entry != result) {
          item = new Entry<K, V>(hashCode, key, value, result);
        }
        else {
          if (level < 5) {
            item = new Tree<K, V>(hashCode, key, value,
                entry.hashCode, entry.key, entry.value,
                level + 1);
          }
          else {
            item = new Entry<K, V>(hashCode, key, value, entry);
          }
        }
      }
      return new Tree<K, V>(this, item, prefix & MASK);
    }

    Tree<K, V> remove(int hashCode, K key, int prefix) {
      Item<K, V> item = item(prefix);
      if (item == null) {
        return this;
      }
      if (item instanceof Tree) {
        Tree<K, V> tree = (Tree<K, V>) item;
        Tree<K, V> result = tree.remove(hashCode, key, prefix >>> MASK_WIDTH);
        if (tree != result) {
          return compact(this, result, prefix & MASK);
        }
        return this;
      }
      Entry<K, V> entry = (Entry<K, V>) item;
      Entry<K, V> result = entry.remove(hashCode, key);
      if (entry != result) {
        return compact(this, result, prefix & MASK);
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
      int bit = 1 << (prefix & MASK);
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
