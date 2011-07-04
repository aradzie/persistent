package fingertree;

import debug.IndentingPrintWriter;
import org.junit.Test;

import java.io.OutputStreamWriter;

public class FingerTreeTest {
  @Test
  public void test() {
    FingerTree t = new FingerTree();
    for (int n = 0; n < 1000; n++) {
      t.cons(n);
    }
    t.dump(new IndentingPrintWriter(new OutputStreamWriter(System.out)));
  }
}
