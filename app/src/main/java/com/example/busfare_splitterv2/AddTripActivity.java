package com.example.busfare_splitterv2;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddTripActivity extends AppCompatActivity {

    EditText etRoute, etDate, etTotalCost;
    RecyclerView rvPassengers;
    PassengerAdapter passengerAdapter;
    List<PassengerShare> passengerList = new ArrayList<>();
    TextView tvAddPassenger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);

        etRoute = findViewById(R.id.etRoute);
        etDate = findViewById(R.id.etDate);
        etTotalCost = findViewById(R.id.etTotalCost);
        rvPassengers = findViewById(R.id.rvPassengers);
        tvAddPassenger = findViewById(R.id.tvAddPassenger);
        findViewById(R.id.btnCalculate).setOnClickListener(v -> onCalculate());

        // date picker
        etDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(AddTripActivity.this, (view, year, month, dayOfMonth) -> {
                String s = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth);
                etDate.setText(s);
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        passengerAdapter = new PassengerAdapter(passengerList, pos -> {
            passengerList.remove(pos);
            passengerAdapter.notifyDataSetChanged();
        });

        rvPassengers.setLayoutManager(new LinearLayoutManager(this));
        rvPassengers.setAdapter(passengerAdapter);

        tvAddPassenger.setOnClickListener(v -> showAddPassengerDialog());
    }

    private void showAddPassengerDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        android.view.View view = inflater.inflate(R.layout.dialog_add_passenger, null);
        EditText etName = view.findViewById(R.id.etPassengerName);
        EditText etSurcharge = view.findViewById(R.id.etPassengerSurcharge);

        new AlertDialog.Builder(this)
                .setTitle("Add passenger")
                .setView(view)
                .setPositiveButton("Add", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    double surcharge = 0.0;
                    try {
                        String s = etSurcharge.getText().toString().trim();
                        if (!s.isEmpty()) surcharge = Double.parseDouble(s);
                    } catch (Exception ex) {
                        surcharge = 0.0;
                    }
                    if (name.isEmpty()) {
                        Toast.makeText(this, "Name required", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    passengerList.add(new PassengerShare(name, surcharge));
                    passengerAdapter.notifyDataSetChanged();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void onCalculate() {
        String route = etRoute.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String totalS = etTotalCost.getText().toString().trim();
        if (route.isEmpty() || date.isEmpty() || totalS.isEmpty()) {
            Toast.makeText(this, "Fill route, date and total cost", Toast.LENGTH_SHORT).show();
            return;
        }
        double total;
        try {
            total = Double.parseDouble(totalS);
        } catch (Exception ex) {
            Toast.makeText(this, "Invalid total cost", Toast.LENGTH_SHORT).show();
            return;
        }

        if (passengerList.size() == 0) {
            Toast.makeText(this, "Add at least one passenger", Toast.LENGTH_SHORT).show();
            return;
        }

        // compute split
        double totalSurcharges = 0;
        for (PassengerShare p : passengerList) totalSurcharges += p.surcharge;
        double base = total - totalSurcharges;
        if (base < 0) {
            Toast.makeText(this, "Surcharges exceed total cost", Toast.LENGTH_LONG).show();
            return;
        }
        double equal = base / passengerList.size();
        for (PassengerShare p : passengerList) {
            p.shareAmount = equal + p.surcharge;
        }

        Trip trip = new Trip(route, date, total);
        trip.passengers.addAll(passengerList);


        // Load existing trips
        List<Trip> existing = Storage.loadTrips(this);
        existing.add(trip);
        Storage.saveTrips(this, existing);


        Intent i = new Intent(AddTripActivity.this, SplitResultsActivity.class);
        i.putExtra("trip", trip);
        startActivity(i);
        finish(); // optional: close add screen
    }
}
