package com.example.busfare_splitterv2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.VH> {
    public interface OnTripClick {
        void onTrip(Trip t);
    }

    private final List<Trip> trips;
    private final OnTripClick listener;

    public TripAdapter(List<Trip> trips, OnTripClick l) {
        this.trips = trips;
        this.listener = l;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trip, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Trip t = trips.get(position);
        holder.tvRoute.setText(t.route);
        holder.tvDate.setText(t.date);
        holder.tvTotal.setText(String.format("K%.2f", t.totalCost));
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onTrip(t);
        });
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvRoute, tvDate, tvTotal;

        VH(@NonNull View itemView) {
            super(itemView);
            tvRoute = itemView.findViewById(R.id.tvRoute);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTotal = itemView.findViewById(R.id.tvTotal);
        }
    }
}
