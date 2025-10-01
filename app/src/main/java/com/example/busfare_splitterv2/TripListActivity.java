package com.example.busfare_splitterv2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class TripListActivity extends AppCompatActivity {

    RecyclerView rvTrips;
    TripAdapter adapter;
    List<Trip> trips;
    TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_list);

        rvTrips = findViewById(R.id.rvTrips);
        tvEmpty = findViewById(R.id.tvEmpty);
        FloatingActionButton fab = findViewById(R.id.fabAdd);

        // Load trips from storage
        trips = Storage.loadTrips(this);

        adapter = new TripAdapter(trips, trip -> {
            Intent i = new Intent(TripListActivity.this, SplitResultsActivity.class);
            i.putExtra("trip", trip);
            startActivity(i);
        });

        rvTrips.setLayoutManager(new LinearLayoutManager(this));
        rvTrips.setAdapter(adapter);
        updateEmpty();

        fab.setOnClickListener(v -> {
            Intent i = new Intent(TripListActivity.this, AddTripActivity.class);
            startActivity(i);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload trips when coming back from AddTrip
        trips.clear();
        trips.addAll(Storage.loadTrips(this));
        adapter.notifyDataSetChanged();
        updateEmpty();
    }

    private void updateEmpty() {
        tvEmpty.setVisibility(trips.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
