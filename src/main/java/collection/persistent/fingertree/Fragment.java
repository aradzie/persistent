package collection.persistent.fingertree;

import collection.persistent.Seq;

abstract class Fragment<T> {
  abstract T head();

  abstract Digit<T> asDigit();

  abstract int size();

  abstract T get(int index);

  abstract Fragment<T> set(int index, T v);

  abstract void accept(Seq.Visitor<T> visitor);
}
