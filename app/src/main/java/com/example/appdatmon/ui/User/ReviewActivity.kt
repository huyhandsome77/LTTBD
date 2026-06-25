package com.example.futuresushi.review

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.futuresushi.R

class ReviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_review)

        val btnBack =
            findViewById<ImageView>(R.id.btnBackReview)

        btnBack.setOnClickListener {

            finish()

        }

    }

}