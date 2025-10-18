package com.example.busfare_splitterv2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Locale;

public class SplitResultsActivity extends AppCompatActivity {

    TextView tvTotalCost;
    RecyclerView rvResults;
    Trip trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_split_results);

        tvTotalCost = findViewById(R.id.tvTotalCost);
        rvResults = findViewById(R.id.rvResults);
        Button btnExport = findViewById(R.id.btnExport);

        trip = (Trip) getIntent().getSerializableExtra("trip");
        if (trip == null){
            Toast.makeText(this, "No trip provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // safety: recompute in case
        computeSplit(trip);

        tvTotalCost.setText(String.format(Locale.getDefault(), "Total cost  —  K%.2f", trip.totalCost));
        ResultAdapter ra = new ResultAdapter(trip.passengers);
        rvResults.setLayoutManager(new LinearLayoutManager(this));
        rvResults.setAdapter(ra);

        btnExport.setOnClickListener(v -> {
            StringBuilder sb = new StringBuilder();
            sb.append("Name,Surcharge,ShareAmount\n");
            for (PassengerShare p : trip.passengers){
                sb.append(String.format(Locale.getDefault(), "%s,%.2f,%.2f\n", p.name, p.surcharge, p.shareAmount));
            }
            Intent send = new Intent(Intent.ACTION_SEND);
            send.setType("text/csv");
            send.putExtra(Intent.EXTRA_SUBJECT, "Trip split: " + trip.route);
            send.putExtra(Intent.EXTRA_TEXT, sb.toString());
            startActivity(Intent.createChooser(send, "Share CSV"));
        });
    }

    private void computeSplit(Trip t){
        double total = t.totalCost;
        double totalSurcharges = 0;
        for (PassengerShare p: t.passengers) totalSurcharges += p.surcharge;
        double base = total - totalSurcharges;
        if (base < 0) base = 0;
        double equal = base / Math.max(1, t.passengers.size());
        for (PassengerShare p: t.passengers) p.shareAmount = equal + p.surcharge;
    }
}
