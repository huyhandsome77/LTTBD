package com.example.appdatmon;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class UserFormFragment extends Fragment {

    private EditText edtFullName;
    private EditText edtEmail;
    private EditText edtPhone;

    private Spinner spinnerRole;
    private Spinner spinnerStatus;

    private Button btnSave;
    private Button btnCancel;

    private TextView tvBackTitle;

    public UserFormFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(
                R.layout.fragment_user_form,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        edtFullName = view.findViewById(R.id.edt_full_name);
        edtEmail = view.findViewById(R.id.edt_email);
        edtPhone = view.findViewById(R.id.edt_phone);

        spinnerRole = view.findViewById(R.id.spinner_role);
        spinnerStatus = view.findViewById(R.id.spinner_status);

        btnSave = view.findViewById(R.id.btn_save);
        btnCancel = view.findViewById(R.id.btn_cancel);

        tvBackTitle = view.findViewById(R.id.tv_back_title);

        // Spinner Role
        String[] roles = {
                "Admin",
                "Nhân viên",
                "Thu ngân"
        };

        ArrayAdapter<String> roleAdapter =
                new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        roles
                );

        roleAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerRole.setAdapter(roleAdapter);

        // Spinner Status
        String[] status = {
                "Hoạt động",
                "Đã khóa"
        };

        ArrayAdapter<String> statusAdapter =
                new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        status
                );

        statusAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerStatus.setAdapter(statusAdapter);

        // Nếu là sửa user
        Bundle bundle = getArguments();

        if (bundle != null) {

            edtFullName.setText(
                    bundle.getString("user_name", "")
            );

            edtEmail.setText(
                    bundle.getString("user_email", "")
            );

            edtPhone.setText(
                    bundle.getString("user_phone", "")
            );
        }

        btnSave.setOnClickListener(v -> saveUser());

        btnCancel.setOnClickListener(v ->
                getParentFragmentManager().popBackStack()
        );

        tvBackTitle.setOnClickListener(v ->
                getParentFragmentManager().popBackStack()
        );
    }

    private void saveUser() {

        String fullName =
                edtFullName.getText().toString().trim();

        String email =
                edtEmail.getText().toString().trim();

        String phone =
                edtPhone.getText().toString().trim();

        if (TextUtils.isEmpty(fullName)) {
            edtFullName.setError("Vui lòng nhập họ tên");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Vui lòng nhập email");
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            edtPhone.setError("Vui lòng nhập số điện thoại");
            return;
        }

        // Lưu dữ liệu vào UserData
        UserData.fullName = fullName;
        UserData.email = email;
        UserData.phone = phone;
        UserData.role = spinnerRole.getSelectedItem().toString();
        UserData.status = spinnerStatus.getSelectedItem().toString();

        Toast.makeText(
                requireContext(),
                "Lưu người dùng thành công",
                Toast.LENGTH_SHORT
        ).show();

        getParentFragmentManager().popBackStack();
    }
}