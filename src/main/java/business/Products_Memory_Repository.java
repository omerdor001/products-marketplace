package business;

import java.util.ArrayList;
import java.util.List;

public class Products_Memory_Repository {
    private List<Product> products;

    public Products_Memory_Repository() {
        this.products = new ArrayList<>();
    }

    public void addProduct(String name, String description, String imageUrl) {
        Product product = new Product(name, description, imageUrl);
        products.add(product);
    }

    @SuppressWarnings("unlikely-arg-type")
    public void removeProduct(String productId) {
        products.removeIf(p -> p.getId().equals(productId));
    }

    public void updateCostPrice(String productId, double costPrice) {
        Product product = getProductById(productId);
        if (product != null) {
            product.setCostPrice(costPrice);
        }
    }

    public void updateMarginPercentage(String productId, double marginPercentage) {
        Product product = getProductById(productId);
        if (product != null) {
            product.setMarginPercentage(marginPercentage);
        }
    }

    public void updateValue(String productId, String value) {
        Product product = getProductById(productId);
        if (product != null) {
            product.setValue(value);
        }
    }

    public void updateImageURL(String productId, String imageUrl) {
        Product product = getProductById(productId);
        if (product != null) {
            product.setImageUrl(imageUrl);
        }
    }

    public void markAsSold(String productId) {
        Product product = getProductById(productId);
        if (product != null) {
            product.setSold(true);
        }
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    @SuppressWarnings("unlikely-arg-type")
    public Product getProductById(String productId) {
        return products.stream()
                .filter(p -> p.getId().equals(productId))
                .findFirst()
                .orElse(null);
    }

}
