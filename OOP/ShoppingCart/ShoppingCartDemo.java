package OOP.ShoppingCart;

import OOP.ShoppingCart.entities.Cart;
import OOP.ShoppingCart.entities.Customer;
import OOP.ShoppingCart.entities.Product;
import OOP.ShoppingCart.enums.ProductCategory;
import OOP.ShoppingCart.exceptions.CartException;
import OOP.ShoppingCart.observers.CartEventLogger;
import OOP.ShoppingCart.strategies.FlatAmountDiscountStrategy;
import OOP.ShoppingCart.strategies.PercentageDiscountStrategy;

public class ShoppingCartDemo {
    public static void main(String[] args) {
        System.out.println("=== Shopping Cart Demo ===\n");

        // 1. Get the service (singleton) and create a customer
        ShoppingCartService service = ShoppingCartService.getInstance();
        Customer customer = new Customer("CUST-1", "Alice", "alice@example.com");

        // 2. Create some products
        Product laptop = new Product("P1", "Laptop", 1200.00, ProductCategory.ELECTRONICS, 3);
        Product book = new Product("P2", "Clean Code", 40.00, ProductCategory.BOOKS, 10);
        Product headphones = new Product("P3", "Headphones", 150.00, ProductCategory.ELECTRONICS, 5);

        // 3. Create a cart for the customer
        Cart cart = service.createCart(customer);
        System.out.println("Created cart " + cart.getId() + " for " + customer.getName());

        // 4. Register an observer to log cart events
        cart.addObserver(new CartEventLogger());

        // 5. Add items to the cart
        cart.addItem(laptop, 1);
        cart.addItem(book, 2);
        cart.addItem(headphones, 1);

        System.out.println("\nSubtotal: $" + String.format("%.2f", cart.getSubTotal()));

        // 6. Apply a 10% discount strategy
        cart.applyDiscount(new PercentageDiscountStrategy(10));
        System.out.println("Applied discount: 10% off");
        System.out.println("Total after discount: $" + String.format("%.2f", cart.getTotal()));

        // 7. Switch to a flat-amount discount
        cart.applyDiscount(new FlatAmountDiscountStrategy(100));
        System.out.println("\nSwitched discount: $100 off");
        System.out.println("Total after discount: $" + String.format("%.2f", cart.getTotal()));

        // 8. Check out the cart
        System.out.println("\nChecking out...");
        cart.checkout();
        System.out.println("Cart status: " + cart.getCartStatus());

        // 9. Verify the cart is now locked for modifications
        try {
            cart.addItem(book, 1);
        } catch (CartException e) {
            System.out.println("Expected error: " + e.getMessage());
        }

        System.out.println("\n=== Demo Complete ===");
    }
}
