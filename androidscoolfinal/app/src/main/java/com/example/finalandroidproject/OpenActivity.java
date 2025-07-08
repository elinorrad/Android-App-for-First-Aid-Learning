package com.example.finalandroidproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * OpenActivity is the entry point for users.
 * It allows login with email and password and navigation to the registration screen.
 */
public class OpenActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private FirebaseAuth auth;

    /**
     * Called when the activity is created.
     * Initializes UI components and Firebase authentication.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open);

        auth = FirebaseAuth.getInstance();

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        Button btnLogin = findViewById(R.id.btn_login);
        Button btnRegister = findViewById(R.id.btn_register);

        // Handle login button click
        btnLogin.setOnClickListener(v -> {
            String email = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(OpenActivity.this, "Please enter email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(OpenActivity.this, "Please enter password", Toast.LENGTH_SHORT).show();
                return;
            }

            // Authenticate using Firebase
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(OpenActivity.this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            Toast.makeText(OpenActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(OpenActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(OpenActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Handle register button click
        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(OpenActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}
