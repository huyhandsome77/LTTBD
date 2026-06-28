package com.example.appdatmon;

import android.os.Bundle;
import android.view.View;
<<<<<<< Updated upstream
=======
import android.widget.ImageView;
import android.widget.TextView;

>>>>>>> Stashed changes
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
<<<<<<< Updated upstream
=======

import com.example.appdatmon.data.api.AuthManager;
import com.example.appdatmon.ui.admin.AdminReviewsFragment;
import com.example.appdatmon.ui.admin.KitchenFragment;
import com.example.appdatmon.ui.auth.LoginActivity;
import com.google.android.material.navigation.NavigationView;
>>>>>>> Stashed changes

public class ActivityAdmin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

<<<<<<< Updated upstream
=======
        drawerLayout = findViewById(R.id.drawerLayout);
        tvTitle = findViewById(R.id.tvTitle);
        tvHomeWelcome = findViewById(R.id.tvHomeWelcome);

        ImageView btnMenu = findViewById(R.id.btnMenu);
        NavigationView navView = findViewById(R.id.navView);
        View homeButton = findViewById(R.id.homeButton);

        // Mặc định hiện trang chủ
        if (savedInstanceState == null) {

            if (tvHomeWelcome != null) {
                tvHomeWelcome.setVisibility(View.VISIBLE);
            }

            if (tvTitle != null) {
                tvTitle.setText("Trang chủ");
            }

            Fragment fragment =
                    getSupportFragmentManager()
                            .findFragmentById(R.id.content_container);

            if (fragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .remove(fragment)
                        .commit();
            }
        }

        // Mở Drawer
        if (btnMenu != null) {
            btnMenu.setOnClickListener(v ->
                    drawerLayout.openDrawer(GravityCompat.START));
        }

        // Nút đóng Drawer trong Header
        View headerView = navView.getHeaderView(0);

        if (headerView != null) {

            View btnCloseMenu =
                    headerView.findViewById(R.id.btnCloseMenu);

            if (btnCloseMenu != null) {

                btnCloseMenu.setOnClickListener(v ->
                        drawerLayout.closeDrawer(GravityCompat.START));
            }
        }

        // ==========================
        // NAVIGATION DRAWER
        // ==========================
        navView.setNavigationItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_users) {

                updateUI("Quản lý người dùng");
                loadFragment(new UserListFragment());

            }
            else if (id == R.id.nav_reviews) {

                updateUI("Quản lý đánh giá");
                loadFragment(new AdminReviewsFragment());

            }
            else if (id == R.id.nav_points) {

                updateUI("Quản lý tích điểm");

            }
            else if (id == R.id.nav_stats) {

                updateUI("Xem thống kê");

            }
            else if (id == R.id.nav_kitchen) {

                android.widget.Toast.makeText(
                        this,
                        "Kitchen clicked",
                        android.widget.Toast.LENGTH_LONG
                ).show();

                updateUI("Bộ phận bếp");

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_container,
                                new KitchenFragment())
                        .commit();
            }
            else if (id == R.id.nav_settings) {

                updateUI("Cài đặt hệ thống");

            }
            else if (id == R.id.nav_logout) {

                AuthManager.INSTANCE.clear(this);

                Intent intent =
                        new Intent(this, LoginActivity.class);

                intent.setFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);
                finish();
            }

            drawerLayout.closeDrawer(GravityCompat.START);

            return true;
        });

        // ==========================
        // BOTTOM MENU
        // ==========================
>>>>>>> Stashed changes
        View btnProduct = findViewById(R.id.btn_menu_product);
        View btnTable = findViewById(R.id.btn_menu_table);
        View btnUser = findViewById(R.id.btn_menu_user);
        View btnOrder = findViewById(R.id.btn_menu_order);
        View btnHome = findViewById(R.id.homeButton);

<<<<<<< Updated upstream
        // Mặc định ban đầu mở màn hình quản lý người dùng
        if (savedInstanceState == null) {
            loadFragment(new UserListFragment());
=======
        if (btnProduct != null) {
            btnProduct.setOnClickListener(v -> {

                updateUI("Quản lý sản phẩm");
                loadFragment(new QuanLyDanhMucSanPhamFragment());

            });
        }

        if (btnTable != null) {
            btnTable.setOnClickListener(v -> {

                updateUI("Quản lý bàn");
                loadFragment(new QuanLyBanFragment());

            });
        }

        if (btnUser != null) {
            btnUser.setOnClickListener(v -> {

                updateUI("Quản lý người dùng");
                loadFragment(new UserListFragment());

            });
        }

        if (btnOrder != null) {
            btnOrder.setOnClickListener(v -> {

                updateUI("Quản lý đơn hàng");
                loadFragment(new OrderListFragment());

            });
        }

        // ==========================
        // HOME
        // ==========================
        if (homeButton != null) {

            homeButton.setOnClickListener(v -> {

                if (tvTitle != null) {
                    tvTitle.setText("Trang chủ");
                }

                if (tvHomeWelcome != null) {
                    tvHomeWelcome.setVisibility(View.VISIBLE);
                }

                Fragment fragment =
                        getSupportFragmentManager()
                                .findFragmentById(R.id.content_container);

                if (fragment != null) {

                    getSupportFragmentManager()
                            .beginTransaction()
                            .remove(fragment)
                            .commit();
                }
            });
>>>>>>> Stashed changes
        }

<<<<<<< Updated upstream
        // Bắt sự kiện chuyển đổi các mục quản lý ở menu đáy
        if (btnUser != null) btnUser.setOnClickListener(v -> loadFragment(new UserListFragment()));
        if (btnOrder != null) btnOrder.setOnClickListener(v -> loadFragment(new OrderListFragment()));
        if (btnHome != null) btnHome.setOnClickListener(v -> loadFragment(new UserListFragment()));
=======
    private void updateUI(String title) {

        if (tvTitle != null) {
            tvTitle.setText(title);
        }

        if (tvHomeWelcome != null) {
            tvHomeWelcome.setVisibility(View.GONE);
        }
>>>>>>> Stashed changes
    }

    private void loadFragment(Fragment fragment) {

        if (fragment != null) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.content_container,
                            fragment
                    )
                    .commit();
        }
    }
}