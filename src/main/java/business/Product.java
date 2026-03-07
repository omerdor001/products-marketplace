package business;

import java.time.LocalDateTime;
import java.util.UUID;

public class Product {
    private UUID id;
    private String name;
    private String description;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

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

}