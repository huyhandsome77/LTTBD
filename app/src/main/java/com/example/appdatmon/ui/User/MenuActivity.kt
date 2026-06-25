package com.example.futuresushi

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_menu)

        // BACK BUTTON

        val btnBack = findViewById<ImageView>(R.id.btnBack)

        btnBack.setOnClickListener {

            finish()

        }

        // CART BUTTON

        val btnCart = findViewById<CardView>(R.id.btnCart)

        btnCart.setOnClickListener {

            startActivity(
                Intent(this, CartActivity::class.java)
            )

        }

    }

}