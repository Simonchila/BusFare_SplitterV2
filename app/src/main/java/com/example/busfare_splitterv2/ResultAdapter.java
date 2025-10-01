package com.example.busfare_splitterv2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.VH> {
    private final List<PassengerShare> list;
    public ResultAdapter(List<PassengerShare> list){ this.list = list; }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_result, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        PassengerShare p = list.get(position);
        holder.tvName.setText(p.name);
        holder.tvAmount.setText(String.format("$%.2f", p.shareAmount));
        holder.tvSurcharge.setText(String.format("$%.2f surcharge", p.surcharge));
    }

    @Override public int getItemCount() { return list.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvAmount, tvSurcharge;
        VH(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvResName);
            tvAmount = itemView.findViewById(R.id.tvResAmount);
            tvSurcharge = itemView.findViewById(R.id.tvResSurcharge);
        }
    }
}
