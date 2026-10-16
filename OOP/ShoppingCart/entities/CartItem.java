package OOP.ShoppingCart.entities;

/*
    CartItem captures a product reference, quantity and price snapshot. The priceAtAddition is key design here: it freezes
    the price at the moment the item enters the cart.

    The quantity field is not final here because Cart updates it when the customer changes how many units they want.
    Note: setQuantity is not public, it is package private so only Cart can call it. External code can't directly mutate
    CartItem.
 */
public class CartItem {
    private final Product product;
    private int quantity;
    private final double priceAtAddition;

    public CartItem(Product product, double priceAtAddition, int quantity) {
        this.product = product;
        this.priceAtAddition = priceAtAddition;
        this.quantity = quantity;
    }

    public double getSubtotal() {
        return quantity * priceAtAddition;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPriceAtAddition() {
        return priceAtAddition;
    }
}
