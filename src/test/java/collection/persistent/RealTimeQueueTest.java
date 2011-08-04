package collection.persistent;

public class RealTimeQueueTest extends QueueTest {
  @Override
  <T> Queue<T> create() {
    return new RealTimeQueue<T>();
  }
}
