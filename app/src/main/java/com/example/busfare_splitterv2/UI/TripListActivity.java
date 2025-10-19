package com.example.busfare_splitterv2.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.busfare_splitterv2.R;
import com.example.busfare_splitterv2.UI.Adapters.TripAdapter;
import com.example.busfare_splitterv2.network.ApiClient;
import com.example.busfare_splitterv2.network.ApiService;
import com.example.busfare_splitterv2.UI.Trip;
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
    private ApiService apiService;
    private SharedPreferences prefs;
    private List<Trip> trips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_list);

        rvTrips = findViewById(R.id.rvTrips);
        tvEmpty = findViewById(R.id.tvEmpty);
        trips = new ArrayList<>();
        FloatingActionButton fab = findViewById(R.id.fabAdd);

        apiService = ApiClient.getApiService();
        prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);

        rvTrips.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TripAdapter(trips, trip -> {
            Intent i = new Intent(TripListActivity.this, SplitResultsActivity.class);
            i.putExtra("trip_id", trip.getId()); // <-- pass the server trip ID
            startActivity(i);
        });
        rvTrips.setAdapter(adapter);

        fab.setOnClickListener(v -> startActivity(new Intent(this, AddTripActivity.class)));

        loadTripsFromServer();
    }

    private void loadTripsFromServer() {
        String token = prefs.getString("jwt_token", null);
        if (token == null) {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        Call<List<Trip>> call = apiService.getTrips("Bearer " + token);
        call.enqueue(new Callback<List<Trip>>() {
            @Override
            public void onResponse(Call<List<Trip>> call, Response<List<Trip>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Trip> trips = response.body();
                    adapter.updateTrips(trips);
                    tvEmpty.setVisibility(trips.isEmpty() ? View.VISIBLE : View.GONE);
                } else {
                    Toast.makeText(TripListActivity.this, "Failed to load trips", Toast.LENGTH_SHORT).show();
                    tvEmpty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<Trip>> call, Throwable t) {
                Toast.makeText(TripListActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                tvEmpty.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTripsFromServer();
    }
}
