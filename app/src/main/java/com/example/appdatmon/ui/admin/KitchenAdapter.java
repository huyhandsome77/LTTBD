package com.example.appdatmon.ui.admin;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appdatmon.R;
import java.util.List;

public class KitchenAdapter extends RecyclerView.Adapter<KitchenAdapter.KitchenViewHolder> {

    private List<KitchenOrder> orderList;

    public KitchenAdapter(List<KitchenOrder> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public KitchenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_kitchen_order, parent, false);
        return new KitchenViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KitchenViewHolder holder, int position) {
         KitchenOrder order = orderList.get(position);

        holder.txtTable.setText(order.getTableName());
        holder.txtFood.setText(order.getFoodName());
        holder.txtQuantity.setText("Số lượng: " + order.getQuantity());
        holder.txtStatus.setText(order.getStatus());

        // Cập nhật giao diện dựa trên trạng thái
        updateUIByStatus(holder, order.getStatus());

        holder.btnUpdate.setOnClickListener(v -> {
            String currentStatus = order.getStatus();
            if (currentStatus.equals("Chờ chế biến")) {
                order.setStatus("Đang chế biến");
            } else if (currentStatus.equals("Đang chế biến")) {
                order.setStatus("Chờ phục vụ");
            }
            
            notifyItemChanged(position);
        });
    }

    private void updateUIByStatus(KitchenViewHolder holder, String status) {
        switch (status) {
            case "Chờ chế biến":
                holder.txtStatus.setTextColor(Color.parseColor("#FF9800")); // Orange
                holder.txtStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFF3E0")));
                holder.btnUpdate.setText("BẮT ĐẦU CHẾ BIẾN");
                holder.btnUpdate.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
                holder.btnUpdate.setVisibility(View.VISIBLE);
                break;
            case "Đang chế biến":
                holder.txtStatus.setTextColor(Color.parseColor("#2196F3")); // Blue
                holder.txtStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E3F2FD")));
                holder.btnUpdate.setText("HOÀN THÀNH (CHỜ PHỤC VỤ)");
                holder.btnUpdate.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF5722")));
                holder.btnUpdate.setVisibility(View.VISIBLE);
                break;
            case "Chờ phục vụ":
                holder.txtStatus.setTextColor(Color.parseColor("#4CAF50")); // Green
                holder.txtStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E8F5E9")));
                holder.btnUpdate.setVisibility(View.GONE); // Đã xong việc tại bếp
                break;
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class KitchenViewHolder extends RecyclerView.ViewHolder {
        TextView txtTable, txtFood, txtQuantity, txtStatus;
        Button btnUpdate;

        public KitchenViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTable = itemView.findViewById(R.id.txtTable);
            txtFood = itemView.findViewById(R.id.txtFood);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            btnUpdate = itemView.findViewById(R.id.btnUpdate);
        }
    }
}
