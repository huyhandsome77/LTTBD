package com.example.appdatmon

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.example.appdatmon.ui.auth.LoginActivity
import com.example.appdatmon.review.ReviewActivity
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

        // OPEN MENU

        menuBtn.setOnClickListener {

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
        val btnScanQR =
            findViewById<LinearLayout>(R.id.btnScanQR)

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

    }

}
