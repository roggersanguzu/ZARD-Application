package com.example.ecommerce;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainShoppingCart extends AppCompatActivity implements CartAdapter.OnCartItemChangeListener {
    private RecyclerView cartRecyclerView;
    private TextView cartSubtotal, continueShopping;
    private Button checkoutButton;
    private ArrayList<CartItem> cartItems;
    private CartAdapter cartAdapter;
    String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_shopping_cart);

        // Initialize views
        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        cartSubtotal = findViewById(R.id.cartSubtotal);
        continueShopping = findViewById(R.id.continueShopping);
        checkoutButton = findViewById(R.id.checkoutButton);

//        Intent sending = getIntent();
//
//        final String location = sending.getStringExtra("location");

        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs",MODE_PRIVATE);
        location = sharedPreferences.getString("Selected_place",null);

        continueShopping.setOnClickListener(view -> {
            Intent intent = new Intent(MainShoppingCart.this, LandingPage.class);
            startActivity(intent);
        });

        // Retrieve cart items from intent
        cartItems = getIntent().getParcelableArrayListExtra("cartItems");


        // Set up RecyclerView
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(this, cartItems, this);
        cartRecyclerView.setAdapter(cartAdapter);

        // Update subtotal
        updateSubtotal();

        // Set Checkout Button Listener
        checkoutButton.setOnClickListener(view -> saveCartToFirebase());
//        checkoutButton.setOnClickListener(view -> stripePaymentgo());
    }

//    private void stripePaymentgo() {
//        Intent intent =
//    }

    // Calculate and update subtotal
    int subtotal = 0;
    public int updateSubtotal() {
        for (CartItem item : cartItems) {
            subtotal += item.getProductPrice() * item.getQuantity();
        }
        cartSubtotal.setText(String.format("$%d", subtotal));
        return subtotal;
    }


    @Override
    public void onQuantityChanged() {
        updateSubtotal();
    }

    @Override
    public void onItemRemoved(CartItem removedItem) {
        updateSubtotal();
        Toast.makeText(this, removedItem.getProductName() + " removed from cart", Toast.LENGTH_SHORT).show();
    }

    // Save cart items to Firebase
    private void saveCartToFirebase() {
        if (cartItems == null || cartItems.isEmpty()) {
            Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("checkoutOrders");
        String orderId = databaseReference.push().getKey(); // Generate unique order ID



        if (orderId != null) {
            Map<String, Object> orderData = new HashMap<>();
            ArrayList<Map<String, Object>> itemsList = new ArrayList<>();

            String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

            for (CartItem item : cartItems) {
                Map<String, Object> itemData = new HashMap<>();
                itemData.put("name", item.getProductName());
                itemData.put("price", item.getProductPrice());
                itemData.put("quantity", item.getQuantity());
                orderData.put("date", currentDate);
                // Add the current date
                itemsList.add(itemData);
            }

            orderData.put("items", itemsList);
            orderData.put("subtotal", cartSubtotal.getText().toString());
            orderData.put("status", "Paid");
            orderData.put("location",location);// Optional: Add an order status

            databaseReference.child(orderId).setValue(orderData)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(MainShoppingCart.this, "Order placed successfully", Toast.LENGTH_SHORT).show();
                        // Clear cart after saving
//                        cartItems.clear();
                        cartAdapter.notifyDataSetChanged();
                        updateSubtotal();
                    })
                    .addOnFailureListener(e -> Toast.makeText(MainShoppingCart.this, "Failed to save order: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Failed to generate order ID", Toast.LENGTH_SHORT).show();
        }

        Intent intent = new Intent(MainShoppingCart.this, Pay.class);
        intent.putExtra("amount",cartSubtotal.getText().toString());
        startActivity(intent);

    }
}
