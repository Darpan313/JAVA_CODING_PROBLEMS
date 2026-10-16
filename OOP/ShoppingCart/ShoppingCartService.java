package OOP.ShoppingCart;

import OOP.ShoppingCart.entities.Cart;
import OOP.ShoppingCart.entities.Customer;
import OOP.ShoppingCart.enums.CartStatus;
import OOP.ShoppingCart.exceptions.CartException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ShoppingCartService {
    private static volatile ShoppingCartService instance;
    private static final Object lock = new Object();
    private final ConcurrentHashMap<String, Cart> carts;
    private final AtomicInteger cartCounter;

    private ShoppingCartService() {
        this.carts = new ConcurrentHashMap<>();
        this.cartCounter = new AtomicInteger(0);
    }

    static ShoppingCartService getInstance() {
        if(instance == null) {
            synchronized (lock) {
                if(instance == null) {
                    instance = new ShoppingCartService();
                }
            }
        }
        return instance;
    }

    Cart createCart(Customer customer) {
        // check customer has already an active cart
        Cart existing = getActiveCartForCustomer(customer.getId());
        if(existing != null) {
            return existing;
        }

        String cartId = "CART-" + cartCounter.incrementAndGet();
        Cart cart = new Cart(cartId, customer);
        carts.put(cartId, cart);
        return cart;
    }

    public Cart getCart(String cartId) {
        Cart cart = carts.get(cartId);
        if(cart == null) {
            throw new CartException("Cart " + cartId + " not found");
        }
        return cart;
    }

    Cart getActiveCartForCustomer(String customerId) {
        for(Cart cart : carts.values()) {
            if(cart.getCustomer().getId().equals(customerId)
            && cart.getCartStatus() == CartStatus.ACTIVE) {
                return cart;
            }
        }
        return null;
    }
}
