package OOP.VendingMachine.model;

public class Slot {
    final String code;
    final Product product;
    int quantity;

    public Slot(String code, Product product, int quantity) {
        this.code = code;
        this.product = product;
        this.quantity = quantity;
    }

    public String getCode() {
        return code;
    }

    public int getQuantity() {
        return quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setQuantity(int qty) {
        this.quantity = qty;
    }
}
