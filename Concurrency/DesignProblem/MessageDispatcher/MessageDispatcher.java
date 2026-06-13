package Concurrency.DesignProblem.MessageDispatcher;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/*
    The dispatcher handles registration, unregistration and routing.
 */
public class MessageDispatcher {
    // The thread safe registry
    private final Map<String, List<MessageHandler<? extends Message>>> registry = new ConcurrentHashMap<>();

    /*
        Register a handler for a specific message type. Thread safe
     */
    public <T extends Message> void register(String type, MessageHandler<T> handler) {
        registry.computeIfAbsent(type, k-> new CopyOnWriteArrayList<>()).add(handler);
    }

    /*
        Unregister a handler. Thread Safe
     */
    public <T extends Message> void unregister(String type, MessageHandler<T> handler) {
        List<MessageHandler<? extends Message>> handlers = registry.get(type);
        if(handler != null) {
            handlers.remove(handler);
            if(handlers.isEmpty()) {
                registry.remove(type);
            }
        }
    }

    /*
        Dispatches a message to all registered handlers for its type. Thread safe
     */
    public void dispatch(Message message) {
        if(message == null || message.getType() == null) {
            throw new IllegalArgumentException("Message and type cannot be null");
        }

        List<MessageHandler<? extends Message>> handlers = registry.get(message.getType());
        if(handlers != null || !handlers.isEmpty()) {
            for(MessageHandler<? extends Message> handler : handlers) {
                ((MessageHandler<Message>)handler).handle(message);
            }
        }
        else {
            System.out.println("No handlers registered for type: " + message.getType());
        }
    }

}
