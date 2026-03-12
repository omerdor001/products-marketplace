package com.example.demo.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

import com.example.demo.domain.Coupon.ValueType;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "Products")
public abstract class Product {
    @JsonView(Views.Public.class)
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JsonView(Views.Public.class)
    private String name;

    @JsonView(Views.Public.class)
    private String description;

    @JsonView(Views.Public.class)
    private String imageUrl;

    @JsonView(Views.Admin.class)
    private LocalDateTime createdAt = LocalDateTime.now();

    @JsonView(Views.Admin.class)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Product() {}

    public Product(String name, String description, String imageUrl) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Product(UUID id,String name, String description, String imageUrl) {
        this.id=id;
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

    public ValueType getValueType() {
        throw new UnsupportedOperationException("Value is only supported for coupons");
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

    public void setValueType(Coupon.ValueType valueType) {
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