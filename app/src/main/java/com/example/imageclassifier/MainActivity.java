package com.example.imageclassifier;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.imageclassifier.databinding.ActivityMainBinding;

/** Entrypoint for app */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check login status
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);

        // Redirect to AuthActivity if not logged in
        if (!isLoggedIn) {
            startActivity(new Intent(MainActivity.this, AuthActivity.class));
            finish();
            return; // Prevent further execution in MainActivity
        }

        // Set up the binding and content view
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Retrieve logged-in user's email from SharedPreferences
        String loggedInUserEmail = preferences.getString("loggedInUserEmail", "No Email Found");

        // Set the logged-in email to the TextView using View Binding
        binding.userEmailTextView.setText("Welcome, " + loggedInUserEmail + "!");

        // Set up the logout button using View Binding
        binding.logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        // Set up the profile button using View Binding
        binding.profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            }
        });

    }

    private void logoutUser() {
        // Clear login status from SharedPreferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.remove("loggedInUserEmail"); // Remove the saved email
        editor.apply();

        // Redirect to AuthActivity
        Intent intent = new Intent(MainActivity.this, AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}