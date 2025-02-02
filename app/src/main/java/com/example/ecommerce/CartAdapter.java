package com.example.ecommerce;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private Context context;
    private ArrayList<CartItem> cartItems;
    private OnCartItemChangeListener listener;

    // Constructor
    public CartAdapter(Context context, ArrayList<CartItem> cartItems, OnCartItemChangeListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_item_cart_product, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        // Bind data to views
        holder.productName.setText(item.getProductName());
        holder.productPrice.setText(String.format("$%d", item.getProductPrice()));
        holder.productImage.setImageResource(item.getProductImageResId());

        // Update quantity TextView
        holder.quantity.setText(String.valueOf(item.getQuantity()));

        // Handle increase button
        holder.increaseButton.setOnClickListener(v -> {
            item.increaseQuantity();
            holder.quantity.setText(String.valueOf(item.getQuantity())); // Update quantity in UI
            notifyItemChanged(position); // Notify RecyclerView of change
            listener.onQuantityChanged(); // Notify the activity
        });

        // Handle decrease button
        holder.decreaseButton.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                item.decreaseQuantity();
                holder.quantity.setText(String.valueOf(item.getQuantity())); // Update quantity in UI
                notifyItemChanged(position); // Notify RecyclerView of change
                listener.onQuantityChanged(); // Notify the activity
            }
        });

        // Handle delete button
        holder.deleteButton.setOnClickListener(v -> {
            CartItem removedItem = cartItems.get(position);
            cartItems.remove(position);
            notifyItemRemoved(position);
            listener.onItemRemoved(removedItem);
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    // ViewHolder
    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, quantity;
        ImageView productImage, deleteButton;
        Button increaseButton, decreaseButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            quantity = itemView.findViewById(R.id.productQuantity);
            productImage = itemView.findViewById(R.id.productImage);
            increaseButton = itemView.findViewById(R.id.increaseQuantityButton);
            decreaseButton = itemView.findViewById(R.id.decreaseQuantityButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    // Listener interface for subtotal updates and item removal
    public interface OnCartItemChangeListener {
        void onQuantityChanged();
        void onItemRemoved(CartItem removedItem);
    }
}
