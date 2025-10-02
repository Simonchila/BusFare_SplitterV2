package com.example.busfare_splitterv2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PassengerAdapter extends RecyclerView.Adapter<PassengerAdapter.VH> {

    public interface OnRemove { void onRemove(int pos); }

    private final OnEdit editListener;

    private final List<PassengerShare> list;
    private final OnRemove removeListener;
    public PassengerAdapter(List<PassengerShare> list, OnRemove removeListener, OnEdit editListener) {
        this.list = list;
        this.removeListener = removeListener;
        this.editListener = editListener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_passenger, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        PassengerShare p = list.get(position);
        holder.tvName.setText(p.name);
        holder.tvSurcharge.setText(String.format("+$%.2f", p.surcharge));

        // Remove button
        holder.btnRemove.setOnClickListener(v -> {
            if (removeListener != null)
                removeListener.onRemove(holder.getAdapterPosition());
        });

        // Tap to edit
        holder.itemView.setOnClickListener(v -> {
            if (editListener != null)
                editListener.onEdit(holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnEdit {
        void onEdit(int pos);
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvSurcharge;
        ImageButton btnRemove;
        VH(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvSurcharge = itemView.findViewById(R.id.tvSurcharge);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
}
