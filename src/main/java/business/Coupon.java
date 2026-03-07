package business;

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
    private String value;

    public Coupon(String name, String description, String imageUrl, double costPrice, double marginPercentage, ValueType valueType, String value) {
        super(name, description, imageUrl);
        this.costPrice = costPrice;
        this.marginPercentage = marginPercentage;
        this.valueType = valueType;
        this.value = value;
        calculateMinimumSellPrice();
    }

    // ---------- Getters ----------

    public double getCostPrice() {
        return costPrice;
    }

    public double getMarginPercentage() {
        return marginPercentage;
    }

    public double getMinimumSellPrice() {
        return minimumSellPrice;
    }

    public boolean isSold() {
        return isSold;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public String getValue() {
        return value;
    }

    // ---------- Setters ----------

    public void setCostPrice(double costPrice) {
        this.costPrice = costPrice;
        calculateMinimumSellPrice();
    }

    public void setMarginPercentage(double marginPercentage) {
        this.marginPercentage = marginPercentage;
        calculateMinimumSellPrice();
    }

    public void setSold(boolean sold) {
        isSold = sold;
    }

    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }

    public void setValue(String value) {
        this.value = value;
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
}