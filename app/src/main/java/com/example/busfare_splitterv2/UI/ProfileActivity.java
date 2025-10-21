package com.example.busfare_splitterv2.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.busfare_splitterv2.R;
import com.google.android.material.navigation.NavigationView;

public class ProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private SharedPreferences prefs;

    // References to the header views inside the Navigation Drawer
    private TextView tvNavHeaderName, tvNavHeaderEmail;

    // References to the top bar buttons (from app_bar_main.xml)
    private ImageButton btnOpenDrawer, btnPastTrips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Assuming your main layout file with the DrawerLayout is activity_main
        setContentView(R.layout.activity_profile);

        prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);

        initViews();
        setupNavigationDrawer();
        loadDrawerHeaderDetails();
        setupAppBarListeners();

        // Check if the user is logged in
        if (!prefs.contains("jwt_token")) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Get views from the included app_bar_main.xml layout
        btnOpenDrawer = findViewById(R.id.btnOpenDrawer);
        btnPastTrips = findViewById(R.id.btnPastTrips);
    }

    private void setupNavigationDrawer() {
        // Set up the toggle button (though we're using a custom ImageButton)
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setupAppBarListeners() {
        // 1. Profile Image Button (to open the drawer)
        btnOpenDrawer.setOnClickListener(v -> drawerLayout.openDrawer(navigationView));

        // 2. Past Trips Button (on the main screen's app bar)
        btnPastTrips.setOnClickListener(v -> navigateToTripsList());
    }

    private void loadDrawerHeaderDetails() {
        View headerView = navigationView.getHeaderView(0);
        tvNavHeaderName = headerView.findViewById(R.id.tvNavHeaderName);
        tvNavHeaderEmail = headerView.findViewById(R.id.tvNavHeaderEmail);

        // Load details from SharedPreferences
        String userName = prefs.getString("user_name", "User Name");
        String userEmail = prefs.getString("user_email", "user@example.com");

        tvNavHeaderName.setText(userName);
        tvNavHeaderEmail.setText(userEmail);
    }

    // --- Navigation Item Selection Handler ---

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Close the drawer before performing navigation
        drawerLayout.closeDrawers();

        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Already on the Home screen (or AddTrip screen)
            // No action needed if Home is the current activity.
            return true;
        } else if (id == R.id.nav_view_trips) {
            navigateToTripsList();
        } else if (id == R.id.nav_settings) {
            // Implement Settings Activity/Fragment later
            Toast.makeText(this, "Settings coming soon!", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_logout) {
            showLogoutDialog();
            return true; // Handle logout immediately
        }

        return true;
    }

    // --- Action Methods ---

    private void navigateToTripsList() {
        // Assuming you have a TripListActivity to show all past trips
        // startActivity(new Intent(MainActivity.this, TripListActivity.class));
        Toast.makeText(this, "Navigating to Past Trips...", Toast.LENGTH_SHORT).show();
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Log Out", (dialog, which) -> performLogout())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void performLogout() {
        // Clear authentication data
        prefs.edit().clear().apply();

        // Redirect to the Login screen and clear the activity stack
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        Toast.makeText(this, "Logged out successfully.", Toast.LENGTH_SHORT).show();
        finish();
    }
}