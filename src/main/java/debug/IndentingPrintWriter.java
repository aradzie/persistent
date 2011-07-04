package debug;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;

public class IndentingPrintWriter extends Writer {
  private final Writer writer;
  private final LinkedList<String> stack;
  private boolean nl;

  public IndentingPrintWriter(Writer out) {
    writer = out;
    stack = new LinkedList<String>();
  }

  @Override
  public void write(int c) {
    try {
      if (c == '\n') {
        nl = true;
      }
      else {
        if (nl) {
          for (String s : stack) {
            writer.write(s);
          }
          nl = false;
        }
      }
      writer.write(c);
      if (nl) {
        writer.flush();
      }
    }
    catch (IOException ex) {
      throw new IllegalStateException(ex);
    }
  }

  @Override
  public void write(char[] buf, int off, int len) {
    for (int n = 0; n < len; n++) {
      write(buf[off + n]);
    }
  }

  @Override
  public void write(char[] buf) {
    write(buf, 0, buf.length);
  }

  @Override
  public void write(String s, int off, int len) {
    for (int n = 0; n < len; n++) {
      write(s.charAt(off + n));
    }
  }

  @Override
  public void write(String s) {
    write(s, 0, s.length());
  }

  @Override
  public void flush() {
    try {
      writer.flush();
    }
    catch (IOException ex) {
      throw new IllegalStateException(ex);
    }
  }

  @Override
  public void close() {
    try {
      writer.close();
    }
    catch (IOException ex) {
      throw new IllegalStateException(ex);
    }
  }

  public void print(boolean b) {
    write(b ? "true" : "false");
  }

  public void print(char c) {
    write(c);
  }

  public void print(int i) {
    write(String.valueOf(i));
  }

  public void print(long l) {
    write(String.valueOf(l));
  }

  public void print(float f) {
    write(String.valueOf(f));
  }

  public void print(double d) {
    write(String.valueOf(d));
  }

  public void print(char s[]) {
    write(s);
  }

  public IndentingPrintWriter print(String s) {
    if (s == null) {
      s = "null";
    }
    write(s);
    return this;
  }

  public IndentingPrintWriter print(Object o) {
    write(String.valueOf(o));
    return this;
  }

  public IndentingPrintWriter println() {
    write('\n');
    return this;
  }

  public IndentingPrintWriter println(boolean x) {
    print(x);
    println();
    return this;
  }

  public IndentingPrintWriter println(char x) {
    print(x);
    println();
    return this;
  }

  public IndentingPrintWriter println(int x) {
    print(x);
    println();
    return this;
  }

  public IndentingPrintWriter println(long x) {
    print(x);
    println();
    return this;
  }

  public IndentingPrintWriter println(float x) {
    print(x);
    println();
    return this;
  }

  public IndentingPrintWriter println(double x) {
    print(x);
    println();
    return this;
  }

  public IndentingPrintWriter println(char x[]) {
    print(x);
    println();
    return this;
  }

  public IndentingPrintWriter println(String x) {
    print(x);
    println();
    return this;
  }

  public IndentingPrintWriter println(Object x) {
    print(String.valueOf(x));
    println();
    return this;
  }

  public IndentingPrintWriter indent(String s) {
    stack.addLast(s);
    return this;
  }

  public IndentingPrintWriter unindent() {
    stack.removeLast();
    return this;
  }
}
