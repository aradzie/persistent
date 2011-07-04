package debug;

public interface Printable {
  void print(IndentingPrintWriter w);

  public static class Util {
    public static void print(IndentingPrintWriter w, Object v) {
      if (v instanceof Printable) {
        ((Printable) v).print(w);
      }
      else {
        w.print("\"");
        w.print(v);
        w.print("\"");
      }
    }
  }
}
