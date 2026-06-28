package com.example.appdatmon.ui.admin;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_kitchen, parent, false);
        return new KitchenViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KitchenViewHolder holder, int position) {
        KitchenOrder order = orderList.get(position);

        holder.tvTableName.setText("Bàn: " + order.getTableName());
        holder.tvFoodName.setText("Món ăn: " + order.getFoodName());
        holder.tvQuantity.setText("Số lượng: " + order.getQuantity());
        holder.tvStatus.setText("Trạng thái: " + order.getStatus());

        // --- 1. CHỨC NĂNG SỬA (Bấm nút cập nhật trạng thái tuần hoàn) ---
        holder.btnUpdate.setOnClickListener(v -> {
            switch (order.getStatus()) {
                case "Chờ chế biến":
                    order.setStatus("Đang chế biến");
                    break;
                case "Đang chế biến":
                    order.setStatus("Đã hoàn thành");
                    break;
                default:
                    order.setStatus("Chờ chế biến");
                    break;
            }
            notifyItemChanged(position);
            Toast.makeText(v.getContext(), "Đã chuyển trạng thái!", Toast.LENGTH_SHORT).show();
        });

        // --- 2. CHỨC NĂNG XÓA (Nhấn giữ lâu vào dòng món ăn để xóa khỏi bếp) ---
        holder.itemView.setOnLongClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Xóa đơn hàng")
                    .setMessage("Bạn có chắc chắn muốn xóa món này khỏi danh sách bếp?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        // Xóa phần tử khỏi danh sách dữ liệu
                        orderList.remove(position);
                        // Thông báo cập nhật vị trí và vẽ lại hiệu ứng cuộn mượt mà
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, orderList.size());
                        Toast.makeText(v.getContext(), "Đã xóa món ăn!", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }

    public static class KitchenViewHolder extends RecyclerView.ViewHolder {
        TextView tvTableName, tvFoodName, tvQuantity, tvStatus;
        Button btnUpdate;

        public KitchenViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTableName = itemView.findViewById(R.id.txtTable);
            tvFoodName = itemView.findViewById(R.id.txtFood);
            tvQuantity = itemView.findViewById(R.id.txtQuantity);
            tvStatus = itemView.findViewById(R.id.txtStatus);
            btnUpdate = itemView.findViewById(R.id.btnUpdate);
        }
    }
}