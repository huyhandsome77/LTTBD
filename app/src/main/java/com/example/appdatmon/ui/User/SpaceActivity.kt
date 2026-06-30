package com.example.futuresushi

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SpaceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_space)

        findViewById<ImageView>(R.id.btnBackSpace)
            .setOnClickListener {

                finish()

            }
    }
}