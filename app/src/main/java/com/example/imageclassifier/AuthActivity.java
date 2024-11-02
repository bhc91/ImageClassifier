package com.example.imageclassifier;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AuthActivity extends AppCompatActivity {
    private EditText emailEditText, passwordEditText;
    private Button loginButton, registerButton;
    private UserDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        dbHelper = new UserDatabaseHelper(this);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                // Input validation
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(AuthActivity.this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (dbHelper.loginUser(email, password)) {
                    // Save email and password to SharedPreferences
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(AuthActivity.this);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("loggedInUserEmail", email);
                    editor.putString("userPassword", password);
                    editor.putBoolean("isLoggedIn", true);
                    editor.apply();

                    Toast.makeText(AuthActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AuthActivity.this, MainActivity.class));
                    finish(); // Close AuthActivity and open MainActivity
                } else {
                    Toast.makeText(AuthActivity.this, "Login failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Updated registerButton Click Listener to redirect to RegisterActivity
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to RegisterActivity
                Intent intent = new Intent(AuthActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}

