package com.example.imageclassifier;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.imageclassifier.databinding.ActivityProfileBinding;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;
    private static final String TAG = "ProfileActivity";
    private static final String PASSWORD_KEY = "userPassword";
    private UserDatabaseHelper dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize database helper
        dbHelper = new UserDatabaseHelper(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userEmail = preferences.getString("loggedInUserEmail", "No Email Found");
        binding.emailTextView.setText(userEmail);

        Log.d(TAG, "Profile initialized for user: " + userEmail);

        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });
    }

    private void saveProfile() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userEmail = preferences.getString("loggedInUserEmail", null);

        if (userEmail == null) {
            Log.e(TAG, "No logged in user email found");
            Toast.makeText(this, "No user session found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get input values
        String currentPassword = binding.currentPasswordEditText.getText().toString().trim();
        String newPassword = binding.newPasswordEditText.getText().toString().trim();
        String confirmNewPassword = binding.confirmNewPasswordEditText.getText().toString().trim();

        Log.d(TAG, "Attempting password change for user: " + userEmail);

        // Verify current password in database
        if (!dbHelper.loginUser(userEmail, currentPassword)) {
            Log.e(TAG, "Current password verification failed in database");
            Toast.makeText(this, "Current password is incorrect", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verify new passwords match
        if (newPassword.isEmpty() || !newPassword.equals(confirmNewPassword)) {
            Log.e(TAG, "New passwords don't match or are empty");
            Toast.makeText(this, "New passwords don't match or are empty", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Update password in database first
            boolean dbUpdateSuccess = dbHelper.updateUserPassword(userEmail, newPassword);

            if (!dbUpdateSuccess) {
                Log.e(TAG, "Failed to update password in database");
                Toast.makeText(this, "Failed to update password in database", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d(TAG, "Password updated successfully in database");

            // If database update successful, update SharedPreferences
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(PASSWORD_KEY, newPassword);
            boolean prefUpdateSuccess = editor.commit();

            if (prefUpdateSuccess) {
                Log.d(TAG, "Password updated successfully in SharedPreferences");

                // Clear password fields
                binding.currentPasswordEditText.setText("");
                binding.newPasswordEditText.setText("");
                binding.confirmNewPasswordEditText.setText("");

                Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Log.e(TAG, "Failed to update password in SharedPreferences");
                // Revert database change if SharedPreferences update fails
                dbHelper.updateUserPassword(userEmail, currentPassword);
                Toast.makeText(this, "Failed to save password changes", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception while saving password: " + e.getMessage());
            Toast.makeText(this, "Error saving password: " + e.getMessage(), Toast.LENGTH_SHORT).show();

            // Attempt to revert database change in case of exception
            try {
                dbHelper.updateUserPassword(userEmail, currentPassword);
            } catch (Exception revertError) {
                Log.e(TAG, "Failed to revert database changes: " + revertError.getMessage());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}