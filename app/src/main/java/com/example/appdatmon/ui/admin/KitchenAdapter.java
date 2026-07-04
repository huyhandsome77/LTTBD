package com.example.appdatmon.ui.admin;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appdatmon.R;
import com.example.appdatmon.data.api.RetrofitClient;
import com.example.appdatmon.data.model.Order;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KitchenAdapter extends RecyclerView.Adapter<KitchenAdapter.KitchenViewHolder> {

    private List<KitchenOrder> orderList;

    public KitchenAdapter(List<KitchenOrder> orderList) {
        this.orderList = orderList;
    }

    public void updateData(List<KitchenOrder> newList) {
        this.orderList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public KitchenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_kitchen_order, parent, false);
        return new KitchenViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KitchenViewHolder holder, int position) {
         KitchenOrder item = orderList.get(position);

        holder.txtTable.setText(item.getTableName());
        holder.txtFood.setText(item.getFoodName());
        holder.txtQuantity.setText("Số lượng: " + item.getQuantity());
        
        String statusLabel = "Chờ chế biến";
        if (item.getStatus().equals("PREPARING")) statusLabel = "Đang chế biến";
        else if (item.getStatus().equals("READY")) statusLabel = "Chờ phục vụ";
        holder.txtStatus.setText(statusLabel);

        updateUIByStatus(holder, item.getStatus());

        holder.btnUpdate.setOnClickListener(v -> {
            String nextStatus = "";
            if (item.getStatus().equals("CONFIRMED")) nextStatus = "PREPARING";
            else if (item.getStatus().equals("PREPARING")) nextStatus = "READY";

            if (!nextStatus.isEmpty()) {
                updateOrderStatusOnServer(item.getId(), nextStatus, position, holder.itemView);
            }
        });
    }

    private void updateOrderStatusOnServer(long orderId, String status, int position, View view) {
        Map<String, String> body = new HashMap<>();
        body.put("status", status);

        RetrofitClient.getOrderApi().updateOrderStatus(orderId, body).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(view.getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    // Local update
                    orderList.get(position).setStatus(status);
                    notifyItemChanged(position);
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                Toast.makeText(view.getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUIByStatus(KitchenViewHolder holder, String status) {
        switch (status) {
            case "CONFIRMED":
                holder.txtStatus.setTextColor(Color.parseColor("#FF9800"));
                holder.txtStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFF3E0")));
                holder.btnUpdate.setText("BẮT ĐẦU CHẾ BIẾN");
                holder.btnUpdate.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
                holder.btnUpdate.setVisibility(View.VISIBLE);
                break;
            case "PREPARING":
                holder.txtStatus.setTextColor(Color.parseColor("#2196F3"));
                holder.txtStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E3F2FD")));
                holder.btnUpdate.setText("CHẾ BIẾN XONG");
                holder.btnUpdate.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF5722")));
                holder.btnUpdate.setVisibility(View.VISIBLE);
                break;
            case "READY":
                holder.txtStatus.setTextColor(Color.parseColor("#4CAF50"));
                holder.txtStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E8F5E9")));
                holder.btnUpdate.setVisibility(View.GONE);
                break;
            default:
                holder.btnUpdate.setVisibility(View.GONE);
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
