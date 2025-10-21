package com.example.busfare_splitterv2.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.busfare_splitterv2.R;
import com.example.busfare_splitterv2.UI.Adapters.TripAdapter;
import com.example.busfare_splitterv2.network.ApiClient;
import com.example.busfare_splitterv2.network.ApiService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TripListActivity extends AppCompatActivity {

    private RecyclerView rvTrips;
    private TripAdapter adapter;
    private TextView tvEmpty;
    private ProgressBar progressBar;
    private ApiService apiService;
    private SharedPreferences prefs;
    private List<Trip> trips;

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_list);

        // UI References
        rvTrips = findViewById(R.id.rvTrips);
        trips = new ArrayList<>();
        tvEmpty = findViewById(R.id.tvEmpty);
        progressBar = findViewById(R.id.progressBar);
        imageView = findViewById(R.id.ivProfile);
        FloatingActionButton fab = findViewById(R.id.fabAdd);

        // API + prefs
        apiService = ApiClient.getApiService();
        prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);

        // RecyclerView setup
        rvTrips.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TripAdapter(trips, trip -> {
            // Go to split screen for this trip
            Intent i = new Intent(TripListActivity.this, TripDetailsActivity.class);
            i.putExtra("trip_id", trip.getId());
            startActivity(i);
        });

        rvTrips.setAdapter(adapter);

        // Set click listener for the ImageView
        imageView.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });

        // Floating button
        fab.setOnClickListener(v -> startActivity(new Intent(this, AddTripActivity.class)));

        // Load data
        loadTripsFromServer();
    }

    private void loadTripsFromServer() {
        String token = prefs.getString("jwt_token", null);
        if (token == null) {
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
        rvTrips.setVisibility(View.GONE);

        apiService.getTrips("Bearer " + token).enqueue(new Callback<List<Trip>>() {
            @Override
            public void onResponse(Call<List<Trip>> call, Response<List<Trip>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<Trip> trips = response.body();
                    adapter.updateTrips(trips);
                    if (trips.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                    } else {
                        rvTrips.setVisibility(View.VISIBLE);
                    }
                } else {
                    tvEmpty.setVisibility(View.VISIBLE);
                    Toast.makeText(TripListActivity.this, "Failed to load trips", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Trip>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                tvEmpty.setVisibility(View.VISIBLE);
                Toast.makeText(TripListActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTripsFromServer(); // Auto-refresh after returning
    }
}
