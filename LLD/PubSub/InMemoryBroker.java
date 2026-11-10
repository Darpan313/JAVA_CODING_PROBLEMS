package LLD.PubSub;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/*
    Thread-safe, low latency in-memory message broker.
 */
public class InMemoryBroker {
    private final Map<String, List<BlockingQueue<Message>>> topics = new ConcurrentHashMap<>();
    private final int capacity;

    public InMemoryBroker(int capacity) {
        this.capacity = capacity;
    }

    public boolean publish(String topic, Message message) {
        List<BlockingQueue<Message>> subscribers = topics.get(topic);
        if(subscribers == null || subscribers.isEmpty()) {
            return true;
        }

        boolean allDelivered = true;
        for(BlockingQueue<Message> subscriberQueue : subscribers) {
            if(!subscriberQueue.offer(message)){
                return false;
            }
        }
        return allDelivered;
    }

    public BlockingQueue<Message> subscribe(String topic) {
        // ComputeIfAbsent ensures thread safe atomic initialization of the topic list.
        topics.computeIfAbsent(topic, k -> new CopyOnWriteArrayList<>());
        BlockingQueue<Message> consumerQueue = new ArrayBlockingQueue<>(capacity);
        topics.get(topic).add(consumerQueue);
        return consumerQueue;
    }

    public void unsubscribe(String topic, BlockingQueue<Message> consumerQueue) {
        List<BlockingQueue<Message>> subscribers = topics.get(topic);
        if(subscribers != null) {
            subscribers.remove(consumerQueue);
        }
    }

    public record Message(String id, String payload, long timestamp) {
        public Message(String id, String payload) {
            this(id, payload, System.currentTimeMillis());
        }
    }
}
