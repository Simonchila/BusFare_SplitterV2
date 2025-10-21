package com.example.busfare_splitterv2.UI.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.busfare_splitterv2.R;
import com.example.busfare_splitterv2.UI.PassengerShare;
import com.example.busfare_splitterv2.network.PassengerRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PassengerAdapter extends RecyclerView.Adapter<PassengerAdapter.VH> {

    private List<PassengerShare> passengers;

    public void setPassengers(List<PassengerShare> passengers) {
        this.passengers.clear();
        if (passengers != null) {
            this.passengers.addAll(passengers);
        }
        notifyDataSetChanged();
    }

    public interface OnRemove { void onRemove(int pos); }
    public interface OnEdit { void onEdit(int pos); }

    private final OnRemove removeListener;
    private final OnEdit editListener;

    public PassengerAdapter(List<PassengerRequest> passengers, OnRemove removeListener, OnEdit editListener) {
        this.passengers = new ArrayList<PassengerShare>();
        this.removeListener = removeListener;
        this.editListener = editListener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_passenger, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        PassengerShare p = passengers.get(position);

        holder.tvName.setText(p.getName());


        // Show calculated share amount
        holder.tvShare.setText(String.format(Locale.getDefault(), "K%.2f", p.getShareAmount()));

        holder.btnRemove.setOnClickListener(v -> {
            if (removeListener != null)
                removeListener.onRemove(holder.getAdapterPosition());
        });

        holder.itemView.setOnClickListener(v -> {
            if (editListener != null)
                editListener.onEdit(holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return passengers.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvShare;
        ImageButton btnRemove;

        VH(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvShare = itemView.findViewById(R.id.tvShare);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
}
