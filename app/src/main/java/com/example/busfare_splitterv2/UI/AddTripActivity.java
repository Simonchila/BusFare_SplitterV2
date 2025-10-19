package com.example.busfare_splitterv2.UI;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
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
import com.example.busfare_splitterv2.network.TripRequest;
import com.example.busfare_splitterv2.network.TripResponse;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddTripActivity extends AppCompatActivity {

    private final String[] cities = {
            "Lusaka", "Ndola", "Kitwe", "Chingola", "Livingstone",
            "Kabwe", "Chipata", "Mufulira", "Mpika", "Solwezi", "Kasama"
    };

    private AutoCompleteTextView actvStart, actvDestination;
    private RecyclerView rvPassengers;
    private PassengerAdapter passengerAdapter;
    private List<PassengerRequest> passengerList = new ArrayList<>();
    private TextView tvAddPassenger;
    private EditText etDate, etTotalCost;

    private ApiService apiService;
    private String authToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);

        actvStart = findViewById(R.id.actvStart);
        actvDestination = findViewById(R.id.actvDestination);
        etDate = findViewById(R.id.etDate);
        etTotalCost = findViewById(R.id.etTotalCost);
        rvPassengers = findViewById(R.id.rvPassengers);
        tvAddPassenger = findViewById(R.id.tvAddPassenger);

        findViewById(R.id.btnCalculate).setOnClickListener(v -> onCalculate());

        // Setup city adapters
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, cities);
        actvStart.setAdapter(cityAdapter);
        actvDestination.setAdapter(cityAdapter);
        actvStart.setOnClickListener(v -> actvStart.showDropDown());
        actvDestination.setOnClickListener(v -> actvDestination.showDropDown());

        // Date picker
        etDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(AddTripActivity.this, (view, year, month, dayOfMonth) -> {
                etDate.setText(String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth));
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Passenger adapter
        passengerAdapter = new PassengerAdapter(passengerList,
                pos -> { passengerList.remove(pos); passengerAdapter.notifyDataSetChanged(); },
                pos -> showEditPassengerDialog(pos, passengerList.get(pos))
        );

        rvPassengers.setLayoutManager(new LinearLayoutManager(this));
        rvPassengers.setAdapter(passengerAdapter);

        tvAddPassenger.setOnClickListener(v -> showAddPassengerDialog());

        // Initialize API service and auth token
        apiService = ApiClient.getApiService();
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        authToken = "Bearer " + prefs.getString("jwt_token", null);
    }

    private void showAddPassengerDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        android.view.View view = inflater.inflate(R.layout.dialog_add_passenger, null);
        EditText etName = view.findViewById(R.id.etPassengerName);
        EditText etSurcharge = view.findViewById(R.id.etPassengerSurcharge);

        new AlertDialog.Builder(this)
                .setTitle("Add Passenger")
                .setView(view)
                .setPositiveButton("Add", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    double surcharge = 0;
                    try { surcharge = Double.parseDouble(etSurcharge.getText().toString().trim()); } catch (Exception ignored) {}
                    if (name.isEmpty()) { Toast.makeText(this, "Name required", Toast.LENGTH_SHORT).show(); return; }
                    passengerList.add(new PassengerRequest(name, surcharge));
                    passengerAdapter.notifyDataSetChanged();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showEditPassengerDialog(int position, PassengerRequest passenger) {
        LayoutInflater inflater = LayoutInflater.from(this);
        android.view.View view = inflater.inflate(R.layout.dialog_add_passenger, null);
        EditText etName = view.findViewById(R.id.etPassengerName);
        EditText etSurcharge = view.findViewById(R.id.etPassengerSurcharge);

        etName.setText(passenger.name);
        etSurcharge.setText(String.valueOf(passenger.surcharge));

        new AlertDialog.Builder(this)
                .setTitle("Edit Passenger")
                .setView(view)
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    double surcharge = 0;
                    try { surcharge = Double.parseDouble(etSurcharge.getText().toString().trim()); } catch (Exception ignored) {}
                    if (name.isEmpty()) { Toast.makeText(this, "Name required", Toast.LENGTH_SHORT).show(); return; }
                    passengerList.set(position, new PassengerRequest(name, surcharge));
                    passengerAdapter.notifyDataSetChanged();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void onCalculate() {
        String start = actvStart.getText().toString().trim();
        String dest = actvDestination.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String totalS = etTotalCost.getText().toString().trim();

        if (start.isEmpty() || dest.isEmpty() || date.isEmpty() || totalS.isEmpty()) {
            Toast.makeText(this, "Fill start, destination, date and total cost", Toast.LENGTH_SHORT).show();
            return;
        }

        double total;
        try { total = Double.parseDouble(totalS); } catch (Exception ex) { Toast.makeText(this, "Invalid total cost", Toast.LENGTH_SHORT).show(); return; }

        if (passengerList.isEmpty()) {
            Toast.makeText(this, "Add at least one passenger", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare TripRequest for the backend
        TripRequest request = new TripRequest(
                start,
                dest,
                date,
                total,
                passengerList
        );

        // Send to server
        apiService.addTrip(authToken, request).enqueue(new Callback<TripResponse>() {
            @Override
            public void onResponse(Call<TripResponse> call, Response<TripResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TripResponse tripResponse = response.body();

                    // Use server response directly
                    Trip trip = new Trip(tripResponse.getRoute(), tripResponse.getDate(), tripResponse.getTotalCost());
                    trip.setId(tripResponse.getId()); // set the server ID
                    trip.getPassengers().addAll(tripResponse.getPassengers());

                    Intent i = new Intent(AddTripActivity.this, SplitResultsActivity.class);
                    i.putExtra("trip_id", trip.getId());
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(AddTripActivity.this, "Failed to create trip", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TripResponse> call, Throwable t) {
                Toast.makeText(AddTripActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }
}
