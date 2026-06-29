package com.example.appdatmon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import com.example.appdatmon.data.api.AuthManager;
import com.example.appdatmon.ui.admin.AdminReviewsFragment;
import com.example.appdatmon.ui.auth.LoginActivity;
import com.google.android.material.navigation.NavigationView;

public class ActivityAdmin extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private TextView tvTitle;
    private View tvHomeWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        drawerLayout = findViewById(R.id.drawerLayout);
        tvTitle = findViewById(R.id.tvTitle);
        tvHomeWelcome = findViewById(R.id.tvHomeWelcome);
        ImageView btnMenu = findViewById(R.id.btnMenu);
        NavigationView navView = findViewById(R.id.navView);
        View homeButton = findViewById(R.id.homeButton);

        // Mặc định ban đầu hiện lời chào trang chủ
        if (savedInstanceState == null) {
            if (tvHomeWelcome != null) tvHomeWelcome.setVisibility(View.VISIBLE);
            if (tvTitle != null) tvTitle.setText("Trang chủ");

            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_container);
            if (fragment != null) {
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }
        }

        // Mở menu khi nhấn nút 3 gạch
        if (btnMenu != null) {
            btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        }

        // Xử lý nút tắt Menu trong Header
        View headerView = navView.getHeaderView(0);
        if (headerView != null) {
            View btnCloseMenu = headerView.findViewById(R.id.btnCloseMenu);
            if (btnCloseMenu != null) {
                btnCloseMenu.setOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.START));
            }
        }

        // Navigation Drawer Items
        navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_users) {
                updateUI("Quản lý người dùng");
                loadFragment(new UserListFragment());
            } else if (id == R.id.nav_reviews) {
                updateUI("Quản lý đánh giá");
                loadFragment(new AdminReviewsFragment());
            } else if (id == R.id.nav_kitchen) {
                updateUI("Bộ phận bếp");
                loadFragment(new com.example.appdatmon.ui.admin.KitchenFragment());
            } else if (id == R.id.nav_logout) {
                AuthManager.INSTANCE.clear(this);
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Bottom Navigation Buttons
        View btnProduct = findViewById(R.id.btn_menu_product);
        View btnTable = findViewById(R.id.btn_menu_table);
        View btnUser = findViewById(R.id.btn_menu_user);
        View btnOrder = findViewById(R.id.btn_menu_order);

        if (btnProduct != null) btnProduct.setOnClickListener(v -> {
            updateUI("Quản lý sản phẩm");
            loadFragment(new QuanLyDanhMucSanPhamFragment());
        });
        if (btnTable != null) btnTable.setOnClickListener(v -> {
            updateUI("Quản lý bàn");
            loadFragment(new QuanLyBanFragment());
        });
        if (btnUser != null) btnUser.setOnClickListener(v -> {
            updateUI("Quản lý người dùng");
            loadFragment(new UserListFragment());
        });
        if (btnOrder != null) btnOrder.setOnClickListener(v -> {
            updateUI("Quản lý đơn hàng");
            loadFragment(new OrderListFragment());
        });

        if (homeButton != null) {
            homeButton.setOnClickListener(v -> {
                if (tvTitle != null) tvTitle.setText("Trang chủ");
                if (tvHomeWelcome != null) tvHomeWelcome.setVisibility(View.VISIBLE);
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_container);
                if (fragment != null) {
                    getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                }
            });
        }
    }

    private void updateUI(String title) {
        if (tvTitle != null) tvTitle.setText(title);
        if (tvHomeWelcome != null) tvHomeWelcome.setVisibility(View.GONE);
    }

    private void loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_container, fragment)
                    .commit();
        }
    }
}
