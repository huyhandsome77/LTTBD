package com.example.futuresushi

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.widget.LinearLayout
class BookingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_booking)

        // BACK

        val btnBack =
            findViewById<ImageView>(R.id.btnBackBooking)

        btnBack.setOnClickListener {

            finish()

        }

    }

}