package com.example.busfare_splitterv2.UI;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View; // Import for View.VISIBLE/GONE
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.busfare_splitterv2.R;
import com.example.busfare_splitterv2.UI.Adapters.PassengerAdapter;
import com.example.busfare_splitterv2.network.ApiClient;
import com.example.busfare_splitterv2.network.ApiService;
import com.example.busfare_splitterv2.network.PassengerRequest;
import com.example.busfare_splitterv2.network.TripResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TripDetailsActivity extends AppCompatActivity {

    private static final String TAG = "TripDetailsActivity"; // Good practice for logging

    private RecyclerView rvPassengers;
    private PassengerAdapter passengerAdapter;
    // UI elements that need to be accessed outside of onCreate must be fields
    private TextView tvRouteDate;
    private TextView tvTotalCost;
    private TextView tvPassengerCount;
    private ProgressBar progressBar;

    private ApiService apiService;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        rvPassengers = findViewById(R.id.rvResults);
        tvRouteDate = findViewById(R.id.tvRouteDate);
        tvTotalCost = findViewById(R.id.tvTotalCost);
        tvPassengerCount = findViewById(R.id.tvPassengerCount);
        progressBar = findViewById(R.id.progressBar);

        // 2. Setup dependencies
        prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        apiService = ApiClient.getApiService();

        // 3. Setup RecyclerView
        rvPassengers.setLayoutManager(new LinearLayoutManager(this));
        // Use a PassengerRequest list for the adapter, as that's what we convert to for display
        passengerAdapter = new PassengerAdapter(
                new ArrayList<>(),
                pos -> {}, // No remove in details for now
                pos -> {}  // No edit in details
        );
        rvPassengers.setAdapter(passengerAdapter);

        // 4. Load data
        loadTripDetails();
    }

    private void loadTripDetails() {
        int tripId = getIntent().getIntExtra("trip_id", -1);
        if (tripId == -1) {
            Toast.makeText(this, "Invalid trip ID. Cannot load details.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String token = prefs.getString("jwt_token", null);
        if (token == null) {
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Show loading indicator
        progressBar.setVisibility(View.VISIBLE);

        apiService.getTrip("Bearer " + token, tripId).enqueue(new Callback<TripResponse>() {
            @Override
            public void onResponse(Call<TripResponse> call, Response<TripResponse> response) {
                progressBar.setVisibility(View.GONE); // Hide loading indicator regardless of success
                if (response.isSuccessful() && response.body() != null) {
                    TripResponse trip = response.body();
                    displayTripDetails(trip);
                } else {
                    // Log the error body for better debugging in a real scenario
                    Log.e(TAG, "Failed to load trip: " + response.code() + " " + response.message());
                    Toast.makeText(TripDetailsActivity.this, "Failed to load trip details.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TripResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE); // Hide loading indicator
                Toast.makeText(TripDetailsActivity.this, "Network error. Check your connection.", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Error fetching trip details", t);
            }
        });
    }


    private void displayTripDetails(TripResponse trip) {
        // Set main trip info TextViews
        String routeDate = String.format(Locale.getDefault(),
                "%s to %s - %s",
                trip.getStart(),
                trip.getDestination(),
                trip.getDate());
        tvRouteDate.setText(routeDate);

        String totalCost = String.format(Locale.getDefault(), "Total Cost: K%.2f", trip.getTotalCost());
        tvTotalCost.setText(totalCost);

        // Convert PassengerShare -> PassengerRequest and update RecyclerView
        List<PassengerRequest> passengerRequests = new ArrayList<>();
        int passengerCount = 0;

        if (trip.getPassengers() != null) {
            for (PassengerShare ps : trip.getPassengers()) {
                // Ensure share amount is correctly represented as cost for display
                passengerRequests.add(new PassengerRequest(ps.getName(), ps.getShareAmount()));
            }
            passengerCount = trip.getPassengers().size();
        }

        String countText = String.format(Locale.getDefault(), "Passengers: %d", passengerCount);
        tvPassengerCount.setText(countText);

        passengerAdapter.setPassengers(passengerRequests);
    }
}