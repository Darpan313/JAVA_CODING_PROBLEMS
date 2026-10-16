package OOP.Amazon.services;

import OOP.Amazon.models.Product;
import OOP.ShoppingCart.enums.ProductCategory;

import java.util.Collection;
import java.util.List;

public class SearchService {
    private final Collection<Product> productsCatalog;

    public SearchService(Collection<Product> productsCatalog) {
        this.productsCatalog = productsCatalog;
    }

    public List<Product> searchByName(String name) {
        return productsCatalog.stream()
                .filter(p -> p.getName().toLowerCase().contains(name.toLowerCase()))
                .toList();
    }

    public List<Product> searchByCategory(ProductCategory productCategory) {
        return productsCatalog.stream()
                .filter(p -> p.getProductCategory() == productCategory)
                .toList();
    }
}
