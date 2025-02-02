package com.example.ecommerce;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class History extends AppCompatActivity {

    private RecyclerView ordersRecyclerView;
    private OrderAdapter orderAdapter;
    private List<Map<String, Object>> orderList;
    private DatabaseReference ordersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ordersRecyclerView = findViewById(R.id.ordersRecyclerView);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(this, orderList);
        ordersRecyclerView.setAdapter(orderAdapter);

        // Initialize Firebase database reference
        ordersRef = FirebaseDatabase.getInstance().getReference("checkoutOrders");

        // Fetch orders from Firebase
        fetchOrders();
    }

    private void fetchOrders() {
        ordersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                    String orderId = orderSnapshot.getKey();
                    String subtotal = orderSnapshot.child("subtotal").getValue(String.class);
                    String status = orderSnapshot.child("status").getValue(String.class);
                    String date = orderSnapshot.child("date").getValue(String.class);
                    String location = orderSnapshot.child("location").getValue(String.class);

                    // Initialize a list for item names
                    List<String> itemNames = new ArrayList<>();
                    for (DataSnapshot itemSnapshot : orderSnapshot.child("items").getChildren()) {
                        String itemName = itemSnapshot.child("name").getValue(String.class);
                        itemNames.add(itemName);
                    }

                    // Join the item names into a single string separated by commas
                    String items = TextUtils.join(", ", itemNames);

                    // Create a map for each order
                    Map<String, Object> orderData = new HashMap<>();
                    orderData.put("orderId", orderId);
                    orderData.put("items", items);
                    orderData.put("subtotal", subtotal);
                    orderData.put("status", status);
                    orderData.put("date", date);
                    orderData.put("location", location );

                    orderList.add(orderData);
                }

                // Notify the adapter to update the UI
                orderAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(History.this, "Failed to load orders.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
