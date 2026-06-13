package Concurrency.DesignProblem.MessageDispatcher;

/*
    Interface for components that want to handle messages
 */
public interface MessageHandler<T> {
    void handle(T message);
}
