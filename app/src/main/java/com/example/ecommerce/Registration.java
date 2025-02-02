package com.example.ecommerce;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Registration extends AppCompatActivity {
    Button btnregister;
    EditText name, email, username, password, confirmPassword;
    FirebaseDatabase db;
    DatabaseReference root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);

        // Initialize UI components
        btnregister = findViewById(R.id.registerbtn);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        username = findViewById(R.id.Username);
        password = findViewById(R.id.pass);
        confirmPassword = findViewById(R.id.confirmpass);

        // Initialize Firebase database


        // Set up register button click listener
        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db = FirebaseDatabase.getInstance();
                root = db.getReference("users");

                String Name = name.getText().toString().trim();
                String Email = email.getText().toString().trim();
                String Username = username.getText().toString().trim();
                String Password = password.getText().toString().trim();
                String ConfirmPassword = confirmPassword.getText().toString().trim();

                // Validation checks
                if (Name.isEmpty()) {
                    name.setError("Name is required!");
                    name.requestFocus();
                    return;
                }

                if (Email.isEmpty()) {
                    email.setError("Email is required!");
                    email.requestFocus();
                    return;
                }

                if (Username.isEmpty()) {
                    username.setError("Username is required!");
                    username.requestFocus();
                    return;
                }

                if (Password.isEmpty()) {
                    password.setError("Password is required!");
                    password.requestFocus();
                    return;
                }

                if (ConfirmPassword.isEmpty()) {
                    confirmPassword.setError("Confirmation password is required!");
                    confirmPassword.requestFocus();
                    return;
                }

                if (!Password.equals(ConfirmPassword)) {
                    confirmPassword.setError("Password mismatch!");
                    confirmPassword.requestFocus();
                    return;
                }

                db = FirebaseDatabase.getInstance();
                root = db.getReference("users");

                // Save credentials to Firebase
                Credentials creds = new Credentials(Name, Email, Username, Password);
                String uniqueKey = root.push().getKey();

                root.child(uniqueKey).setValue(creds).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Registration.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), Login.class));
                    } else {
                        Toast.makeText(Registration.this, "Registration Failed!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Insets listener for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
