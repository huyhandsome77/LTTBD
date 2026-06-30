package com.example.futuresushi

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class FoodActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_food)

        findViewById<ImageView>(R.id.btnBackFood)
            .setOnClickListener {

                finish()

            }
    }
}