package fingertree;

import debug.IndentingPrintWriter;
import org.junit.Test;

import java.io.OutputStreamWriter;

import static org.junit.Assert.*;

public class FingerTreeTest {
  @Test
  public void test() {
    FingerTree t = new FingerTree();
    for (int n = 0; n < 1000; n++) {
      t.cons(n);
    }
    assertEquals(1000, t.size());
    t.dump(new IndentingPrintWriter(new OutputStreamWriter(System.out)));
  }
}
