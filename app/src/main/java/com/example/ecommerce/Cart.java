package com.example.ecommerce;

import java.util.ArrayList;
import java.util.List;

public class Cart {

    private List<CartItem> cartItems = new ArrayList<>();

    // Add item to the cart
    public void addItem(CartItem item) {
        cartItems.add(item);
    }

    // Get the cart items
    public List<CartItem> getCartItems() {
        return cartItems;
    }

    // Update item quantity
    public void updateItemQuantity(int position, int quantity) {
        CartItem item = cartItems.get(position);
        //item.setQuantity(quantity);
    }

    // Get total item count
    public int getTotalItemCount() {
        int total = 0;
        for (CartItem item : cartItems) {
            //total += item.getQuantity();
        }
        return total;
    }


    public double getSubtotal() {
        double total = 0;
        for (CartItem item : cartItems) {
            double price = item.getProductPrice(); // Remove '$' sign
            total += price * item.getQuantity();
        }
        return total;
    }
}

