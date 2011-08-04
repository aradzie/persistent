package collection.persistent;

public class BatchedQueueTest extends QueueTest {
  @Override
  <T> Queue<T> create() {
    return new BatchedQueue<T>();
  }
}
