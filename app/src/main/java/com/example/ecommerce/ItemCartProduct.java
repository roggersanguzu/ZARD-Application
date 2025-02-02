package com.example.ecommerce;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ItemCartProduct extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_cart_product);

        // Retrieve cart items from the Intent
        ArrayList<CartItem> cartItems = (ArrayList<CartItem>) getIntent().getSerializableExtra("cartItems");

        // Set up RecyclerView

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up Adapter
        //cartAdapter = new CartAdapter(this, cartItems,this);

        recyclerView.setAdapter(cartAdapter);
    }
}
