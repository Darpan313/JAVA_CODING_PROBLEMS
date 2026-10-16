package OOP.Amazon.services;

import OOP.Amazon.exceptions.OutOfStockException;
import OOP.Amazon.models.OrderLineItem;
import OOP.Amazon.models.Product;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InventoryService {
    private final Map<String, Integer> stock;

    public InventoryService() {
        stock = new ConcurrentHashMap<>();
    }

    public void addStock(Product product, int quantity) {
        stock.put(product.getId(), stock.getOrDefault(product.getId(), 0)+quantity);
    }

    public synchronized void updateStockForOrder(List<OrderLineItem> items) {
        for(OrderLineItem item : items) {
            if(stock.getOrDefault(item.getProductId(), 0)< item.getQuantity()) {
                throw new OutOfStockException("Not enough stock for product ID: " + item.getProductId());
            }
        }

        for(OrderLineItem item : items) {
            stock.compute(item.getProductId(), (id, currentStock) -> currentStock - item.getQuantity());
        }
    }

    public synchronized void restock(List<OrderLineItem> items) {
        for (OrderLineItem item : items) {
            stock.compute(item.getProductId(), (id, currentStock) -> (currentStock == null ? 0 :
                    currentStock + item.getQuantity()));
        }
    }

}
