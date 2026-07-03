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
        View view = inflater.inflate(R.layout.fragment_kitchen, container, false);

        edtTableName = view.findViewById(R.id.edtTableName);
        edtFoodName = view.findViewById(R.id.edtFoodName);
        edtQuantity = view.findViewById(R.id.edtQuantity);
        btnAddNewOrder = view.findViewById(R.id.btnAddNewOrder);
        rcvKitchen = view.findViewById(R.id.rcvKitchen);

        if (rcvKitchen != null) {
            rcvKitchen.setLayoutManager(new LinearLayoutManager(getContext()));
        }

        orderList = new ArrayList<>();

        // Dữ liệu mẫu ban đầu
        orderList.add(new KitchenOrder(autoIncrementId++, "Bàn 1", "Sashimi Cá Hồi", 2, "Chờ chế biến"));
        orderList.add(new KitchenOrder(autoIncrementId++, "Bàn 3", "Mì Ramen Nhật", 1, "Đang chế biến"));
        orderList.add(new KitchenOrder(autoIncrementId++, "Bàn 5", "Tempura Tôm", 3, "Chờ phục vụ"));

        kitchenAdapter = new KitchenAdapter(orderList);
        if (rcvKitchen != null) {
            rcvKitchen.setAdapter(kitchenAdapter);
        }

        if (btnAddNewOrder != null) {
            btnAddNewOrder.setOnClickListener(v -> {
                String table = edtTableName.getText().toString().trim();
                String food = edtFoodName.getText().toString().trim();
                String qtyStr = edtQuantity.getText().toString().trim();

                if (table.isEmpty() || food.isEmpty() || qtyStr.isEmpty()) {
                    Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    int quantity = Integer.parseInt(qtyStr);
                    orderList.add(0, new KitchenOrder(autoIncrementId++, table, food, quantity, "Chờ chế biến"));

                    if (kitchenAdapter != null) {
                        kitchenAdapter.notifyItemInserted(0);
                        rcvKitchen.scrollToPosition(0);
                    }

                    edtTableName.setText("");
                    edtFoodName.setText("");
                    edtQuantity.setText("");

                    Toast.makeText(getContext(), "Đã gửi yêu cầu vào bếp!", Toast.LENGTH_SHORT).show()
;
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Số lượng không hợp lệ!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        return view;
    }
}
