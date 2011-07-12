package collection;

/**
 * A persistent map based on the data structure as described
 * in the publication <em>Ideal Hash Trees</em> by <em>Phil Bagwell</em>.
 *
 * @param <K> Key type.
 * @param <V> Value type.
 */
public class HashTreeMap<K, V> implements Map<K, V> {
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
    return new HashTreeMap<K, V>(result, size - 1);
  }

  private abstract static class Item<K, V> {
  }

  private static final class Entry<K, V> extends Item<K, V> {
    final int hashCode;
    final K key;
    final V value;
    final Entry<K, V> next;

    Entry(int hashCode, K key, V value, Entry<K, V> next) {
      this.hashCode = hashCode;
      this.key = key;
      this.value = value;
      this.next = next;
    }

    static <K, V> V find(Entry<K, V> entry, int hashCode, K key) {
      while (entry != null) {
        if (entry.hashCode == hashCode && entry.key.equals(key)) {
          return entry.value;
        }
        entry = entry.next;
      }
      return null;
    }

    Entry<K, V> remove(int hashCode, K key) {
      if (this.hashCode == hashCode && this.key.equals(key)) {
        return next;
      }
      if (next == null) {
        return this;
      }
      else {
        Entry<K, V> result = next.remove(hashCode, key);
        if (next != result) {
          return new Entry<K, V>(this.hashCode, this.key, this.value, result);
        }
        return this;
      }
    }
  }

  private static final class Tree<K, V> extends Item<K, V> {
    final Item<K, V>[] items;

    Tree() {
      items = new Item[MASK + 1];
    }

    Tree(Tree<K, V> that, Item<K, V> entry, int prefix) {
      this();
      for (int n = 0; n < items.length; n++) {
        if (n == (prefix & MASK)) {
          items[n] = entry;
        }
        else {
          items[n] = that.items[n];
        }
      }
    }

    V find(int hashCode, K key, int prefix) {
      Item<K, V> item = items[prefix & MASK];
      if (item instanceof Tree) {
        Tree<K, V> tree = (Tree<K, V>) item;
        return tree.find(hashCode, key, prefix >>> MASK_WIDTH);
      }
      return Entry.find((Entry<K, V>) item, hashCode, key);
    }

    Tree<K, V> insert(int hashCode, K key, V value, int prefix, int level) {
      Item<K, V> item = items[prefix & MASK];
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
            Tree<K, V> tree = new Tree<K, V>();
            tree.insert(hashCode, key, value,
                entry.hashCode, entry.key, entry.value,
                level + 1);
            item = tree;
          }
          else {
            item = new Entry<K, V>(hashCode, key, value, entry);
          }
        }
      }
      return new Tree<K, V>(this, item, prefix);
    }

    void insert(int hashCode1, K key1, V value1,
                int hashCode2, K key2, V value2,
                int level) {
      int index1 = (hashCode1 >>> (MASK_WIDTH * level)) & MASK;
      int index2 = (hashCode2 >>> (MASK_WIDTH * level)) & MASK;
      if (index1 == index2) {
        if (level < 5) {
          Tree<K, V> tree = new Tree<K, V>();
          tree.insert(hashCode1, key1, value1,
              hashCode2, key2, value2,
              level + 1);
          items[index1] = tree;
        }
        else {
          items[index1] =
              new Entry<K, V>(hashCode1, key1, value1,
                  new Entry<K, V>(hashCode2, key2, value2,
                      null));
        }
      }
      else {
        items[index1] =
            new Entry<K, V>(hashCode1, key1, value1, null);
        items[index2] =
            new Entry<K, V>(hashCode2, key2, value2, null);
      }
    }

    Tree<K, V> remove(int hashCode, K key, int prefix) {
      Item<K, V> item = items[prefix & MASK];
      if (item == null) {
        return this;
      }
      if (item instanceof Tree) {
        Tree<K, V> tree = (Tree<K, V>) item;
        Tree<K, V> result = tree.remove(hashCode, key, prefix >>> MASK_WIDTH);
        if (tree != result) {
          return new Tree<K, V>(this, result, prefix);
        }
        return this;
      }
      Entry<K, V> entry = (Entry<K, V>) item;
      Entry<K, V> result = entry.remove(hashCode, key);
      if (entry != result) {
        return new Tree<K, V>(this, result, prefix);
      }
      return this;
    }
  }
}
