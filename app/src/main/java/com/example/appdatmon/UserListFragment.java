package com.example.appdatmon;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdatmon.data.api.RetrofitClient;
import com.example.appdatmon.data.model.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserListFragment extends Fragment implements UserAdapter.OnUserActionListener {

    private RecyclerView rvUsers;
    private UserAdapter adapter;
    private EditText edtSearch;
    private List<User> userList = new ArrayList<>();

    public UserListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(
                R.layout.fragment_user_list,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        rvUsers = view.findViewById(R.id.rvUsers);
        rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new UserAdapter(userList, this);
        rvUsers.setAdapter(adapter);

        edtSearch = view.findViewById(R.id.edtSearch);
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Thêm User
        View btnAddUser = view.findViewById(R.id.btn_add_user);
        if (btnAddUser != null) {
            btnAddUser.setOnClickListener(v -> {
                UserFormFragment formFragment = new UserFormFragment();
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_container, formFragment)
                        .addToBackStack(null)
                        .commit();
            });
        }

        loadUsers("");
    }

    private void loadUsers(String query) {
        RetrofitClient.getUserApi().getAllUsers(query).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    userList = response.body();
                    adapter.updateData(userList);
                } else {
                    String errorMsg = "Lỗi " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += ": " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.e("UserListFragment", "Error loading users: " + errorMsg);
                    Toast.makeText(requireContext(), "Lỗi tải danh sách người dùng (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                if (!isAdded()) return;
                Log.e("UserListFragment", "onFailure: " + t.getMessage());
                Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onEdit(User user) {
        Bundle bundle = new Bundle();
        bundle.putLong("user_id", user.getId());
        bundle.putString("user_name", user.getFullName());
        bundle.putString("user_email", user.getEmail());
        bundle.putString("user_phone", user.getPhone());
        bundle.putString("user_username", user.getUsername());
        bundle.putString("user_role", user.getRole());
        bundle.putString("user_status", user.getStatus());

        UserFormFragment formFragment = new UserFormFragment();
        formFragment.setArguments(bundle);

        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.content_container, formFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDelete(User user) {
        if (user.getId() == null) return;

        new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa người dùng '" + user.getFullName() + "'?")
                .setPositiveButton("Xóa", (dialog, which) -> performDelete(user.getId()))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void performDelete(long userId) {
        RetrofitClient.getUserApi().deleteUser(userId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!isAdded()) return;
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Xóa thành công", Toast.LENGTH_SHORT).show();
                    loadUsers(edtSearch != null ? edtSearch.getText().toString() : "");
                } else {
                    Toast.makeText(requireContext(), "Xóa thất bại (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (edtSearch != null) {
            loadUsers(edtSearch.getText().toString());
        }
    }
}
