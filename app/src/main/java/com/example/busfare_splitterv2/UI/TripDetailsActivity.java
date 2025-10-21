package com.example.busfare_splitterv2.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull; // Import for better annotations
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.busfare_splitterv2.R;
import com.example.busfare_splitterv2.UI.Adapters.PassengerAdapter;
import com.example.busfare_splitterv2.network.ApiClient;
import com.example.busfare_splitterv2.network.ApiService;
import com.example.busfare_splitterv2.network.TripResponse; // Assuming this contains passenger data
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TripDetailsActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView tvRouteDate, tvTotalCost, tvPassengerCount;
    private RecyclerView rvResults;
    private MaterialButton btnExport;

    private ApiService apiService;
    private SharedPreferences prefs;
    private int tripId;
    private Trip trip; // Assuming 'Trip' class exists and holds trip/passenger data

    private PassengerAdapter passengerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        apiService = ApiClient.getApiService();
        prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);

        tripId = getIntent().getIntExtra("trip_id", -1);
        if (tripId == -1) {
            Toast.makeText(this, "Error: No trip ID provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupAdapterAndRecyclerView();
        setupListeners();
        fetchTripFromServer();
    }
    //-----------------------------------------------------------------------------
    private void initViews() {
        // Renamed from initViews to bindViews for clarity
        btnBack = findViewById(R.id.btnBack);
        tvRouteDate = findViewById(R.id.tvRouteDate);
        tvTotalCost = findViewById(R.id.tvTotalCost);
        tvPassengerCount = findViewById(R.id.tvPassengerCount);
        rvResults = findViewById(R.id.rvResults);
        btnExport = findViewById(R.id.btnExport);
    }
    //-----------------------------------------------------------------------------
    private void setupAdapterAndRecyclerView() {
        // Initialize adapter with an empty list for safety and correct type usage
        passengerAdapter = new PassengerAdapter(
                new ArrayList<>(), // Initialize with empty list
                this::removePassenger, // Method reference for remove
                this::editPassenger   // Method reference for edit
        );

        rvResults.setLayoutManager(new LinearLayoutManager(this));
        rvResults.setAdapter(passengerAdapter);
    }
    //-----------------------------------------------------------------------------
    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish()); // Simplified back navigation

        btnExport.setOnClickListener(v -> exportSharesToCSV());
    }
    //-----------------------------------------------------------------------------
    // Listener implementation for removing a passenger
    private void removePassenger(int pos) {
        if (trip != null && trip.getPassengers() != null) {
            try {
                PassengerShare passenger = trip.getPassengers().get(pos);
                int passengerId = passenger.getId(); // Ensure PassengerShare has getId()

                // --- Call server delete ---
                deletePassengerOnServer(trip.getId(), passengerId);

                // --- Update local list ---
                trip.getPassengers().remove(pos);
                passengerAdapter.setPassengers(trip.getPassengers());
                updatePassengerCountUI();

            } catch (IndexOutOfBoundsException e) {
                Log.e("TripDetailsActivity", "Error removing passenger: Index out of bounds at pos " + pos);
            } catch (Exception e) {
                Log.e("TripDetailsActivity", "Error removing passenger", e);
            }
        }
    }

    // Listener implementation for editing a passenger
    private void editPassenger(int pos) {
        // Implementation for opening a dialog or new activity to edit surcharge
        Toast.makeText(this, "Edit functionality for passenger at position " + pos, Toast.LENGTH_SHORT).show();
    }
    //-----------------------------------------------------------------------------
    private void exportSharesToCSV() {
        if (trip == null || trip.getPassengers().isEmpty()) {
            Toast.makeText(this, "No data to export.", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Name,Surcharge,ShareAmount\n");
        for (PassengerShare p : trip.getPassengers()) {
            sb.append(String.format(Locale.getDefault(), "%s,%.2f,%.2f\n",
                    p.getName(), p.getSurcharge(), p.getShareAmount()));
        }

        Intent send = new Intent(Intent.ACTION_SEND);
        send.setType("text/csv");
        send.putExtra(Intent.EXTRA_SUBJECT, "Trip split: " + trip.getStart() + " → " + trip.getDestination());
        send.putExtra(Intent.EXTRA_TEXT, sb.toString());
        startActivity(Intent.createChooser(send, "Share CSV"));
    }
    //-----------------------------------------------------------------------------
    private void fetchTripFromServer() {
        String token = prefs.getString("jwt_token", null);
        if (token == null) {
            Log.e("TripDetailsActivity", "No JWT token found");
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
            // Replaced finish() with starting login activity to guide user
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        Call<TripResponse> call = apiService.getTrip("Bearer " + token, tripId);
        call.enqueue(new Callback<TripResponse>() {
            @Override
            public void onResponse(@NonNull Call<TripResponse> call, @NonNull Response<TripResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TripResponse tripResponse = response.body();

                    // Map the TripResponse to the local Trip model
                    trip = new Trip(
                            tripResponse.getId(),
                            tripResponse.getStart(),
                            tripResponse.getDestination(),
                            tripResponse.getDate(),
                            tripResponse.getTotalCost()
                    );

                    // Use a new list to avoid side effects from Retrofit's response body list
                    List<PassengerShare> fetchedPassengers = new ArrayList<>(tripResponse.getPassengers());
                    trip.getPassengers().addAll(fetchedPassengers);

                    displayTripDetails();
                    passengerAdapter.setPassengers(trip.getPassengers());

                } else {
                    Log.e("TripDetailsActivity", "Failed to load trip: " + response.code());
                    Toast.makeText(TripDetailsActivity.this, "Failed to load trip details", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<TripResponse> call, @NonNull Throwable t) {
                Log.e("TripDetailsActivity", "Error fetching trip", t);
                Toast.makeText(TripDetailsActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
    //-----------------------------------------------------------------------------
    private void displayTripDetails() {
        tvRouteDate.setText(String.format("%s → %s | %s", trip.getStart(), trip.getDestination(), trip.getDate()));
        tvTotalCost.setText(String.format(Locale.getDefault(), "Total Cost — K%.2f", trip.getTotalCost()));
        updatePassengerCountUI();
    }

    private void updatePassengerCountUI() {
        tvPassengerCount.setText(String.format(Locale.getDefault(), "%d Passengers split the fare", trip.getPassengers().size()));
    }

    private void deletePassengerOnServer(int tripId, int passengerId) {
        String token = prefs.getString("jwt_token", null);
        if (token == null) {
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<Void> call = apiService.deletePassenger("Bearer " + token, tripId, passengerId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(TripDetailsActivity.this, "Passenger deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(TripDetailsActivity.this, "Failed to delete passenger", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("TripDetailsActivity", "Delete passenger failed", t);
                Toast.makeText(TripDetailsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}