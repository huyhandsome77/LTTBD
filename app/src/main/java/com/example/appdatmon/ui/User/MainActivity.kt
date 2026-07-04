package com.example.appdatmon.ui.User

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.drawerlayout.widget.DrawerLayout
import com.example.appdatmon.R
import com.example.appdatmon.data.api.AuthManager
import com.example.appdatmon.ui.auth.LoginActivity
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    lateinit var menuBtn: AppCompatImageView
    lateinit var navigationView: NavigationView
    lateinit var btnScanQR: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawerLayout)
        menuBtn = findViewById(R.id.menuBtn)
        navigationView = findViewById(R.id.navigationView)
        btnScanQR = findViewById(R.id.btnScanQR)

        updateMenuVisibility()

        // OPEN MENU

        menuBtn.setOnClickListener {
            updateMenuVisibility()
            drawerLayout.open()
        }

        // CLICK MENU

        navigationView.setNavigationItemSelectedListener {

            when(it.itemId){

                // STORY

                R.id.nav_story -> {

                    startActivity(
                        Intent(
                            this,
                            StoryActivity::class.java
                        )
                    )

                }

                R.id.nav_login -> {

                    startActivity(
                        Intent(
                            this,
                            LoginActivity::class.java
                        )
                    )

                }

                R.id.nav_logout -> {
                    AuthManager.clear(this)
                    updateMenuVisibility()
                    Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show()
                }


                // CHEF

                R.id.nav_chef -> {

                    startActivity(
                        Intent(
                            this,
                            ChefActivity::class.java
                        )
                    )

                }

                // FOOD

                R.id.nav_food -> {

                    startActivity(
                        Intent(
                            this,
                            FoodActivity::class.java
                        )
                    )

                }

                // SPACE

                R.id.nav_space -> {
                    startActivity(
                        Intent(
                            this,
                            SpaceActivity::class.java
                        )
                    )

                }

                // CONTACT

                R.id.nav_contact -> {

                    startActivity(
                        Intent(
                            this,
                            ContactActivity::class.java
                        )
                    )

                }

            }


            drawerLayout.close()

            true
        }

        // CLICK QR
        btnScanQR.setOnClickListener {
            startActivity(
                Intent(this, ScanQRActivity::class.java)
            )
        }

        // đặt bàn

        val btnBooking =
            findViewById<LinearLayout>(R.id.btnBooking)

        btnBooking.setOnClickListener {

            startActivity(
                Intent(this, BookingActivity::class.java)
            )

        }


        // tài khoản
        val idAccount =
            findViewById<LinearLayout>(R.id.idAccount)

        idAccount.setOnClickListener {

            startActivity(
                Intent(this, ProfileActivity::class.java)
            )

        }

        //đánh giá
        val btnReview =
            findViewById<LinearLayout>(R.id.btnReview)

        btnReview.setOnClickListener {
            startActivity(
                Intent(this, ReviewActivity::class.java)
            )
        }

        // Xem đánh giá (Banner)
        val btnViewReview = findViewById<Button>(R.id.btnViewReview)
        btnViewReview?.setOnClickListener {
            startActivity(
                Intent(this, ReviewActivity::class.java)
            )
        }

    }

    private fun updateMenuVisibility() {
        val menu = navigationView.menu
        val isLoggedIn = AuthManager.getToken(this) != null
        
        menu.findItem(R.id.nav_login).isVisible = !isLoggedIn
        menu.findItem(R.id.nav_logout).isVisible = isLoggedIn
    }

    override fun onResume() {
        super.onResume()
        updateMenuVisibility()
    }

}
