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

import java.util.List;

public class PassengerAdapter extends RecyclerView.Adapter<PassengerAdapter.VH> {

    public interface OnRemove { void onRemove(int pos); }
    public interface OnEdit { void onEdit(int pos); }

    private final List<PassengerRequest> list;
    private final OnRemove removeListener;
    private final OnEdit editListener;

    public PassengerAdapter(List<PassengerRequest> list, OnRemove removeListener, OnEdit editListener) {
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
        PassengerRequest p = list.get(position);

        holder.tvName.setText(p.name);

        // Show surcharge if not zero, else just "-"
        holder.tvSurcharge.setText(p.surcharge > 0
                ? String.format("+K%.2f", p.surcharge)
                : "-");


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
        return list.size();
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
