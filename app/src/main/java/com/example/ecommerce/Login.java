package com.example.ecommerce;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Login extends AppCompatActivity {
    Button loginbtn;
    EditText loginUsername, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        loginbtn = findViewById(R.id.login);
        loginUsername = findViewById(R.id.username);
        password = findViewById(R.id.pass);


        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateUsername() | !validatePassword()) {

                } else {
                    checkUser();
                }

            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public Boolean validateUsername() {
        String Uname = loginUsername.getText().toString();
        if (Uname.isEmpty()) {
            loginUsername.setError("Username cannot be empty");
            return false;
        } else {
            loginUsername.setError(null);
            return true;
        }
    }

    public Boolean validatePassword() {
        String Passwd = password.getText().toString();
        if (Passwd.isEmpty()) {
            password.setError("Password cannot be empty");
            return false;
        } else {
            password.setError(null);
            return true;
        }
    }

    public void checkUser() {
        String Uusername = loginUsername.getText().toString().trim();
        String userPassword = password.getText().toString().trim();
        DatabaseReference root = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = root.orderByChild("username").equalTo(Uusername);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Loop through the snapshot to get the user data
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String passwordfromDB = userSnapshot.child("password").getValue(String.class);

                        if (Objects.equals(passwordfromDB, userPassword)) {
                            // Password matches, proceed to the next activity
                            if(Uusername.equals("ADMIN") && (userPassword.equals("1234"))){
                              startActivity(new Intent(Login.this, AdminAddProducts.class));
                              return;
                            }
                            password.setError(null);
                            Intent intent = new Intent(Login.this, LandingPage.class);
                            startActivity(intent);
                            return;


                        } else {
                            // Password does not match
                            password.setError("Invalid Credentials");
                            password.requestFocus();
                        }
                    }
                } else {
                    loginUsername.setError("User does not exist");
                    loginUsername.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Optional: Handle potential error here, such as displaying a message
            }
        });
    }
}
