package com.example.appdatmon.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdatmon.R;
// Import chính xác các class hỗ trợ cùng gói (hoặc gói gốc) của bạn
import com.example.appdatmon.ui.admin.KitchenAdapter;
import com.example.appdatmon.ui.admin.KitchenOrder;

import java.util.ArrayList;
import java.util.List;

public class KitchenFragment extends Fragment {

    private RecyclerView rcvKitchen;
    private KitchenAdapter kitchenAdapter;
    private List<KitchenOrder> orderList;
    private EditText edtTableName, edtFoodName, edtQuantity;
    private Button btnAddNewOrder;
    private int autoIncrementId = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Nạp đúng file giao diện layout fragment_kitchen.xml
        View view = inflater.inflate(R.layout.fragment_kitchen, container, false);

        // Ánh xạ các View nhập liệu từ file XML
        edtTableName = view.findViewById(R.id.edtTableName);
        edtFoodName = view.findViewById(R.id.edtFoodName);
        edtQuantity = view.findViewById(R.id.edtQuantity);
        btnAddNewOrder = view.findViewById(R.id.btnAddNewOrder);
        rcvKitchen = view.findViewById(R.id.rcvKitchen);

        // Cài đặt cấu trúc hiển thị danh sách dạng cuộn dọc (Vertical)
        if (rcvKitchen != null) {
            rcvKitchen.setLayoutManager(new LinearLayoutManager(getContext()));
        }

        // Khởi tạo danh sách chứa các đơn hàng phòng bếp
        orderList = new ArrayList<>();

        // THÊM SẴN DỮ LIỆU MẪU ĐỂ CHẮC CHẮN LUÔN HIỂN THỊ KỂ CẢ KHI MẤT MẠNG/MẤT SERVER
        orderList.add(new KitchenOrder(autoIncrementId++, "Bàn 1", "Pizza Hải Sản", 2, "Chờ chế biến"));
        orderList.add(new KitchenOrder(autoIncrementId++, "Bàn 2", "Mì Ý", 1, "Đang chế biến"));

        // Gắn adapter dữ liệu vào RecyclerView
        kitchenAdapter = new KitchenAdapter(orderList);
        if (rcvKitchen != null) {
            rcvKitchen.setAdapter(kitchenAdapter);
        }

        // Xử lý sự kiện khi bấm nút "Thêm món vào bếp" thủ công trên giao diện
        if (btnAddNewOrder != null) {
            btnAddNewOrder.setOnClickListener(v -> {
                String table = edtTableName.getText().toString().trim();
                String food = edtFoodName.getText().toString().trim();
                String qtyStr = edtQuantity.getText().toString().trim();

                // Kiểm tra điều kiện nhập trống thông tin
                if (table.isEmpty() || food.isEmpty() || qtyStr.isEmpty()) {
                    Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    int quantity = Integer.parseInt(qtyStr);

                    // Thêm một item mới vào danh sách hiện tại
                    orderList.add(new KitchenOrder(autoIncrementId++, table, food, quantity, "Chờ chế biến"));

                    // Thông báo cho Adapter biết danh sách vừa có sự thay đổi để vẽ lại giao diện
                    if (kitchenAdapter != null) {
                        kitchenAdapter.notifyItemInserted(orderList.size() - 1);
                        // Cuộn màn hình xuống vị trí món ăn vừa thêm mới nhất
                        rcvKitchen.scrollToPosition(orderList.size() - 1);
                    }

                    // Xóa trống các ô nhập để người dùng nhập món tiếp theo
                    edtTableName.setText("");
                    edtFoodName.setText("");
                    edtQuantity.setText("");

                    Toast.makeText(getContext(), "Đã gửi món vào bếp thành công!", Toast.LENGTH_SHORT).show();

                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Số lượng phải là số hợp lệ!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        return view;
    }
}