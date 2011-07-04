package collection;

import debug.IndentingPrintWriter;
import org.junit.Ignore;
import org.junit.Test;

import java.io.OutputStreamWriter;

import static org.junit.Assert.*;

public class FingerTreeSeqTest {
  @Test(expected = Seq.EmptyException.class)
  public void head() throws Exception {
    new FingerTreeSeq<String>().head();
  }

  @Ignore
  @Test(expected = Seq.EmptyException.class)
  public void tail() throws Exception {
    new FingerTreeSeq<String>().tail();
  }

  @Test
  public void test() {
    FingerTreeSeq<Integer> t = new FingerTreeSeq<Integer>();
    for (int n = 0; n < 1000; n++) {
      t = t.cons(n);
      assertEquals(n, (int) t.head());
      assertEquals(n + 1, t.size());
      for (int m = 0; m < t.size(); m++) {
        assertEquals(m, (int) t.nth(m));
      }
    }
    t.dump(new IndentingPrintWriter(new OutputStreamWriter(System.out)));
  }
}
