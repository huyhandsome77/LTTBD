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

import com.example.appdatmon.data.api.RetrofitClient;
import com.example.appdatmon.data.model.RegisterResponse;
import com.example.appdatmon.data.model.User;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserFormFragment extends Fragment {

    private EditText edtFullName, edtEmail, edtPhone, edtUsername, edtPassword;
    private Spinner spinnerRole, spinnerStatus;
    private Button btnSave, btnCancel;
    private TextView tvBackTitle, tvTitle;
    private Long userId = null;
    private Integer userPoints = 0;

    private final String[] roles = {"ADMIN", "STAFF", "KITCHEN", "CUSTOMER"};
    private final String[] statuses = {"ACTIVE", "BLOCKED"};

    public UserFormFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_form, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        edtFullName = view.findViewById(R.id.edt_full_name);
        edtEmail = view.findViewById(R.id.edt_email);
        edtPhone = view.findViewById(R.id.edt_phone);
        edtUsername = view.findViewById(R.id.edt_username);
        edtPassword = view.findViewById(R.id.edt_password);

        spinnerRole = view.findViewById(R.id.spinner_role);
        spinnerStatus = view.findViewById(R.id.spinner_status);

        btnSave = view.findViewById(R.id.btn_save);
        btnCancel = view.findViewById(R.id.btn_cancel);

        tvBackTitle = view.findViewById(R.id.tv_back_title);
        tvTitle = view.findViewById(R.id.tv_form_title);

        setupSpinners();

        Bundle bundle = getArguments();
        if (bundle != null) {
            userId = bundle.getLong("user_id", -1);
            if (userId == -1) userId = null;

            if (userId != null) {
                tvTitle.setText("Sửa người dùng");
                edtFullName.setText(bundle.getString("user_name", ""));
                edtEmail.setText(bundle.getString("user_email", ""));
                edtPhone.setText(bundle.getString("user_phone", ""));
                edtUsername.setText(bundle.getString("user_username", ""));

                // Set selection cho Spinner Role
                String role = bundle.getString("user_role", "");
                int rolePos = Arrays.asList(roles).indexOf(role.toUpperCase());
                if (rolePos >= 0) spinnerRole.setSelection(rolePos);

                // Set selection cho Spinner Status
                String status = bundle.getString("user_status", "");
                int statusPos = Arrays.asList(statuses).indexOf(status.toUpperCase());
                if (statusPos >= 0) spinnerStatus.setSelection(statusPos);

                userPoints = bundle.getInt("user_points", 0);
                
                // Khi sửa user: Ẩn mật khẩu, hiện username (có thể sửa)
                edtPassword.setVisibility(View.GONE);
                view.findViewById(R.id.tv_label_password).setVisibility(View.GONE);
                
                edtUsername.setVisibility(View.VISIBLE); // Đảm bảo hiện username
            }
        }

        btnSave.setOnClickListener(v -> saveUser());
        btnCancel.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        tvBackTitle.setOnClickListener(v -> getParentFragmentManager().popBackStack());
    }

    private void setupSpinners() {
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, roles);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(roleAdapter);

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, statuses);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);
    }

    private void saveUser() {
        String fullName = edtFullName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String username = edtUsername.getText().toString().trim();
        String role = spinnerRole.getSelectedItem().toString();
        String status = spinnerStatus.getSelectedItem().toString();

        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(username)) {
            Toast.makeText(requireContext(), "Vui lòng nhập đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userId == null) {
            // THÊM MỚI
            String password = edtPassword.getText().toString().trim();
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(requireContext(), "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }
            User newUser = new User(null, fullName, username, email, phone, 0, role, status, password);
            RetrofitClient.getUserApi().createUser(newUser).enqueue(new Callback<RegisterResponse>() {
                @Override
                public void onResponse(@NonNull Call<RegisterResponse> call, @NonNull Response<RegisterResponse> response) {
                    if (!isAdded()) return;
                    if (response.isSuccessful()) {
                        Toast.makeText(requireContext(), "Thêm thành công", Toast.LENGTH_SHORT).show();
                        getParentFragmentManager().popBackStack();
                    } else {
                        Toast.makeText(requireContext(), "Thêm thất bại", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<RegisterResponse> call, @NonNull Throwable t) {
                    if (!isAdded()) return;
                    Toast.makeText(requireContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // CẬP NHẬT
            User userUpdate = new User(userId, fullName, username, email, phone, userPoints, role, status, null);
            RetrofitClient.getUserApi().updateUser(userId, userUpdate).enqueue(new Callback<RegisterResponse>() {
                @Override
                public void onResponse(@NonNull Call<RegisterResponse> call, @NonNull Response<RegisterResponse> response) {
                    if (!isAdded()) return;
                    if (response.isSuccessful()) {
                        Toast.makeText(requireContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show()
;
                        getParentFragmentManager().popBackStack();
                    } else {
                        Toast.makeText(requireContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<RegisterResponse> call, @NonNull Throwable t) {
                    if (!isAdded()) return;
                    Toast.makeText(requireContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
