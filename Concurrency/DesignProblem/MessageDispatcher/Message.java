package Concurrency.DesignProblem.MessageDispatcher;

/*
    Base interface for all messages
    Requires a type field for the dispatcher to route properly
 */
public interface Message {
    String getType();
}
