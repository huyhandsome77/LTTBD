package com.example.appdatmon;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class UserListFragment extends Fragment {

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

        // Thêm User
        View btnAddUser = view.findViewById(R.id.btn_add_user);

        if (btnAddUser != null) {
            btnAddUser.setOnClickListener(v -> {

                UserFormFragment formFragment =
                        new UserFormFragment();

                getParentFragmentManager()
                        .beginTransaction()
                        .replace(
                                R.id.content_container,
                                formFragment
                        )
                        .addToBackStack(null)
                        .commit();
            });
        }

        // Sửa User A
        View btnEditA = view.findViewById(R.id.btnEditA);

        if (btnEditA != null) {

            btnEditA.setOnClickListener(v -> {

                Bundle bundle = new Bundle();

                bundle.putString(
                        "user_name",
                        "Nguyễn Văn A"
                );

                bundle.putString(
                        "user_email",
                        "admin@gmail.com"
                );

                bundle.putString(
                        "user_phone",
                        "037990279"
                );

                UserFormFragment formFragment =
                        new UserFormFragment();

                formFragment.setArguments(bundle);

                getParentFragmentManager()
                        .beginTransaction()
                        .replace(
                                R.id.content_container,
                                formFragment
                        )
                        .addToBackStack(null)
                        .commit();
            });
        }

        // Xóa User A
        View btnDeleteA = view.findViewById(R.id.btnDeleteA);

        if (btnDeleteA != null) {

            btnDeleteA.setOnClickListener(v -> {

                View userCard =
                        view.findViewById(R.id.userA);

                if (userCard != null) {
                    userCard.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        View root = getView();

        if (root == null) return;

        LinearLayout userNew =
                root.findViewById(R.id.userNew);

        TextView tvName =
                root.findViewById(R.id.tvNewName);

        TextView tvEmail =
                root.findViewById(R.id.tvNewEmail);

        TextView tvRole =
                root.findViewById(R.id.tvNewRole);

        if (userNew == null ||
                tvName == null ||
                tvEmail == null ||
                tvRole == null) {
            return;
        }

        if (!UserData.fullName.isEmpty()) {

            userNew.setVisibility(View.VISIBLE);

            tvName.setText(UserData.fullName);
            tvEmail.setText(UserData.email);
            tvRole.setText(UserData.role);
        }
    }
}