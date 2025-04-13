package com.example.finalandroidproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    private EditText etNewEmail, etNewPassword, etConfirmPassword, etUsername;
    private Button btnRegisterNewUser;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etNewEmail = findViewById(R.id.et_new_username);
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        etUsername = findViewById(R.id.et_username);
        btnRegisterNewUser = findViewById(R.id.btn_register_new_user);

        auth = FirebaseAuth.getInstance();

        btnRegisterNewUser.setOnClickListener(v -> handleSignupBtnClick());
    }

    private void handleSignupBtnClick() {
        String email = etNewEmail.getText().toString();
        String password = etNewPassword.getText().toString();
        String passwordAgain = etConfirmPassword.getText().toString();
        String username = etUsername.getText().toString();

        if (validateInput(email, password, username)) {
            if (password.equals(passwordAgain)) {
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = auth.getCurrentUser();
                                if (user != null) {
                                    saveUserInFirebaseRealtimeDatabase(user, username);
                                }
                            } else {
                                Toast.makeText(RegisterActivity.this, "Signup failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(this, "Passwords don't match, please try again", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Invalid input, please try again", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInput(String email, String password, String username) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(username)) {
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void saveUserInFirebaseRealtimeDatabase(FirebaseUser firebaseUser, String username) {
        User newUser = new User(firebaseUser.getUid(), username);
        newUser.setAdmin(false);

        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        newUser.addTestResult(currentDate, 100);

        newUser.saveToFirebase();

        Toast.makeText(RegisterActivity.this, "User registered successfully!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(RegisterActivity.this, OpenActivity.class));
        finish();
    }
}
