package com.example.ecommerce;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminAddProducts extends AppCompatActivity {

    private TextInputEditText etItemName, etDescription, etPrice, etCategory, etQuantity, etImageUrl;
    private Button btnSave, btnHistory;
    private ProgressBar progressBar;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_products);

        // Initialize views
        etItemName = findViewById(R.id.et_item_name);
        etDescription = findViewById(R.id.et_small_price);
        etPrice = findViewById(R.id.et_medium_price);
        etCategory = findViewById(R.id.et_large_price);
        etQuantity = findViewById(R.id.et_qnt);
        etImageUrl = findViewById(R.id.et_image_url);
        btnSave = findViewById(R.id.btn_save_item);
        progressBar = findViewById(R.id.progressBar);
        btnHistory = findViewById(R.id.btnHistory);

        // Initialize Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("products");

        // Set OnClickListener for saving product
        btnSave.setOnClickListener(v -> saveProductToFirebase());

        //Display a history of orders
        btnHistory.setOnClickListener(v -> viewHistory());

    }

    private void viewHistory() {
        Intent intent = new Intent(AdminAddProducts.this, History.class );
        startActivity(intent);
    }


    private void saveProductToFirebase() {
        String name = etItemName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String category = etCategory.getText().toString().trim();
        String quantityStr = etQuantity.getText().toString().trim();
        String imageUrl = etImageUrl.getText().toString().trim();

        // Parse price and quantity
        int price = priceStr.isEmpty() ? 0 : Integer.parseInt(priceStr); // Parse price as int
        int quantity = quantityStr.isEmpty() ? 0 : Integer.parseInt(quantityStr);

        // Validate inputs
        if (name.isEmpty() || description.isEmpty() || price <= 0 || category.isEmpty() || quantity <= 0 || imageUrl.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Create product object
        String productId = databaseReference.push().getKey();
        if (productId == null) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(AdminAddProducts.this, "Failed to generate product ID", Toast.LENGTH_SHORT).show();
            return;
        }

        ProductAdmin product = new ProductAdmin(name, description, price, category, imageUrl, quantity);

        // Save product to Firebase
        databaseReference.child(productId).setValue(product)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AdminAddProducts.this, "Product saved successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AdminAddProducts.this, LandingPage.class);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AdminAddProducts.this, "Failed to save product: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
