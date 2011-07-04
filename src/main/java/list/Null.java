package list;

public class Null {
  private static final Null NULL = new Null();

  private Null() {}

  public static Object mask(Object v) {
    if (v == null) {
      return NULL;
    }
    return v;
  }

  public static Object unmask(Object v) {
    if (v == NULL) {
      return null;
    }
    return v;
  }

  public static boolean isNull(Object v) {
    return v == NULL;
  }
}
