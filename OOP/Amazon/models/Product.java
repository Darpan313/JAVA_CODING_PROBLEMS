package OOP.Amazon.models;

import OOP.ShoppingCart.enums.ProductCategory;

import java.util.UUID;

public class Product {
    private String id;
    private String name;
    private ProductCategory productCategory;
    private double price;
    private String description;

    protected Product() {

    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public ProductCategory getProductCategory() {
        return productCategory;
    }

    public double getPrice() {
        return price;
    }

    public static class Builder {
        private final String name;
        private final double price;
        private String description = "";
        private ProductCategory productCategory;

        public Builder(String name, double price) {
            this.name = name;
            this.price = price;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withCategory(ProductCategory category) {
            this.productCategory = productCategory;
            return this;
        }

        public Product build() {
            Product product = new Product();
            product.id = UUID.randomUUID().toString();
            product.name = name;
            product.description = description;
            product.price = price;
            product.productCategory = productCategory;
            return product;
        }
    }
}
