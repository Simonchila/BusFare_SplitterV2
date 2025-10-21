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
import androidx.recyclerview.widget.ItemTouchHelper;
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

        // Swipe-to-delete setup
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView,
                                  RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false; // no drag & drop
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Trip tripToDelete = trips.get(position);
                deleteTrip(tripToDelete.getId(), position);
            }
        }).attachToRecyclerView(rvTrips);

        // Profile click
        imageView.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });

        // Floating button
        fab.setOnClickListener(v -> startActivity(new Intent(this, AddTripActivity.class)));

        // Load trips
        loadTripsFromServer();
    }

    private void deleteTrip(int tripId, int position) {
        String token = prefs.getString("jwt_token", null);
        if (token == null) {
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        apiService.deleteTrip("Bearer " + token, tripId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    trips.remove(position);
                    adapter.notifyItemRemoved(position);
                    Toast.makeText(TripListActivity.this, "Trip deleted", Toast.LENGTH_SHORT).show();
                    if (trips.isEmpty()) tvEmpty.setVisibility(View.VISIBLE);
                } else {
                    adapter.notifyItemChanged(position); // restore swipe
                    Toast.makeText(TripListActivity.this, "Failed to delete trip", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                adapter.notifyItemChanged(position); // restore swipe
                Toast.makeText(TripListActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
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
                    trips = response.body();
                    adapter.updateTrips(trips);
                    if (trips.isEmpty()) tvEmpty.setVisibility(View.VISIBLE);
                    else rvTrips.setVisibility(View.VISIBLE);
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
        loadTripsFromServer(); // Auto-refresh
    }
}
