package com.example.ecommerce;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LandingPage extends AppCompatActivity {
    TextView btnShoe, btnElectronic, btnClothe, btnFurniture,btnAll;
    GridLayout gridLayout;
    DatabaseReference databaseReference;
    Button location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_landing_page);

        // Initialize views
        btnShoe = findViewById(R.id.btnShoe);
        btnClothe = findViewById(R.id.btnClothe);
        btnElectronic = findViewById(R.id.btnElectronic);
        btnAll = findViewById(R.id.btnAll);
        btnFurniture = findViewById(R.id.btnFurniture);
        gridLayout = findViewById(R.id.gridLayout);
        location = findViewById(R.id.locationbtn);

        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("products");

        // Load all products by default
        loadProductDataAndDisplay(null);

        // Set click listeners for category buttons
        btnShoe.setOnClickListener(view -> loadProductDataAndDisplay("shoes"));
        btnElectronic.setOnClickListener(view -> loadProductDataAndDisplay("electronics"));
        btnClothe.setOnClickListener(view -> loadProductDataAndDisplay("clothes"));
        btnFurniture.setOnClickListener(view -> loadProductDataAndDisplay("furniture"));
        btnAll.setOnClickListener(view -> loadProductDataAndDisplay(null));
        location.setOnClickListener(view -> viewMap());



    }

    private void viewMap() {
        Intent intent = new Intent(LandingPage.this, GoogleMaps.class);
        startActivity(intent);
    }


    private void loadProductDataAndDisplay(String categoryFilter) {
        // Clear the grid layout to avoid duplicates
        gridLayout.removeAllViews();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                    String productName = productSnapshot.child("name").getValue(String.class);
                    String productCategory = productSnapshot.child("category").getValue(String.class); // Read category
                    int productPrice = Integer.parseInt(String.valueOf(productSnapshot.child("price").getValue()));
                    String imageName = productSnapshot.child("imageUrl").getValue(String.class);
                    String productDesc = productSnapshot.child("description").getValue(String.class);

                    // Apply category filter
                    if ((categoryFilter == null || categoryFilter.isEmpty() || productCategory.equalsIgnoreCase(categoryFilter))
                            && imageName != null && productName != null && productPrice != 0) {
                        int imageResId = getResources().getIdentifier(imageName, "drawable", getPackageName());
                        if (imageResId != 0) {
                            createProductCard(imageResId, productName, String.valueOf(productPrice), productDesc);
                        } else {
                            Log.e("ProductData", "Image not found in drawable for imageName: " + imageName);
                            Toast.makeText(LandingPage.this, "Image not found in drawable", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase Error", "Failed to load product data: " + databaseError.getMessage());
                Toast.makeText(LandingPage.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createProductCard(int imageResId, String productName, String productPrice, String productDesc) {
        // Create CardView with larger dimensions and margin for spacing
        CardView cardView = new CardView(this);
        GridLayout.LayoutParams cardParams = new GridLayout.LayoutParams();
        cardParams.width = 400;  // Increased width for larger cards
        cardParams.height = GridLayout.LayoutParams.WRAP_CONTENT; // Height stays dynamic based on content
        cardParams.setMargins(16, 16, 16, 16); // Add margin between cards
        cardView.setLayoutParams(cardParams);
        cardView.setCardElevation(10f);
        cardView.setRadius(16f);
        cardView.setPadding(16, 16, 16, 16);

        // Create LinearLayout inside CardView
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16, 16, 16, 16); // Padding inside layout for spacing
        cardView.addView(layout);

        // Create ImageView for product image with larger dimensions
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, // Width matches parent to fill the card
                250)); // Increased height for the image
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(imageResId);
        layout.addView(imageView);

        // Create TextView for product name
        TextView nameTextView = new TextView(this);
        nameTextView.setText(productName);
        nameTextView.setTextSize(20); // Increased text size for visibility
        nameTextView.setPadding(0, 16, 0, 8);
        layout.addView(nameTextView);

        // Create TextView for product price
        TextView priceTextView = new TextView(this);
        priceTextView.setText(productPrice);
        priceTextView.setTextSize(18); // Increased text size for visibility
        priceTextView.setTextColor(getResources().getColor(android.R.color.darker_gray));
        layout.addView(priceTextView);

        // Set OnClickListener on the CardView to open ProductDescription
        cardView.setOnClickListener(v -> {
            Intent intent = new Intent(LandingPage.this, ProductDescription.class);
            intent.putExtra("productImageResId", imageResId);
            intent.putExtra("productName", productName);
            intent.putExtra("productPrice", Integer.parseInt(productPrice)); // Pass price as an integer
            intent.putExtra("productDesc", productDesc); // Pass product description
            startActivity(intent);
        });

        // Add CardView to GridLayout
        gridLayout.addView(cardView);
    }
}
