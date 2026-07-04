package com.example.appdatmon.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.appdatmon.R;
import com.example.appdatmon.data.api.RetrofitClient;
import com.example.appdatmon.data.model.Order;
import com.example.appdatmon.data.model.OrderItem;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KitchenFragment extends Fragment {

    private RecyclerView rcvKitchen;
    private KitchenAdapter kitchenAdapter;
    private List<KitchenOrder> kitchenOrderList;
    private SwipeRefreshLayout swipeRefresh;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kitchen, container, false);

        rcvKitchen = view.findViewById(R.id.rcvKitchen);
        swipeRefresh = view.findViewById(R.id.swipeRefreshKitchen);

        if (rcvKitchen != null) {
            rcvKitchen.setLayoutManager(new LinearLayoutManager(getContext()));
        }

        kitchenOrderList = new ArrayList<>();
        kitchenAdapter = new KitchenAdapter(kitchenOrderList);
        if (rcvKitchen != null) {
            rcvKitchen.setAdapter(kitchenAdapter);
        }

        if (swipeRefresh != null) {
            swipeRefresh.setOnRefreshListener(this::fetchKitchenOrders);
        }

        fetchKitchenOrders();

        return view;
    }

    private void fetchKitchenOrders() {
        if (swipeRefresh != null) swipeRefresh.setRefreshing(true);

        // Lấy các đơn hàng đang ở trạng thái CONFIRMED (Chờ) hoặc PREPARING (Đang làm)
        RetrofitClient.getOrderApi().getAllOrders(null).enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    processOrders(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                Toast.makeText(getContext(), "Lỗi tải dữ liệu bếp", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processOrders(List<Order> orders) {
        kitchenOrderList.clear();
        for (Order order : orders) {
            // Chỉ hiển thị các đơn hàng cần bếp xử lý
            if (order.getStatus().equals("CONFIRMED") || order.getStatus().equals("PREPARING")) {
                if (order.getOrderItems() != null) {
                    for (OrderItem item : order.getOrderItems()) {
                        String tableName = "Mang về";
                        if (order.getRestaurantTable() != null) {
                            tableName = "Bàn " + order.getRestaurantTable().getTableNumber();
                        }
                        
                        String foodName = (item.getProduct() != null) ? item.getProduct().getName() : "Món ăn";
                        
                        kitchenOrderList.add(new KitchenOrder(
                            order.getId(),
                            tableName,
                            foodName,
                            item.getQuantity(),
                            order.getStatus(),
                            order
                        ));
                    }
                }
            }
        }
        kitchenAdapter.updateData(kitchenOrderList);
    }
}
