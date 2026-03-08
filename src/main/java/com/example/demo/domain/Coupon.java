package com.example.demo.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "Coupons")
public class Coupon extends Product {

    public enum ValueType {
        STRING,
        IMAGE
    }

    private double costPrice;
    private double marginPercentage;
    private double minimumSellPrice;
    private boolean isSold;
    private ValueType valueType;
    private String couponValue;

    public Coupon() {
        super();
    }

    public Coupon(String name, String description, String imageUrl, double costPrice, double marginPercentage, ValueType valueType, String value) {
        super(name, description, imageUrl);
        this.costPrice = costPrice;
        this.marginPercentage = marginPercentage;
        this.valueType = valueType;
        this.couponValue = value;
        calculateMinimumSellPrice();
    }

    // ---------- Getters ----------

    @Override
    public double getCostPrice() {
        return costPrice;
    }

    @Override
    public double getMarginPercentage() {
        return marginPercentage;
    }

    @Override
    public double getMinimumSellPrice() {
        return minimumSellPrice;
    }

    @Override
    public boolean isSold() {
        return isSold;
    }

    @Override
    public ValueType getValueType() {
        return valueType;
    }

    @Override
    public String getValue() {
        return couponValue;
    }

    // ---------- Setters ----------

    @Override
    public void setCostPrice(double costPrice) {
        this.costPrice = costPrice;
        calculateMinimumSellPrice();
    }

    @Override
    public void setMarginPercentage(double marginPercentage) {
        this.marginPercentage = marginPercentage;
        calculateMinimumSellPrice();
    }

    public void setSold(boolean sold) {
        isSold = sold;
    }

    @Override
    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }

    @Override
    public void setValue(String value) {
        this.couponValue = value;
    }

    private void calculateMinimumSellPrice() {
        if (costPrice < 0) {
            throw new IllegalArgumentException("Cost price cannot be negative");
        }
        if (marginPercentage < 0 || marginPercentage > 100) {
            throw new IllegalArgumentException("Margin percentage must be between 0 and 100");
        }
        this.minimumSellPrice = costPrice * (1 + marginPercentage / 100);
    }

    // ---------- Validation ----------
    @Override
    public void validate() {
       super.validate(); 
        if (getCostPrice() < 0) {
            throw new IllegalArgumentException("Cost price cannot be negative");
        }
        if (getMarginPercentage() < 0 || getMarginPercentage() > 100) {
            throw new IllegalArgumentException("Margin percentage must be between 0 and 100");
        }
        if (valueType == null) {
            throw new IllegalArgumentException("Value type cannot be null");
        }
        if (getValue() == null || getValue().trim().isEmpty()) {
            throw new IllegalArgumentException("Coupon value cannot be null or empty");
        }
    }
}