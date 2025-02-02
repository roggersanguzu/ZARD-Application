package com.example.ecommerce;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ProductDescription extends AppCompatActivity {
    private static ArrayList<CartItem> cartItems = new ArrayList<>(); // Shared cart items list

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_description);

        // Retrieve data from Intent
        int productImageResId = getIntent().getIntExtra("productImageResId", 0);
        String productName = getIntent().getStringExtra("productName");
        int productPrice = getIntent().getIntExtra("productPrice",0);  // Price as an integer
        String productDesc = getIntent().getStringExtra("productDesc");

        // Set data to the views
        ImageView productImage = findViewById(R.id.productImage);
        TextView productNameView = findViewById(R.id.productName);
        TextView productPriceView = findViewById(R.id.productPrice);
        TextView productDescView = findViewById(R.id.productDesc);

        // Set the image resource if available
        if (productImageResId != 0) {
            productImage.setImageResource(productImageResId);
        }
        // Set the product name, price (formatted), and description
        productNameView.setText(productName);
        //productNameView.setText(productPrice);
        if (productPrice != 0) {
            productPriceView.setText(String.format("$%d", productPrice));  // Format price as currency
        } else {
           productPriceView.setText("$0");  // Default price if not available
        }
        productDescView.setText(productDesc);

        // Handle the Add to Cart button
        Button addToCartButton = findViewById(R.id.addtocart);
        addToCartButton.setOnClickListener(view -> {
            // Create a CartItem
            CartItem cartItem = new CartItem(productName, productPrice, productImageResId);

            // Add the CartItem to the static list
            cartItems.add(cartItem);

            // Show a Toast message
            Toast.makeText(ProductDescription.this, "Added to Cart", Toast.LENGTH_SHORT).show();

            // Create an Intent to pass the CartItem to the next activity
            Intent intent = new Intent(ProductDescription.this, MainShoppingCart.class);
            intent.putParcelableArrayListExtra("cartItems", cartItems);  // Pass the cartItems list
            startActivity(intent);
        });
    }
}
