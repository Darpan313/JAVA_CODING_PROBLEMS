package OOP.InventoryManagement.exceptions;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String sku, int wanted, int available) {
        super("SKU= " + sku + " wanted=" + wanted + " available=" + available);
    }
}
