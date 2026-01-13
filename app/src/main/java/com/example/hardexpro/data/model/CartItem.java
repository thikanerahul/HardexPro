package com.example.hardexpro.data.model;

import java.io.Serializable;

public class CartItem implements Serializable {
    private Item item;
    private double quantity;
    private double total;

    public CartItem(Item item, double quantity) {
        this.item = item;
        this.quantity = quantity;
        calculateTotal();
    }

    public void calculateTotal() {
        this.total = item.getSellingPrice() * quantity;
    }

    // Getters and Setters
    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
        calculateTotal();
    }

    public double getTotal() {
        return total;
    }
}
