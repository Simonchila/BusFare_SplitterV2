package com.example.busfare_splitterv2.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.busfare_splitterv2.R;
import com.example.busfare_splitterv2.UI.Adapters.ResultAdapter;
import com.example.busfare_splitterv2.network.ApiClient;
import com.example.busfare_splitterv2.network.ApiService;
import com.example.busfare_splitterv2.network.TripResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.Locale;

public class SplitResultsActivity extends AppCompatActivity {

    private TextView tvTotalCost;
    private RecyclerView rvResults;
    private Button btnExport;

    private ApiService apiService;
    private SharedPreferences prefs;
    private int tripId; // ID of the trip to fetch

    private Trip trip; // Local copy for UI

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_split_results);

        tvTotalCost = findViewById(R.id.tvTotalCost);
        rvResults = findViewById(R.id.rvResults);
        btnExport = findViewById(R.id.btnExport);

        apiService = ApiClient.getApiService();
        prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);

        tripId = getIntent().getIntExtra("trip_id", -1);
        if (tripId == -1) {
            Toast.makeText(this, "No trip ID provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        fetchTripFromServer();

        btnExport.setOnClickListener(v -> {
            if (trip == null || trip.getPassengers().isEmpty()) return;

            StringBuilder sb = new StringBuilder();
            sb.append("Name,Surcharge,ShareAmount\n");
            for (PassengerShare p : trip.getPassengers()) {
                sb.append(String.format(Locale.getDefault(), "%s,%.2f,%.2f\n",
                        p.name, p.surcharge, p.shareAmount));
            }
            Intent send = new Intent(Intent.ACTION_SEND);
            send.setType("text/csv");
            send.putExtra(Intent.EXTRA_SUBJECT, "Trip split: " + trip.getRoute());
            send.putExtra(Intent.EXTRA_TEXT, sb.toString());
            startActivity(Intent.createChooser(send, "Share CSV"));
        });
    }

    private void fetchTripFromServer() {
        String token = prefs.getString("jwt_token", null);
        if (token == null) {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        Call<TripResponse> call = apiService.getTrip("Bearer " + token, tripId);
        call.enqueue(new Callback<TripResponse>() {
            @Override
            public void onResponse(Call<TripResponse> call, Response<TripResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TripResponse tripResponse = response.body();
                    trip = new Trip(tripResponse.getRoute(), tripResponse.getDate(), tripResponse.getTotalCost());
                    trip.getPassengers().addAll(tripResponse.getPassengers());

                    tvTotalCost.setText(String.format(Locale.getDefault(), "Total cost  —  K%.2f", trip.getTotalCost()));
                    ResultAdapter ra = new ResultAdapter(trip.getPassengers());
                    rvResults.setLayoutManager(new LinearLayoutManager(SplitResultsActivity.this));
                    rvResults.setAdapter(ra);
                } else {
                    Toast.makeText(SplitResultsActivity.this, "Failed to load trip details", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<TripResponse> call, Throwable t) {
                Toast.makeText(SplitResultsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
}
