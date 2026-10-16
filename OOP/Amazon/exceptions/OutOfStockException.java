package OOP.Amazon.exceptions;

/*
    OutOfOrderException signals that an order cannot be fulfilled because inventory is insufficient.
    It extends RunTimeException so callers are not forced to declare everywhere, but the order flow still catches.

 */
public class OutOfStockException extends RuntimeException {
    public OutOfStockException(String message) {
        super(message);
    }
}
