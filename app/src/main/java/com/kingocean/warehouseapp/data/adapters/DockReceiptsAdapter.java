package com.kingocean.warehouseapp.data.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kingocean.warehouseapp.R;
import com.kingocean.warehouseapp.data.model.DockReceipt;

import java.util.ArrayList;
import java.util.List;

public class DockReceiptsAdapter extends RecyclerView.Adapter<DockReceiptsAdapter.ViewHolder> {
    private List<DockReceipt> dockReceipts;

    public DockReceiptsAdapter(List<DockReceipt> dockReceipts) {

        if (dockReceipts != null) {
            this.dockReceipts = dockReceipts;
        } else {
            this.dockReceipts = new ArrayList<>(); // Initialize an empty list if dockReceipts is null
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_dock_receipt, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DockReceipt dockReceipt = dockReceipts.get(position);

        holder.textViewDrNumber.setText(dockReceipt.getDrNumber());
        holder.textViewPkgType.setText(dockReceipt.getPkgType());
        holder.textViewSeqNumber.setText(String.valueOf(dockReceipt.getDrSeq()));
        holder.textViewUnitHeight.setText(String.valueOf(dockReceipt.getUnitHeight()));
        holder.textViewUnitLength.setText(String.valueOf(dockReceipt.getUnitLength()));
        holder.textViewUnitWidth.setText(String.valueOf(dockReceipt.getUnitWidth()));
//        holder.textViewUnit.setText(String.valueOf(dockReceipt.getUnit()));
    }

    @Override
    public int getItemCount() {
        return dockReceipts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDrNumber;
//        TextView textViewUnit;

        TextView textViewPkgType;

        TextView textViewSeqNumber;
        TextView textViewUnitHeight;
        TextView textViewUnitLength;
        TextView textViewUnitWidth;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDrNumber = itemView.findViewById(R.id.textViewDrNumber);
            textViewPkgType = itemView.findViewById(R.id.tvPkgType);
            textViewSeqNumber = itemView.findViewById(R.id.tvSeqNumber);
            textViewUnitHeight = itemView.findViewById(R.id.tvHeight);
            textViewUnitLength = itemView.findViewById(R.id.tvWidth);
            textViewUnitWidth = itemView.findViewById(R.id.tvLength);
//            textViewUnit = itemView.findViewById(R.id.tvUnits);
        }
    }
}
