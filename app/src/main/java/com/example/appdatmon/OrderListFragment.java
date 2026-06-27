package com.example.appdatmon;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class OrderListFragment extends Fragment {

    private Button btnTabPending;
    private Button btnTabProcessing;

    private TextView tvOrderStatusContent;

    public OrderListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(
                R.layout.fragment_order_list,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        btnTabPending =
                view.findViewById(R.id.btn_tab_pending);

        btnTabProcessing =
                view.findViewById(R.id.btn_tab_processing);

        tvOrderStatusContent =
                view.findViewById(R.id.tv_order_status_content);

        // Tab mặc định
        selectPending();

        // Chờ xác nhận
        btnTabPending.setOnClickListener(v -> {
            selectPending();
        });

        // Đang xử lý
        btnTabProcessing.setOnClickListener(v -> {
            selectProcessing();
        });

        // Click vào đơn hàng để xem chi tiết
        View orderItem =
                view.findViewById(R.id.order_item);

        if (orderItem != null) {

            orderItem.setOnClickListener(v -> {

                OrderDetailFragment detailFragment =
                        new OrderDetailFragment();

                Bundle bundle = new Bundle();

                bundle.putString(
                        "order_id",
                        "#DH001"
                );

                bundle.putString(
                        "customer_name",
                        "Nguyễn Văn A"
                );

                bundle.putString(
                        "phone",
                        "037990279"
                );

                bundle.putString(
                        "total",
                        "250.000 VNĐ"
                );

                detailFragment.setArguments(bundle);

                getParentFragmentManager()
                        .beginTransaction()
                        .replace(
                                R.id.content_container,
                                detailFragment
                        )
                        .addToBackStack(null)
                        .commit();
            });
        }
    }

    private void selectPending() {

        btnTabPending.setBackgroundTintList(
                ColorStateList.valueOf(
                        Color.parseColor("#0055FF")
                )
        );

        btnTabPending.setTextColor(Color.WHITE);

        btnTabProcessing.setBackgroundTintList(
                ColorStateList.valueOf(
                        Color.parseColor("#E0E0E0")
                )
        );

        btnTabProcessing.setTextColor(Color.BLACK);

        tvOrderStatusContent.setText(
                "Danh sách đơn hàng CHỜ XÁC NHẬN"
        );
    }

    private void selectProcessing() {

        btnTabProcessing.setBackgroundTintList(
                ColorStateList.valueOf(
                        Color.parseColor("#0055FF")
                )
        );

        btnTabProcessing.setTextColor(Color.WHITE);

        btnTabPending.setBackgroundTintList(
                ColorStateList.valueOf(
                        Color.parseColor("#E0E0E0")
                )
        );

        btnTabPending.setTextColor(Color.BLACK);

        tvOrderStatusContent.setText(
                "Danh sách đơn hàng ĐANG XỬ LÝ"
        );
    }
}