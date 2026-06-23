package com.example.appdatmon;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class OrderDetailFragment extends Fragment {

    private TextView tvOrderId;
    private TextView tvCustomer;
    private TextView tvPhone;
    private TextView tvTotal;

    private Spinner spinnerStatus;

    private Button btnSave;
    private Button btnCancel;

    public OrderDetailFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(
                R.layout.fragment_order_detail,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        tvOrderId = view.findViewById(R.id.tv_order_id);
        tvCustomer = view.findViewById(R.id.tv_customer_name);
        tvPhone = view.findViewById(R.id.tv_customer_phone);
        tvTotal = view.findViewById(R.id.tv_total_money);

        spinnerStatus = view.findViewById(R.id.spinner_order_status);

        btnSave = view.findViewById(R.id.btn_save_order);
        btnCancel = view.findViewById(R.id.btn_cancel_order);

        String[] statusList = {
                "Chờ xác nhận",
                "Đang xử lý",
                "Đang giao",
                "Hoàn thành"
        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        statusList
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerStatus.setAdapter(adapter);

        Bundle bundle = getArguments();

        if (bundle != null) {

            tvOrderId.setText(
                    bundle.getString("order_id", "")
            );

            tvCustomer.setText(
                    bundle.getString("customer_name", "")
            );

            tvPhone.setText(
                    bundle.getString("phone", "")
            );

            tvTotal.setText(
                    bundle.getString("total", "")
            );
        }

        btnSave.setOnClickListener(v -> {

            Toast.makeText(
                    requireContext(),
                    "Cập nhật đơn hàng thành công",
                    Toast.LENGTH_SHORT
            ).show();

            getParentFragmentManager().popBackStack();
        });

        btnCancel.setOnClickListener(v ->
                getParentFragmentManager().popBackStack()
        );
    }
}