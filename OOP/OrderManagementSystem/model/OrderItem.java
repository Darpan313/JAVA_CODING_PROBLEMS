package OOP.OrderManagementSystem.model;

public class OrderItem {
    private final String sku;
    private final int qty;
    private final long unitPriceCents;

    public OrderItem(String sku, int qty, long unitPriceCents) {
        if(qty<=0) throw new IllegalArgumentException("Quantity must be > 0");
        this.sku = sku;
        this.qty = qty;
        this.unitPriceCents = unitPriceCents;
    }

    public String getSku() {
        return sku;
    }

    public int getQty() {
        return qty;
    }

    public long getUnitPriceCents() {
        return unitPriceCents;
    }
}
