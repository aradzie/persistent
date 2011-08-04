package collection.persistent;

public class BankersQueueTest extends QueueTest {
  @Override
  <T> Queue<T> create() {
    return new BankersQueue<T>();
  }
}
