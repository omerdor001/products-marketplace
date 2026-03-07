package domain;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class Product {
    private UUID id = UUID.randomUUID();
    private String name;
    private String description;
    private String imageUrl;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Product(String name, String description, String imageUrl) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // ---------- Getters ----------

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }


    public String getValue() {
        throw new UnsupportedOperationException("Value is only supported for coupons");
    }

    public double getMinimumSellPrice() {
        throw new UnsupportedOperationException("Minimum sell price is only supported for coupons");
    }

    public double getMarginPercentage() {
        throw new UnsupportedOperationException("Unimplemented method 'getMarginPercentage'");
    }

    public double getCostPrice() {
        throw new UnsupportedOperationException("Unimplemented method 'getCostPrice'");
    }

    
    // ---------- Setters ----------
    
    public void setName(String name) {
        this.name = name;
        this.updatedAt = LocalDateTime.now();
    }

    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        this.updatedAt = LocalDateTime.now();
    }

    public void setCostPrice(double costPrice) {
      throw new UnsupportedOperationException("Cost price is only supported for coupons");
    }

    public void setMarginPercentage(double marginPercentage) {
        throw new UnsupportedOperationException("Margin percentage is only supported for coupons");
    }

    public void setValue(String value) {
        throw new UnsupportedOperationException("Value is only supported for coupons");
    }

    public void setSold(boolean isSold) {
        throw new UnsupportedOperationException("Sold status is only supported for coupons");
    }

    public boolean isSold() {
        throw new UnsupportedOperationException("Unimplemented method 'isSold'");
    }

    // ---------- Validation ----------

    public void validate() {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }

        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Product description cannot be null or empty");
        }

        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Image URL cannot be null or empty");
        }

        if (!imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
            throw new IllegalArgumentException("Image URL must start with http:// or https://");
        }
    }



   

}