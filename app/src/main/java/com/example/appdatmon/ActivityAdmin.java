package com.example.appdatmon;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class ActivityAdmin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        View btnProduct = findViewById(R.id.btn_menu_product);
        View btnTable = findViewById(R.id.btn_menu_table);
        View btnUser = findViewById(R.id.btn_menu_user);
        View btnOrder = findViewById(R.id.btn_menu_order);
        View btnHome = findViewById(R.id.homeButton);

        // Mặc định ban đầu mở màn hình quản lý người dùng
        if (savedInstanceState == null) {
            loadFragment(new UserListFragment());
        }

        // Bắt sự kiện chuyển đổi các mục quản lý ở menu đáy
        if (btnUser != null) btnUser.setOnClickListener(v -> loadFragment(new UserListFragment()));
        if (btnOrder != null) btnOrder.setOnClickListener(v -> loadFragment(new OrderListFragment()));
        if (btnHome != null) btnHome.setOnClickListener(v -> loadFragment(new UserListFragment()));
    }

    private void loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_container, fragment)
                    .commit();
        }
    }
}